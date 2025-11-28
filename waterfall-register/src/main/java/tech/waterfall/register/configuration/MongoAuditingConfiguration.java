package tech.waterfall.register.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * MongoDB auditing configuration to automatically populate createdDate and lastModifiedDate fields
 */
@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "tech.waterfall.register.dao")
@Slf4j
public class MongoAuditingConfiguration {

    public MongoAuditingConfiguration() {
        log.info("MongoDB Auditing enabled for automatic timestamp management");
    }
}

