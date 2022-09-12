package org.game.myDeployment.stackManagement.data.indexLifecyclePolicies;

import co.elastic.clients.elasticsearch.ilm.get_lifecycle.Lifecycle;
import co.elastic.clients.elasticsearch.ingest.Pipeline;
import org.game.myDeployment.AbstractApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

// modified date
// policy
// version
public class IlmPolicyImpl extends AbstractApiImpl {
    private static final Logger logger = LoggerFactory.getLogger(IlmPolicyImpl.class);
    private Map<String, Lifecycle> mapOfIlmPolicies; // reference of all index lifecycle policy ids

    // pipeline being used by filebeat.input filestream
    private static Pipeline websocketLogPipeline;

    public IlmPolicyImpl() throws IOException {
        mapOfIlmPolicies = client.ilm().getLifecycle().result();
        logger.info("life cycle policies map{}", mapOfIlmPolicies);
    }

    public Lifecycle get(String ilmPolicyId) throws IOException {
        var result =  client.ilm().getLifecycle().get(ilmPolicyId);
        logger.info(String.format("client.ilm().getLifecycle().get(%s) {}", result),
                ilmPolicyId);
        return result;
    }

    public Map<String, Lifecycle> getMapOfIlmPolicies() {
        return mapOfIlmPolicies;
    }

    public void setMapOfIlmPolicies(Map<String, Lifecycle> mapOfIlmPolicies) {
        this.mapOfIlmPolicies = mapOfIlmPolicies;
    }

    public Pipeline getPipeline() {
        return websocketLogPipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        IlmPolicyImpl.websocketLogPipeline = pipeline;
    }
}
