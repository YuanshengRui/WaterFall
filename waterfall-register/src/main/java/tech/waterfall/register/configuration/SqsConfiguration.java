package tech.waterfall.register.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import tech.waterfall.register.service.FutureTaskIngestion;
import tech.waterfall.register.service.GenericTaskIngestion;
import tech.waterfall.register.service.IFutureTaskService;
import tech.waterfall.register.service.IGenericTaskService;
import tech.waterfall.register.service.ITaskMetaDataManager;
import tech.waterfall.register.service.TaskIngestion;
import tech.waterfall.register.service.sqs.SqsMessageSender;
import tech.waterfall.register.sqs.SqsTemplate;

@Configuration
public class SqsConfiguration {
//    @Bean
//    @ConditionalOnProperty(value = "app.test", havingValue = "false", matchIfMissing = true)
//    public AwsCredentialsProvider awsCredentialsProvider() {
//        return WebIdentityTokenFileCredentialsProvider.builder().build();
//    }
@ConditionalOnMissingBean
@Bean
public SqsTemplate sqsTemplate() {
    return new SqsTemplate();
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
