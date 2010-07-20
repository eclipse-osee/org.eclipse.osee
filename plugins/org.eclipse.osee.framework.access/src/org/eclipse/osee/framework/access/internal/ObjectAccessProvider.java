/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ObjectAccessProvider implements IAccessProvider {

   private final AccessControlService accessService;

   public ObjectAccessProvider(AccessControlService accessService) {
      this.accessService = accessService;
   }

   @Override
   public void computeAccess(IBasicArtifact<?> userArtifact, Collection<?> objToCheck, AccessData accessData) throws OseeCoreException {
      PermissionEnum userPermission = null;
      PermissionEnum branchPermission = null;
      Branch branch = null;
      Artifact keyArtifact = null;

      for (Object object : objToCheck) {

         boolean isArtifact = object instanceof Artifact;
         if (isArtifact) {
            Artifact artifact = (Artifact) object;
            keyArtifact = artifact;
            branch = artifact.getBranch();

            userPermission = accessService.getArtifactPermission(userArtifact, (Artifact) object);

            if (artifact.isHistorical() && (userPermission == null || userPermission.matches(PermissionEnum.READ))) {
               userPermission = PermissionEnum.READ;
            }
         } else if (object instanceof Branch) {
            branch = (Branch) object;
         } else {
            throw new IllegalStateException("Unhandled object type for access control - " + object);
         }
         branchPermission = accessService.getBranchPermission(userArtifact, branch);

         if (branchPermission == PermissionEnum.DENY || userPermission == null) {
            userPermission = branchPermission;
         }

         if (isArtifact) {
            setAttributeTypeSameAsArtifact(keyArtifact, object, userPermission, accessData);
         }
         accessData.add(keyArtifact, object, userPermission);
      }
   }

   private void setAttributeTypeSameAsArtifact(Artifact artifact, Object object, PermissionEnum permissionEnum, AccessData accessData) throws OseeCoreException {
      for (IAttributeType type : artifact.getAttributeTypes()) {
         accessData.add(artifact, type, permissionEnum);
      }
   }
}
