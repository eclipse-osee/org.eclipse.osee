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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.testscript.ScriptImportEndpoint;
import org.eclipse.osee.testscript.TmoImportApi;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptImportEndpointImpl implements ScriptImportEndpoint {

   private final BranchId branch;
   private final TmoImportApi tmoImportApi;

   public ScriptImportEndpointImpl(BranchId branch, TmoImportApi tmoImportApi) {
      this.branch = branch;
      this.tmoImportApi = tmoImportApi;
   }

   @Override
   public TransactionBuilderData getTxBuilderData(InputStream stream, ArtifactId ciSetId) {

      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         ScriptDefToken scriptDef =
            tmoImportApi.getScriptDefinition(new ByteArrayInputStream(outputStream.toByteArray()), ciSetId);
         outputStream.close();
         stream.close();
         return tmoImportApi.getTxBuilderData(branch, scriptDef);
      } catch (IOException ex) {
         System.out.println(ex);
         return new TransactionBuilderData();
      }
   }

   @Override
   public TransactionResult importFile(InputStream stream, ArtifactId ciSetId) {
      return this.tmoImportApi.importFile(stream, branch, ciSetId);
   }

   @Override
   public TransactionResult importBatch(InputStream stream, ArtifactId ciSetId) {
      return this.tmoImportApi.importBatch(stream, branch, ciSetId);
   }

   @Override
   public ScriptDefToken getScriptDefinition(InputStream stream) {
      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         ScriptDefToken scriptDef =
            tmoImportApi.getScriptDefinition(new ByteArrayInputStream(outputStream.toByteArray()), ArtifactId.SENTINEL);
         outputStream.close();
         stream.close();
         return scriptDef;
      } catch (IOException ex) {
         System.out.println(ex);
         return ScriptDefToken.SENTINEL;
      }
   }

}
