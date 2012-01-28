/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.docretrieve.entity;

import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.orchestration.CONNECTInboundOrchestrator;
import gov.hhs.fha.nhinc.orchestration.OutboundOrchestratable;
import gov.hhs.fha.nhinc.orchestration.NhinAggregator;
import gov.hhs.fha.nhinc.orchestration.Orchestratable;
import gov.hhs.fha.nhinc.orchestration.PolicyTransformer;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType.DocumentRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author mweaver
 */
public class OutboundDocRetrieveOrchestratorImpl extends CONNECTInboundOrchestrator {

    private static final Log logger = LogFactory.getLog(OutboundDocRetrieveOrchestratorImpl.class);

    @Override
    public Orchestratable processEnabledMessage(Orchestratable message) {
        OutboundDocRetrieveOrchestratable EntityDROrchMessage = (OutboundDocRetrieveOrchestratable) message;
        for (DocumentRequest docRequest : EntityDROrchMessage.getRequest().getDocumentRequest()) {
            OutboundOrchestratable impl = new OutboundDocRetrieveOrchestratable(message.getPolicyTransformer(), message.getAuditTransformer(), EntityDROrchMessage.getNhinDelegate(), EntityDROrchMessage.getAggregator());
            RetrieveDocumentSetRequestType rdRequest = new RetrieveDocumentSetRequestType();
            rdRequest.getDocumentRequest().add(docRequest);
            ((OutboundDocRetrieveOrchestratable) impl).setRequest(rdRequest);
            ((OutboundDocRetrieveOrchestratable) impl).setAssertion(message.getAssertion());
            ((OutboundDocRetrieveOrchestratable) impl).setTarget(buildHomeCommunity(docRequest.getHomeCommunityId()));

            NhinAggregator agg = EntityDROrchMessage.getAggregator();
            agg.aggregate((OutboundOrchestratable)message, (OutboundOrchestratable)super.processEnabledMessage(impl));
        }// TODO Auto-generated method stub
        return message;
    }

    private NhinTargetSystemType buildHomeCommunity(String homeCommunityId) {
        NhinTargetSystemType nhinTargetSystem = new NhinTargetSystemType();
        HomeCommunityType homeCommunity = new HomeCommunityType();
        homeCommunity.setHomeCommunityId(homeCommunityId);
        nhinTargetSystem.setHomeCommunity(homeCommunity);
        return nhinTargetSystem;
    }

    @Override
    public Log getLogger() {
        return logger;
    }
}
