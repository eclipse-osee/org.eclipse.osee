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
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Roberto E. Escobar
 */
public class AccessPolicyImpl implements AccessPolicy {

   public IAccessControlService service;

   public void setAccessControlService(IAccessControlService service) {
      this.service = service;
   }

   private User getCurrentUser() throws OseeCoreException {
      return UserManager.getUser();
   }

   private IAccessControlService getAccessService() {
      return service;
   }

   private boolean printErrorMessage(User user, Collection<?> objects, PermissionStatus permissionStatus, Level level) {
      boolean notMatched = !permissionStatus.matched();

      if (notMatched) {
         String objectString = org.eclipse.osee.framework.jdk.core.util.Collections.toString("; ", objects);
         String reasonString = permissionStatus.getReason();
         if (Strings.isValid(reasonString)) {
            reasonString = String.format("\n reason:[%s]", reasonString);
         } else {
            reasonString = "";
         }
         OseeLog.logf(Activator.class, level,
            "Access Denied - [%s] does not have valid permission to edit\n item(s) : [%s]%s", user, objectString,
            reasonString);
      }
      return notMatched;
   }

   @Override
   public void removePermissions(BranchId branch) throws OseeCoreException {
      getAccessService().removePermissions(branch);
   }

   @Override
   public boolean isReadOnly(Artifact artifact) {
      try {
         return artifact.isDeleted() || artifact.isHistorical() || !BranchManager.isEditable(
            artifact.getBranch()) || !getAccessService().hasPermission(artifact, PermissionEnum.WRITE);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return true;
      }
   }

   @Override
   public PermissionStatus hasBranchPermission(BranchId branch, PermissionEnum permission, Level level) throws OseeCoreException {
      User currentUser = getCurrentUser();
      AccessDataQuery query = getAccessService().getAccessData(currentUser, java.util.Collections.singleton(branch));
      PermissionStatus permissionStatus = null;
      if (!OseeClientProperties.isInDbInit()) {
         permissionStatus = new PermissionStatus();
         query.branchMatches(permission, branch, permissionStatus);
         printErrorMessage(currentUser, java.util.Collections.singleton(branch), permissionStatus, level);
      } else {
         permissionStatus = new PermissionStatus(true, "In DB Init; All permission enabled");
      }
      return permissionStatus;
   }

   /**
    * @param level - A level of OseeLevel.SEVERE_POPUP will cause an error dialog to be displayed to the user. All
    * others will write to the log.
    */
   @Override
   public PermissionStatus hasAttributeTypePermission(Collection<? extends ArtifactToken> artifacts, AttributeTypeId attributeType, PermissionEnum permission, Level level) throws OseeCoreException {
      User currentUser = getCurrentUser();
      AccessDataQuery query = getAccessService().getAccessData(currentUser, artifacts);

      PermissionStatus permissionStatus = null;
      if (!OseeClientProperties.isInDbInit()) {
         permissionStatus = new PermissionStatus();
         if (artifacts != null) {
            for (ArtifactToken artifact : artifacts) {
               query.attributeTypeMatches(permission, artifact, attributeType, permissionStatus);

               if (printErrorMessage(currentUser, artifacts, permissionStatus, level)) {
                  break;
               }
            }
         }
      } else {
         permissionStatus = new PermissionStatus(true, "In DB Init; All permission enabled");
      }

      return permissionStatus;
   }

   @Override
   public PermissionStatus hasArtifactPermission(Collection<Artifact> artifacts, PermissionEnum permission, Level level) throws OseeCoreException {
      User currentUser = getCurrentUser();
      AccessDataQuery query = getAccessService().getAccessData(currentUser, artifacts);

      PermissionStatus permissionStatus = null;
      if (!OseeClientProperties.isInDbInit()) {
         permissionStatus = new PermissionStatus();
         if (artifacts != null) {
            for (Artifact artifact : artifacts) {
               if (artifact.isInDb()) {
                  query.artifactMatches(permission, artifact, permissionStatus);
               }
               if (printErrorMessage(currentUser, artifacts, permissionStatus, level)) {
                  break;
               }
            }
         }
      } else {
         permissionStatus = new PermissionStatus(true, "In DB Init; All permission enabled");
      }

      return permissionStatus;
   }

