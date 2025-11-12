package tech.waterfall.register.dao;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.TaskInfo;
import tech.waterfall.register.support.column.TaskInfoColumn;

public abstract class TaskBaseDao<T extends TaskInfo> extends BaseDao<T, String> {

    public TaskBaseDao(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate, String.class);
    }

    protected T applyId(T task, String id) {
        task.setId(id);
        if (!StringUtils.hasLength(task.getTaskId())) {
            task.setTaskId(id);
        }
        return task;
    }

    public Mono<String> insertWithNextId(T task) {
        return this.getNextId().flatMap(id -> super.insert(applyId(task, id)));
    }

    public Mono<T> insertOne(T task) {
        return this.getNextId().flatMap(id -> this.mongoTemplate.insert(applyId(task, id)));
    }

    protected Mono<T> findByTask(String taskName, String taskId, String... includeColumns) {
        Criteria criteria = Criteria.where(TaskInfoColumn.taskName.name()).is(taskName)
                .and(TaskInfoColumn.taskId.name()).is(taskId);
        Query query = Query.query(criteria);
        if (includeColumns != null && includeColumns.length > 0) {
            query.fields().include(includeColumns);
        }
        return findOne(query);
    }
}
