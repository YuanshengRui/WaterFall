package tech.waterfall.register.dao;

import com.mongodb.DuplicateKeyException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tech.waterfall.register.model.AppServer;
import tech.waterfall.register.utils.LocalHostUtils;

@Slf4j
public class AppServerIdAccessor {
    private static final long initialId = 2048L;
    private static final long mask = -32L;
    private AppServerDao appServerDao;
    private String appName;

    public AppServerIdAccessor(AppServerDao appServerDao, String appName) {
        this.appServerDao = appServerDao;
        this.appName = appName;
    }

    public Mono<Long> getAppServerId() throws UnknownHostException {
        String hostName = LocalHostUtils.getHostName();
        return this.tryUseMyPrevOne(hostName).switchIfEmpty(this.tryOccupyOneAppServer(hostName)).switchIfEmpty(this.createAppServer(hostName)).map(AppServer::getId);
    }

    private Instant getCurrentDateTime() {
        return Instant.now();
    }

    private Date getExpirationDateTime(Instant instant) {
        return Date.from(instant.minus(1L, ChronoUnit.DAYS));
    }

    private Mono<AppServer> tryUseMyPrevOne(String hostName) {
        Instant now = this.getCurrentDateTime();
        return this.appServerDao.findAndModifyLastModifiedDateAfter(this.appName, hostName, this.getExpirationDateTime(now), Date.from(now));
    }

    private Mono<AppServer> tryOccupyOneAppServer(String hostName) {
        Instant now = this.getCurrentDateTime();
        return this.appServerDao.findAndModifyLastModifiedDateBefore(this.appName, this.getExpirationDateTime(now), hostName, Date.from(now));
    }

    private Mono<AppServer> createAppServer(String hostName) {
        AppServer appServer = new AppServer();
        appServer.setHostName(hostName);
        appServer.setStatus(AppServer.Status.Used);
        appServer.setAppName(this.appName);
        appServer.setCreatedDate(new Date());
        appServer.setLastModifiedDate(appServer.getCreatedDate());
        Mono<AppServer> appServerMono = this.getMaxId().map((maxId) -> {
            appServer.setId(this.generateId(maxId + 1L));
            return appServer;
        });
        return appServerMono.flatMap(appServerDao::insert).doOnError((e) -> log.info("appServer id duplicated, try next")).retryWhen(Retry.indefinitely().filter((e) -> e instanceof DuplicateKeyException));
    }

    private Mono<Long> getMaxId() {
        return this.appServerDao.findMaxId().map(AppServer::getId).switchIfEmpty(Mono.just(2048L));
    }

    private long generateId(long serverId) {
        while((-32L & serverId) == serverId) {
            ++serverId;
        }

        return serverId;
    }
}
