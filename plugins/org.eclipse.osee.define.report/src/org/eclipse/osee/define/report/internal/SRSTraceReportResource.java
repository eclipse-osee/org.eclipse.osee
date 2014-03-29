/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
@Path("/traceability/srstrace")
public final class SRSTraceReportResource {

   private final Log logger;
   private final IResourceRegistry resourceRegistry;
   private final OrcsApi orcsApi;
   private final Map<String, Object> properties;

   public SRSTraceReportResource(Log logger, IResourceRegistry resourceRegistry, OrcsApi orcsApi, Map<String, Object> properties) {
      this.logger = logger;
      this.resourceRegistry = resourceRegistry;
      this.orcsApi = orcsApi;
      this.properties = properties;
   }

   private List<TraceMatch> getTraceMatchers(String traceType) {
      String[] primaryRegexs = (String[]) properties.get("osee.trace.myII." + traceType + ".primary.regexs");
      String[] secondaryRegexs = (String[]) properties.get("osee.trace.myII." + traceType + ".secondary.regexs");

      List<TraceMatch> traceMatchers = new ArrayList<TraceMatch>();
      for (int i = 0; i < primaryRegexs.length; i++) {
         traceMatchers.add(new TraceMatch(primaryRegexs[i], secondaryRegexs[i]));
      }
      return traceMatchers;
   }

   /**
    * Produce the SRS Trace Report
    * 
    * @param branch The Branch uuid to run the SRS Trace Report on.
    * @param codeRoot The root directory accessible on the server for the code traces.
    * @param csci The desired CSCI.
    * @param traceType The desired trace type.
    * @return Produces a streaming xml file containing the SRS Trace Report
    */
   @Path("gen")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getStdStpReport(@QueryParam("branch") String branchUuid, @QueryParam("code_root") String codeRoot, @QueryParam("csci") String csci, @QueryParam("traceType") String traceType) {
      traceType = traceType.replace(' ', '_');
      csci = csci.toUpperCase();

      TraceAccumulator traceAccumulator =
         new TraceAccumulator(".*\\.(java|ada|ads|adb|c|h)", getTraceMatchers(traceType));

      StreamingOutput streamingOutput =
         new SRSTraceReportStreamingOutput(logger, orcsApi, Long.valueOf(branchUuid), codeRoot, traceAccumulator, csci,
            traceType);

      ResponseBuilder builder = Response.ok(streamingOutput);
      String fileName = csci + "_" + traceType.replace(' ', '_') + "_SRS_Trace_Report.xml";
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   /**
    * Provides the user interface for the SRS Traceability Report
    * 
    * @return Returns the html page for the SRS Traceability Report
    */
   @Path("ui")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getApplet() {
      OseeAppletPage pageUtil = new OseeAppletPage(orcsApi.getQueryFactory(null).branchQuery());
      return pageUtil.realizeApplet(resourceRegistry, "SRSTraceReport.html", getClass());
   }
}
