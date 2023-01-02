package fml.plus.auth.service;

import cn.hutool.system.SystemUtil;
import com.fasterxml.uuid.Generators;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.redisson.lock.LockService;
import fml.plus.auth.common.transaction.IAfterCommitExecutor;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.dto.resp.MonitorResp;
import fml.plus.auth.entity.MonitorEntity;
import fml.plus.auth.mapper.MonitorMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(rollbackFor = Exception.class)
public class MonitorService {
    private LockService lockService;
    private ExceptionLogService exceptionLogService;
    private MonitorMapper monitorMapper;
    private StringRedisTemplate redis;
    private IAfterCommitExecutor afterCommitExecutor;

    public List<MonitorResp> monitor(String server, int minutes) {
        var query = monitorMapper.wrapper();
        query.eq(MonitorEntity::getServer, server);
        query.ge(MonitorEntity::getCreateTime, LocalDateTime.now().minusMinutes(minutes));
        query.orderByAsc(MonitorEntity::getCreateTime);
        var list = query.list();
        return list.stream().map(MonitorResp::new).collect(Collectors.toList());
    }

    public List<String> servers() {
        var ops = redis.boundValueOps(RedisConstants.MonitorKeys.getServersKey());
        var str = ops.get();
        if(Strings.isNullOrEmpty(str)) {
            var list = monitorMapper.findServers().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            ops.set(GsonUtils.gson().toJson(list), 10, TimeUnit.MINUTES);
            return list;
        }
        return GsonUtils.gson().fromJson(str, new TypeToken<List<String>>(){}.getType());
    }

    public void incrementRequestCount() {
        try {
            var time = LocalDateTime.now();
            var server = SystemUtil.getHostInfo().getAddress();


            redis.executePipelined(new SessionCallback<Void>() {
                public Void execute(RedisOperations operations) throws DataAccessException {
                    var ops = operations.boundValueOps(RedisConstants.MonitorKeys.getRequestCounterKey(server, time));
                    ops.increment();
                    ops.expire(10, TimeUnit.MINUTES);
                    return null;
                }
            });
        } catch (Exception e) {
            exceptionLogService.save(e);
        }

    }

    private static long youngGC;
    private static long youngGCTime;
    private static long oldGC;
    private static long oldGCTime;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void monitor() {
        try {
            String server = SystemUtil.getHostInfo().getAddress();
            var monitor = new MonitorEntity();
            monitor.setId(Generators.timeBasedGenerator().generate());
            monitor.setCreateTime(LocalDateTime.now());
            monitor.setServer(server);

            // GC信息
            List<GarbageCollectorMXBean> gcmList = ManagementFactory.getGarbageCollectorMXBeans();
            for(var gc : gcmList) {
                var gcCount = gc.getCollectionCount();
                var gcTime = gc.getCollectionTime();
                switch (gc.getName()) {
                    case "G1 Young Generation" -> {
                        monitor.setYoungGC((int)(gcCount - youngGC));
                        monitor.setYoungGCTime(gcTime - youngGCTime);
                        youngGC = gcCount;
                        youngGCTime = gcTime;
                    }
                    case "G1 Old Generation" -> {
                        monitor.setOldGC((int)(gcCount - oldGC));
                        monitor.setOldGCTime(gcTime - oldGCTime);
                        oldGC = gcCount;
                        oldGCTime = gcTime;
                    }
                }
            }

            //线程使用情况
            ThreadMXBean threads = ManagementFactory.getThreadMXBean();
            monitor.setThreadCount(threads.getThreadCount());
            monitor.setThreadDaemonCount(threads.getDaemonThreadCount());

            //堆内存使用情况
            MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            monitor.setUsedHeap(heapMemoryUsage.getUsed());

            //操作系统情况
            OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
            monitor.setCpuLoadAverage(BigDecimal.valueOf(os.getSystemLoadAverage()).setScale(2, RoundingMode.DOWN));

            var preTime = monitor.getCreateTime().minusMinutes(1);
            var ops = redis.boundValueOps(RedisConstants.MonitorKeys.getRequestCounterKey(server, preTime));
            monitor.setRequestCounter(NumberUtils.toLong(ops.get(), 0L));
            monitorMapper.insert(monitor);

            afterCommitExecutor.run(() -> redis.delete(RedisConstants.MonitorKeys.getRequestCounterKey(server, preTime)));
        } catch (Exception e) {
            exceptionLogService.save(e);
        }
    }

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 1)
    public void cleanMonitor() {
        lockService.lock(RedisConstants.LockKeys.monitorCleanKey(), 0, 30, () -> {
            try {
                var query = monitorMapper.wrapper();
                query.le(MonitorEntity::getCreateTime, LocalDateTime.now().minusHours(6));
                query.delete();
            } catch (Exception e) {
                exceptionLogService.save(e);
            }
        });
    }

}
