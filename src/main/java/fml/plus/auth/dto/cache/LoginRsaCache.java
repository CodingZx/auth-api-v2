package fml.plus.auth.dto.cache;

import lombok.Data;

@Data
public class LoginRsaCache {
    private String publicKey;
    private String privateKey;
}
