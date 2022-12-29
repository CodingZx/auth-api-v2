package fml.plus.auth.dto.resp;

import fml.plus.auth.entity.LogEntity;
import fml.plus.auth.common.util.time.LocalDateTimeUtils;
import lombok.Data;

import java.util.UUID;

@Data
public class LogDetailResp {
    private UUID id; // 日志ID
    private String title; // 日志标题
    private String type; // 操作类型
    private String uri; // 请求URI
    private String requestMethod; // 请求方式
    private String operName; // 操作人
    private String ip; // 操作IP
    private String createTime; // 操作时间
    private String method; // 请求java方法
    private String operParam; // 参数
    private String errorMsg; // 错误信息

    public LogDetailResp(LogEntity log) {
        this.id = log.getId();
        this.title = log.getTitle();
        this.type = log.getBusinessType().getName();
        this.requestMethod = log.getRequestMethod();
        this.operName = log.getOperName();
        this.ip = log.getOperIp();
        this.createTime = LocalDateTimeUtils.format(log.getCreateTime());
        this.method = log.getMethod();
        this.operParam = log.getOperParam();
        this.errorMsg = log.getErrorMsg();
        this.uri = log.getOperUri();
    }
}
