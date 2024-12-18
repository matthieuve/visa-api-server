package eu.ill.visa.business.services;

import eu.ill.visa.business.InstanceConfiguration;
import eu.ill.visa.core.entity.Instance;
import eu.ill.visa.core.entity.InstanceActivity;
import eu.ill.visa.core.entity.User;
import eu.ill.visa.core.entity.enumerations.InstanceActivityType;
import eu.ill.visa.persistence.repositories.InstanceActivityRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Transactional
@Singleton
public class InstanceActivityService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceActivityService.class);

    private final InstanceActivityRepository repository;
    private final InstanceConfiguration configuration;

    @Inject
    public InstanceActivityService(final InstanceActivityRepository repository,
                                   final InstanceConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    public List<InstanceActivity> getAll() {
        return this.repository.getAll();
    }

    public List<InstanceActivity> getAllForUser(User user) {
        return this.repository.getAllForUser(user);
    }

    public List<InstanceActivity> getAllForInstance(Instance instance) {
        return this.repository.getAllForInstance(instance);
    }

    public boolean cleanupActive() {
        return this.configuration.activityRetentionPeriodDays() != 0;
    }

    public void cleanup() {
        if (this.cleanupActive()) {
            this.repository.cleanup(this.configuration.activityRetentionPeriodDays());
        }
    }

    public InstanceActivity create(User user, Instance instance, InstanceActivityType action) {
        InstanceActivity instanceActivity = new InstanceActivity(user, instance, action);
        this.repository.save(instanceActivity);

        return instanceActivity;
    }

}
