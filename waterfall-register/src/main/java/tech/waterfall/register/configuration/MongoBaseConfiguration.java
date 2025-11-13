package tech.waterfall.register.configuration;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import tech.waterfall.register.dao.ObjectIdGenerator;
import tech.waterfall.register.utils.BsonUndefinedToNullObjectConverter;

@Import({LongIdConfiguration.class})
@Configuration
public class MongoBaseConfiguration {
    @Bean
    ObjectIdGenerator objectIdGenerator() {
        return new ObjectIdGenerator();
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(new BsonUndefinedToNullObjectConverter()));
    }
}
