package com.opcon.bff.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkflowMapperTest {

    private final WorkflowMapper workflowMapper = new WorkflowMapper();

    @Test
    void shouldMapKnownStatusesToUiLabels() {
        assertThat(workflowMapper.toUiStatus("SUCCESS")).isEqualTo("Completed");
        assertThat(workflowMapper.toUiStatus("FAILED")).isEqualTo("Failed");
        assertThat(workflowMapper.toUiStatus("RUNNING")).isEqualTo("In Progress");
    }

    @Test
    void shouldConvertUnknownStatusesToTitleCase() {
        assertThat(workflowMapper.toUiStatus("WAITING_FOR_RETRY")).isEqualTo("Waiting For Retry");
    }
}
