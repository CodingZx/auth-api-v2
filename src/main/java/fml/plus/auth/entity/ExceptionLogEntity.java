package fml.plus.auth.entity;

import com.fasterxml.uuid.Generators;
import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import fml.plus.auth.common.mybatis.mapper.annotation.Table;
import fml.plus.auth.common.util.SMUtils;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "a_exception_log", remark = "上报信息")
public class ExceptionLogEntity {
    @Column(value = "id", id = true)
    private UUID id; // ID
    @Column(value = "log_msg", remark = "日志信息")
    private String logMsg;
    @Column(value = "msg_detail", remark = "日志信息详情")
    private String msgDetail;
    @Column(value = "msg_md5", remark = "详情摘要. 去重使用")
    private String msgMD5;
    @Column(value = "status", remark = "处理状态 待处理/已处理/忽略")
    private ExceptionLogStatus status;
    @Column(value = "counter", remark = "计数器 上报次数")
    private Long counter;
    @Column(value = "create_time", remark = "创建时间")
    private LocalDateTime createTime;
    @Column(value = "last_report_time", remark = "最后上报时间")
    private LocalDateTime lastReportTime;

    @Getter
    public enum ExceptionLogStatus {
        WAIT_PROCESS(0, "待处理"),
        ALREADY_PROCESS(1,"已处理"),
        IGNORE(-1, "已忽略"),
        ;

        private final String desc;
        private final int value;

        ExceptionLogStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static ExceptionLogStatus of(int value) {
            for(var e : values()) {
                if(e.value == value) return e;
            }
            return WAIT_PROCESS;
        }
    }


    public ExceptionLogEntity(ExceptionLogBackupEntity backup) {
        this.id = Generators.timeBasedGenerator().generate();
        this.createTime = backup.getCreateTime();
        this.logMsg = backup.getLogMsg();
        this.lastReportTime = backup.getCreateTime();
        this.counter = 1L;
        this.status = ExceptionLogStatus.WAIT_PROCESS;
        this.msgMD5 = backup.getMsgMD5();
        this.msgDetail = backup.getMsgDetail();
    }


    public ExceptionLogEntity(String message, String detail) {
        this.id = Generators.timeBasedGenerator().generate();
        this.createTime = LocalDateTime.now();
        this.logMsg = message;
        this.lastReportTime = this.createTime;
        this.counter = 1L;
        this.status = ExceptionLogStatus.WAIT_PROCESS;
        this.msgMD5 = digestHex(message, detail);
        this.msgDetail = detail;
    }

    public static String digestHex(String message, String detail) {
        // ios-1.0.0-def-exception-detail
        var str = message.trim() + "\n" + detail.trim();
        return SMUtils.digestHex(str);
    }
}
