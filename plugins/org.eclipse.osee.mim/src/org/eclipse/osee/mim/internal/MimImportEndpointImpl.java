/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.mim.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimImportApi;
import org.eclipse.osee.mim.MimImportEndpoint;
import org.eclipse.osee.mim.types.MIMImportUtil;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.mim.types.MimImportToken;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan T. Baldwin
 */
public class MimImportEndpointImpl implements MimImportEndpoint {

   private final MimApi mimApi;

   public MimImportEndpointImpl(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Override
   public List<MimImportToken> getImportOptions() {
      return mimApi.getMimArtifactsApi().getMimImports();
   }

   @Override
   public TransactionResult performSummaryImport(BranchId branchId, MimImportSummary summary) {
      TransactionResult txResult = new TransactionResult();
      ObjectMapper mapper = new ObjectMapper();
      TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(mimApi.getOrcsApi());
      TransactionBuilderData txData =
         MIMImportUtil.getTxBuilderDataFromImportSummary(branchId, ApplicabilityId.BASE, summary);

      try {
         TransactionBuilder tx = txBdf.loadFromJson(mapper.writeValueAsString(txData));
         TransactionToken token = tx.commit();
         txResult.setTx(token);
         txResult.getResults().setIds(
            tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
      } catch (JsonProcessingException ex) {
         txResult.getResults().error("Error processing tx json");
      }

      if (txResult.isFailed() && txResult.getResults().isSuccess()) {
         txResult.getResults().error("There was an error when importing the summary");
      }

      return txResult;
   }

   @Override
   public MimImportSummary getImportSummary(BranchId branch, ArtifactId transportTypeId, String fileName,
      InputStream stream) {
      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         MimImportApi importer = new IcdImportApiImpl(branch, transportTypeId, fileName,
            new ByteArrayInputStream(outputStream.toByteArray()), mimApi);
         outputStream.close();
         stream.close();
         return importer.getSummary();
      } catch (IOException ex) {
         System.out.println(ex);
         return new MimImportSummary();
      }
   }

   @Override
   public TransactionBuilderData getTxBuilderDataFromImportSummary(BranchId branch, ApplicabilityId applicId,
      MimImportSummary summary) {
      if (applicId == null) {
         applicId = ApplicabilityId.BASE;
      }
      return MIMImportUtil.getTxBuilderDataFromImportSummary(branch, applicId, summary);
   }

   @Override
   public MimImportSummary getTypesImportSummary(InputStream stream) {
      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         MimImportApi importer =
            new PlatformTypeImportApiImpl(new ByteArrayInputStream(outputStream.toByteArray()), mimApi);
         outputStream.close();
         stream.close();
         return importer.getSummary();
      } catch (IOException ex) {
         System.out.println(ex);
         return new MimImportSummary();
      }
   }

}