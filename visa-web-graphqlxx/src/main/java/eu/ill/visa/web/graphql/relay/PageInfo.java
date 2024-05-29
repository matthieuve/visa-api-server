package eu.ill.visa.web.graphql.relay;

import io.smallrye.graphql.api.AdaptToScalar;
import io.smallrye.graphql.api.Scalar;

public class PageInfo {

    private final Integer currentPage;
    private final Integer totalPages;
    @AdaptToScalar(Scalar.Int.class)
    private final Long count;
    private final Integer offset;
    private final Integer limit;

    public PageInfo(final Long count, final Integer limit, final Integer offset) {
        this.count = count;
        this.offset = offset;
        this.limit = limit;
        this.currentPage = Math.max(offset / limit, 0) + 1;
        this.totalPages = (int) Math.ceil((float) count / limit);
    }

    public Boolean getHasNextPage() {
        return currentPage < totalPages;
    }

    public Boolean getHasPrevPage() {
        return currentPage > 1;
    }

    public Integer getNextPage() {
        if (currentPage.equals(totalPages)) {
            return totalPages;
        }
        return currentPage + 1;
    }

    public Integer getPrevPage() {
        return currentPage - 1;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Long getCount() {
        return count;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }
}
