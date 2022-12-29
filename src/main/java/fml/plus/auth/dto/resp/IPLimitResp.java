package fml.plus.auth.dto.resp;

import com.fasterxml.uuid.Generators;
import com.google.common.reflect.TypeToken;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.common.util.time.LocalDateTimeUtils;
import fml.plus.auth.entity.IPLimitEntity;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class IPLimitResp {
    private UUID id;
    private String refIP;
    private String createTime;
    private List<AccountInfoResp> accounts;

    public IPLimitResp(IPLimitEntity model) {
        this.id = model.getId();
        this.refIP = model.getIpAddr();
        this.createTime = LocalDateTimeUtils.format(model.getCreateTime());

        List<IPLimitEntity.IPLimitAccount> accounts = GsonUtils.gson().fromJson(model.getRequestAccount(), new TypeToken<List<IPLimitEntity.IPLimitAccount>>(){}.getType());
        this.accounts = accounts.stream().map(r -> {
            var resp = new AccountInfoResp();
            resp.setKey(Generators.timeBasedGenerator().generate().toString());
            resp.setAccount(r.getAccount());
            resp.setPassword(r.getPassword());
            return resp;
        }).toList();
    }

    @Data
    public static class AccountInfoResp {
        private String account;
        private String password;
        private String key;
    }
}
