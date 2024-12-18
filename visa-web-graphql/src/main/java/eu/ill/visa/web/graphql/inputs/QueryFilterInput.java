package eu.ill.visa.web.graphql.inputs;

import eu.ill.visa.core.domain.Parameter;
import eu.ill.visa.core.domain.QueryFilter;
import org.eclipse.microprofile.graphql.Input;

import java.util.ArrayList;
import java.util.List;

@Input("QueryFilter")
public class QueryFilterInput {

    private String query;
    private List<ParameterInput> parameters = new ArrayList<>();

    public QueryFilterInput() {
    }

    public QueryFilterInput(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<ParameterInput> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterInput> parameters) {
        this.parameters = parameters;
    }

    public static QueryFilter toQueryFilter(final QueryFilterInput input) {
        if (input == null) {
            return null;
        }
        return new QueryFilter(input.query, input.parameters.stream().map(ParameterInput::toParameter).toList());
    }

    @Input("Parameter")
    public static class ParameterInput {

        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static Parameter toParameter(final ParameterInput input) {
            if (input == null) {
                return null;
            }
            return new Parameter(input.name, input.value);
        }
    }
}
