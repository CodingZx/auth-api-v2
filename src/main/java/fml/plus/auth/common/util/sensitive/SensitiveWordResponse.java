package fml.plus.auth.common.util.sensitive;

import lombok.Data;

import java.util.List;

@Data
public class SensitiveWordResponse {
    private String resultText; // 替换后的文本内容
    private List<String> matchWords; // 触发的敏感字
    private boolean mark; // 是否标记
}
