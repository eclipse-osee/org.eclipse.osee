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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.mim.MimArtifactsApi;
import org.eclipse.osee.mim.types.MimImportToken;
import org.eclipse.osee.mim.types.MimReportToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan T. Baldwin
 */
public class MimArtifactsApiImpl implements MimArtifactsApi {

   private final OrcsApi orcsApi;

   public MimArtifactsApiImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public List<MimReportToken> getMimReports() {
      List<MimReportToken> reports = new LinkedList<>();
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(
         BranchId.valueOf(CoreBranches.COMMON.getId())).andIsOfType(CoreArtifactTypes.MimReport).asArtifacts()) {
         if (art.isValid()) {
            reports.add(new MimReportToken(art));
         }
      }
      return reports;
   }

   @Override
   public List<MimImportToken> getMimImports() {
      List<MimImportToken> importOptions = new LinkedList<>();
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(
         BranchId.valueOf(CoreBranches.COMMON.getId())).andIsOfType(CoreArtifactTypes.MimImport).asArtifacts()) {
         if (art.isValid()) {
            importOptions.add(new MimImportToken(art));
         }
      }
      return importOptions;
   }

}
