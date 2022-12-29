package fml.plus.auth.common.util.sensitive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SensitiveWord {
    private String word;
    private boolean hint;
    private boolean mark;
    private String replaceWord;
}
