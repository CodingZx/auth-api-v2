package fml.plus.auth.entity;

import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import fml.plus.auth.common.mybatis.mapper.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "a_monitor", remark = "监控信息")
public class MonitorEntity {

    @Column(value = "id", id = true)
    private UUID id;
    @Column(value = "server", remark = "服务器")
    private String server;
    @Column(value = "young_gc", remark = "年轻代GC次数")
    private Integer youngGC;
    @Column(value = "young_gc_time", remark = "年轻代GC时间")
    private Long youngGCTime;
    @Column(value = "old_gc", remark = "年老代GC次数")
    private Integer oldGC;
    @Column(value = "old_gc_time", remark = "年老代GC时间")
    private Long oldGCTime;
    @Column(value = "used_heap", remark = "已使用堆内存")
    private Long usedHeap;
    @Column(value = "thread_count", remark = "线程数")
    private Integer threadCount;
    @Column(value = "thread_daemon_count", remark = "守护线程数")
    private Integer threadDaemonCount;
    @Column(value = "cpu_load_average", remark = "CPU负载")
    private BigDecimal cpuLoadAverage;
    @Column(value = "request_counter", remark = "请求次数")
    private Long requestCounter;
    @Column(value = "create_time", remark = "创建时间")
    private LocalDateTime createTime;
}

