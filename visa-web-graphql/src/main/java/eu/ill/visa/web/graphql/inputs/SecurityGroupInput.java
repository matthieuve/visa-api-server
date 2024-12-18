package eu.ill.visa.web.graphql.inputs;

import io.smallrye.graphql.api.AdaptToScalar;
import io.smallrye.graphql.api.Scalar;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.graphql.Input;

@Input("SecurityGroupInput")
public class SecurityGroupInput {

    @Size(min = 1, max = 250)
    private @NotNull String name;
    private @AdaptToScalar(Scalar.Int.class) Long cloudId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCloudId() {
        return cloudId;
    }

    public void setCloudId(Long cloudId) {
        this.cloudId = cloudId;
    }

}
