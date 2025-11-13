package tech.waterfall.register.dao;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public class ObjectIdGenerator implements IIdGenerator<String> {
    public Mono<String> getNextId(MaxIdReteiver maxIdReteiver) {
        return Mono.just(ObjectId.get().toString());
    }
}
