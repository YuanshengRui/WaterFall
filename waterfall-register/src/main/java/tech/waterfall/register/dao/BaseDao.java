package tech.waterfall.register.dao;

import com.mongodb.client.result.UpdateResult;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.BaseInfo;
import tech.waterfall.register.support.column.BaseInfoColumn;
import tech.waterfall.register.model.IIdentity;
import tech.waterfall.register.support.column.IdColumn;

public class BaseDao<T extends IIdentity<IdT>, IdT> implements MaxIdReteiver<IdT>, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(BaseDao.class);
    private ApplicationContext applicationContext;
    protected ReactiveMongoTemplate mongoTemplate;
    private Class<T> clazz;
    private Class<IdT> clsId;
    private IIdGenerator idGenerator;

    public BaseDao() {
        ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
        this.clazz = (Class)type.getActualTypeArguments()[0];
        this.clsId = (Class)type.getActualTypeArguments()[1];
    }

    public BaseDao(ReactiveMongoTemplate mongoTemplate) {
        this();
        this.mongoTemplate = mongoTemplate;
    }

    public BaseDao(ReactiveMongoTemplate mongoTemplate, Class idClass) {
        this.mongoTemplate = mongoTemplate;
        this.clsId = idClass;
        ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
        this.clazz = (Class)type.getActualTypeArguments()[0];
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Mono<IdT> getNextId() {
        return this.getKeyGenerator().getNextId(this);
    }

    public IIdGenerator<IdT> getKeyGenerator() {
        if (this.idGenerator == null) {
            synchronized(this) {
                if (this.idGenerator == null) {
                    if (this.clsId == Long.class) {
                        this.idGenerator = (IIdGenerator)this.applicationContext.getBean(LongIdGenerator.class);
                    } else if (this.clsId == String.class) {
                        this.idGenerator = (IIdGenerator)this.applicationContext.getBean(ObjectIdGenerator.class);
                    } else {
                        log.error("id generating, unknown ID type");
                    }
                }
            }
        }

        return this.idGenerator;
    }

    public Class<T> getClazz() {
        Assert.state(this.clazz != null, "Clazz have not yet been applied");
        return this.clazz;
    }

    public Mono<IdT> getMaxIdInRange(IdT min, IdT max) {
        Criteria criteria = Criteria.where(IdColumn._id.name()).gt(min).lt(max);
        Query query = Query.query(criteria).with(Sort.by(Direction.DESC, new String[]{IdColumn._id.name()}));
        query.fields().include(IdColumn._id.name());
        return this.mongoTemplate.findOne(query, this.clazz).map(IIdentity::getId);
    }

    public String getObjectId() {
        return ObjectId.get().toString();
    }

    public Mono<T> findById(IdT id) {
        return this.mongoTemplate.findById(id, this.clazz);
    }

    public Mono<T> findById(IdT id, Collection<String> columns, boolean include) {
        Criteria criteria = Criteria.where(IdColumn._id.getName()).is(id);
        Query query = this.fields(criteria, columns, include);
        return this.findOne(query);
    }

    public Flux<T> find(Query query) {
        return this.mongoTemplate.find(query, this.clazz);
    }

    public Flux<T> find(Query query, Collection<String> columns, boolean include) {
        return this.mongoTemplate.find(this.fields(query, columns, include), this.clazz);
    }

    public Flux<T> find(Criteria criteria, Collection<String> columns, boolean include) {
        return this.mongoTemplate.find(this.fields(criteria, columns, include), this.clazz);
    }

    public Flux<T> findByIds(Collection<Long> ids, Collection<String> columns, boolean include) {
        Criteria criteria = Criteria.where(IdColumn._id.getName()).in(ids);
        Query query = this.fields(criteria, columns, include);
        return this.find(query);
    }

    public Mono<T> findOne(Query query) {
        return this.mongoTemplate.findOne(query, this.clazz);
    }

    public Mono<T> findOne(Criteria criteria, Collection<String> columns, boolean include) {
        return this.mongoTemplate.findOne(this.fields(criteria, columns, include), this.clazz);
    }

    public Flux<T> findAll() {
        return this.mongoTemplate.findAll(this.clazz);
    }

    public Mono<Long> count(Query query) {
        return this.mongoTemplate.count(query, this.clazz);
    }

    public Mono<IdT> insert(T entity) {
        return this.mongoTemplate.insert(entity).map(IIdentity::getId);
    }

    public Mono<IdT> insertWithNextId(T entity) {
        return this.getNextId().flatMap((id) -> {
            entity.setId(id);
            return this.mongoTemplate.insert(entity).map(IIdentity::getId);
        });
    }

    public Mono<IdT> save(T entity) {
        return this.mongoTemplate.save(entity).map(IIdentity::getId);
    }

    public void updateFirst(long id, String updateFeilds) {
        Criteria criteria = Criteria.where(IdColumn._id.getName());
        this.updateFirst(Query.query(criteria), updateFeilds);
    }

    public Mono<UpdateResult> updateFirst(Query query, String updateFeileds) {
        return this.mongoTemplate.updateFirst(query, Update.fromDocument(Document.parse(updateFeileds), new String[0]), this.clazz);
    }

    public Mono<UpdateResult> updateFirst(Query query, Update update) {
        return this.mongoTemplate.updateFirst(query, update, this.clazz);
    }

    public Mono<UpdateResult> updateMulti(Query query, Update update) {
        return this.mongoTemplate.updateMulti(query, update, this.clazz);
    }

    protected Query fields(Criteria criteria, Collection<String> columns, boolean include) {
        Query query = Query.query(criteria);
        if (!CollectionUtils.isEmpty(columns)) {
            if (include) {
                Field var10001 = query.fields();
                columns.forEach(var10001::include);
            } else {
                Field var5 = query.fields();
                columns.forEach(var5::exclude);
            }
        }

        return query;
    }

    protected Query fields(Query query, Collection<String> columns, boolean include) {
        if (!CollectionUtils.isEmpty(columns)) {
            if (include) {
                Field var10001 = query.fields();
                columns.forEach(var10001::include);
            } else {
                Field var4 = query.fields();
                columns.forEach(var4::exclude);
            }
        }

        return query;
    }

    protected Query with(Query query, List<Sort.Order> orders) {
        return CollectionUtils.isEmpty(orders) ? query : query.with(Sort.by(orders));
    }

    protected Query with(Criteria criteria, List<Sort.Order> orders) {
        Query query = Query.query(criteria);
        return CollectionUtils.isEmpty(orders) ? query : query.with(Sort.by(orders));
    }

    public Flux<T> findByPage(Query query, long skip, int limit, Sort sort) {
        if (sort != null) {
            query.with(sort);
        }

        if (skip > 0L) {
            query.skip(skip);
        }

        if (limit > 0) {
            query.limit(limit);
        }

        return this.find(query);
    }

    public Update setLastModifiedBaseInfo(Update update, BaseInfo baseInfo) {
        return update.set(BaseInfoColumn.lastModifiedId.getName(), baseInfo.getLastModifiedId()).set(BaseInfoColumn.lastModifiedName.getName(), baseInfo.getLastModifiedName()).set(BaseInfoColumn.lastModifiedProxyName.getName(), baseInfo.getLastModifiedProxyName()).set(BaseInfoColumn.lastModifiedDate.getName(), new Date());
    }

    public Flux<T> bulkInsert(Collection<T> tasks) {
        return this.mongoTemplate.insert(tasks, this.clazz);
    }

    public Flux<T> bulkInsert(Mono<List<T>> tasks) {
        return this.mongoTemplate.insertAll(tasks, this.clazz);
    }
}
