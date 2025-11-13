package tech.waterfall.register.dao;

import java.net.UnknownHostException;
import java.sql.Date;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;
import tech.waterfall.register.utils.LocalHostUtils;

@Slf4j
public class AppServerRefresher {
    private static final long REFRESH_RATE = 300000L;
    private AppServerDao appServerDao;
    private String appName;

    public AppServerRefresher(String appName, AppServerDao appServerDao) {
        this.appName = appName;
        this.appServerDao = appServerDao;
    }

    @Scheduled(
        fixedRate = 300000L
    )
    public void refresh() {
        try {
            String hostName = LocalHostUtils.getHostName();
            this.appServerDao.findAndModifyLastModifiedDate(this.appName, hostName, Date.from(Instant.now()));
        } catch (UnknownHostException uhe) {
            log.warn("Can not retrieve host name!", uhe);
        }

    }
}
