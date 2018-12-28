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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.activity.api.ActivityLog;
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
   private final boolean validate;

   public SafetyStreamingOutput(ActivityLog activityLog, OrcsApi orcsApi, BranchId branchId, String codeRoot, String isOn) {
      this.activityLog = activityLog;
      this.orcsApi = orcsApi;
      this.branchId = branchId;
      this.codeRoot = codeRoot;
      this.validate = isOn != null && isOn.equals("on") ? true : false;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output);
         if (validate) {
            ValidatingSafetyReportGenerator teamSafetyReport = new ValidatingSafetyReportGenerator(activityLog);
            teamSafetyReport.runOperation(orcsApi, branchId, codeRoot, writer);
         } else {
            SafetyReportGenerator safetyReport = new SafetyReportGenerator(activityLog);
            safetyReport.runOperation(orcsApi, branchId, codeRoot, writer);
         }
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }
}
