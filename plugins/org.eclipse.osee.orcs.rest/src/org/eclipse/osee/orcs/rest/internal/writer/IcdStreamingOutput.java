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
public final class IcdStreamingOutput implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final String connectionName;

   public IcdStreamingOutput(OrcsApi orcsApi, BranchId branch, String connectionName) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.connectionName = connectionName;
   }

   @Override
   public void write(OutputStream output) {
      try {
         Writer writer = new OutputStreamWriter(output);
         IcdGenerator generator = new IcdGenerator(orcsApi);
         generator.runOperation(orcsApi, writer, branch, connectionName);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }
}
