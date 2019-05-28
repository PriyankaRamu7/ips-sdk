package com.hpe.sis.sie.fe.ips.transmap.service;

import com.hpe.sis.sie.fe.ips.processing.messages.TransformationRequest;
import com.hpe.sis.sie.fe.ips.transmap.exception.TransformationException;

public interface TransformationService {

	String transform(TransformationRequest transformationRequest) throws TransformationException;

}
