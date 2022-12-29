package fml.plus.auth.dto.resp;

import fml.plus.auth.common.util.time.LocalDateTimeUtils;
import fml.plus.auth.entity.ExceptionLogEntity;
import lombok.Data;

import java.util.UUID;

@Data
public class ExceptionLogResp {
    private UUID id; // ID
    private String logMsg; // 日志信息
    private String msgDetail; // 日志信息详情
    private int status; // 处理状态 0 待处理/1已处理/-1忽略
    private Long counter; // 计数器 上报次数
    private String createTime; // 创建时间
    private String lastReportTime; // 最后上报时间

    public ExceptionLogResp(ExceptionLogEntity log, boolean list) {
        this.id = log.getId();
        this.logMsg = log.getLogMsg();
        this.msgDetail = list ? "" : log.getMsgDetail();
        this.status = log.getStatus().getValue();
        this.counter = log.getCounter();
        this.createTime = LocalDateTimeUtils.format(log.getCreateTime());
        this.lastReportTime = LocalDateTimeUtils.format(log.getLastReportTime());
    }

}
