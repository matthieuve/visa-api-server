package eu.ill.visa.web.bundles.graphql.queries.resolvers.fields;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.ill.visa.cloud.services.CloudClient;
import eu.ill.visa.core.domain.ImageProtocol;
import graphql.kickstart.tools.GraphQLResolver;

import static eu.ill.visa.business.services.PortService.isPortOpen;

@Singleton
public class ImageProtocolResolver implements GraphQLResolver<ImageProtocol> {

    private final CloudClient client;

    @Inject
    public ImageProtocolResolver(CloudClient client) {
        this.client = client;
    }

    boolean isUp(ImageProtocol protocol) {
        final Integer port = protocol.getPort();
        return isPortOpen("localhost", port);
    }
}
