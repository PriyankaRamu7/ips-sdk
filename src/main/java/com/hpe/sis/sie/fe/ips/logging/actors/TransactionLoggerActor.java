package com.hpe.sis.sie.fe.ips.logging.actors;

import com.hpe.sis.sie.fe.ips.logging.util.TransactionLog;
import com.hpe.sis.sie.fe.ips.processing.model.Transaction;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;

import akka.actor.UntypedAbstractActor;

public class TransactionLoggerActor extends UntypedAbstractActor{

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof Transaction) {
			Transaction transaction = (Transaction) message;
			TraceContext traceContext = Tracer.start(transaction.getTransactionId(), "TransactionLoggerActor -- CDR Logging" , "TransactionLoggerActor.onReceive", getSelf().path().toString());
			System.out.println(getSelf().path() +" Received TransactionLogMessage for Transaction: " + transaction.getTransactionId());
			TransactionLog.logTransaction(transaction);
			Tracer.end(traceContext, "TransactionLoggerActor -- CDR Logging", TraceConstants.SUCCESS);
		}
		
	}

}
