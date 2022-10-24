/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.define.rest.internal;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
public final class SafetyReqtsOnlyStreamingOutput implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final BranchId branchId;
   private final ArtifactId view;

   public SafetyReqtsOnlyStreamingOutput(ActivityLog activityLog, OrcsApi orcsApi, BranchId branchId, ArtifactId view) {
      this.orcsApi = orcsApi;
      this.branchId = branchId;
      this.view = view;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output);

         SafetyReqtsOnlyReportGenerator safetyReport = new SafetyReqtsOnlyReportGenerator(orcsApi, branchId, view);
         safetyReport.runOperation(writer);

      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }
}
