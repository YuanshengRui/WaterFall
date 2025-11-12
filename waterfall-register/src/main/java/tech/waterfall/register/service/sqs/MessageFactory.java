package tech.waterfall.register.service.sqs;


import tech.waterfall.register.model.GenericTask;
import tech.waterfall.register.model.Message;

public class MessageFactory {
    public Message createMessage(GenericTask task) {

        return new Message(task.getId(), task.getTaskId(), task.getTaskName());
    }
}
