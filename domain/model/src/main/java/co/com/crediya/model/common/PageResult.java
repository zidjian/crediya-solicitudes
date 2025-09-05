package co.com.crediya.model.common;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements
) {
    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }

    public boolean isFirst() {
        return page == 0;
    }

    public boolean isLast() {
        return page >= getTotalPages() - 1;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
