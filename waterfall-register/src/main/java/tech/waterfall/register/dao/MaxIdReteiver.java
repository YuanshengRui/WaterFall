package tech.waterfall.register.dao;

import reactor.core.publisher.Mono;

public interface MaxIdReteiver<ID> {
    Mono<ID> getMaxIdInRange(ID var1, ID var2);
}
