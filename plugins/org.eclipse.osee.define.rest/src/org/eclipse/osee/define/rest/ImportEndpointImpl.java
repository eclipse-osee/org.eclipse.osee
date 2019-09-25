/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
   public XResultData importSetup(BranchId branch, String baseDir, Integer startBranch, boolean handleRelations, boolean singleBranch) {
      return defineApi.getImportOperations().importSetup(branch, baseDir, startBranch, handleRelations, singleBranch);
   }

   @Override
   public XResultData postProcess(Integer startBranch, boolean singleBranch) {
      return null;
   }
}