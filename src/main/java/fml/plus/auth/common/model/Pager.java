package fml.plus.auth.common.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public final class Pager<T> {
    private long count;
    private List<T> data;

    private Pager(List<T> data, long count){
        this.data = data;
        this.count = count;
    }

    public static <T> Pager<T> ofEmpty(long count) {
        return new Pager<>(new ArrayList<>(), count);
    }

    public static <T> Pager<T> of(List<T> data, long count) {
        return new Pager<>(data, count);
    }

}
