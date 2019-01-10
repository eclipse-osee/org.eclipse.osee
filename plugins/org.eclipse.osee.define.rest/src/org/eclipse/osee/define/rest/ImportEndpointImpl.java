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
import org.eclipse.osee.framework.core.data.TransactionToken;

/**
 * @author David W. Miller
 */
public class ImportEndpointImpl implements ImportEndpoint {
   private final DefineApi defineApi;

   public ImportEndpointImpl(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   @Override
   public TransactionToken importWord(BranchId branch, String wordURI, ArtifactId parentArtifactId) {
      return defineApi.getImportOperations().importWord(branch, wordURI, parentArtifactId);
   }
}