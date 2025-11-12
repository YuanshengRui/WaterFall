package tech.waterfall.register.dao;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.GenericTask;
import tech.waterfall.register.support.TaskStatus;
import tech.waterfall.register.support.column.GenericTaskColumn;
import tech.waterfall.register.support.column.TaskInfoColumn;

import java.time.Instant;
import java.util.Collection;

public class GenericTaskDao extends TaskBaseDao<GenericTask> {
    public GenericTaskDao(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public Mono<Void> updateStatus(String taskName, String taskId, TaskStatus status) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(TaskInfoColumn.taskId.name()).is(taskId);
        Update update = Update.update(GenericTaskColumn.taskStatus.name(), status)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());
        return updateFirst(Query.query(criteria), update).then();
    }

    public Mono<Void> updateStatus(String id, TaskStatus status) {
        Criteria criteria = Criteria.where(BaseInfoColumn._id.name()).is(id);
        Update update = Update.update(GenericTaskColumn.taskStatus.name(), status)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());
        return updateFirst(Query.query(criteria), update).then();
    }

    public Mono<Void> updateStatusAndRetries(String id, TaskStatus status, int retries) {
        Criteria criteria = Criteria.where(BaseInfoColumn._id.name()).is(id);
        Update update = Update.update(GenericTaskColumn.taskStatus.name(), status)
                .set(GenericTaskColumn.retries.name(), retries)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());
        return updateFirst(Query.query(criteria), update).then();
    }


    public Mono<GenericTask> findByTask(String taskName, String taskId) {
        return super.findByTask(taskName, taskId);
    }

    public Mono<Long> countNotCompletedByTask(String taskName, String taskId) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(TaskInfoColumn.taskId.name()).is(taskId)
                .and(GenericTaskColumn.taskStatus.name()).ne(TaskStatus.Completed);
        Query query = Query.query(criteria);
        return count(query);
    }

    public Flux<GenericTask> findStuckTasks(Collection<String> taskNames, Instant staleTime) {
        Criteria criteria = Criteria.where(TaskInfoColumn.lastModifiedTime.name())
                .lt(staleTime)
                .and(GenericTaskColumn.taskStatus.name()).ne(TaskStatus.Completed)
                .and(TaskInfoColumn.taskName.name()).in(taskNames);
        Query query = Query.query(criteria);
        query.fields().include(TaskInfoColumn.taskName.name(),
                TaskInfoColumn.taskId.name(),
                TaskInfoColumn.groupId.name(),
                GenericTaskColumn.retries.name());
        return find(query);
    }

    public Flux<GenericTask> findStuckTasks(String taskName, Instant staleTime, int maxReties) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(GenericTaskColumn.taskStatus.name()).ne(TaskStatus.Completed)
                .and(TaskInfoColumn.lastModifiedTime.name()).lt(staleTime)
                .orOperator(Criteria.where(GenericTaskColumn.retries.name()).exists(false),
                        Criteria.where(GenericTaskColumn.retries.name()).lt(maxReties));
        Query query = Query.query(criteria);
        query.fields().include(TaskInfoColumn.taskName.name(),
                TaskInfoColumn.taskId.name(),
                TaskInfoColumn.groupId.name(),
                GenericTaskColumn.retries.name());
        return find(query);
    }

    public Mono<String> updateTask(GenericTask genericTask) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(genericTask.getTaskName())
                .and(TaskInfoColumn.taskId.name()).is(genericTask.getTaskId());
        Update update = Update.update(TaskInfoColumn.lastModifiedTime.name(), Instant.now())
                .set(TaskInfoColumn.taskRawData.name(), genericTask.getTaskRawData())
                .set(GenericTaskColumn.taskStatus.name(), genericTask.getTaskStatus());

        return mongoTemplate.findAndModify(Query.query(criteria), update, GenericTask.class)
                .map(GenericTask::getTaskId);
    }

    public Mono<GenericTask> findAndUpdateStatus(String id, TaskStatus taskStatus, boolean returnNew) {
        Criteria criteria = Criteria.where(BaseInfoColumn._id.name()).is(id);
        Update update = Update.update(GenericTaskColumn.taskStatus.name(), taskStatus)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());
        return mongoTemplate.findAndModify(Query.query(criteria), update,
                FindAndModifyOptions.options().returnNew(returnNew),
                GenericTask.class);
    }

    public Mono<GenericTask> findAndUpdateStatus(String id, TaskStatus oldStatus,
            TaskStatus newStatus, boolean returnNew) {
        Criteria criteria = Criteria.where(BaseInfoColumn._id.name()).is(id)
                .and(GenericTaskColumn.taskStatus.name()).is(oldStatus);
        Update update = Update.update(GenericTaskColumn.taskStatus.name(), newStatus)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());

        return mongoTemplate.findAndModify(Query.query(criteria), update,
                FindAndModifyOptions.options().returnNew(returnNew),
                GenericTask.class);
    }

    public Mono<GenericTask> findAndReplace(GenericTask genericTask,
            Collection<TaskStatus> statuses,
            boolean returnNew) {

        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(genericTask.getTaskName())
                .and(TaskInfoColumn.taskId.name()).is(genericTask.getTaskId())
                .and(GenericTaskColumn.taskStatus.name()).in(statuses);
        FindAndReplaceOptions findAndReplaceOptions = FindAndReplaceOptions.options();
        if (returnNew) {
            findAndReplaceOptions.returnNew();
        }
        return mongoTemplate.findAndReplace(Query.query(criteria), genericTask, findAndReplaceOptions);
    }
}
