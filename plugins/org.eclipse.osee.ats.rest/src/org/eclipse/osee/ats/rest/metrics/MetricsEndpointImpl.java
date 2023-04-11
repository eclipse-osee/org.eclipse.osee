/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.rest.metrics;

import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.metrics.MetricsEndpointApi;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Stephen J. Molaro
 */
@Path("metrics")
public class MetricsEndpointImpl implements MetricsEndpointApi {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public MetricsEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   @Path("DevProgress/{targetVersion}")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response devProgressReport(@PathParam("targetVersion") String targetVersion,
      @QueryParam("startDate") Date startDate, @QueryParam("endDate") Date endDate,
      @QueryParam("allTime") boolean allTime) {
      return (new MetricsReportOperations(atsApi, orcsApi)).generateDevProgressReport(targetVersion, startDate, endDate,
         allTime);
   }

   @Override
   @Path("SoftwareReqVolatility/{targetVersion}")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response softwareReqVolatility(@PathParam("targetVersion") String targetVersion,
      @QueryParam("startDate") Date startDate, @QueryParam("endDate") Date endDate,
      @QueryParam("allTime") boolean allTime) {
      return (new MetricsReportOperations(atsApi, orcsApi)).generateSoftwareReqVolatility(targetVersion, startDate,
         endDate, allTime);
   }

   @Override
   @Path("BranchDiffData/{atsId}")
   @POST
   public Response setBranchDiffData(@PathParam("atsId") String atsId) {
      return (new MetricsReportOperations(atsApi, orcsApi)).setBranchDiffData(atsId);
   }

}