/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.internal.writer;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Donald G. Dunne
 */
public class OrcsValidationHelperAdapter implements IOrcsValidationHelper {

   private final QueryFactory queryFactory;
   private final OrcsTokenService tokenService;

   public OrcsValidationHelperAdapter(OrcsApi orcsApi) {
      queryFactory = orcsApi.getQueryFactory();
      tokenService = orcsApi.tokenService();
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
      return tokenService.getArtifactType(artifactTypeUuid) != null;
   }

   @Override
   public boolean isApplicabilityExist(BranchId branch, String value) {
      return queryFactory.applicabilityQuery().featureExistsOnBranch(branch, value);
   }

   @Override
   public boolean isRelationTypeExist(long relationTypeUuid) {
      return tokenService.getRelationType(relationTypeUuid) != null;
   }

   @Override
   public boolean isAttributeTypeExists(long attributeTypeUuid) {
      return tokenService.getAttributeType(attributeTypeUuid) != null;
   }
}