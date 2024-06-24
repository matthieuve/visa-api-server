package eu.ill.visa.vdi.gateway.listeners;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import eu.ill.visa.vdi.business.services.DesktopAccessService;
import eu.ill.visa.vdi.gateway.events.AccessRequestReply;

public class ClientAccessReplyListener implements DataListener<AccessRequestReply> {

    private final DesktopAccessService desktopAccessService;

    public ClientAccessReplyListener(final DesktopAccessService desktopAccessService) {
        this.desktopAccessService = desktopAccessService;
    }

    @Override
    public void onData(final SocketIOClient client, final AccessRequestReply data, final AckRequest ackRequest) {
        this.desktopAccessService.respondToAccessRequest(data.instanceId(), data.requesterConnectionId(), data.getRole());
    }
}
