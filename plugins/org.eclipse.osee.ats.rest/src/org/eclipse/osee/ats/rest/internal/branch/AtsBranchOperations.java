/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.branch;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.CreateBranchData;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchOperations {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsBranchOperations(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public BranchData createBranch(BranchData bd) {
      atsApi.getBranchService().validate(bd, atsApi);
      if (bd.isValidate() || bd.getResults().isErrors()) {
         return bd;
      }

      CreateBranchData cbd = new CreateBranchData();
      BranchType type = bd.getBranchType();
      ArtifactToken assocArt = bd.getAssociatedArt();
      if (type.equals(BranchType.BASELINE) && bd.getAssociatedArt().isInvalid()) {
         assocArt = AtsArtifactToken.AtsCmAccessControl;
      } else if (assocArt.isInvalid()) {
         bd.getResults().errorf("Associated Artifact is invalid for %s branch", bd.getBranchType());
         return bd;
      }
      cbd.setBranchType(type);
      cbd.setName(bd.getBranchName());
      cbd.setAssociatedArtifact(assocArt);
      cbd.setCreationComment(bd.getCreationComment());
      cbd.setInheritAccess(true);
      cbd.setParentBranch(bd.getParent());
      cbd.setCategories(orcsApi.getQueryFactory().branchQuery().getBranchCategories(bd.getParent()));
      orcsApi.getBranchOps().createBranch(cbd);
      return bd;
   }

}
