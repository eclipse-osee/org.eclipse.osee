/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import java.util.List;
import org.eclipse.osee.define.api.DataRightsEndpoint;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;

/**
 * @author Angel Avila
 */
public class DataRightsEndpointImpl implements DataRightsEndpoint {

   private final DefineApi defineApi;

   public DataRightsEndpointImpl(DefineApi defineApi) {
      this.defineApi = defineApi;
   }

   @Override
   public DataRightResult getDataRights(BranchId branch, String overrideClassification, List<ArtifactId> artifacts) {
      return defineApi.getDataRightsOperations().getDataRights(artifacts, branch, overrideClassification);
   }

   @Override
   public DataRightResult getDataRights(BranchId branch, List<ArtifactId> artifacts) {
      return defineApi.getDataRightsOperations().getDataRights(artifacts, branch);
   }

   /**
    * Create request to determine all data rights for a collection of Artifacts
    *
    * @param data Collection of DataRightArtifacts to find data rights for
    * @return mapping Mapping of DataRightArtifacts to Footers
    */

}
