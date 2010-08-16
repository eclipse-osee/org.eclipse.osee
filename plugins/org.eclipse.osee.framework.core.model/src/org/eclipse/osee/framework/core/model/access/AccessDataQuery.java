/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.RelationTypeSide;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class AccessDataQuery {

   private final AccessData accessData;

   public AccessDataQuery(AccessData accessData) {
      this.accessData = accessData;
   }

   @Override
   public String toString() {
      return accessData.toString();
   }

   public void branchMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, PermissionStatus permissionStatus) throws OseeCoreException {
      Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
      checkAccess(branchAccessDetails, branchToMatch, permissionToMatch, permissionStatus);
   }

   public void branchArtifactTypeMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, IArtifactType artifactType, PermissionStatus permissionStatus) throws OseeCoreException {
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
         checkAccess(branchAccessDetails, artifactType, permissionToMatch, permissionStatus);
      }
   }

   public void branchAttributeTypeMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, IAttributeType attributeType, PermissionStatus permissionStatus) throws OseeCoreException {
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
         checkAccess(branchAccessDetails, attributeType, permissionToMatch, permissionStatus);
      }
   }

   public void branchRelationTypeMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, IRelationType relationType, PermissionStatus permissionStatus) throws OseeCoreException {
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
         checkAccess(branchAccessDetails, relationType, permissionToMatch, permissionStatus);
      }
   }

   public void artifactTypeMatches(PermissionEnum permissionToMatch, IBasicArtifact<?> artifact, PermissionStatus permissionStatus) throws OseeCoreException {
      IArtifactType typeToMatch = artifact.getArtifactType();
      IOseeBranch branchToMatch = artifact.getBranch();
      branchArtifactTypeMatches(permissionToMatch, branchToMatch, typeToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
         checkAccess(artifactAccessDetails, typeToMatch, permissionToMatch, permissionStatus);
      }
   }

   public void artifactMatches(PermissionEnum permissionToMatch, IBasicArtifact<?> artifact, PermissionStatus permissionStatus) throws OseeCoreException {
      artifactTypeMatches(permissionToMatch, artifact, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
         checkAccess(artifactAccessDetails, artifact, permissionToMatch, permissionStatus);
      }
   }

   public void attributeTypeMatches(PermissionEnum permissionToMatch, IBasicArtifact<?> artifact, IAttributeType attributeType, PermissionStatus permissionStatus) throws OseeCoreException {
      artifactMatches(permissionToMatch, artifact, permissionStatus);
      if (permissionStatus.matched()) {

         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(artifact.getBranch());
         checkAccess(branchAccessDetails, attributeType, permissionToMatch, permissionStatus);

         if (permissionStatus.matched()) {
            Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
            checkAccess(artifactAccessDetails, attributeType, permissionToMatch, permissionStatus);
         }
      }
   }

   public void relationTypeMatches(RelationTypeSide relationTypeSide, PermissionEnum permissionToMatch, PermissionStatus permissionStatus) throws OseeCoreException {
      Collection<AccessDetail<?>> relationAccessDetails = accessData.getAccess(relationTypeSide);
      checkAccess(relationAccessDetails, relationTypeSide, permissionToMatch, permissionStatus);
   }

   public boolean matchesAll(PermissionEnum permissionToMatch) throws OseeCoreException {
      PermissionStatus permissionStatus = new PermissionStatus();
      for (Object objectKey : accessData.keySet()) {
         if (objectKey instanceof IBasicArtifact<?>) {
            artifactMatches(permissionToMatch, (IBasicArtifact<?>) objectKey, permissionStatus);
         } else if (objectKey instanceof IOseeBranch) {
            branchMatches(permissionToMatch, (IOseeBranch) objectKey, permissionStatus);
         }
         if (!permissionStatus.matched()) {
            break;
         }
      }
      return permissionStatus.matched();
   }

   private <T> void checkAccess(Collection<AccessDetail<?>> accessList, T itemToMatch, PermissionEnum permissionToMatch, PermissionStatus status) {
      for (AccessDetail<?> data : accessList) {
         Object object = data.getAccessObject();
         if (itemToMatch.equals(object)) {
            boolean matches = data.getPermission().matches(permissionToMatch);
            if (!matches) {
               status.setReason(data.getReason());
            }
            status.setMatches(matches);
            break;
         }
      }
   }
}
