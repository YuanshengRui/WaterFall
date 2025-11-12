package tech.waterfall.register.service;


import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import tech.waterfall.register.dao.TaskMetaDataDao;

@AllArgsConstructor
public class TaskMetaDataService implements ITaskMetaDataService {
    private TaskMetaDataDao taskMetaDataDao;

    @Override
    public Mono<String> getQueueName(String taskName) {
        return taskMetaDataDao.getQueueNameByTaskName(taskName);
    }
}
