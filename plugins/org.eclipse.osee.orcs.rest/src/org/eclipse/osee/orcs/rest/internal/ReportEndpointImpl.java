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

package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.writers.PublishTemplateReport;
import org.eclipse.osee.orcs.rest.model.ReportEndpoint;

/**
 * @author David W. Miller
 */
public final class ReportEndpointImpl implements ReportEndpoint {

   private final OrcsApi orcsApi;

   public ReportEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public Response getReportFromTemplate(BranchId branch, ArtifactId view, ArtifactId templateArt) {
      StreamingOutput streamingOutput = new PublishTemplateReport(orcsApi, branch, view, templateArt);
      String fileName = String.format("Generic_Trace_Report_%s.xml", Lib.getDateTimeString());

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

}
