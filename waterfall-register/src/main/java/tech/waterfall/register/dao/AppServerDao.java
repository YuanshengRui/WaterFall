//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tech.waterfall.register.dao;

import java.util.Date;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.AppServer;
import tech.waterfall.register.support.column.AppServerColumn;
import tech.waterfall.register.support.column.IdColumn;

public class AppServerDao {
    ReactiveMongoTemplate mongoTemplate;

    public AppServerDao(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<AppServer> findMaxId() {
        Query query = new Query();
        query.with(Sort.by(Direction.DESC, new String[]{IdColumn._id.name()}));
        query.fields().include(IdColumn._id.name());
        return this.mongoTemplate.findOne(query.limit(1), AppServer.class);
    }

    public Mono<AppServer> findAndModifyLastModifiedDateAfter(String appName, String hostName, Date before, Date lastModifiedDate) {
        Criteria criteria = Criteria.where(AppServerColumn.hostName.getName()).is(hostName).and(AppServerColumn.appName.getName()).is(appName).and(AppServerColumn.lastModifiedDate.getName()).gt(before);
        Update update = Update.update(AppServerColumn.lastModifiedDate.getName(), lastModifiedDate);
        return this.mongoTemplate.findAndModify(Query.query(criteria), update, AppServer.class);
    }

    public Mono<AppServer> findAndModifyLastModifiedDateBefore(String appName, Date before, String hostName, Date lastModifiedDate) {
        Criteria criteria = Criteria.where(AppServerColumn.appName.getName()).is(appName).and(AppServerColumn.lastModifiedDate.getName()).lt(before);
        Update update = Update.update(AppServerColumn.hostName.getName(), hostName).set(AppServerColumn.lastModifiedDate.getName(), lastModifiedDate);
        return this.mongoTemplate.findAndModify(Query.query(criteria), update, AppServer.class);
    }

    public Mono<AppServer> findAndModifyLastModifiedDate(String appName, String hostName, Date lastModifiedDate) {
        Criteria criteria = Criteria.where(AppServerColumn.appName.getName()).is(appName).and(AppServerColumn.hostName.getName()).is(hostName);
        Update update = Update.update(AppServerColumn.lastModifiedDate.getName(), lastModifiedDate);
        return this.mongoTemplate.findAndModify(Query.query(criteria), update, AppServer.class);
    }

    public Mono<AppServer> updateStatus(long id, String status) {
        Criteria criteria = Criteria.where(IdColumn._id.name()).is(id).and(AppServerColumn.status.getName()).ne(status);
        Query query = Query.query(criteria);
        Update update = Update.update(AppServerColumn.status.getName(), status);
        return this.mongoTemplate.findAndModify(query, update, AppServer.class);
    }

    public Mono<AppServer> insert(AppServer appServer) {
        return this.mongoTemplate.insert(appServer);
    }
}
