package fml.plus.auth.dto.resp;

import fml.plus.auth.entity.MonitorEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Data
public class MonitorResp {
    private int youngGC; // 年轻代GC次数
    private long youngGCTime; // 毫秒
    private int oldGC; // 年老代GC次数
    private long oldGCTime; // 毫秒

    private String usedHeap; // 当前堆大小 MB
    private int threadCount; // 线程数
    private int threadDaemonCount; // 守护线程数
    private BigDecimal cpuLoadAverage; // CPU负载
    private long reqCounter;
    private String createTime;

    public MonitorResp(MonitorEntity monitor) {
        this.createTime = monitor.getCreateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.youngGC = monitor.getYoungGC();
        this.youngGCTime = monitor.getYoungGCTime();
        this.oldGC = monitor.getOldGC();
        this.oldGCTime = monitor.getOldGCTime();
        this.usedHeap = toMB(monitor.getUsedHeap());
        this.threadCount = monitor.getThreadCount();
        this.threadDaemonCount = monitor.getThreadDaemonCount();
        this.cpuLoadAverage = monitor.getCpuLoadAverage();
        this.reqCounter = monitor.getRequestCounter();
    }

    private static String toMB(long bytes) {
        BigDecimal size = BigDecimal.valueOf(bytes);
        size = size.divide(BigDecimal.valueOf(1024.0), 2, RoundingMode.DOWN); // KB
        size = size.divide(BigDecimal.valueOf(1024.0), 2, RoundingMode.DOWN); // MB
        return new DecimalFormat("#.##").format(size);
    }

}
