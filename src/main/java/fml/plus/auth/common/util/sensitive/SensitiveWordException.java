package fml.plus.auth.common.util.sensitive;

import lombok.Getter;

public class SensitiveWordException extends Exception {
    @Getter
    private final String word; // 触发的敏感词

    public SensitiveWordException(String word) {
        this.word = word;
    }
}
