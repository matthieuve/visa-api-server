package eu.ill.visa.web.graphql.resources;

import eu.ill.preql.exception.InvalidQueryException;
import eu.ill.visa.business.services.ExperimentService;
import eu.ill.visa.core.domain.OrderBy;
import eu.ill.visa.core.domain.QueryFilter;
import eu.ill.visa.core.entity.Role;
import eu.ill.visa.web.graphql.exceptions.DataFetchingException;
import eu.ill.visa.web.graphql.inputs.OrderByInput;
import eu.ill.visa.web.graphql.inputs.PaginationInput;
import eu.ill.visa.web.graphql.inputs.QueryFilterInput;
import eu.ill.visa.web.graphql.types.Connection;
import eu.ill.visa.web.graphql.types.PageInfo;
import eu.ill.visa.web.graphql.types.ExperimentType;
import io.smallrye.graphql.api.AdaptToScalar;
import io.smallrye.graphql.api.Scalar;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

import static eu.ill.visa.web.graphql.inputs.QueryFilterInput.toQueryFilter;
import static eu.ill.visa.web.graphql.inputs.PaginationInput.toPagination;
import static eu.ill.visa.web.graphql.inputs.OrderByInput.toOrderBy;
import static java.lang.String.format;
import static java.util.Objects.requireNonNullElseGet;

@GraphQLApi
@RolesAllowed(Role.ADMIN_ROLE)
public class ExperimentResource {

    private final ExperimentService experimentService;

    @Inject
    public ExperimentResource(final ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    /**
     * Get a list of experiments
     *
     * @param filter     the given query filter
     * @param orderBy    the ordering of results
     * @param pagination the pagination (limit and offset)
     * @return a list of experiments
     * @throws DataFetchingException thrown if there was an error fetching the results
     */
    @Query
    public @NotNull Connection<ExperimentType> experiments(final QueryFilterInput filter, final OrderByInput orderBy, @NotNull final PaginationInput pagination) throws DataFetchingException {
        try {
            if (!pagination.isLimitBetween(0, 50)) {
                throw new DataFetchingException(format("Limit must be between %d and %d", 0, 200));
            }
            final List<ExperimentType> results = experimentService.getAll(
                requireNonNullElseGet(toQueryFilter(filter), QueryFilter::new),
                requireNonNullElseGet(toOrderBy(orderBy), () -> new OrderBy("id", true)), toPagination(pagination)
            ).stream()
                .map(ExperimentType::new)
                .toList();
            final PageInfo pageInfo = new PageInfo(experimentService.countAll(toQueryFilter(filter)), pagination.getLimit(), pagination.getOffset());
            return new Connection<>(pageInfo, results);
        } catch (InvalidQueryException exception) {
            throw new DataFetchingException(exception.getMessage());
        }
    }

    /**
     * Count all experiments
     *
     * @param filter a filter to filter the results
     * @return a count of experiments
     * @throws DataFetchingException thrown if there was an error fetching the result
     */
    @Query
    public @NotNull @AdaptToScalar(Scalar.Int.class) Long countExperiments(final QueryFilterInput filter) throws DataFetchingException {
        try {
            return experimentService.countAll(requireNonNullElseGet(toQueryFilter(filter), QueryFilter::new));
        } catch (InvalidQueryException exception) {
            throw new DataFetchingException(exception.getMessage());
        }
    }

}
