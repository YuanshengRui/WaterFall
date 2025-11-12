package tech.waterfall.register.service;

import reactor.core.publisher.Mono;

public interface ITaskMetaDataService {
    Mono<String> getQueueName(String taskName);
}
