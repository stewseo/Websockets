package org.game.myDeployment.stackManagement.data.ilmPolicy;

import org.game.myDeployment.stackManagement.data.indexLifecyclePolicies.IlmPolicyHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// tests index management lifecycle policy configurations.
// verifies what tasks are automatically managed on hot and warm datastreams and indices
class IlmPolicyImplTests {
    public static IlmPolicyHandler ilmPolicyImpl;

    @BeforeAll
    static void setup() throws IOException {
        ilmPolicyImpl = new IlmPolicyHandler();
    }
    // test default index management policy keys and values
    @Test
    void getListOfIlmPoliciesTest() {
        // expected total number of ilm policies. 41 is default without creating or deleting.
        assertThat(ilmPolicyImpl.getMapOfIlmPolicies().size()).isEqualTo(41);

        String json = "{\"description\"=\"default policy for the ILM history indices\", \"managed\"=true}";
        assertThat(ilmPolicyImpl
                .getMapOfIlmPolicies()
                .values()
                .stream()
                .map(e ->
                        e.policy()
                                .meta()).toList().get(0))
                .isEqualTo(json);

        // expected index lifecycle management policy ids
        assertThat(ilmPolicyImpl.getMapOfIlmPolicies().keySet()).isEqualTo(
                "\"[\"ilm-history-ilm-policy\"," +
                        "\"metrics-apm.profile_metrics-default_policy\"," +
                        "\"synthetics-synthetics.browser-default_policy\"," +
                        "\"synthetics-synthetics.icmp-default_policy\"," +
                        "\"30-days-default\",\".preview.alerts-security.alerts-policy\"," +
                        "\"logs-app_search.analytics-default\"," +
                        "\"logs-workplace_search.content_events-default\",\"slm-history-ilm-policy\",\"logs-apm.error_logs-default_policy\",\n" +
                        "\"logs-crawler-default\"," +
                        "\"synthetics-synthetics.tcp-default_policy\","+
                        "\"logs-enterprise_search.audit-default\"," +
                        "\".deprecation-indexing-ilm-policy\"," +
                        "\"ml-size-based-ilm-policy\","+
                        "\"synthetics-synthetics.browser_screenshot-default_policy\"," +
                        "\"filebeat\"," +
                        "\"metrics-apm.internal_metrics-default_policy\",\n" +
                        "\"180-days-default\"," +
                        "\"logs\"," +
                        "\"90-days-default\"," +
                        "\"traces-apm.traces-default_policy\"," +
                        "\"watch-history-ilm-policy-16\"," +
                        "\"traces-apm.rum_traces-default_policy\"," +
                        "\"metrics-apm.app_metrics-default_policy\"," +
                        "\"synthetics\"," +
                        "\"kibana-event-log-policy\",\n" +
                        "\"kibana-reporting\",\n" +
                        "\"synthetics-synthetics.browser_network-default_policy\",\n" +
                        "\".alerts-ilm-policy\",\n" +
                        "\".monitoring-8-ilm-policy\",\n" +
                        "\"logs-app_search.search_relevance_suggestions-default\",\n" +
                        "\"traces-apm.sampled_traces-default_policy\",\n" +
                        "\"synthetics-synthetics.http-default_policy\",\n" +
                        "\".fleet-actions-results-ilm-policy\",\n" +
                        "\"365-days-default\",\n" +
                        "\"7-days-default\",\n" +
                        "\"metrics\",\n" +
                        "\"logs-workplace_search.analytics-default\",\n" +
                        "\"logs-apm.app_logs-default_policy\",\n" +
                        "\"logs-enterprise_search.api-default\"]\"");
        // test policy values

    }

    //
    @Test
    void getIlmPolicyTest(){
        // getKeys (JsonObject)
        List<String> indexTemplates = List.of("logs-enterprise_search.api-default", "logs-enterprise_search.audit-default", "filebeat");
        for(String indexTemplateId : indexTemplates){
            assertThat(ilmPolicyImpl.getMapOfIlmPolicies().get(indexTemplateId)).isNotNull();
        }
    }
    @Test
    void getPipelineTest() {
        assertThat(ilmPolicyImpl.getPipeline()).isEqualTo("test");
    }

    @Test
    void setPriorityTest() {

    }
}