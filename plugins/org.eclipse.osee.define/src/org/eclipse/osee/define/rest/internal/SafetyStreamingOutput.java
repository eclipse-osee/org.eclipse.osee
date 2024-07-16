/*********************************************************************
 * Copyright (c) 2013 Boeing
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
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public final class SafetyStreamingOutput implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final BranchId branchId;
   private final String codeRoot;
   private final ActivityLog activityLog;
   private final ArtifactId view;
   private final boolean validate;

   public SafetyStreamingOutput(ActivityLog activityLog, OrcsApi orcsApi, BranchId branchId, ArtifactId view, String codeRoot, String isOn) {
      this.activityLog = activityLog;
      this.orcsApi = orcsApi;
      this.branchId = branchId;
      this.view = view;
      this.codeRoot = codeRoot;
      this.validate = isOn != null && isOn.equals("on") ? true : false;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output);
         if (validate) {
            ValidatingSafetyReportGenerator teamSafetyReport = new ValidatingSafetyReportGenerator(activityLog);
            teamSafetyReport.runOperation(orcsApi, branchId, view, codeRoot, writer);
         } else {
            SafetyReportGenerator safetyReport = new SafetyReportGenerator(activityLog);
            safetyReport.runOperation(orcsApi, branchId, codeRoot, writer);
         }
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }
}
