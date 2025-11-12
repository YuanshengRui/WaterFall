package tech.waterfall.register.service;

import reactor.core.publisher.Mono;

public interface ITaskMetaDataManager {
    Mono<String> queueName(String taskName);
}
