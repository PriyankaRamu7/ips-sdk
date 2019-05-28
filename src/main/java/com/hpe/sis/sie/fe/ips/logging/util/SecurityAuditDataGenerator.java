package com.hpe.sis.sie.fe.ips.logging.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecurityAuditDataGenerator
{
  private static final Logger log = LoggerFactory.getLogger(SecurityAuditDataGenerator.class);

  public String formatSALine(Map<String, String> saData, String saFieldKeys, String fieldDelimiterSA)
  {
    if (log.isDebugEnabled()) {
      log.debug("[AuditDataGenerator::formatSALine] Entry: SA_DATA : " + saData);
    }
    String fieldSeparator = ",";
    StringBuffer saString = new StringBuffer(128);

    try
    {
      if (saData == null) {
        log.warn("[AuditDataGenerator::formatSALine] SecurityAuditException Data is null in the HashMap");
      } else {
        List<String> defaultTDRFields = new ArrayList<>();
        if (saFieldKeys != null) {
          defaultTDRFields = Arrays.asList(saFieldKeys.split(fieldSeparator));
        }
        Iterator<String> tdrKeys = defaultTDRFields.iterator();
        while (tdrKeys.hasNext()) {
          String tdrKey = (String)tdrKeys.next();
          if (log.isDebugEnabled())
            log.debug("tdrKey :" + tdrKey);

          String value = (String)saData.get(tdrKey);
          value = value != null ? value : "";

          if (tdrKey.equalsIgnoreCase("DETAILS")) {
            if (log.isDebugEnabled())
              log.debug("[AuditDataGenerator::formatSALine] Initial SecurityAudit reason for failure " + value);
            int index = value.indexOf("\n");
            if (log.isDebugEnabled())
              log.debug("[AuditDataGenerator::formatSALine] Index value of '\n' is " + index);
            if (index > 0) {
              if (log.isDebugEnabled())
                log.debug("[AuditDataGenerator::formatSALine] field has multiple lines");
              String valueStr = value.substring(0, index);
              if (log.isDebugEnabled())
                log.debug("[AuditDataGenerator::formatSALine] SecurityAudit reason for failure after removing multiple lines" + valueStr);
              value = valueStr.replaceAll(fieldSeparator, "");
            } else {
              if (log.isDebugEnabled())
                log.debug("[AuditDataGenerator::formatSALine] field has only a single line");
              value = value.replaceAll(fieldSeparator, "");
            }
          }

          saString.append(value);
          if (tdrKeys.hasNext()) {
            saString.append(fieldDelimiterSA);
          }

        }

        if (log.isDebugEnabled())
          log.debug("[AuditDataGenerator::formatSALine] returning " + saString);
      }
    }
    catch (Exception e) {
      log.error("[AuditDataGenerator::formatSALine] Exception occured", e);
    }
    if (log.isDebugEnabled())
      log.debug("[AuditDataGenerator::formatSALine] Exit");
    return saString.toString();
  }
}