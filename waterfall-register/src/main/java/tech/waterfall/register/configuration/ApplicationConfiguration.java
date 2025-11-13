package tech.waterfall.register.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import tech.waterfall.register.dao.FutureTaskDao;
import tech.waterfall.register.dao.GenericTaskDao;
import tech.waterfall.register.dao.TaskMetaDataDao;
import tech.waterfall.register.service.FutureTaskService;
import tech.waterfall.register.service.GenericTaskService;
import tech.waterfall.register.service.IFutureTaskService;
import tech.waterfall.register.service.IGenericTaskService;
import tech.waterfall.register.service.ITaskMetaDataManager;
import tech.waterfall.register.service.ITaskMetaDataService;
import tech.waterfall.register.service.TaskMetaDataManager;
import tech.waterfall.register.service.TaskMetaDataService;
import tech.waterfall.register.watcher.TaskMetaDataWatcher;

@Configuration
@Import(MongoBaseConfiguration.class)
public class ApplicationConfiguration {
    @Bean
    public GenericTaskDao genericTaskDao(ReactiveMongoTemplate reactiveMongoTemplate) {
        return new GenericTaskDao(reactiveMongoTemplate);
    }

    @Bean
    public FutureTaskDao futureTaskDao(ReactiveMongoTemplate reactiveMongoTemplate) {
        return new FutureTaskDao(reactiveMongoTemplate);
    }

    @Bean
    public TaskMetaDataDao taskMetaDataDao(ReactiveMongoTemplate reactiveMongoTemplate) {
        return new TaskMetaDataDao(reactiveMongoTemplate);
    }

    @Bean
    public IGenericTaskService genericTaskService(GenericTaskDao genericTaskDao) {
        return new GenericTaskService(genericTaskDao);
    }

    @Bean
    public IFutureTaskService futureTaskService(FutureTaskDao futureTaskDao) {
        return new FutureTaskService(futureTaskDao);
    }

    @Bean
    public ITaskMetaDataService taskMetaDataService(TaskMetaDataDao taskMetaDataDao) {
        return new TaskMetaDataService(taskMetaDataDao);
    }

    @Bean
    public TaskMetaDataWatcher taskMetaDataWatcher(ReactiveMongoTemplate mongoTemplate) {
        return new TaskMetaDataWatcher(mongoTemplate);
    }

    @Bean
    public ITaskMetaDataManager taskMetaDataManager(ITaskMetaDataService taskMetaDataService,
            TaskMetaDataWatcher taskMetaDataWatcher) {
        return new TaskMetaDataManager(taskMetaDataService, taskMetaDataWatcher);
    }
}
