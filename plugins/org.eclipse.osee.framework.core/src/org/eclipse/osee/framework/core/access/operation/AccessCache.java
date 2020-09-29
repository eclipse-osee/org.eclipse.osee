/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.access.object.AccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Donald G. Dunne
 */
public class AccessCache {

   public final Map<Long, IUserGroup> idToUserGroup = new HashMap<>();

   /**
    * Cache to store branch/artifact permission for user or group<br/>
    * groupId, subjectId (user or group)
    */
   public final DoubleKeyHashMap<Long, AccessObject, PermissionEnum> accessControlListCache = new DoubleKeyHashMap<>();

   /**
    * Cache to store user/group that has access control for artifact/branch<br/>
    * <br/>
    * groupId, subjectId (user or group)
    */
   public final HashCollection<AccessObject, ArtifactId> objectToSubjectCache = new HashCollection<>(true);

   /**
    * Cache to store user/group that belongs to group<br/>
    * groupId, subjectId (user or group)
    */
   public final HashCollection<Long, ArtifactId> subjectToGroupCache = new HashCollection<>(true); // <subjectId, groupId>

   /**
    * Cache to store users belonging to user group<br/>
    * <br/>
    * groupId, subjectId (user or group)
    */
   public final HashCollection<Long, ArtifactId> groupToSubjectsCache = new HashCollection<>(true);

   /**
    * Cache to store entry if lock exists for artifact for user or group<br/>
    * <br/>
    * branch_id, art_id, subject_id (user or group)
    */
   public final CompositeKeyHashMap<BranchId, ArtifactId, ArtifactId> artifactLockCache = new CompositeKeyHashMap<>();
   private ArtifactAclOperations artAclOps;
   private BranchAclOperations brchAclOps;
   private UserGroupOperations userGrpOps;

   private final IAccessControlService accessService;

   public AccessCache(IAccessControlService accessService) {
      this.accessService = accessService;
   }

   public void cacheAccessObject(ArtifactId subjectId, PermissionEnum permission, AccessObject accessObject) {
      accessControlListCache.put(subjectId.getId(), accessObject, permission);
      objectToSubjectCache.put(accessObject, subjectId);
   }

   public void initializeCaches() {
      accessControlListCache.clear();
      objectToSubjectCache.clear();
      subjectToGroupCache.clear();
      groupToSubjectsCache.clear();
      artifactLockCache.clear();
   }

   public void cacheAccessControlData(AccessControlData data) {
      AccessObject accessObject = data.getObject();
      PermissionEnum permission = data.getPermission();
      ArtifactId subject = data.getSubject();

      if (!permission.equals(PermissionEnum.USER_LOCK)) {
         accessControlListCache.put(data.getSubject().getId(), accessObject, permission);
         objectToSubjectCache.put(accessObject, subject);

         accessService.populateGroupMembers(subject);
      }
   }

   public void deCacheAccessControlData(AccessControlData data) {
      if (data == null) {
         throw new IllegalArgumentException("Can not remove a null AccessControlData.");
      }

      AccessObject accessObject = data.getObject();
      ArtifactId subject = data.getSubject();

      accessControlListCache.remove(data.getSubject().getId(), accessObject);
      objectToSubjectCache.removeValue(accessObject, subject);
      Collection<ArtifactId> members = groupToSubjectsCache.getValues(subject.getId());

      if (members != null) {
         for (ArtifactId member : members) {
            subjectToGroupCache.removeValue(member.getId(), subject);
         }
      }
      groupToSubjectsCache.removeValues(subject.getId());
      if (!objectToSubjectCache.containsKey(accessObject)) {
         accessObject.removeFromCache();
      }
   }

   public ArtifactAclOperations getArtAclOps() {
      return artAclOps;
   }

   public BranchAclOperations getBrchAclOps() {
      return brchAclOps;
   }

   public UserGroupOperations getUserGrpOps() {
      return userGrpOps;
   }

   public void setArtAclOps(ArtifactAclOperations artAclOps) {
      this.artAclOps = artAclOps;
   }

   public void setBrchAclOps(BranchAclOperations brchAclOps) {
      this.brchAclOps = brchAclOps;
   }

   public void setUserGrpOps(UserGroupOperations userGrpOps) {
      this.userGrpOps = userGrpOps;
   }

}
