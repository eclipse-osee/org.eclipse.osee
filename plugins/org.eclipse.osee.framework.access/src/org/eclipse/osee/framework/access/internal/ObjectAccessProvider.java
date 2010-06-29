/*
 * Created on Jun 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class ObjectAccessProvider implements IAccessProvider {

   @Override
   public void computeAccess(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artsToCheck, AccessData accessData) throws OseeCoreException {

//      PermissionEnum branchPermission = null;
//      PermissionEnum permission = null;
//      Branch branch = null;
//
//      if (object instanceof Artifact) {
//         Artifact artifact = (Artifact) object;
//         branch = artifact.getBranch();
//         userPermission = accessService.getArtifactPermission(userArtifact, artifact, permission);
//      } else if (object instanceof Branch) {
//         branch = (Branch) object;
//      } else {
//         throw new OseeStateException("Unhandled object type for access control - " + object);
//      }
//
//      PermissionEnum userPermission = null;
//      PermissionEnum branchPermission = accessService.getBranchPermission(userArtifact, branch, permission);
//      if (branchPermission == PermissionEnum.DENY || userPermission == null) {
//         userPermission = branchPermission;
//      }
//
//      userPermission.matches();
   }
}
