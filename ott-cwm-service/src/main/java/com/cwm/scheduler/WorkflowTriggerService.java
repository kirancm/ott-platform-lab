package com.cwm.scheduler;

import com.cwm.service.WorkflowSchedulerService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowTriggerService {

    private final WorkflowSchedulerService workflowSchedulerService;

    public void trigger(UUID workflowId) {
        workflowSchedulerService.scheduleWorkflow(workflowId);
    }
}
