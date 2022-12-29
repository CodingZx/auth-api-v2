package fml.plus.auth.dto.req.def;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class IdReq {
    @NotNull(message = "id不能为空")
    private UUID id;
}
