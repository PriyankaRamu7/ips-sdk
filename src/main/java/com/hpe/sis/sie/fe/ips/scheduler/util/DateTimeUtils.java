package com.hpe.sis.sie.fe.ips.scheduler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static Date getDateFromStr(String dateStr) throws Exception {

    	String format = "dd-MM-yyyy HH:mm:ss z";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(dateStr);
  }

}
