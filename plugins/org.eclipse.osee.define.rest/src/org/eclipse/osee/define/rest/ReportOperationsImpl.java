/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.ReportOperations;
import org.eclipse.osee.define.rest.internal.PublishTemplateReport;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
public class ReportOperationsImpl implements ReportOperations {

   private final OrcsApi orcsApi;
   private final ActivityLog log;

   public ReportOperationsImpl(OrcsApi orcsApi, ActivityLog log) {
      this.orcsApi = orcsApi;
      this.log = log;
   }

   @Override
   public Response getReportFromTemplate(BranchId branch, ArtifactId view, ArtifactId templateArt) {
      StreamingOutput streamingOutput = new PublishTemplateReport(log, orcsApi, branch, view, templateArt);
      String fileName = String.format("Master_Template_Trace_Report_%s.xml", Lib.getDateTimeString());

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }
}
