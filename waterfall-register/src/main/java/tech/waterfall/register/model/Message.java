package tech.waterfall.register.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    String id;
    String taskId;
    String taskName;

    public Message() {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\": ").append('"').append(id).append('"').append(",")
                .append("\"taskId\": ").append('"').append(taskId).append('"').append(",")
                .append("\"taskName\": ").append('"').append(taskName).append('"').append("}");
        return sb.toString();
    }
}
