/*
 * Created on Jun 30, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.writer;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.OrcsApi;

public class OrcsValidationHelperAdapter implements IOrcsValidationHelper {

   private final OrcsApi orcsApi;

   public OrcsValidationHelperAdapter(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public boolean isBranchExists(long branchUuid) {
      return orcsApi.getQueryFactory().branchQuery().andUuids(branchUuid).getResultsAsId().size() == 1;
   }

   @Override
   public boolean isUserExists(String userId) {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.UserId, userId).getResults().getAtMostOneOrNull() != null;
   }

   @Override
   public boolean isArtifactExists(long branchUuid, long artifactUuid) {
      int matchedArtifacts = orcsApi.getQueryFactory().fromBranch(branchUuid).andUuid(artifactUuid).getResults().size();
      return matchedArtifacts == 1;
   }

   @Override
   public boolean isArtifactTypeExist(long artifactTypeUuid) {
      return orcsApi.getOrcsTypes().getArtifactTypes().getByUuid(artifactTypeUuid) != null;
   }

   @Override
   public boolean isRelationTypeExist(long relationTypeUuid) {
      return orcsApi.getOrcsTypes().getRelationTypes().getByUuid(relationTypeUuid) != null;
   }

   @Override
   public boolean isAttributeTypeExists(long attributeTypeUuid) {
      return orcsApi.getOrcsTypes().getAttributeTypes().getByUuid(attributeTypeUuid) != null;
   }

}
