package tech.waterfall.register.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import tech.waterfall.register.dao.AppServerDao;
import tech.waterfall.register.dao.AppServerIdAccessor;
import tech.waterfall.register.dao.AppServerIdPostProcessor;
import tech.waterfall.register.dao.AppServerRefresher;
import tech.waterfall.register.dao.LongIdGenerator;

@Configuration
@ConditionalOnProperty(
    value = {"app.id.long.use"},
    havingValue = "true",
    matchIfMissing = true
)
public class LongIdConfiguration {
    @Bean
    public AppServerDao appServerDao(ReactiveMongoTemplate mongoTemplate) {
        return new AppServerDao(mongoTemplate);
    }

    @Bean
    public AppServerIdAccessor appServerIdProcessor(AppServerDao appServerDao, @Value("${spring.application.name}") String appName) {
        return new AppServerIdAccessor(appServerDao, appName);
    }

    @Bean
    public LongIdGenerator longIdGenerator(@Value("${database.id:1}") int databaseId) {
        LongIdGenerator longIdGenerator = new LongIdGenerator();
        longIdGenerator.setDatabaseId(databaseId);
        return longIdGenerator;
    }

    @Bean
    public AppServerIdPostProcessor appServerIdPostProcessor(AppServerIdAccessor appServerIdAccessor, @Value("${appserver.id:0}") int appServerId) {
        return new AppServerIdPostProcessor(appServerIdAccessor, appServerId);
    }

    @Bean
    public AppServerRefresher appServerRefresher(@Value("${spring.application.name}") String appName, AppServerDao appServerDao) {
        return new AppServerRefresher(appName, appServerDao);
    }
}
