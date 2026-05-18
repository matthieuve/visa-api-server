package eu.ill.visa.core.entity;

import eu.ill.visa.core.entity.enumerations.InstanceState;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "instance_state_record")
public class InstanceStateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "instance_id", nullable = false)
    @JoinColumn(name = "instance_id")
    private Long instanceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_state", nullable = false)
    private InstanceState oldState;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_state", nullable = false)
    private InstanceState newState;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;

    public InstanceStateRecord() {
    }

    public InstanceStateRecord(Instance instance, InstanceState oldState, InstanceState newState) {
        this.instanceId = instance.getId();
        this.oldState = oldState;
        this.newState = newState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public InstanceState getOldState() {
        return oldState;
    }

    public void setOldState(InstanceState oldState) {
        this.oldState = oldState;
    }

    public InstanceState getNewState() {
        return newState;
    }

    public void setNewState(InstanceState newState) {
        this.newState = newState;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Entity mapping used to force foreign key constraints onto instance_id. This entity is not used elsewhere.
     */
    @Entity
    @Table(name = "instance_state_record")
    private static class InstanceStateRecordInner {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "instance_id", foreignKey = @ForeignKey(name = "fk_instance_id"), nullable = false)
        private Instance instance;
    }
}
