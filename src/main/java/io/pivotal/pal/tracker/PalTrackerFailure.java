package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static org.springframework.boot.availability.AvailabilityChangeEvent.publish;
import static org.springframework.boot.availability.LivenessState.BROKEN;
import static org.springframework.boot.availability.LivenessState.CORRECT;

@Component
@Endpoint(id = "palTrackerFailure")
public class PalTrackerFailure {
    private final ApplicationEventPublisher eventPublisher;

    public PalTrackerFailure(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @WriteOperation
    public void set() {
        publish(this.eventPublisher, "Simulate recovered state", BROKEN);
    }

    @DeleteOperation
    public void unset() {
        publish(this.eventPublisher, "Simulate recovered state", CORRECT);
    }
}
