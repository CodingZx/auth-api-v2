package fml.plus.auth.dto.req.def;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class ValueReq {
    @NotEmpty(message = "参数不能为空")
    private String value;
}
