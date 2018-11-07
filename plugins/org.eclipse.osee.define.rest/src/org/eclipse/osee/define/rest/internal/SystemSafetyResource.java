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
package org.eclipse.osee.define.rest.internal;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.app.OseeAppletPage;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
@Path("/")
public final class SystemSafetyResource {
   private final OrcsApi orcsApi;
   private final IResourceRegistry resourceRegistry;
   private final ActivityLog activityLog;

   public SystemSafetyResource(ActivityLog activityLog, IResourceRegistry resourceRegistry, OrcsApi orcsApi) {
      this.activityLog = activityLog;
      this.resourceRegistry = resourceRegistry;
      this.orcsApi = orcsApi;
   }

   /**
    * Produce the System Safety Report
    *
    * @param branchId The Branch to run the System Safety Report on.
    * @param codeRoot The root directory accessible on the server for the code traces.
    * @return Produces a streaming xml file containing the System Safety Report
    */
   @Path("safety")
   @GET
   @Produces(MediaType.APPLICATION_XML)
   public Response getSystemSafetyReport(@QueryParam("branch") BranchId branchId, @QueryParam("code_root") String codeRoot, @DefaultValue("on") @QueryParam("style") String validate) {
      StreamingOutput streamingOutput = new SafetyStreamingOutput(activityLog, orcsApi, branchId, codeRoot, validate);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "Safety_Report.xml");
      return builder.build();
   }

   /**
    * Provides the user interface for the System Safety Report
    */
   @Path("ui/safety")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getApplet() {
      OseeAppletPage pageUtil = new OseeAppletPage(orcsApi.getQueryFactory().branchQuery());
      return pageUtil.realizeApplet(resourceRegistry, "systemSafetyReport.html", getClass());
   }
}
