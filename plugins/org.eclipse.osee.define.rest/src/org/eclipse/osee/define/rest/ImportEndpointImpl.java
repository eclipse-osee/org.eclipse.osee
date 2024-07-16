/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest;

import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.ImportEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author David W. Miller
 */
public class ImportEndpointImpl implements ImportEndpoint {
   private final DefineApi defineApi;

   public ImportEndpointImpl(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   @Override
   public XResultData importWord(BranchId branch, String wordURI, ArtifactId parentArtifactId, Integer tier) {
      return defineApi.getImportOperations().importWord(branch, wordURI, parentArtifactId, tier);
   }

   @Override
   public XResultData verifyWordImport(BranchId branch, String wordURI, ArtifactId parentArtifactId, Integer tier) {
      return defineApi.getImportOperations().verifyWordImport(branch, wordURI, parentArtifactId, tier);
   }

   @Override
   public XResultData rectifyWordImport(BranchId branch, String wordURI, ArtifactId parentArtifactId, Integer tier, String doorsIds) {
      return defineApi.getImportOperations().rectifyWordImport(branch, wordURI, parentArtifactId, tier, doorsIds);
   }

   @Override
   public XResultData importSetup(BranchId branch, String baseDir, Integer startBranch, boolean handleRelations, boolean singleBranch) {
      return defineApi.getImportOperations().importSetup(branch, baseDir, startBranch, handleRelations, singleBranch);
   }

   @Override
   public XResultData postProcess(Integer startBranch, boolean singleBranch) {
      return null;
   }

   @Override
   public XResultData postProcessBranch(BranchId branch, ArtifactId figure, ArtifactId caption) {
      return null;
   }

   @Override
   public XResultData postProcessBranchLinks(BranchId branch, ArtifactId parent) {
      return null;
   }
}