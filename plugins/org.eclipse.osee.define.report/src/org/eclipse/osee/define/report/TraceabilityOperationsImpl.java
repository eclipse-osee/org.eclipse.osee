/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report;

import java.io.Writer;
import javax.ws.rs.WebApplicationException;
import org.eclipse.osee.define.report.api.TraceabilityOperations;
import org.eclipse.osee.define.report.internal.TraceReportGenerator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.orcs.OrcsApi;

public class TraceabilityOperationsImpl implements TraceabilityOperations {

   private final OrcsApi orcsApi;

   public TraceabilityOperationsImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public void generateTraceReport(BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter, IArtifactType artifactType, AttributeTypeToken attributeType) {
      TraceReportGenerator generator = new TraceReportGenerator(artifactType, attributeType);
      try {
         generator.generate(orcsApi, branchId, codeRoot, traceRoot, providedWriter);
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

}
