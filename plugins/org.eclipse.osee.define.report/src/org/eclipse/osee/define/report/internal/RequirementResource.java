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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.define.report.OseeDefineResourceTokens;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Megumi Telles
 */
@Path("define/publish")
public final class RequirementResource {
   private final OrcsApi orcsApi;

   public RequirementResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Path("requirements")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getRequirementyReport(@QueryParam("branch") String branchGuid, @QueryParam("srs_root") String srsRoot) {
      StreamingOutput streamingOutput = new RequirementStreamingOutput(orcsApi, branchGuid, srsRoot);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "SRS_Report.xml");
      return builder.build();
   }

   @Path("ui/requirements")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getApplet() {
      OseeAppletPage pageUtil = new OseeAppletPage(orcsApi.getQueryFactory(null).branchQuery());
      return pageUtil.realizeApplet(orcsApi.getResourceRegistry(), OseeDefineResourceTokens.RequirementReportHtml);
   }
}
