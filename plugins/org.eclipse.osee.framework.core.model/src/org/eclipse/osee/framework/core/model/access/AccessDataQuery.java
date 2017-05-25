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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

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

   public void branchMatches(PermissionEnum permissionToMatch, BranchId branchToMatch, PermissionStatus permissionStatus) throws OseeCoreException {
      Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
      setTypePermissionStatus(permissionStatus, permissionToMatch, branchAccessDetails, branchToMatch);
   }

   public void branchArtifactTypeMatches(PermissionEnum permissionToMatch, BranchId branchToMatch, ArtifactTypeId artifactType, PermissionStatus permissionStatus) throws OseeCoreException {
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
         setTypePermissionStatus(permissionStatus, permissionToMatch, branchAccessDetails, artifactType);
      }
   }

   public void artifactTypeMatches(PermissionEnum permissionToMatch, ArtifactToken artifact, PermissionStatus permissionStatus) throws OseeCoreException {
      IArtifactType typeToMatch = artifact.getArtifactType();
      BranchId branchToMatch = artifact.getBranch();
      branchArtifactTypeMatches(permissionToMatch, branchToMatch, typeToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
         setTypePermissionStatus(permissionStatus, permissionToMatch, artifactAccessDetails, artifact);
      }
   }

   public void artifactMatches(PermissionEnum permissionToMatch, ArtifactToken artifact, PermissionStatus permissionStatus) throws OseeCoreException {
      BranchId branchToMatch = artifact.getBranch();
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> mainList = accessData.getAccess(artifact);
         AccessDetail<?> mostSpecific = getNetAccess(mainList, new AccessFilter() {

            @Override
            public boolean accept(AccessDetail<?> access) {
               return access.getAccessObject().equals(artifact) || access.getAccessObject().equals(
                  artifact.getArtifactType());
            }
         });

         setPermissionStatus(permissionStatus, permissionToMatch, mostSpecific);
      }
   }

   private class ObjectFilter implements AccessFilter {
      Object toFilter;

      ObjectFilter(Object toFilter) {
         this.toFilter = toFilter;
      }

      @Override
      public boolean accept(AccessDetail<?> access) {
         return access.getAccessObject().equals(toFilter);
      }
   }

   private class RelationTypeSideFilter implements AccessFilter {
      RelationTypeSide toFilter;

      RelationTypeSideFilter(RelationTypeSide toFilter) {
         this.toFilter = toFilter;
      }

      @Override
      public boolean accept(AccessDetail<?> access) {
         if (access.getAccessObject() instanceof RelationTypeSide) {
            RelationTypeSide toCheck = (RelationTypeSide) access.getAccessObject();
            return toCheck.getSide().equals(toFilter.getSide()) && toCheck.getGuid().equals(toFilter.getGuid());
         }
         return false;
      }
   }

   public void attributeTypeMatches(PermissionEnum permissionToMatch, ArtifactToken artifact, final AttributeTypeId attributeType, PermissionStatus permissionStatus) throws OseeCoreException {
      artifactMatches(permissionToMatch, artifact, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(artifact.getBranch());
         AccessFilter filter = new ObjectFilter(attributeType);
         AccessDetail<?> branchPermission = getNetAccess(branchAccessDetails, filter);
         setPermissionStatus(permissionStatus, permissionToMatch, branchPermission);

         if (permissionStatus.matched()) {
            Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
            AccessDetail<?> attributePermission = getNetAccess(artifactAccessDetails, filter);
            setPermissionStatus(permissionStatus, permissionToMatch, attributePermission);
         }
      }
   }

   public void relationTypeMatches(PermissionEnum permissionToMatch, ArtifactToken artifact, RelationTypeSide relationTypeSide, PermissionStatus permissionStatus) throws OseeCoreException {
      BranchId branchToMatch = artifact.getBranch();
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matched()) {
         Collection<AccessDetail<?>> relationAccessDetails = accessData.getAccess(artifact);
         AccessFilter filter = new RelationTypeSideFilter(relationTypeSide);
         AccessDetail<?> relationDetail = getNetAccess(relationAccessDetails, filter);
         setPermissionStatus(permissionStatus, permissionToMatch, relationDetail);
      }
   }

   public boolean matchesAll(PermissionEnum permissionToMatch) throws OseeCoreException {
      PermissionStatus permissionStatus = new PermissionStatus();
      for (Object objectKey : accessData.keySet()) {
         if (objectKey instanceof ArtifactToken) {
            artifactMatches(permissionToMatch, (ArtifactToken) objectKey, permissionStatus);
         } else if (objectKey instanceof BranchId) {
            branchMatches(permissionToMatch, (BranchId) objectKey, permissionStatus);
         }
         if (!permissionStatus.matched()) {
            break;
         }
      }
      return permissionStatus.matched();
   }

   private <T> void setTypePermissionStatus(PermissionStatus permissionStatus, PermissionEnum permissionToMatch, Collection<AccessDetail<?>> accessDetails, T toMatch) {
      AccessFilter filter = new ObjectFilter(toMatch);
      AccessDetail<?> accessDetail = getNetAccess(accessDetails, filter);
      setPermissionStatus(permissionStatus, permissionToMatch, accessDetail);
   }

   private void setPermissionStatus(PermissionStatus status, PermissionEnum permissionToMatch, AccessDetail<?> mostSpecific) {
      if (mostSpecific == null) {
         return;
      }
      if (mostSpecific.getPermission() == null) {
         status.setMatches(false);
      } else {
         boolean matches = mostSpecific.getPermission().matches(permissionToMatch);
         if (!matches) {
            status.setReason(mostSpecific.getReason());
         }
         status.setMatches(matches);
      }
   }

   private interface AccessFilter {

      public boolean accept(AccessDetail<?> access);

   }

   private AccessDetail<?> getNetAccess(Collection<AccessDetail<?>> toFilter, AccessFilter filter) {
      AccessDetail<?> mostSpecific = null;
      for (AccessDetail<?> entry : toFilter) {
         if (filter.accept(entry)) {
            mostSpecific = AccessDetail.resolveAccess(mostSpecific, entry);
         }
      }
      return mostSpecific;
   }
}
