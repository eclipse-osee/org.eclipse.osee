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
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimImportApi;
import org.eclipse.osee.mim.MimImportEndpoint;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.mim.types.MimImportToken;

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
   public MimImportSummary getImportSummary(BranchId branch, InputStream stream) {
      try {
         // Transfer to output stream to prevent the stream from closing mid-read
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         stream.transferTo(outputStream);
         MimImportApi importer =
            new IcdImportApiImpl(branch, new ByteArrayInputStream(outputStream.toByteArray()), mimApi);
         outputStream.close();
         stream.close();
         return importer.getSummary();
      } catch (IOException ex) {
         System.out.println(ex);
         return new MimImportSummary();
      }
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