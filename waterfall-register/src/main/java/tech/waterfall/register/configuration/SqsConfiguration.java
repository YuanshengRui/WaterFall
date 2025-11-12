package tech.waterfall.register.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsConfiguration {
    @Bean
    @ConditionalOnProperty(value = "app.test", havingValue = "false", matchIfMissing = true)
    public AwsCredentialsProvider awsCredentialsProvider() {
        return WebIdentityTokenFileCredentialsProvider.builder().build();
    }

    @Bean
    @ConditionalOnProperty(value = "app.test", havingValue = "false", matchIfMissing = true)
    public SqsMessageSender sqsMessageSender(SqsTemplate sqsTemplate) {
        return new SqsMessageSender(sqsTemplate);
    }

    @Bean
    public GenericTaskIngestion genericTaskIngestion(IGenericTaskService genericTaskService,
            SqsMessageSender sqsMessageSender,
            ITaskMetaDataManager taskMetaDataManager) {
        return new GenericTaskIngestion(genericTaskService, sqsMessageSender, taskMetaDataManager);
    }

    @Bean
    public FutureTaskIngestion futureTaskIngestion(IFutureTaskService futureTaskService,
            ITaskMetaDataManager taskMetaDataManager) {
        return new FutureTaskIngestion(futureTaskService, taskMetaDataManager);
    }

    @Bean
    public TaskIngestion taskIngestion(GenericTaskIngestion genericTaskIngestion,
            FutureTaskIngestion futureTaskIngestion) {
        return new TaskIngestion(genericTaskIngestion, futureTaskIngestion);
    }
}
