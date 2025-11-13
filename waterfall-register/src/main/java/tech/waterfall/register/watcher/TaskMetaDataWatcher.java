package tech.waterfall.register.watcher;

import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.Disposable;
import tech.waterfall.register.model.TaskMetaData;

import java.util.function.Consumer;

public class TaskMetaDataWatcher {
    private ReactiveMongoTemplate mongoTemplate;
    private Disposable disposableWatcher;

    public TaskMetaDataWatcher(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void start(Consumer<ChangeStreamEvent<TaskMetaData>> consumer) {
//        disposableWatcher = mongoTemplate.changeStream("taskMetaData",
//                ChangeStreamOptions.empty(),
//                TaskMetaData.class)
//                .subscribe(consumer);
    }

    public void stop() {
        if (disposableWatcher != null) {
            disposableWatcher.dispose();
        }
    }
}
