package fml.plus.auth.entity;

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
@Table(name = "a_ip_limit", remark = "IP限制记录")
public class IPLimitEntity {
    @Column(value = "id", id = true)
    private UUID id;
    @Column(value = "ip_addr", remark = "IP地址")
    private String ipAddr;
    @Column(value = "request_account", remark = "请求的账号密码信息")
    private String requestAccount;
    @Column(value = "create_time", remark = "创建时间")
    private LocalDateTime createTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IPLimitAccount {
        private String account;
        private String password;
    }

}
