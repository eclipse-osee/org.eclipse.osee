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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimImportEndpoint;
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

}