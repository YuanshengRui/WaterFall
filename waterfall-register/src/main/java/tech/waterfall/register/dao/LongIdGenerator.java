package tech.waterfall.register.dao;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class LongIdGenerator implements IIdGenerator<Long>, AppServerIdAware, DatabaseIdAware {
    private static final Logger log = LoggerFactory.getLogger(LongIdGenerator.class);
    private static final long MAX_ID_MASK = 4294967295L;
    private static final int MIN_SERVER_ID = 2048;
    private static final int MAX_SERVER_ID = 65536;
    private ConcurrentHashMap<MaxIdReteiver, AtomicLong> currentIds = new ConcurrentHashMap(6);
    private int appServerId;
    private int databaseId;

    public void setAppServerId(int appServerId) {
        Assert.state(appServerId > 0 && appServerId <= 65536, "appServerId isn't valid, the value is " + appServerId);
        this.appServerId = appServerId;
    }

    public void setDatabaseId(int databaseId) {
        Assert.state(databaseId > 0 && databaseId <= 64, "databaseId isn't valid, the value is " + databaseId);
        this.databaseId = databaseId;
    }

    public Mono<Long> getNextId(MaxIdReteiver maxIdReteiver) {
        Mono<AtomicLong> atomicLongMono = Mono.justOrEmpty(this.currentIds.get(maxIdReteiver)).switchIfEmpty(this.init(maxIdReteiver).map((currentId) -> {
            this.currentIds.putIfAbsent(maxIdReteiver, (AtomicLong)currentId);
            return this.currentIds.get(maxIdReteiver);
        }));
        return atomicLongMono.map((currentId) -> currentId.incrementAndGet());
    }

    public static long generateNodeIdMask(int appServerId, int databaseId) {
        int newAppServerId = appServerId < 2048 ? appServerId << 10 : appServerId << 5;
        long nodeIdMask = (long)(newAppServerId | databaseId);
        nodeIdMask <<= 32;
        return nodeIdMask;
    }

    private synchronized Mono<AtomicLong> init(MaxIdReteiver<Long> maxIdReteiver) {
        AtomicLong currentId = (AtomicLong)this.currentIds.get(maxIdReteiver);
        if (currentId != null) {
            return Mono.just(currentId);
        } else {
            long nodeIdMask = generateNodeIdMask(this.appServerId, this.databaseId);
            long maxId = 4294967295L | nodeIdMask;
            log.debug("current id Range:%s,%s", nodeIdMask, maxId);
            return maxIdReteiver.getMaxIdInRange(nodeIdMask, maxId).defaultIfEmpty(nodeIdMask).map((id) -> new AtomicLong(id));
        }
    }
}
