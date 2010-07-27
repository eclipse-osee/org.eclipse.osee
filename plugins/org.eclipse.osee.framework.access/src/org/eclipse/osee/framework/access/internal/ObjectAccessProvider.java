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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ObjectAccessProvider implements IAccessProvider {

   private final AccessControlService accessService;

   public ObjectAccessProvider(AccessControlService accessService) {
      this.accessService = accessService;
   }

   @Override
   public void computeAccess(IBasicArtifact<?> userArtifact, Collection<?> objToCheck, AccessData accessData) throws OseeCoreException {
      for (Object object : objToCheck) {
         if (object instanceof Artifact) {
            setArtifactAccessData(userArtifact, (Artifact) object, accessData);
         } else if (object instanceof Branch) {
            setBranchAccessData(userArtifact, (Branch) object, accessData);
         } else {
            throw new OseeStateException("Unhandled object type for access control - " + object);
         }
      }
   }

   private void setArtifactAccessData(IBasicArtifact<?> userArtifact, Artifact artifact, AccessData accessData) throws OseeCoreException {
      setBranchAccessData(userArtifact, artifact.getBranch(), accessData);
      String reason = "Legacy Artifact Permission";
      PermissionEnum userPermission = accessService.getArtifactPermission(userArtifact, artifact);

      if (userPermission == null) {
         reason = "User Permission was null in setArtifactAccessData  - artifact is read only";
         userPermission = PermissionEnum.READ;
      } else if (artifact.isHistorical()) {
         userPermission = PermissionEnum.READ;
         reason = "User Permission set to Read - artifact is historical  - artifact is read only";
      } else if (!artifact.getBranch().isEditable()) {
         userPermission = PermissionEnum.READ;
         reason = "User Permission set to Read - artifact's branch is not editable - artifact is read only";
      }
      //artifact.isDeleted()
      accessData.add(artifact, new AccessDetail<IBasicArtifact<Artifact>>(artifact, userPermission, reason));
   }

   private void setBranchAccessData(IBasicArtifact<?> userArtifact, Branch branch, AccessData accessData) throws OseeCoreException {
      String reason = "Legacy Branch Permission";
      PermissionEnum userPermission = accessService.getBranchPermission(userArtifact, branch);
      accessData.add(branch, new AccessDetail<IOseeBranch>(branch, userPermission, reason));
   }
}