   @Override
   public PermissionStatus hasArtifactTypePermission(BranchId branch, Collection<? extends ArtifactTypeId> artifactTypes, PermissionEnum permission, Level level) throws OseeCoreException {
      User currentUser = getCurrentUser();
      AccessDataQuery query = getAccessService().getAccessData(currentUser, java.util.Collections.singleton(branch));

      PermissionStatus permissionStatus = null;
      if (!OseeClientProperties.isInDbInit()) {
         permissionStatus = new PermissionStatus();
         if (artifactTypes != null) {
            for (ArtifactTypeId artifactType : artifactTypes) {
               query.branchArtifactTypeMatches(permission, branch, artifactType, permissionStatus);

               if (printErrorMessage(currentUser, artifactTypes, permissionStatus, level)) {
                  break;
               }
            }
         }
      } else {
         permissionStatus = new PermissionStatus(true, "In DB Init; All permission enabled");
      }

      return permissionStatus;
   }

   @Override
   public PermissionStatus canRelationBeModified(Artifact subject, Collection<Artifact> toBeRelated, RelationTypeSide relationTypeSide, Level level) throws OseeCoreException {
      PermissionStatus subjectPermission = canRelationBeModifiedHelper(subject, null, relationTypeSide, level);
      if (subjectPermission.matched() && toBeRelated != null && !toBeRelated.isEmpty()) {
         for (Artifact art : toBeRelated) {
            RelationSide otherSide = relationTypeSide.getSide().oppositeSide();
            PermissionStatus toBeRelatedPermission =
               canRelationBeModifiedHelper(art, null, new RelationTypeSide(relationTypeSide, otherSide), level);
            if (!toBeRelatedPermission.matched()) {
               return toBeRelatedPermission;
            }
         }
      }
      return subjectPermission;
   }

   private PermissionStatus canRelationBeModifiedHelper(Artifact subject, Collection<Artifact> toBeRelated, RelationTypeSide relationTypeSide, Level level) throws OseeCoreException {
      PermissionStatus status = hasArtifactRelationPermission(java.util.Collections.singleton(subject),
         java.util.Collections.singleton(relationTypeSide), PermissionEnum.WRITE, level);

      if (!status.matched()) {
         ArrayList<Artifact> artifacts = new ArrayList<>();
         artifacts.add(subject);
         if (toBeRelated != null) {
            artifacts.addAll(toBeRelated);
         }
         status = hasArtifactPermission(artifacts, PermissionEnum.WRITE, level);
      }
      return status;
   }

   private PermissionStatus hasArtifactRelationPermission(Collection<? extends ArtifactToken> artifacts, Collection<? extends RelationTypeSide> relationTypeSides, PermissionEnum permission, Level level) throws OseeCoreException {
      AccessDataQuery query = getAccessService().getAccessData(getCurrentUser(), artifacts);

      PermissionStatus permissionStatus = null;
      if (!OseeClientProperties.isInDbInit()) {
         permissionStatus = new PermissionStatus();
         if (!OseeClientProperties.isInDbInit()) {
            permissionStatus = new PermissionStatus();
            for (ArtifactToken artifact : artifacts) {
               for (RelationTypeSide relationTypeSide : relationTypeSides) {
                  query.relationTypeMatches(permission, artifact, relationTypeSide, permissionStatus);
               }
            }
         } else {
            permissionStatus = new PermissionStatus(true, "In DB Init; All permission enabled");
         }
      } else {
         permissionStatus = new PermissionStatus(true, "In DB Init; All permission enabled");
      }

      return permissionStatus;
   }
}
