package tech.waterfall.register.service;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.dto.IngestResult;
import tech.waterfall.register.dto.Task;
import tech.waterfall.register.exception.InvalidTaskException;
import tech.waterfall.register.model.GenericTask;
import tech.waterfall.register.service.sqs.SqsMessageSender;
import tech.waterfall.register.support.TaskStatus;

@Slf4j
public class GenericTaskIngestion {
    private final IGenericTaskService genericTaskService;
    private SqsMessageSender sqsMessageSender;
    private final ITaskMetaDataManager taskMetaDataManager;

    public GenericTaskIngestion(IGenericTaskService genericTaskService, SqsMessageSender sqsMessageSender,
            ITaskMetaDataManager taskMetaDataManager) {
        this.genericTaskService = genericTaskService;
        this.sqsMessageSender = sqsMessageSender;
        this.taskMetaDataManager = taskMetaDataManager;
    }

    void setSqsMessageSender(SqsMessageSender sqsMessageSender) {
        this.sqsMessageSender = sqsMessageSender;
    }

    public Mono<String> ingest(Task task) {
        log.info("begin to ingest a immediate task, {}", task);
        return Mono.justOrEmpty(task)
                .flatMap(this::validate)
                .map(this::toGenericTask)
                .flatMap(this::doIngest)
                .doOnError(e -> log.warn("failed to ingest a immediate task, taskName: {}, taskId: {}",
                        task.getTaskName(), task.getTaskId(), e))
                .doOnSuccess(taskId -> log.info("succeeded to ingest a immediate task,  taskName: {}, taskId: {}",
                        task.getTaskName(), task.getTaskId()));
    }

    public Mono<IngestResult> bulkIngest(List<Task> tasks) {
        log.info("begin to ingest immediate tasks, size: {}", tasks.size());
        return Flux.fromIterable(tasks)
                .flatMap(task -> ingest(task)
                        .map(taskId -> onSuccess(task))
                        .onErrorResume(onError(task)))
                .collectList()
                .map(this::gather);
    }

    public Mono<Task> find(String taskName, String taskId) {
        return genericTaskService.findByTask(taskName, taskId).map(this::toTask);
    }

    private Mono<String> doIngest(GenericTask task) {
        return tryStoreTask(task)
                .flatMap(this::queueTask)
                .flatMap(this::afterQueued);
    }

    private Mono<GenericTask> tryStoreTask(GenericTask task) {
        return Mono.just(task).filter(t -> StringUtils.isNotEmpty(t.getTaskId()))
                .flatMap(t -> genericTaskService.findByTask(t.getTaskName(), t.getTaskId()))
                .flatMap(t -> genericTaskService.replaceUnqueued(task)
                        .doOnNext(newTask -> log.info("An unqueued task(taskName:{}, taskId:{}) exists, try to replace it",
                                newTask.getTaskName(), newTask.getTaskId()))
                        .switchIfEmpty(Mono.fromCallable(() -> {
                            log.info("A queued task(taskName:{}, taskId:{}) exists, skip this ingestion",
                                    task.getTaskName(), task.getTaskId());
                            return t;
                        })))
                .switchIfEmpty(Mono.defer(() -> genericTaskService.insert(task)));
    }

    private Mono<GenericTask> queueTask(GenericTask task) {
        return Mono.just(task).filter(t -> t.getTaskStatus().equals(TaskStatus.Created))
                .flatMap(t -> taskMetaDataManager.queueName(t.getTaskName())
                        .map(queueName -> sqsMessageSender.send(queueName, t)))
                .thenReturn(task);

    }

    private Mono<String> afterQueued(GenericTask task) {
        return Mono.just(task).filter(t -> t.getTaskStatus().equals(TaskStatus.Created))
                        .flatMap(t -> genericTaskService.findAndUpdateStatus(t.getId(),
                                TaskStatus.Created, TaskStatus.Queued, true))
                        .thenReturn(task.getTaskId());
    }

    private GenericTask toGenericTask(Task task) {
        GenericTask genericTask = new GenericTask();
        genericTask.setTaskName(task.getTaskName());
        genericTask.setTaskId(task.getTaskId());
        genericTask.setTaskRawData(task.getTaskRawData());
        genericTask.setTaskHeaders(task.getTaskHeaders());
        genericTask.setTaskStatus(TaskStatus.Created);
        genericTask.setGroupId(task.getGroupId());
        genericTask.setTriggerTime(task.getTriggerTime());
        genericTask.setParentId(task.getParentId());
        return genericTask;
    }

    protected IngestResult gather(List<IngestResult.TaskStub> taskStubs) {
        IngestResult result = new IngestResult();
        result.setTasks(taskStubs);
        return result;
    }

    protected IngestResult.TaskStub onSuccess(Task task) {
        IngestResult.TaskStub taskStub = new IngestResult.TaskStub();
        taskStub.setTaskId(task.getTaskId());
        taskStub.setTaskName(task.getTaskName());
        return taskStub;
    }

    protected Function<Throwable, Mono<IngestResult.TaskStub>> onError(Task task) {
        return t -> {
            IngestResult.TaskStub taskStub = new IngestResult.TaskStub();
            taskStub.setTaskId(task.getTaskId());
            taskStub.setTaskName(task.getTaskName());
            if (t instanceof MessagingException) {
                taskStub.setErrorCode("SQS");
            } else if (t instanceof DataAccessException) {
                taskStub.setErrorCode("Mongo");
            } else {
                taskStub.setErrorCode("Internal");
            }

            return Mono.just(taskStub);
        };
    }

    private Mono<Task> validate(Task task) {
        log.debug("validate task, {}", task);
        if (StringUtils.isEmpty(task.getTaskName())) {
            return Mono.error(new InvalidTaskException(task,
                    "Invalid immediate task, taskName must be present"));
        }
        return taskMetaDataManager.queueName(task.getTaskName())
                .map(queueName -> task);
    }

    private Task toTask(GenericTask genericTask) {
        Task task = new Task();
        task.setTaskName(genericTask.getTaskName());
        task.setTaskId(genericTask.getTaskId());
        task.setTaskRawData(genericTask.getTaskRawData());
        return task;
    }
}
