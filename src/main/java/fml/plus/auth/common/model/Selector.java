package fml.plus.auth.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
public class Selector {
    private String id;
    private String name;

    public Selector(UUID id, String name) {
        this.id = id.toString();
        this.name = name;
    }

    public Selector(String type, String name) {
        this.id = type;
        this.name = name;
    }
}
