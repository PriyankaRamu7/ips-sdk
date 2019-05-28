package com.hpe.sis.sie.fe.ips.logging.actors;

import com.hpe.sis.sie.fe.ips.logging.util.SecurityAuditLog;
import com.hpe.sis.sie.fe.ips.processing.model.SecurityAudit;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SecurityAuditLoggerActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) throws Throwable {

		if (message instanceof SecurityAudit) {
			SecurityAudit securityAudit = (SecurityAudit) message;
			TraceContext traceContext = Tracer.start(securityAudit.getTransactionId(), "Security Audit logging", "SecurityAuditLoggerActor.onReceive", getSelf().path().toString());
			log.info(getSelf().path() + " Received SecurityAuditMessage for Transaction: " + securityAudit.getTransactionId());
			SecurityAuditLog.logSecurityAudit(securityAudit);
			Tracer.end(traceContext, "Security Audit logging", TraceConstants.SUCCESS);
		}

	}
}
