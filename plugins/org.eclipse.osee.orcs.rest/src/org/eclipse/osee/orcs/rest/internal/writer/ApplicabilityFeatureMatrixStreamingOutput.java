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

package org.eclipse.osee.orcs.rest.internal.writer;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Audrey E. Denk
 */
public final class ApplicabilityFeatureMatrixStreamingOutput implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final String filter;

   public ApplicabilityFeatureMatrixStreamingOutput(OrcsApi orcsApi, BranchId branch, String filter) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.filter = filter;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output);
         ApplicabilityFeatureMatrixGenerator generator = new ApplicabilityFeatureMatrixGenerator(orcsApi);
         generator.runOperation(orcsApi, writer, branch, filter);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }
}
