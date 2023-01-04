/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.rest.metrics;

import java.util.Date;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */
public class MetricsReportOperations {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public MetricsReportOperations(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public Response generateDevProgressReport(String targetVersion, Date startDate, Date endDate, int weekday, int iterationLength, boolean periodic, boolean nonPeriodic, boolean periodicTask, boolean nonPeriodicTask) {
      StreamingOutput streamingOutput = new DevProgressMetricsReport(orcsApi, atsApi, targetVersion, startDate, endDate,
         weekday, iterationLength, periodic, nonPeriodic, periodicTask, nonPeriodicTask);
      String fileName = String.format("DevelopmentProgressMetrics_%s_%s.xml", targetVersion, Lib.getDateTimeString());

      ResponseBuilder builder = Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM);
      builder.header("Content-Disposition", "attachment; filename=" + fileName).header("FileName", fileName);
      return builder.build();
   }
}