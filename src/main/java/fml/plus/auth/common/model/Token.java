package fml.plus.auth.common.model;

import com.google.common.base.Strings;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.common.util.SMUtils;
import fml.plus.auth.common.constants.GlobalConstants;
import fml.plus.auth.entity.AccountEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class Token {
    @Getter
    private final UUID id;
    @Getter
    private final UUID roleId;
    @Getter
    private final LocalDateTime createTime;

    private Token(AccountEntity admin) {
        this.id = admin.getId();
        this.roleId = admin.getRoleId();
        this.createTime = LocalDateTime.now();
    }

    public String str() {
        return SMUtils.ecrypt(GsonUtils.gson().toJson(this), GlobalConstants.Token.PUBLIC_KEY);
    }

    public static Token make(AccountEntity admin) {
        return new Token(admin);
    }

    public static Token from(String tokenStr) {
        if (Strings.isNullOrEmpty(tokenStr)) {
            return null;
        }
        try {
            String tokenJson = SMUtils.decrypt(tokenStr, GlobalConstants.Token.PRIVATE_KEY);
            if (Strings.isNullOrEmpty(tokenJson)) {
                return null;
            }
            return GsonUtils.gson().fromJson(tokenJson, Token.class);
        } catch (Exception e) {
            return null;
        }
    }
}
