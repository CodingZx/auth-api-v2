package fml.plus.auth.entity;

import com.fasterxml.uuid.Generators;
import fml.plus.auth.common.mybatis.mapper.annotation.Column;
import fml.plus.auth.common.mybatis.mapper.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "a_exception_log_backup", remark = "上报信息")
public class ExceptionLogBackupEntity {
    @Column(value = "id", id = true)
    private UUID id; // ID
    @Column(value = "log_msg", remark = "日志信息")
    private String logMsg;
    @Column(value = "msg_detail", remark = "日志信息详情")
    private String msgDetail;
    @Column(value = "msg_md5", remark = "详情摘要 去重使用")
    private String msgMD5;
    @Column(value = "create_time", remark = " 创建时间")
    private LocalDateTime createTime;

    public ExceptionLogBackupEntity(String message, String detail) {
        this.id = Generators.timeBasedGenerator().generate();
        this.createTime = LocalDateTime.now();
        this.logMsg = message;
        this.msgDetail = detail;
        this.msgMD5 = ExceptionLogEntity.digestHex(message, detail);
    }

}
