package tech.waterfall.register.rest;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import tech.waterfall.register.dto.IngestResult;
import tech.waterfall.register.dto.Task;
import tech.waterfall.register.dto.TaskUpdater;
import tech.waterfall.register.service.ITaskIngestion;

@RestController
@RequestMapping(value = "{version}/tasks")
@AllArgsConstructor
@Slf4j
public class TaskIngestionController {

    private ITaskIngestion taskIngestion;


    @GetMapping(value = "future/{taskName}/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Task> findFutureTask(@PathVariable("taskName") String taskName,
            @PathVariable("taskId") String taskId) {
        return taskIngestion.findFutureTask(taskName, taskId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> ingest(@Valid @RequestBody Task task) {
        return taskIngestion.ingest(task);
    }

    @PostMapping(value = "immediate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> ingestImmediateTask(@Valid @RequestBody Task task) {
        return taskIngestion.ingestImmediateTask(task);
    }

    @GetMapping(value = "immediate/{taskName}/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Task> getImmediateTask(@PathVariable("taskName") String taskName,
            @PathVariable("taskId") String taskId) {
        return taskIngestion.findImmediateTask(taskName, taskId);
    }

    @PostMapping(value = "future", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> ingestFutureTask(@Valid @RequestBody Task task) {
        return taskIngestion.ingestFutureTask(task);
    }

    /**
     * deprecated, replaced by insertImmediateTasks
     */
    @Deprecated
    @PostMapping(value = "bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<IngestResult> bulkInsert(@RequestBody List<Task> tasks) {
        return taskIngestion.ingestImmediateTasks(tasks);
    }

    /**
     * Accept a list of tasks but process them one by one.
     *
     * @param tasks Tasks to ingest into task process framework.
     * @return process result with each task.
     */
    @PostMapping(value = "bulk/immediate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<IngestResult> insertImmediateTasks(@RequestBody List<Task> tasks) {
        return taskIngestion.ingestImmediateTasks(tasks);
    }

    /**
     * Ingest a list of tasks in the fire_and_forget way to improve the performance.
     * deprecated, replaced by insertFutureTasks
     */
    @Deprecated
    @PostMapping(value = "bulkIngest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Integer> bulkInsertInFireAndForgetWay(@RequestBody List<Task> tasks) {
        log.info("bulkIngest for tasks. size={}", CollectionUtils.isEmpty(tasks) ? 0 : tasks.size());
        return taskIngestion.ingestFutureTasks(tasks);
    }

    /**
     * Ingest a list of tasks in the fire_and_forget way to improve the performance.
     *
     * @param tasks Tasks to ingest into task process framework.
     * @return to indicate batch process ok or not.
     */
    @PostMapping(value = "bulk/future", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Integer> insertFutureTasks(@RequestBody List<Task> tasks) {
        log.info("bulkIngest for tasks. size={}", CollectionUtils.isEmpty(tasks) ? 0 : tasks.size());
        return taskIngestion.ingestFutureTasks(tasks);
    }

    /**
     * deprecated, replaced by updateFutureTask
     */
    @Deprecated
    @PutMapping(value = "{taskName}/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> updateTask(@PathVariable("taskName") String taskName,
                                 @PathVariable("taskId") String taskId,
                                 @RequestBody TaskUpdater taskUpdater) {
        return taskIngestion.updateFutureTask(taskName, taskId, taskUpdater);
    }

    @PutMapping(value = "future/{taskName}/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> updateFutureTask(@PathVariable("taskName") String taskName,
            @PathVariable("taskId") String taskId,
            @RequestBody TaskUpdater taskUpdater) {
        return taskIngestion.updateFutureTask(taskName, taskId, taskUpdater);
    }

    /**
     * deprecated, replaced by cancelFutureTask
     */
    @Deprecated
    @DeleteMapping("{taskName}/{taskId}")
    public Mono<Void> cancel(@PathVariable("taskName") String taskName,
                             @PathVariable("taskId") String taskId) {
        return taskIngestion.cancelFutureTask(taskName, taskId);
    }

    @DeleteMapping("future/{taskName}/{taskId}")
    public Mono<Void> cancelFutureTask(@PathVariable("taskName") String taskName,
            @PathVariable("taskId") String taskId) {
        return taskIngestion.cancelFutureTask(taskName, taskId);
    }

    @Deprecated
    @DeleteMapping(value = "{taskName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Long> bulkCancel(@PathVariable("taskName") String taskName, @RequestBody List<String> taskIds) {
        // TODO: how to add Span for bulkCancel.
        log.info("bulkCancel {}.  size={}", taskName, CollectionUtils.isEmpty(taskIds) ? 0 : taskIds.size());
        return taskIngestion.cancelFutureTasks(taskName, taskIds);
    }

    @DeleteMapping(value = "future/{taskName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Long> cancelFutureTasks(@PathVariable("taskName") String taskName, @RequestBody List<String> taskIds) {
        log.info("bulkCancel {}.  size={}", taskName, CollectionUtils.isEmpty(taskIds) ? 0 : taskIds.size());
        return taskIngestion.cancelFutureTasks(taskName, taskIds);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlerWebClientException(IllegalArgumentException illegalArgumentException) {
        return illegalArgumentException.getMessage();
    }
}
