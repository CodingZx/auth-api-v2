package fml.plus.auth.dto.resp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.OshiUtil;
import lombok.Data;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

@Data
public class SysInfoResp {


    private String osName; // 操作系统名称
    private String osVersion; // 操作系统版本
    private String osArch; // 操作系统内核

    private String cpuCores; // 处理器数量
    private String systemTotalMemory; // 机器总内存

    private String systemAvailableMemory; // 机器可用内存

    private String maxHeapMemory; // 最大堆内存
    private String initHeapMemory; // 初始化堆内存
    private String committedHeapMemory; // 可使用堆内存


    public SysInfoResp() {

        var os = SystemUtil.getOperatingSystemMXBean();
        this.osName = os.getName();
        this.osVersion = os.getVersion();
        this.osArch = os.getArch();
        this.cpuCores = Integer.toString(os.getAvailableProcessors());

        var memory = OshiUtil.getMemory();
        this.systemTotalMemory = FileUtil.readableFileSize(memory.getTotal());
        this.systemAvailableMemory = FileUtil.readableFileSize(memory.getAvailable());

        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        this.maxHeapMemory = FileUtil.readableFileSize(heapMemoryUsage.getMax());
        this.initHeapMemory = FileUtil.readableFileSize(heapMemoryUsage.getInit());
        this.committedHeapMemory = FileUtil.readableFileSize(heapMemoryUsage.getCommitted());
    }

}
