package eu.ill.visa.web.graphql.types;

import eu.ill.visa.core.entity.Plan;
import io.smallrye.graphql.api.AdaptToScalar;
import io.smallrye.graphql.api.Scalar;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.graphql.Type;

@Type("Plan")
public class PlanType {

    @AdaptToScalar(Scalar.Int.class)
    private final @NotNull Long id;
    private final @NotNull ImageType image;
    private final @NotNull FlavourType flavour;
    private final @NotNull Boolean preset;

    public PlanType(final Plan plan) {
        this.id = plan.getId();
        this.image = new ImageType(plan.getImage());
        this.flavour = new FlavourType(plan.getFlavour());
        this.preset = plan.getPreset();
    }

    public Long getId() {
        return id;
    }

    public ImageType getImage() {
        return image;
    }

    public FlavourType getFlavour() {
        return flavour;
    }

    public Boolean getPreset() {
        return preset;
    }
}
