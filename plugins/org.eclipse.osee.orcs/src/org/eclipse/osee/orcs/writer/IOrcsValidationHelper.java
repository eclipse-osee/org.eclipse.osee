/*
 * Created on Jun 30, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.writer;

public interface IOrcsValidationHelper {

   boolean isBranchExists(long branchUuid);

   boolean isUserExists(String userId);

   boolean isArtifactTypeExist(long artifactTypeUuid);

   boolean isRelationTypeExist(long relationTypeUuid);

   boolean isAttributeTypeExists(long attributeTypeUuid);

   public boolean isArtifactExists(long branchUuid, long artifactUuid);

}
