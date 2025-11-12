package tech.waterfall.register.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronExpression;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.dao.FutureTaskDao;
import tech.waterfall.register.exception.InvalidTaskException;
import tech.waterfall.register.model.FutureTask;
import tech.waterfall.register.support.column.FutureTaskColumn;

@AllArgsConstructor
@Slf4j
public class FutureTaskService implements IFutureTaskService {
    private FutureTaskDao futureTaskDao;

    @Override
    public Mono<String> insert(FutureTask futureTask) {
        return Mono.justOrEmpty(futureTask)
                .doOnNext(this::sanitize)
                .doOnNext(this::calcFireTime)
                .doOnNext(this::trySetTimeInfo)
                .flatMap(futureTaskDao::insertWithNextId);
    }

    public Mono<String> save(FutureTask futureTask) {
        return Mono.justOrEmpty(futureTask)
                .doOnNext(this::sanitize)
                .doOnNext(this::calcFireTime)
                .doOnNext(ft -> {
                    if (futureTask.getLastModifiedTime() == null) {
                        futureTask.setLastModifiedTime(Instant.now());
                    }
                })
                .flatMap(futureTaskDao::save);
    }

    @Override
    public Mono<Void> cancel(String taskName, String taskId) {
        return futureTaskDao.deleteByTask(taskName, taskId);
    }

    @Override
    public Mono<Long> cancel(String taskName, List<String> taskIds) {
        return futureTaskDao.deleteTasks(taskName, taskIds);
    }

    @Override
    public Mono<Void> update(String taskName, String taskId, Map taskUpdater) {
        //if there is any field in 'Task(DTO)' is different from which in 'FutureTask(DAO model)'
        //we need to translate it here.
        //For now, they are all same.
        return Mono.justOrEmpty(taskUpdater)
                .map(updater -> sanitize(taskName, taskId, updater))
                .flatMap(updater -> futureTaskDao.update(taskName, taskId, updater)
                        .flatMap(setFireTime(updater)));
    }

    @Override
    public Mono<Boolean> hasTask(String taskName, String taskId) {
        return futureTaskDao.findIdByTask(taskName, taskId)
                .map(StringUtils::isNotEmpty)
                .switchIfEmpty(Mono.just(false));
    }

    @Override
    public Flux<FutureTask> getAll() {
        return futureTaskDao.find(null);
    }

    @Override
    public Mono<FutureTask> findByTask(String taskName, String taskId) {
        return futureTaskDao.findByTask(taskName, taskId);
    }

    @Override
    public Mono<String> findIdByTask(String taskName, String taskId) {
        return futureTaskDao.findIdByTask(taskName, taskId);
    }

    //have to fetch startTime to calc fire time when updating;
    private Function<FutureTask, Mono<Void>> setFireTime(Map taskUpdater) {
        return futureTask -> {
            if (taskUpdater.get(FutureTaskColumn.fireTime.name()) != null) {
                return Mono.empty();
            }
            if (!taskUpdater.containsKey(FutureTaskColumn.cronExpression.name())
                    && !taskUpdater.containsKey(FutureTaskColumn.fixRateSecs.name())) {
                return Mono.empty();
            }
            recalcFireTime(futureTask);
            return futureTaskDao.updateFireTime(futureTask.getId(), futureTask.getFireTime());
        };
    }

    @Override
    public Flux<FutureTask> bulkInsert(List<FutureTask> tasks) {
        return futureTaskDao.bulkInsert(tasks);
    }

    @Override
    public void calcFireTime(FutureTask futureTask) {
        if (futureTask.getFireTime() != null) {
            return;
        }
        if (StringUtils.isNotEmpty(futureTask.getCronExpression())) {
            futureTask.setFireTime(calcFireTimeFromCron(futureTask.getCronExpression(),
                    futureTask.getStartTime(), futureTask.getTimeZoneId()));
            return;
        }
        if (futureTask.getFixRateSecs() > 0) {
            futureTask.setFireTime(calcFireTimeFromFixRate(futureTask.getFixRateSecs(),
                    futureTask.getStartTime()));
            return;
        }
        throw new InvalidTaskException(String.join("",
                "Invalid future task(taskName: ", futureTask.getTaskName(),
                " taskId: {}), ", futureTask.getTaskId(),
                "fireTime, cronExpression, fixRateSecs can not be empty simultaneously"));
    }

    @Override
    public void trySetTimeInfo(FutureTask futureTask) {
        Instant now = Instant.now();
        if (futureTask.getCreatedTime() == null) {
            futureTask.setCreatedTime(now);
        }
        if (futureTask.getLastModifiedTime() == null) {
            futureTask.setLastModifiedTime(now);
        }
    }

