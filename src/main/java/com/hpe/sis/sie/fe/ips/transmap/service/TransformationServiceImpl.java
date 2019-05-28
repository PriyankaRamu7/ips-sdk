package com.hpe.sis.sie.fe.ips.transmap.service;

import java.io.IOException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hpe.sis.sie.fe.ips.processing.messages.TransformationRequest;
import com.hpe.sis.sie.fe.ips.transmap.exception.TransformationException;
import com.hpe.sis.sie.fe.ips.transmap.util.TransformationUtil;

public class TransformationServiceImpl implements TransformationService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TransformationServiceImpl.class);

	@Override
	public String transform(TransformationRequest transformationRequest)
			throws TransformationException {

		String url = null;
		try {
			 url = TransformationUtil.fetchTaskUrl(transformationRequest);
			LOGGER.info("Complete task url is:"+url);
			
		} catch (TransformationException| JSONException | IOException e) {		
			LOGGER.error("transform failed due to:"+e.getMessage());
			LOGGER.error("ERROR_TRANSFORMATION_INVALID_JSON");
			e.printStackTrace();
			//vo.getTracingUtil().log("Transforming the BackEnd URL","failed",className,"transform"); //TODO tracing
			//throw new TransformationException(e.getCause()); //TODO Exception Handling
		}
		//vo.getTracingUtil().log("Transforming the BackEnd URL","success",className,"transform"); //TODO tracing
		return url;
	}
	
}
