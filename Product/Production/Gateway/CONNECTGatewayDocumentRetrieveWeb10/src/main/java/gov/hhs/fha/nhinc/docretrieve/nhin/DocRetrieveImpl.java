/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *  
 * Copyright 2010(Year date of delivery) United States Government, as represented by the Secretary of Health and Human Services.  All rights reserved.
 *  
 */
package gov.hhs.fha.nhinc.docretrieve.nhin;

import gov.hhs.fha.nhinc.async.AsyncMessageIdExtractor;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.orchestration.InboundDelegate;
import gov.hhs.fha.nhinc.orchestration.AuditTransformer;
import gov.hhs.fha.nhinc.orchestration.CONNECTInboundOrchestrator;
import gov.hhs.fha.nhinc.orchestration.PolicyTransformer;
import gov.hhs.fha.nhinc.saml.extraction.SamlTokenExtractor;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import javax.xml.ws.WebServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the Web Service implementation of Nhin Doc Retrieve.
 *
 * @author vvickers, Les Westberg
 */
class DocRetrieveImpl {

    private static Log log = LogFactory.getLog(DocRetrieveImpl.class);

    RetrieveDocumentSetResponseType respondingGatewayCrossGatewayRetrieve(RetrieveDocumentSetRequestType body, WebServiceContext context) {
        log.debug("Entering DocRetrieveImpl.respondingGatewayCrossGatewayRetrieve");

        AssertionType assertion = SamlTokenExtractor.GetAssertion(context);

        // Extract the message id value from the WS-Addressing Header and place it in the Assertion Class
        if (assertion != null) {
            assertion.setMessageId(AsyncMessageIdExtractor.GetAsyncMessageId(context));
        }

        PolicyTransformer pt = new NhinDocRetrievePolicyTransformer_g0();
        AuditTransformer at = new NhinDocRetrieveAuditTransformer_g0();
        InboundDelegate ad = new AdapterDocRetrieveDelegate();
        NhinDocRetrieveOrchestratableImpl_g0 NhinDROrchImpl = new NhinDocRetrieveOrchestratableImpl_g0(body, assertion, pt, at, ad);
        CONNECTInboundOrchestrator oOrchestrator = new CONNECTInboundOrchestrator();
        oOrchestrator.process(NhinDROrchImpl);
        RetrieveDocumentSetResponseType response = NhinDROrchImpl.getResponse();

        // Send response back to the initiating Gateway
        log.debug("Exiting DocRetrieveImpl.respondingGatewayCrossGatewayRetrieve");
        return response;
    }
}
