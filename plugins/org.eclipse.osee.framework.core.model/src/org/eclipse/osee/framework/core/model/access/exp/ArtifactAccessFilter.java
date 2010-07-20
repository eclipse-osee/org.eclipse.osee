/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class ArtifactAccessFilter extends AbstractAccessFilter {

   private PermissionEnum artifactPermission;
   private final IBasicArtifact<?> artifact;

   public ArtifactAccessFilter(PermissionEnum artifactPermission, IBasicArtifact<?> artifact) {
      super();
      this.artifactPermission = artifactPermission;
      this.artifact = artifact;
   }

   @Override
   public int getRank() {
      return 30;
   }

   @Override
   public PermissionEnum filter(Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
      if (agrPermission == PermissionEnum.DENY || artifactPermission == null) {
         artifactPermission = agrPermission;
      }
      return artifactPermission;
   }

   @Override
   public boolean acceptToObject(Object object) {
      return !(object instanceof IOseeBranch);
   }
}
