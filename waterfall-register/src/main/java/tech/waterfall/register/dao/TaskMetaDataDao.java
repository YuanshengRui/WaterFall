package tech.waterfall.register.dao;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.ConsumerSettings;
import tech.waterfall.register.model.TaskMetaData;
import tech.waterfall.register.support.column.TaskMetaDataColumn;

import java.util.Collection;
import static tech.waterfall.register.support.column.TaskMetaDataColumn.taskCategory;


public class TaskMetaDataDao extends BaseDao<TaskMetaData, String> {

    public TaskMetaDataDao(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public Mono<String> getQueueNameByTaskName(String taskName) {
        if (!StringUtils.hasLength(taskName)) {
            return Mono.empty();
        }
        Criteria criteria = Criteria.where(TaskMetaDataColumn.taskName.name()).is(taskName);
        Query query = Query.query(criteria);
        query.fields().include(TaskMetaDataColumn.queueName.name());
        return findOne(query).map(TaskMetaData::getQueueName);
    }

    public Mono<ConsumerSettings> getConsumerSettingsByTaskName(String taskName) {
        if (!StringUtils.hasLength(taskName)) {
            return Mono.empty();
        }

        Criteria criteria = Criteria.where(TaskMetaDataColumn.taskName.name()).is(taskName);
        Query query = Query.query(criteria);
        query.fields().include(TaskMetaDataColumn.consumerSettings.name());
        return findOne(query).map(TaskMetaData::getConsumerSettings);
    }

    /**
     * Return all task metadata records belongs to the given task category name.
     *
     * @param category the task category
     * @return the list of the task metadata belongs to the task category
     */
    public Flux<TaskMetaData> findAllByCategory(String category) {
        if (!StringUtils.hasLength(category)) {
            return Flux.empty();
        }

        Criteria criteria = Criteria.where(taskCategory.name()).is(category);
        return find(Query.query(criteria));
    }

    public Flux<TaskMetaData> findAllByCategories(Collection<String> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return Flux.empty();
        }

        Criteria criteria = Criteria.where(taskCategory.name()).in(categories);
        return find(Query.query(criteria));
    }

    public Flux<TaskMetaData> findAllRecoverySettings() {
        Query query = new Query();
        query.fields().include(TaskMetaDataColumn.taskName.name(),
                TaskMetaDataColumn.queueName.name(),
                TaskMetaDataColumn.recoverySettings.name());
        return find(query);
    }
}
