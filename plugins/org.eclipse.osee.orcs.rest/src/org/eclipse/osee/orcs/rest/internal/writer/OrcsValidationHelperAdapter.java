/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class OrcsValidationHelperAdapter implements IOrcsValidationHelper {

   private final OrcsApi orcsApi;

   public OrcsValidationHelperAdapter(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public boolean isBranchExists(BranchId branch) {
      return orcsApi.getQueryFactory().branchQuery().andId(branch).getResultsAsId().size() == 1;
   }

   @Override
   public boolean isUserExists(String userId) {
      return orcsApi.getQueryFactory().fromBranch(COMMON).and(CoreAttributeTypes.UserId,
         userId).getResults().getAtMostOneOrNull() != null;
   }

   @Override
   public boolean isArtifactExists(BranchId branch, long artifactUuid) {
      int matchedArtifacts = orcsApi.getQueryFactory().fromBranch(branch).andUuid(artifactUuid).getResults().size();
      return matchedArtifacts == 1;
   }

   @Override
   public boolean isArtifactTypeExist(long artifactTypeUuid) {
      return orcsApi.getOrcsTypes().getArtifactTypes().get(artifactTypeUuid) != null;
   }

   @Override
   public boolean isRelationTypeExist(long relationTypeUuid) {
      return orcsApi.getOrcsTypes().getRelationTypes().get(relationTypeUuid) != null;
   }

   @Override
   public boolean isAttributeTypeExists(long attributeTypeUuid) {
      return orcsApi.getOrcsTypes().getAttributeTypes().get(attributeTypeUuid) != null;
   }

   @Override
   public boolean isAttributeTypeExists(String attributeTypeName) {
      for (AttributeTypeToken type : orcsApi.getOrcsTypes().getAttributeTypes().getAll()) {
         if (type.getName().equals(attributeTypeName)) {
            return true;
         }
      }
      return false;
   }

}
