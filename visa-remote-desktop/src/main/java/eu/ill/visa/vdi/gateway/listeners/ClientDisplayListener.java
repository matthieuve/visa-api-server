package eu.ill.visa.vdi.gateway.listeners;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import eu.ill.visa.business.services.InstanceActivityService;
import eu.ill.visa.business.services.InstanceService;
import eu.ill.visa.business.services.InstanceSessionService;
import eu.ill.visa.core.entity.Instance;
import eu.ill.visa.core.entity.InstanceSessionMember;
import eu.ill.visa.core.entity.enumerations.InstanceActivityType;
import eu.ill.visa.core.entity.enumerations.InstanceMemberRole;
import eu.ill.visa.vdi.business.concurrency.ConnectionThread;
import eu.ill.visa.vdi.business.services.DesktopSessionService;
import eu.ill.visa.vdi.domain.models.DesktopSession;
import eu.ill.visa.vdi.domain.models.RemoteDesktopConnection;
import eu.ill.visa.vdi.domain.models.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static java.lang.String.format;

public abstract class ClientDisplayListener<T> implements DataListener<T> {

    private static final Logger logger = LoggerFactory.getLogger(ClientDisplayListener.class);

    private final DesktopSessionService desktopSessionService;
    private final InstanceService instanceService;
    private final InstanceSessionService instanceSessionService;
    private final InstanceActivityService instanceActivityService;

    public ClientDisplayListener(final DesktopSessionService desktopSessionService,
                                 final InstanceService instanceService,
                                 final InstanceSessionService instanceSessionService,
                                 final InstanceActivityService instanceActivityService) {
        this.desktopSessionService = desktopSessionService;
        this.instanceService = instanceService;
        this.instanceSessionService = instanceSessionService;
        this.instanceActivityService = instanceActivityService;
    }

    @Override
    public void onData(final SocketIOClient client, final T data, final AckRequest ackRequest) {
        final SocketClient socketClient = new SocketClient(client, client.getSessionId().toString());
        this.desktopSessionService.findDesktopSessionMember(socketClient).ifPresent(desktopSessionMember -> {

            final DesktopSession desktopSession = desktopSessionMember.getSession();

            final Instance instance = this.instanceService.getById(desktopSession.getInstanceId());
            if (instance == null) {
                return;
            }

            final RemoteDesktopConnection remoteDesktopConnection = desktopSessionMember.getDesktopConnection();
            InstanceActivityType controlActivityType = this.getControlActivityType(data);
            if (controlActivityType != null) {
                remoteDesktopConnection.setInstanceActivity(controlActivityType);
            }

            InstanceMemberRole role = desktopSessionMember.getConnectedUser().getRole();
            if (controlActivityType == null || role.equals(InstanceMemberRole.OWNER) || role.equals(InstanceMemberRole.SUPPORT) || (role.equals(InstanceMemberRole.USER) && !desktopSession.isLocked())) {
                this.writeData(remoteDesktopConnection.getConnectionThread(), data);
            }

            // Update last seen time of instance if more than one minute
            final Date lastSeenDate = remoteDesktopConnection.getLastSeenAt();
            final Date currentDate = new Date();
            if (lastSeenDate == null || (currentDate.getTime() - lastSeenDate.getTime() > 5 * 1000)) {
                instance.updateLastSeenAt();

                if (lastSeenDate == null || lastSeenDate.getTime() <= remoteDesktopConnection.getLastInteractionAt().getTime()) {
                    instance.setLastInteractionAt(remoteDesktopConnection.getLastInteractionAt());
                }

                instanceService.save(instance);

                final InstanceSessionMember instanceSessionMember = this.instanceSessionService.getSessionMemberBySessionId(desktopSessionMember.getId());
                if (instanceSessionMember == null) {
                    logger.warn(format("Instance session member not found for instance %d", instance.getId()));
                } else {
                    instanceSessionMember.updateLastSeenAt();
                    instanceSessionMember.setLastInteractionAt(remoteDesktopConnection.getLastInteractionAt());
                    instanceSessionService.saveInstanceSessionMember(instanceSessionMember);

                    InstanceActivityType instanceActivityType = remoteDesktopConnection.getInstanceActivity();
                    if (instanceActivityType != null) {
                        this.instanceActivityService.create(instanceSessionMember.getUser(), instance, instanceActivityType);
                        remoteDesktopConnection.resetInstanceActivity();
                    }
                }
                remoteDesktopConnection.setLastSeenAt(instance.getLastSeenAt());
            }
        });
    }

    protected abstract InstanceActivityType getControlActivityType(T data);

    protected abstract void writeData(ConnectionThread connectionThread, T data);
}
