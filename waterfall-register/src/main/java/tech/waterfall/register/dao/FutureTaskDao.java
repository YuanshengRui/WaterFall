package tech.waterfall.register.dao;


import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.FutureTask;
import tech.waterfall.register.support.column.FutureTaskColumn;
import tech.waterfall.register.support.column.IdColumn;
import tech.waterfall.register.support.column.TaskInfoColumn;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class FutureTaskDao extends TaskBaseDao<FutureTask> {
    public FutureTaskDao(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public Mono<String> findIdByTask(String taskName, String taskId) {
        return super.findByTask(taskName, taskId, IdColumn._id.name()).map(FutureTask::getId);
    }

    public Mono<FutureTask> findByTask(String taskName, String taskId) {
        return super.findByTask(taskName, taskId);
    }

    public Mono<Void> deleteByTask(String taskName, String taskId) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(TaskInfoColumn.taskId.name()).is(taskId);
        Query query = Query.query(criteria);
        return mongoTemplate.findAndRemove(query, FutureTask.class).then();
    }

    public Mono<Long> deleteTasks(String taskName, List<String> taskIds) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(TaskInfoColumn.taskId.name()).in(taskIds);
        Query query = Query.query(criteria);
        return mongoTemplate.remove(query, FutureTask.class).map(DeleteResult::getDeletedCount);
    }

    public Mono<Void> deleteById(String id) {
        Criteria criteria = Criteria.where(IdColumn._id.name()).is(id);
        return mongoTemplate.remove(Query.query(criteria), FutureTask.class).then();
    }

    public Flux<FutureTask> findByFireTimeInTaskNames(List<String> taskNames,
            Instant timeBefore, int limit) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).in(taskNames)
                .and(FutureTaskColumn.fireTime.name()).lte(timeBefore);
        return find(Query.query(criteria).limit(limit));
    }

    public Mono<Void> updateFireTime(String id, Instant fireTime) {
        Criteria criteria = Criteria.where(IdColumn._id.name()).is(id);
        Update update = Update.update(FutureTaskColumn.fireTime.name(), fireTime)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());
        return updateFirst(Query.query(criteria), update).then();
    }

    public Mono<Void> updateCronExpressionAndFireTime(String taskName, String taskId,
            String cronExpression,
            Instant fireTime) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(TaskInfoColumn.taskId.name()).is(taskId);
        Update update = Update.update(FutureTaskColumn.cronExpression.name(), cronExpression)
                .set(FutureTaskColumn.fireTime.name(), fireTime)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());
        return updateFirst(Query.query(criteria), update).then();
    }

    public Mono<FutureTask> update(String taskName, String taskId,
            Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            return Mono.empty();
        }

        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(TaskInfoColumn.taskId.name()).is(taskId);
        Update update = new Update().currentDate(TaskInfoColumn.lastModifiedTime.name());
        updates.forEach((k, v) -> update.set(k, v));
        return mongoTemplate.findAndModify(Query.query(criteria), update,
                FindAndModifyOptions.options().returnNew(true), FutureTask.class)
                .map(Function.identity());
    }

    public Mono<Void> updateWhenSucceeded(String id, Instant fireTime) {
        Criteria criteria = Criteria.where(IdColumn._id.name()).is(id);
        Update update = Update.update(FutureTaskColumn.fireTime.name(), fireTime)
                .inc(FutureTaskColumn.triggerCount.name(), 1)
                .currentDate(TaskInfoColumn.lastModifiedTime.name());
        return updateFirst(Query.query(criteria), update).then();
    }
}
