package tech.waterfall.register.dao;

import reactor.core.publisher.Mono;

public interface IIdGenerator<ID> {
    Mono<ID> getNextId(MaxIdReteiver var1);
}