    private void recalcFireTime(FutureTask futureTask) {
        futureTask.setFireTime(null);
        calcFireTime(futureTask);
    }

    private Instant calcFireTimeFromCron(String expression, Instant seed, String timeZoneId) {
        Instant startTime = Instant.now();
        if (seed != null) {
            //minus 1 millis for the case where startTime equals
            //fireTime that should be calculated from cron expression
            startTime = seed.minus(1, ChronoUnit.MILLIS);
        }
        CronExpression cronExpression = CronExpression.parse(expression);
        ZonedDateTime currentTime = ZonedDateTime.ofInstant(startTime, zoneId(timeZoneId));
        return cronExpression.next(currentTime).toInstant();
    }

    private Instant calcFireTimeFromFixRate(long fixRateSeconds, Instant startTime) {
        if (startTime != null) {
            return startTime.plus(fixRateSeconds, ChronoUnit.SECONDS);
        }
        return Instant.now().plus(fixRateSeconds, ChronoUnit.SECONDS);
    }

    private ZoneId zoneId(String timeZoneId) {
        try {
            if (StringUtils.isNotEmpty(timeZoneId)) {
                return ZoneId.of(timeZoneId);
            }
            return ZoneId.systemDefault();
        } catch (Exception e) {
            log.warn("Invalid timeZoneId: {}, system default zoneId will be used", timeZoneId);
            return ZoneId.systemDefault();
        }
    }

    private void sanitize(FutureTask futureTask) {
        if (StringUtils.isNotEmpty(futureTask.getCronExpression())) {
            if (futureTask.getFireTime() != null) {
                log.warn("A conflict occurs in task(taskName: {}, taskId: {}), "
                                + "since cronExpression has been set, the fireTime would be ignored",
                        futureTask.getTaskName(), futureTask.getTaskId());
                futureTask.setFireTime(null);
            }
            if (futureTask.getFixRateSecs() != 0) {
                log.warn("A conflict occurs in task(taskName: {}, taskId: {}), "
                                + "since cronExpression has been set, the fixRateSecs would be ignored",
                        futureTask.getTaskName(), futureTask.getTaskId());
                futureTask.setFixRateSecs(0);
            }
        } else if (futureTask.getFixRateSecs() != 0) {
            if (futureTask.getFireTime() != null) {
                log.warn("A conflict occurs in task(taskName: {}, taskId: {}), "
                                + "since fixRateSecs has been set, the fireTime would be ignored",
                        futureTask.getTaskName(), futureTask.getTaskId());
                futureTask.setFireTime(null);
            }
        } else if (futureTask.getFireTime() == null) {
            throw new InvalidTaskException(String.join("",
                    "Invalid future task(taskName: ", futureTask.getTaskName(),
                    "taskId: {}), ", futureTask.getTaskId(),
                    "fireTime, cronExpression, fixRateSecs can not be empty simultaneously"));
        }
    }

    private Map sanitize(String taskName, String taskId, Map<String, Object> updates) {
        Map map = new HashMap(updates);
        if (StringUtils.isNotEmpty((String) updates.get(FutureTaskColumn.cronExpression.name()))) {
            log.info("Will update task(taskName: {}, taskId: {}), "
                    + "since cronExpression has been set, the fireTime and fixRateSecs would be cleared",
                    taskName, taskId);

            map.put(FutureTaskColumn.fireTime.name(), null);
            map.put(FutureTaskColumn.fixRateSecs.name(), 0);
            return map;
        }
        Number fixRateSecs = (Number) updates.get(FutureTaskColumn.fixRateSecs.name());
        if (fixRateSecs != null && fixRateSecs.longValue() != 0) {
            log.info("Will update task(taskName: {}, taskId: {}), "
                    + "since fixRateSecs has been set, the fireTime and cronExpression would be cleared",
                    taskName, taskId);

            map.put(FutureTaskColumn.fireTime.name(), null);
            map.put(FutureTaskColumn.cronExpression.name(), "");
            return map;
        }
        if (updates.get(FutureTaskColumn.fireTime.name()) != null) {
            log.info("Will update task(taskName: {}, taskId: {}), "
                    + "since fireTime has been set, the fixRateSecs and cronExpression would be cleared",
                    taskName, taskId);

            map.put(FutureTaskColumn.cronExpression.name(), "");
            map.put(FutureTaskColumn.fixRateSecs.name(), 0);
        }
        return map;
    }
}
