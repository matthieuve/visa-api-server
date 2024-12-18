package eu.ill.visa.web.graphql.types;

import eu.ill.visa.core.entity.InstanceAttribute;
import io.smallrye.graphql.api.AdaptToScalar;
import io.smallrye.graphql.api.Scalar;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.graphql.Type;

@Type("InstanceAttribute")
public class InstanceAttributeType {

    @AdaptToScalar(Scalar.Int.class)
    private final @NotNull Long id;
    private final @NotNull String name;
    private final @NotNull String value;

    public InstanceAttributeType(final InstanceAttribute attribute) {
        this.id = attribute.getId();
        this.name = attribute.getName();
        this.value = attribute.getValue();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
