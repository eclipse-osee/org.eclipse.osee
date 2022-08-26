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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.mim.ICDImportApi;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimImportEndpoint;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.mim.types.MimImportToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan T. Baldwin
 */
public class MimImportEndpointImpl implements MimImportEndpoint {

   private final OrcsApi orcsApi;

   public MimImportEndpointImpl(MimApi mimApi) {
      this.orcsApi = mimApi.getOrcsApi();
   }

   @Override
   public List<MimImportToken> getImportOptions() {
      List<MimImportToken> importOptions = new LinkedList<>();
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(BranchId.valueOf(570L)).andIsOfType(
         CoreArtifactTypes.MimImport).asArtifacts()) {
         if (art.isValid()) {
            importOptions.add(new MimImportToken(art));
         }
      }
      return importOptions;
   }

   @Override
   public MimImportSummary getImportSummary(InputStream stream) {
      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         ICDImportApi importer = new IcdImportApiImpl(new ByteArrayInputStream(outputStream.toByteArray()));
         outputStream.close();
         stream.close();
         return importer.getSummary();
      } catch (IOException ex) {
         System.out.println(ex);
         return new MimImportSummary();
      }
   }

}