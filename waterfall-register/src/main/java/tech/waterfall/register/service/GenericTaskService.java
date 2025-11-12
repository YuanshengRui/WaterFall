package tech.waterfall.register.service;

import java.time.Instant;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.dao.GenericTaskDao;
import tech.waterfall.register.model.GenericTask;
import tech.waterfall.register.support.TaskStatus;

@AllArgsConstructor
@Slf4j
public class GenericTaskService implements IGenericTaskService {
    private GenericTaskDao genericTaskDao;

    @Override
    public Mono<GenericTask> insert(GenericTask task) {
        log.debug("insert a task into db, taskName: {}, taskId: {}",
                task.getTaskName(), task.getTaskId());
        task.setTaskStatus(TaskStatus.Created);
        setTimeInfo(task);
        return genericTaskDao.insertOne(task);
    }

    @Override
    public Mono<Void> updateStatus(String id, TaskStatus status) {
        return genericTaskDao.updateStatus(id, status);
    }

    @Override
    public Mono<GenericTask> findAndUpdateStatus(String id, TaskStatus oldStatus, TaskStatus newStatus, boolean returnNew) {
        return genericTaskDao.findAndUpdateStatus(id, oldStatus, newStatus, returnNew);
    }

    @Override
    public Mono<GenericTask> findByTask(String taskName, String taskId) {
        log.debug("find an task, taskName: {}, taskId: {}", taskName, taskId);
        return genericTaskDao.findByTask(taskName, taskId);
    }

    @Override
    public Mono<GenericTask> replaceUnqueued(GenericTask genericTask) {
        log.debug("try to replace an task, taskName: {}, taskId: {}",
                genericTask.getTaskName(), genericTask.getTaskId());

        setTimeInfo(genericTask);
        return genericTaskDao.findAndReplace(genericTask,
                List.of(TaskStatus.Created, TaskStatus.Completed), true);
    }

    @Override
    public Flux<GenericTask> getAll() {
        return genericTaskDao.find(null);
    }

    @Override
    public Mono<String> refreshTask(GenericTask genericTask) {
        log.debug("refresh an task, taskName: {}, taskId: {}",
                genericTask.getTaskName(), genericTask.getTaskId());
        genericTask.setTaskStatus(TaskStatus.Created);
        return genericTaskDao.updateTask(genericTask);
    }

    @Override
    public Flux<GenericTask> bulkInsert(List<GenericTask> tasks) {
        log.debug("insert tasks into db, size: {}", tasks.size());
        return genericTaskDao.bulkInsert(tasks);
    }

    private void setTimeInfo(GenericTask task) {
        Instant now = Instant.now();
        if (task.getCreatedTime() == null) {
            task.setCreatedTime(now);
        }
        if (task.getLastModifiedTime() == null) {
            task.setLastModifiedTime(now);
        }
    }
}
