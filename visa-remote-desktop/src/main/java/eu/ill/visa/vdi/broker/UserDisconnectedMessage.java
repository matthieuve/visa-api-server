package eu.ill.visa.vdi.broker;

import eu.ill.visa.vdi.domain.models.ConnectedUser;

public record UserDisconnectedMessage(Long sessionId, String clientId, ConnectedUser user) {
}
