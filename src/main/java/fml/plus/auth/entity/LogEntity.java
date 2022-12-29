package fml.plus.auth.entity;

import fml.plus.auth.common.aop.log.BusinessType;
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
@Table(name = "a_log", remark = "日志信息")
public class LogEntity {

    @Column(value = "id", id = true)
    private UUID id;
    @Column(value = "title", remark = "操作名称")
    private String title;
    @Column(value = "business_type", remark = "业务类型")
    private BusinessType businessType;
    @Column(value = "method", remark = "请求方法")
    private String method;
    @Column(value = "request_method", remark = "请求方式")
    private String requestMethod;
    @Column(value = "oper_uri", remark = "请求Uri")
    private String operUri;
    @Column(value = "oper_ip", remark = "操作IP")
    private String operIp;
    @Column(value = "oper_param", remark = "请求参数")
    private String operParam;
    @Column(value = "error_msg", remark = "错误消息")
    private String errorMsg;
    @Column(value = "oper_name", remark = "操作人")
    private String operName;
    @Column(value = "create_time", remark = "操作时间")
    private LocalDateTime createTime;

}
