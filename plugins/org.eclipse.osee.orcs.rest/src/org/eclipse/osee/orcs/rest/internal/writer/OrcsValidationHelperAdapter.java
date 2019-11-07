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
import java.util.Collection;
import java.util.HashMap;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Donald G. Dunne
 */
public class OrcsValidationHelperAdapter implements IOrcsValidationHelper {

   private final QueryFactory queryFactory;
   private final OrcsTypes orcsTypes;

   public OrcsValidationHelperAdapter(OrcsApi orcsApi) {
      queryFactory = orcsApi.getQueryFactory();
      orcsTypes = orcsApi.getOrcsTypes();
   }

   @Override
   public boolean isBranchExists(BranchId branch) {
      return queryFactory.branchQuery().andId(branch).getResultsAsId().size() == 1;
   }

   @Override
   public boolean isUserExists(String userId) {
      return queryFactory.fromBranch(COMMON).andAttributeIs(CoreAttributeTypes.UserId,
         userId).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL).isValid();
   }

   @Override
   public boolean isArtifactExists(BranchId branch, long artifactUuid) {
      return queryFactory.fromBranch(branch).andUuid(artifactUuid).exists();
   }

   @Override
   public boolean isArtifactTypeExist(long artifactTypeUuid) {
      return orcsTypes.getArtifactTypes().get(artifactTypeUuid) != null;
   }

   @Override
   public boolean isApplicabilityExist(BranchId branch, String value) {

      HashMap<Long, ApplicabilityToken> tokenMap = queryFactory.applicabilityQuery().getApplicabilityTokens(branch);
      Collection<ApplicabilityToken> tokens = tokenMap.values();
      for (ApplicabilityToken applicToken : tokens) {
         if (applicToken.getName().equals(value)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isRelationTypeExist(long relationTypeUuid) {
      return orcsTypes.getRelationTypes().get(relationTypeUuid) != null;
   }

   @Override
   public boolean isAttributeTypeExists(long attributeTypeUuid) {
      return orcsTypes.getAttributeTypes().get(attributeTypeUuid) != null;
   }

   @Override
   public boolean isAttributeTypeExists(String attributeTypeName) {
      for (AttributeTypeToken type : orcsTypes.getAttributeTypes().getAll()) {
         if (type.getName().equals(attributeTypeName)) {
            return true;
         }
      }
      return false;
   }

}
