/*******************************************************************************
 * © Copyright 2017 Hewlett Packard Enterprise Development LP. All Rights Reserved.
 * An unpublished and CONFIDENTIAL work. Reproduction,
 * adaptation, or translation without prior written permission
 * is prohibited except as allowed under the copyright laws.
 * ---------------------------------------------------------------------------
 * Project: SISv1.3
 * Module: SIS IPS
 * Author: HPE SIS Team
 * Organization: Hewlett Packard Enterprise
 * Revision: 1.0
 * Date: 03/10/2017
 * Contents: Tracer.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.tracer;

import java.util.Map;

/**
 * Tracer API
 *
 */
public interface Tracer {
	
	public static TraceContext start(String transactionId, String event, String classAndMethod) {
		return IPSTracer.start(transactionId, event, classAndMethod);
	}
	
	public static TraceContext start(String transactionId, String event, String classAndMethod, String actorPath) {
		return IPSTracer.start(transactionId, event, classAndMethod, actorPath);
	}
	
	public static TraceContext start(String transactionId, String event, String classAndMethod, String actorPath, Map<String, String> dataMap) {
		return IPSTracer.start(transactionId, event, classAndMethod, actorPath, dataMap);
	}
	
	public static void end(TraceContext traceContext, String event, String status) {
		IPSTracer.end(traceContext, event, status);
	}
	
	public static void initializeTracer() {
		IPSTracer.initializeTracer();
	}
	
}
