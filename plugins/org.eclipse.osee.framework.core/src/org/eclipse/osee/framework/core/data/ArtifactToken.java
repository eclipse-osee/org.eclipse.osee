/*
 * Created on Feb 8, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

public class ArtifactToken extends NamedIdentity implements IArtifactToken {

   private final IArtifactType artifactType;
   private final IOseeBranch oseeBranch;

   public ArtifactToken(String guid, String name, IArtifactType artifactType, IOseeBranch oseeBranch) {
      super(guid, name);
      this.artifactType = artifactType;
      this.oseeBranch = oseeBranch;
   }

   @Override
   public IArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   public IOseeBranch getBranch() {
      return oseeBranch;
   }

}
