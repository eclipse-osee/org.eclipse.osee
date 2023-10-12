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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.testscript.ScriptImportEndpoint;
import org.eclipse.osee.testscript.TmoImportApi;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptImportEndpointImpl implements ScriptImportEndpoint {

   private final BranchId branch;
   private final OrcsApi orcsApi;
   private final TmoImportApi tmoImportApi;

   public ScriptImportEndpointImpl(BranchId branch, OrcsApi orcsApi, TmoImportApi tmoImportApi) {
      this.branch = branch;
      this.orcsApi = orcsApi;
      this.tmoImportApi = tmoImportApi;
   }

   @Override
   public TransactionBuilderData getTxBuilderData(InputStream stream) {
      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         ScriptDefToken scriptDef =
            tmoImportApi.getScriptDefinition(new ByteArrayInputStream(outputStream.toByteArray()));
         outputStream.close();
         stream.close();
         return tmoImportApi.getTxBuilderData(branch, scriptDef);
      } catch (IOException ex) {
         System.out.println(ex);
         return new TransactionBuilderData();
      }
   }

   @Override
   public TransactionResult importBatch(InputStream stream) {
      TransactionResult result = new TransactionResult();
      TransactionBuilderData txData = new TransactionBuilderData();
      try {
         ZipInputStream zipStream = new ZipInputStream(stream);
         while (zipStream.getNextEntry() != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            zipStream.transferTo(outputStream);
            ScriptDefToken scriptDef =
               tmoImportApi.getScriptDefinition(new ByteArrayInputStream(outputStream.toByteArray()));
            outputStream.close();
            tmoImportApi.getTxBuilderData(branch, txData, scriptDef, false);
         }
         stream.close();

         TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi);
         ObjectMapper mapper = new ObjectMapper();
         TransactionBuilder tx = txBdf.loadFromJson(mapper.writeValueAsString(txData));
         TransactionToken token = tx.commit();
         result.setTx(token);

      } catch (IOException ex) {
         System.out.println(ex);
      }
      return result;
   }

   @Override
   public ScriptDefToken getScriptDefinition(InputStream stream) {
      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         ScriptDefToken scriptDef =
            tmoImportApi.getScriptDefinition(new ByteArrayInputStream(outputStream.toByteArray()));
         outputStream.close();
         stream.close();
         return scriptDef;
      } catch (IOException ex) {
         System.out.println(ex);
         return ScriptDefToken.SENTINEL;
      }
   }

}
