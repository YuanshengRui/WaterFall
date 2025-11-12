package tech.waterfall.register.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import tech.waterfall.register.exception.UnknownTaskKindException;
import tech.waterfall.register.model.TaskMetaData;
import tech.waterfall.register.watcher.TaskMetaDataWatcher;

@Slf4j
public class TaskMetaDataManager implements ITaskMetaDataManager {

    private Map<String, String> queueNameCache = new ConcurrentHashMap<>();
    private final ITaskMetaDataService taskMetaDataService;
    private final TaskMetaDataWatcher taskMetaDataWatcher;

    public TaskMetaDataManager(ITaskMetaDataService taskMetaDataService,
            TaskMetaDataWatcher taskMetaDataWatcher) {
        this.taskMetaDataService = taskMetaDataService;
        this.taskMetaDataWatcher = taskMetaDataWatcher;
    }

    @PostConstruct
    public void startWatcher() {
        taskMetaDataWatcher.start(this::updateQueueNameCache);
    }

    @PreDestroy
    public void stopWatcher() {
        taskMetaDataWatcher.stop();
    }

    @Override
    public Mono<String> queueName(String taskName) {
        return Mono.justOrEmpty(queueNameCache.get(taskName))
                .switchIfEmpty(Mono.defer(() -> taskMetaDataService.getQueueName(taskName)))
                .filter(StringUtils::hasLength)
                .map(queueName -> {
                    String oldName = queueNameCache.putIfAbsent(taskName, queueName);
                    return oldName == null ? queueName : oldName;
                })
                .switchIfEmpty(Mono.defer(() -> {
                    throw new UnknownTaskKindException(taskName);
                }));
    }

    private void updateQueueNameCache(ChangeStreamEvent<TaskMetaData> changeStreamEvent) {
        if (changeStreamEvent == null) {
            return;
        }
        switch (changeStreamEvent.getOperationType()) {
            case DELETE://TBD only evict the item that has been deleted
            case DROP:
            case DROP_DATABASE:
                queueNameCache.clear();
                break;
            case UPDATE:
                refreshQueueNameCache(changeStreamEvent.getBody());
                break;
            default:
                break;
        }
    }

    private void refreshQueueNameCache(TaskMetaData taskMetaData) {
        if (queueNameCache.containsKey(taskMetaData.getTaskName())) {
            queueNameCache.put(taskMetaData.getTaskName(), taskMetaData.getQueueName());
        }
    }
}
