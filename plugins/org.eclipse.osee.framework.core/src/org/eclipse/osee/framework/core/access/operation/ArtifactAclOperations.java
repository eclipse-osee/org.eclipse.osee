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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.access.AccessArtifactLockTopicEvent;
import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.access.object.AccessObject;
import org.eclipse.osee.framework.core.access.object.ArtifactAccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class ArtifactAclOperations {

   private final AccessCache cache;
   private final IAccessStoreOperations storeOps;
   private final AccessRankOperations rankOps;
   private final IAccessControlService accessService;

   public ArtifactAclOperations(AccessCache cache, IAccessStoreOperations storeOps, IAccessControlService accessService, AccessRankOperations rankOps) {
      this.cache = cache;
      this.storeOps = storeOps;
      this.accessService = accessService;
      this.rankOps = rankOps;
   }

   public XResultData hasArtifactAclPermission(ArtifactToken artifact, PermissionEnum permission, XResultData rd) {
      return hasArtifactAclPermission(accessService.getUser(), artifact, permission, rd);
   }

   public XResultData hasArtifactAclPermission(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, PermissionEnum permission, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      for (ArtifactToken art : artifacts) {
         hasArtifactAclPermission(subject, art, permission, rd);
      }
      return rd;
   }

   public XResultData hasArtifactAclPermission(ArtifactToken subject, ArtifactToken artifact, PermissionEnum permission, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }

      // The artifact is new and has not been persisted.
      if (!accessService.isInDb(artifact)) {
         rd.logf("Artifact ACL: Subject [%s] DOES have [%s] access for artifact %s with permission [%s]\n",
            subject.getName(), permission, artifact.toStringWithId(), "Artifact Not In Db");
         return rd;
      }

      BranchToken branch = artifact.getBranch();
      if (cache.artifactLockCache.containsKey(branch, artifact)) {
         ArtifactId lockOwnerId = cache.artifactLockCache.get(branch, artifact);
         if (subject.equals(lockOwnerId)) {
            rd.logf("Artifact ACL: Subject [%s] DOES have [%s] access for branch %s; permission [%s]\n",
               subject.getName(), permission, branch.toStringWithId(), PermissionEnum.USER_LOCK);
            return rd;
         } else {
            if (permission == PermissionEnum.WRITE || permission == PermissionEnum.FULLACCESS) {
               rd.errorf("Artifact ACL: Subject [%s] DOES NOT have [%s] access for branch %s; permission [%s]\n",
                  subject.getName(), permission, branch.toStringWithId(), PermissionEnum.USER_LOCK);
               return rd;
            } else {
               rd.logf("Artifact ACL: Subject [%s] DOES have [%s] access for branch %s; permission [%s]\n",
                  subject.getName(), permission, branch.toStringWithId(), PermissionEnum.USER_LOCK);
               return rd;
            }
         }
      }

      rd.logf("Artifact ACL: Subject [%s] DOES have [%s] access for artifact %s with permission [%s]\n",
         subject.getName(), permission, artifact.toStringWithId(), PermissionEnum.FULLACCESS);
      return rd;
   }

   public PermissionEnum getArtifactPermission(ArtifactToken subject, ArtifactToken artifact) {
      PermissionEnum userPermission = PermissionEnum.FULLACCESS;
      AccessObject accessObject = null;

      // The artifact is new and has not been persisted.
      if (!accessService.isInDb(artifact)) {
         return PermissionEnum.FULLACCESS;
      }

      BranchId branch = artifact.getBranch();

      accessObject = ArtifactAccessObject.valueOf(artifact);

      if (cache.artifactLockCache.containsKey(branch, artifact)) {

         ArtifactId lockOwnerId = cache.artifactLockCache.get(branch, artifact);
         // this object is locked under a different branch
         if (subject.notEqual(lockOwnerId)) {
            userPermission = PermissionEnum.USER_LOCK;
         }
      }

      if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else {
         userPermission = rankOps.acquirePermissionRank(subject, accessObject);
      }
      return userPermission;
   }

   public void populateArtifactAccessControlListEntry(ArtifactId subjectId, ArtifactToken artifact, PermissionEnum permission, ArtifactTypeToken subjectArtifactType) {
      if (permission != null) {
         // Check for lock by User
         if (permission.equals(PermissionEnum.USER_LOCK)) {
            cache.artifactLockCache.put(artifact.getBranch(), artifact, subjectId);
         } else {
            cache.cacheAccessObject(subjectId, permission, ArtifactAccessObject.valueOf(artifact));

            if (subjectArtifactType.inheritsFrom(CoreArtifactTypes.UserGroup)) {
               accessService.populateGroupMembers(subjectId);
            }
         }
      }
   }

   public void lockArtifacts(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts) {
      Conditions.checkNotNull(subject, "subject");
      Conditions.checkNotNullOrEmpty(artifacts, "artifacts");
      AccessArtifactLockTopicEvent event = new AccessArtifactLockTopicEvent();
      event.setBranch(artifacts.iterator().next().getBranch());
      event.setLocked(true);
      Set<ArtifactToken> lockedArts = new HashSet<>();
      for (ArtifactToken artifact : artifacts) {
         BranchId objectBranch = artifact.getBranch();

         if (!cache.artifactLockCache.containsKey(objectBranch, artifact)) {
            AccessObject accessObject = AccessObject.valueOf(artifact);
            AccessControlData data = new AccessControlData(subject, accessObject, PermissionEnum.USER_LOCK, true);
            storeOps.persistPermission(data);
            cache.artifactLockCache.put(objectBranch, artifact, subject);
            event.addArtifact(artifact);
            lockedArts.add(artifact);
         }
      }
      accessService.kickAccessTopicEvent(event);
   }

   public void unLockArtifacts(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts) {
      AccessArtifactLockTopicEvent event = new AccessArtifactLockTopicEvent();
      event.setBranch(artifacts.iterator().next().getBranch());
      event.setLocked(false);
      Set<ArtifactToken> lockedArts = new HashSet<>();
      for (ArtifactToken artifact : artifacts) {
         BranchId branch = artifact.getBranch();

         boolean inCache = cache.artifactLockCache.containsKey(branch, artifact);
         boolean canUnlockObject = canUnlockObject(subject, artifact);
         if (inCache && canUnlockObject) {
            AccessObject accessObject = AccessObject.valueOf(artifact);
            storeOps.removeAccessControlDataIf(true,
               new AccessControlData(subject, accessObject, PermissionEnum.USER_LOCK, false));
            cache.artifactLockCache.removeAndGet(branch, artifact);
            event.addArtifact(artifact);
            lockedArts.add(artifact);
         }
      }
      accessService.kickAccessTopicEvent(event);
   }

   public boolean hasLock(ArtifactToken artifact) {
      if (!accessService.isInDb(artifact)) {
         return false;
      }
      return cache.artifactLockCache.containsKey(artifact.getBranch(), artifact);
   }

   public boolean canUnlockObject(ArtifactToken subject, ArtifactToken artifact) {
      ArtifactId subjectId = cache.artifactLockCache.get(artifact.getBranch(), artifact);
      return subject.equals(subjectId);
   }

   public ArtifactToken getSubjectFromLockedObject(ArtifactToken artifact) {
      ArtifactToken subject = ArtifactToken.SENTINEL;
      ArtifactId subjectArtId = cache.artifactLockCache.get(artifact.getBranch(), artifact);
      if (subjectArtId != null) {
         subject = accessService.getUserByArtId(subjectArtId);
      }
      return subject;
   }

}