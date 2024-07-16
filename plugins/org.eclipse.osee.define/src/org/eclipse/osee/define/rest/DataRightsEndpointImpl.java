/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import java.util.List;
import org.eclipse.osee.define.api.DataRightsEndpoint;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;

/**
 * @author Angel Avila
 */
public class DataRightsEndpointImpl implements DataRightsEndpoint {

   private final DefineOperations defineOperations;

   public DataRightsEndpointImpl(DefineOperations defineOperations) {
      this.defineOperations = defineOperations;
   }

   @Override
   public DataRightResult getDataRights(BranchId branch, String overrideClassification, List<ArtifactId> artifacts) {
      return defineOperations.getDataRightsOperations().getDataRights(artifacts, branch, overrideClassification);
   }

   @Override
   public DataRightResult getDataRights(BranchId branch, List<ArtifactId> artifacts) {
      return defineOperations.getDataRightsOperations().getDataRights(artifacts, branch);
   }

   /**
    * Create request to determine all data rights for a collection of Artifacts
    *
    * @param data Collection of DataRightArtifacts to find data rights for
    * @return mapping Mapping of DataRightArtifacts to Footers
    */

}
