package org.ppietrzak.dataprocessor.infrastructure.graphql;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Pagination<T> {

    private long count;
    private long totalCount;
    private int page;
    private int totalPages;
    private List<T> content = new ArrayList<>();

    protected Pagination() {
        // empty;
    }

    protected Pagination(Pagination<T> pagination) {
        this.count = pagination.count;
        this.totalCount = pagination.totalCount;
        this.page = pagination.page;
        this.totalPages = pagination.totalPages;
        this.content = pagination.content;
    }

    private Pagination(long count, long totalCount, int page, int totalPages, List<T> content) {
        this.count = count;
        this.totalCount = totalCount;
        this.page = page;
        this.totalPages = totalPages;
        this.content.addAll(content);
    }

    @SuppressWarnings("unchecked") // safe here, we know that types will match
    public static <V> Pagination<V> from(Page<V> page) {
        return new Pagination<>(
                page.getSize(),
                page.getTotalElements(),
                page.getNumber(),
                page.getTotalPages(),
                page.getContent()
        );
    }
}