package tech.waterfall.register.service.sqs;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MimeTypeUtils;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import tech.waterfall.register.model.GenericTask;

@Slf4j
public class SqsMessageSender {
    private final SqsTemplate sqsTemplate;
    private final MessageFactory messageFactory;

    public SqsMessageSender(SqsTemplate queueMessagingTemplate) {
        this.sqsTemplate = queueMessagingTemplate;
        this.messageFactory = new MessageFactory();
    }

    public List<String> sendBatch(String queueName, List<GenericTask> tasks) {
        return sqsTemplate.sendManyAsync(queueName, tasks.stream()
                .map(messageFactory::createMessage)
                .collect(Collectors.toList()));
    }

    public String send(String queueName, GenericTask task) {
        log.debug("sending a task(taskName:{}, taskId:{}) to queue '{}'",
                task.getTaskName(), task.getTaskId(), queueName);
        sqsTemplate.send(sqsSendOptions -> {
            sqsSendOptions.queue(queueName)
                    .payload(messageFactory.createMessage(task))
                    .header(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
            if (StringUtils.isNotEmpty(task.getGroupId())) {
                sqsSendOptions.messageGroupId(task.getGroupId());
                if (StringUtils.isNotEmpty(task.getTaskId())) {
                    sqsSendOptions.messageDeduplicationId(String.join(":", task.getTaskName(), task.getTaskId()));
                }
            }
        });

        log.debug("sent a task(taskName:{}, taskId:{}) to queue '{}'",
                task.getTaskName(), task.getTaskId(), queueName);
        return task.getId();
    }
}
