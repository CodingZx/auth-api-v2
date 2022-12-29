package fml.plus.auth.common.model;

import org.apache.ibatis.session.RowBounds;

/**
 * 分页
 */
public final class Page {
    private final int start;
    private final int size;
    private static final int DEFAULT_SIZE = 20;

    private Page(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public static Page of(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        return new Page((page - 1) * size, size);
    }

    /**
     * 获得offset
     */
    public int offset() {
        return Math.max(start, 0);
    }

    /**
     * 获得limit
     */
    public int limit() {
        return size <= 0 ? DEFAULT_SIZE : size;
    }

    public RowBounds toRow() {
        return new RowBounds(offset(), limit());
    }
}
