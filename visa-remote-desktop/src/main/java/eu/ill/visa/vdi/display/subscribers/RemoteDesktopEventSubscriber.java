package eu.ill.visa.vdi.display.subscribers;

import eu.ill.visa.business.services.InstanceService;
import eu.ill.visa.core.entity.enumerations.InstanceActivityType;
import eu.ill.visa.core.entity.enumerations.InstanceMemberRole;
import eu.ill.visa.core.entity.partial.InstancePartial;
import eu.ill.visa.vdi.business.concurrency.ConnectionThread;
import eu.ill.visa.vdi.business.services.DesktopSessionService;
import eu.ill.visa.vdi.domain.models.DesktopSession;
import eu.ill.visa.vdi.domain.models.DesktopSessionMember;
import eu.ill.visa.vdi.domain.models.RemoteDesktopConnection;
import eu.ill.visa.vdi.domain.models.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public abstract class RemoteDesktopEventSubscriber<T> {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDesktopEventSubscriber.class);

    private final DesktopSessionService desktopSessionService;
    private final InstanceService instanceService;

    public RemoteDesktopEventSubscriber(final DesktopSessionService desktopSessionService,
                                        final InstanceService instanceService) {
        this.desktopSessionService = desktopSessionService;
        this.instanceService = instanceService;
    }

    public void onEvent(final SocketClient socketClient, final T data) {
        this.desktopSessionService.getDesktopSessionMember(socketClient.clientId()).ifPresent(desktopSessionMember -> {

            // Reset the idle handler
            desktopSessionMember.idleSessionHandler().reset();

            final DesktopSession desktopSession = desktopSessionMember.session();

            final RemoteDesktopConnection remoteDesktopConnection = desktopSessionMember.remoteDesktopConnection();

            // Get activity type (returns null for anything other than mouse or keyboard activity)
            InstanceActivityType controlActivityType = this.getControlActivityType(data);

            // Update the instance activity if mouse/keyboard event has been sent to the server
            if (controlActivityType != null) {
                remoteDesktopConnection.setInstanceActivity(controlActivityType);
            }

            InstanceMemberRole role = desktopSessionMember.connectedUser().getRole();
            if (controlActivityType == null || role.equals(InstanceMemberRole.OWNER) || role.equals(InstanceMemberRole.SUPPORT) || (role.equals(InstanceMemberRole.USER) && !desktopSession.isLocked())) {
                this.writeData(remoteDesktopConnection.getConnectionThread(), data);
            }

            // Two things to do:
            //  1: update instance last seen at (so that it isn't deleted): happens for any remote desktop event (ie desktop open)
            //  2: update instance last interaction at (to determine when sessions are active): happens only when the remote desktop has user keyboard or mouse interaction
            final Date lastInstanceUpdateTime = remoteDesktopConnection.getLastInstanceUpdateTime();
            final Date currentDate = new Date();
            if (lastInstanceUpdateTime == null || (currentDate.getTime() - lastInstanceUpdateTime.getTime() > 15 * 1000)) {
                remoteDesktopConnection.setLastInstanceUpdateTime(currentDate);

                // Use virtual thread to update interaction times so that the websocket can return ASAP
                Thread.startVirtualThread(() -> {
                    try {
                        this.updateInstanceActivity(desktopSessionMember, currentDate, remoteDesktopConnection.getLastInteractionAt());

                    } catch (Exception error) {
                        logger.error("Failed to update instance {} activity: {}", desktopSession.getInstanceId(), error.getMessage());
                    }
                });
            }
       });
    }

    private void updateInstanceActivity(final DesktopSessionMember desktopSessionMember, final Date lastSeenAt, final Date lastInteractionAt) {
        final InstancePartial instance = this.instanceService.getPartialById(desktopSessionMember.session().getInstanceId());
        if (instance == null) {
            return;
        }

        // Update instance
        instance.setLastSeenAt(lastSeenAt);
        instance.setLastInteractionAt(lastInteractionAt);
        instanceService.updatePartial(instance);

        this.desktopSessionService.updateSessionMemberActivity(desktopSessionMember, lastInteractionAt);
    }

    protected abstract InstanceActivityType getControlActivityType(T data);

    protected abstract void writeData(ConnectionThread connectionThread, T data);
}
