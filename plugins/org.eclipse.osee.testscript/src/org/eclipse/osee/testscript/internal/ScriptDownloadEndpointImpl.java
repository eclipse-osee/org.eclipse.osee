/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.io.File;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.testscript.ScriptApi;
import org.eclipse.osee.testscript.ScriptDownloadEndpoint;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptDownloadEndpointImpl implements ScriptDownloadEndpoint {

   private final BranchId branch;
   private final ScriptApi scriptApi;

   public ScriptDownloadEndpointImpl(BranchId branch, ScriptApi scriptApi) {
      this.branch = branch;
      this.scriptApi = scriptApi;
   }

   @Override
   public Response downloadTmo(ArtifactId resultId) {
      ScriptResultToken result = scriptApi.getScriptResultApi().get(branch, resultId);
      if (result.isInvalid()) {
         return Response.status(406, "Result artifact not found").build();
      }

      File tmoFile = new File(result.getFileUrl());
      if (!tmoFile.exists()) {
         return Response.status(406, "TMO file not found").build();
      }

      return Response.ok(new TmoFileStreamingOutput(tmoFile)).build();
   }

   @Override
   public Response downloadBatch(ArtifactId batchId) {
      ScriptBatchToken batch = scriptApi.getScriptBatchApi().get(branch, batchId);
      if (batch.isInvalid()) {
         return Response.status(406, "Batch artifact not found").build();
      }

      File batchFolder = new File(batch.getFolderUrl());
      if (!batchFolder.exists()) {
         return Response.status(406, "Batch files not found").build();
      }

      return Response.ok(new ScriptBatchStreamingOutput(batchFolder.listFiles(), batch)).build();
   }

}
