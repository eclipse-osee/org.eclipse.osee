/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.access.object.AccessObject;
import org.eclipse.osee.framework.core.access.object.ArtifactAccessObject;
import org.eclipse.osee.framework.core.access.object.BranchAccessObject;
import org.eclipse.osee.framework.core.access.operation.AccessCache;
import org.eclipse.osee.framework.core.access.operation.AccessRankOperations;
import org.eclipse.osee.framework.core.access.operation.ArtifactAclOperations;
import org.eclipse.osee.framework.core.access.operation.BranchAclOperations;
import org.eclipse.osee.framework.core.access.operation.ContextIdOperations;
import org.eclipse.osee.framework.core.access.operation.IAccessStoreOperations;
import org.eclipse.osee.framework.core.access.operation.UserGroupOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAccessControlService implements IAccessControlService {

   protected final static Collection<IOseeAccessProvider> oseeAccessProviders = new HashSet<>();
   protected final BranchAclOperations brchAclOps;
   protected UserGroupOperations userGrpOps;
   protected final ContextIdOperations contextIdOps;
   protected ArtifactAclOperations artAclOps;
   protected AccessCache cache;
   protected IAccessStoreOperations storeOps;
   private final AccessRankOperations rankOps;
   private final AtomicBoolean ensurePopulated = new AtomicBoolean(false);
   private final List<ArtifactCheck> artifactChecks = new ArrayList<>();
   private UserService userService;

   public AbstractAccessControlService() {
      cache = new AccessCache(this);
      rankOps = new AccessRankOperations(cache);
      contextIdOps = new ContextIdOperations(this);
      cache.setUserGrpOps(userGrpOps);
      cache.setArtAclOps(artAclOps);
      brchAclOps = new BranchAclOperations(cache, rankOps, this);
      cache.setBrchAclOps(brchAclOps);
   }

   public void setStoreOperations(IAccessStoreOperations storeOps) {
      this.storeOps = storeOps;
      artAclOps = new ArtifactAclOperations(cache, storeOps, this, rankOps);
   }

   public synchronized void addArtifactCheck(ArtifactCheck artifactCheck) {
      artifactChecks.add(artifactCheck);
   }

   public void bindUserService(UserService userService) {
      this.userService = userService;
      userGrpOps = new UserGroupOperations(cache, this, userService);
   }

   public synchronized void addOseeAccessProvider(IOseeAccessProvider provider) {
      AccessControlUtil.errorf("%s - Register: %s", getClass().getSimpleName(), provider.getClass().getSimpleName());
      oseeAccessProviders.add(provider);
      // clear so it will be re-populated before next usage
      artifactChecks.clear();
   }

   ////////////////////////////////////
   // Store Permission
   ////////////////////////////////////

   @Override
   public void persistPermission(AccessControlData data) {
      ensurePopulated();
      storeOps.persistPermission(data);
   }

   @Override
   public void persistPermission(AccessControlData data, boolean recurse) {
      ensurePopulated();
      storeOps.persistPermission(data, recurse);
   }

   @Override
   public void setPermission(ArtifactToken subject, ArtifactToken artifact, PermissionEnum permission) {
      ensurePopulated();
      storeOps.setPermission(subject, artifact, permission);
   }

   @Override
   public void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) {
      ensurePopulated();
      storeOps.removeAccessControlDataIf(removeFromDb, data);
   }

   ////////////////////////////////////
   // Branch Permission
   ////////////////////////////////////

   @Override
   public XResultData isModifyAccessEnabled(ArtifactToken subject, BranchToken branch, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      try {
         IUserGroup oseeAccessGroup = userService.getUserGroup(CoreUserGroups.OseeAccessAdmin);
         boolean isOseeAccessAdmin = oseeAccessGroup.isMember(subject.getId());
         if (isOseeAccessAdmin) {
            rd.logf("User %s DOES have Access Modify rights for Branch %s: Reason [%s]", subject.getName(),
               branch.toStringWithId(), CoreUserGroups.OseeAccessAdmin.getName());
         } else {
            boolean objectHasAccessSet = !getAccessControlList(branch).isEmpty();
            if (objectHasAccessSet) {
               XResultData rd2 = hasBranchPermission(subject, branch, PermissionEnum.FULLACCESS, null);
               if (rd2.isErrors()) {
                  rd.errorf("User %s DOES NOT have Access Modify rights for Branch %s: Reason [No FULL_ACCESS]",
                     subject.getName(), branch.toStringWithId());
               } else {
                  rd.logf("User %s DOES have Access Modify rights for Branch %s: Reason [Branch FULL_ACCESS]",
                     subject.getName(), branch.toStringWithId());
               }
            } else {
               rd.errorf("User %s DOES NOT have Access Modify rights for Branch %s: Reason [Access Not Set]",
                  subject.getName(), branch.toStringWithId());
            }
         }
      } catch (OseeCoreException ex) {
         rd.errorf("User %s DOES NOT have Access Modify rights for Branch %s: Reason Exception [%s]", subject.getName(),
            branch.toStringWithId(), Lib.exceptionToString(ex));
      }
      return rd;
   }

   @Override
   public PermissionEnum getPermission(BranchToken branch) {
      ensurePopulated();
      return brchAclOps.getBranchPermission(getUser(), branch);
   }

   @Override
   public void removePermissions(BranchId branch) {
      ensurePopulated();
      storeOps.removePermissions(branch);
   }

   @Override
   public void setPermission(ArtifactToken subject, BranchId branch, PermissionEnum permission) {
      ensurePopulated();
      storeOps.setPermission(subject, branch, permission);
   }

   @Override
   public XResultData hasBranchPermission(BranchToken branch, PermissionEnum permission, XResultData rd) {
      return hasBranchPermission(getUser(), branch, permission, rd);
   }

   @Override
   public XResultData hasBranchPermission(ArtifactToken subject, BranchToken branch, PermissionEnum permission, XResultData rd) {
      ensurePopulated();
      if (rd == null) {
         rd = new XResultData();
      }
      if (branch.isInvalid()) {
         rd.errorf("Branch %s is InValid\n", branch.toStringWithId());
         return rd;
      }

      // FULL_ACCESS stops rest of baseline branch access checks; don't clutter main rd
      XResultData fullAccessRd =
         brchAclOps.hasBranchAclPermission(subject, branch, PermissionEnum.FULLACCESS, new XResultData());
      boolean baselineBranch = isBaselineBranch(branch);
      if (baselineBranch && fullAccessRd.isSuccess()) {
         rd.merge(fullAccessRd);
         return rd;
      }

      // Has ACL branch permissions
      brchAclOps.hasBranchAclPermission(subject, branch, permission, rd);
      if (rd.isErrors()) {
         return rd;
      }
      return rd;
   }

   @Override
   abstract public boolean isBaselineBranch(BranchToken branch);

   ////////////////////////////////////
   // Artifact Permission
   ////////////////////////////////////

   @Override
   public XResultData isModifyAccessEnabled(ArtifactToken subject, ArtifactToken artifact, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      try {
         IUserGroup oseeAccessGroup = userService.getUserGroup(CoreUserGroups.OseeAccessAdmin);
         boolean isOseeAccessAdmin = oseeAccessGroup.isMember(subject.getId());
         if (isOseeAccessAdmin) {
            rd.logf("User %s DOES have Access Modify rights for Artifact %s: Reason [%s]", subject.getName(),
               artifact.toStringWithId(), CoreUserGroups.OseeAccessAdmin.getName());
         } else {
            boolean artifactAccess = true;
            boolean objectHasAccessSet = !getAccessControlList(artifact).isEmpty();
            if (!objectHasAccessSet) {
               objectHasAccessSet = !getAccessControlList(artifact.getBranch()).isEmpty();
               artifactAccess = false;
            }
            if (objectHasAccessSet) {
               XResultData rd2 =
                  hasArtifactPermission(subject, Collections.singleton(artifact), PermissionEnum.FULLACCESS, null);
               if (rd2.isErrors()) {
                  rd.errorf("User %s DOES NOT have Access Modify rights for Artifact %s: Reason [No FULL_ACCESS]",
                     subject.getName(), artifact.toStringWithId());
               } else {
                  rd.logf("User %s DOES have Access Modify rights for Artifact %s: Reason [%s FULL_ACCESS]",
                     subject.getName(), artifact.toStringWithId(), (artifactAccess ? "Artifact" : "Branch"));
               }
            } else {
               rd.errorf("User %s DOES NOT have Access Modify rights for Artifact %s: Reason [Access Not Set]",
                  subject.getName(), artifact.toStringWithId());
            }
         }
      } catch (OseeCoreException ex) {
         rd.errorf("User %s DOES NOT have Access Modify rights for Artifact %s: Reason Exception [%s]",
            subject.getName(), artifact.toStringWithId(), Lib.exceptionToString(ex));
      }
      return rd;
   }

   @Override
   public PermissionEnum getPermission(ArtifactToken artifact) {
      ensurePopulated();
      return artAclOps.getArtifactPermission(getUser(), artifact);
   }

   @Override
   public XResultData hasArtifactPermission(ArtifactToken artifact, PermissionEnum permission, XResultData rd) {
      return hasArtifactPermission(getUser(), Collections.singleton(artifact), permission, rd);
   }

   @Override
   public XResultData hasArtifactPermission(Collection<? extends ArtifactToken> artifacts, PermissionEnum permission, XResultData rd) {
      return hasArtifactPermission(getUser(), artifacts, permission, rd);
   }

   @Override
   public XResultData hasArtifactPermission(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, PermissionEnum permission, XResultData rd) {
      ensurePopulated();
      if (rd == null) {
         rd = new XResultData();
      }

      for (ArtifactToken artifact : artifacts) {
         AccessResult ard = checkBaseBranchAndArtAcl(subject, permission, rd, artifact);
         if (rd.isErrors() || ard.isFullAccess()) {
            return rd;
         }
      }

      // Only need to check Context Access Control if requesting WRITE
      if (permission.matches(PermissionEnum.WRITE) || permission.matches(PermissionEnum.FULLACCESS)) {
         contextIdOps.hasArtifactContextWriteAccess(subject, artifacts, rd);
      } else {
         rd.logf("Context Id: Subject [%s] DOES have [%s] access for artifacts with permission [%s]\n",
            subject.getName(), permission, "Context Id: Only Checked for Write or Full Access");
      }

      return rd;
   }

   @Override
   public ArtifactToken getSubjectFromLockedObject(ArtifactToken artifact) {
      ensurePopulated();
      return artAclOps.getSubjectFromLockedObject(artifact);
   }

   @Override
   public boolean canUnlockObject(ArtifactToken subject, ArtifactToken artifact) {
      ensurePopulated();
      return artAclOps.canUnlockObject(subject, artifact);
   }

   @Override
   public boolean hasLock(ArtifactToken artifact) {
      ensurePopulated();
      return artAclOps.hasLock(artifact);
   }

   @Override
   public void lockArtifacts(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts) {
      ensurePopulated();
      artAclOps.lockArtifacts(subject, artifacts);
   }

   @Override
   public void unLockArtifacts(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts) {
      ensurePopulated();
      artAclOps.unLockArtifacts(subject, artifacts);
   }

   ////////////////////////////////////
   // Attribute Type Permission
   ////////////////////////////////////

   @Override
   public XResultData hasAttributeTypePermission(Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, PermissionEnum permission, XResultData rd) {
      ArtifactToken currentUser = getUser();
      return hasAttributeTypePermission(currentUser, artifacts, attributeType, permission, rd);
   }

   @Override
   public XResultData hasAttributeTypePermission(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, PermissionEnum permission, XResultData rd) {
      ensurePopulated();
      if (isInDbInit()) {
         rd.log("In DB Init; All permission enabled");
      } else {
         if (subject.isInvalid()) {
            rd.errorf("Subject [%s] is InValid\n", subject.getIdString());
            return rd;
         }
         if (artifacts != null) {

            for (ArtifactToken artifact : artifacts) {

               AccessResult ard = checkBaseBranchAndArtAcl(subject, permission, rd, artifact);
               if (rd.isErrors() || ard.isFullAccess()) {
                  return rd;
               }

            }

            // Only need to check Context Access Control if requesting WRITE
            if (permission.matches(PermissionEnum.WRITE) || permission.matches(PermissionEnum.FULLACCESS)) {
               contextIdOps.hasAttributeTypeContextWriteAccess(subject, artifacts, attributeType, rd);
            } else {
               rd.logf("Context Id: Subject [%s] DOES have [%s] access for artifacts with permission [%s]\n",
                  subject.getName(), permission, "Context Id: Only Checked for Write or Full Access");
            }

         }
      }
      return rd;
   }

   abstract protected boolean isInDbInit();

   private AccessResult checkBaseBranchAndArtAcl(ArtifactToken subject, PermissionEnum permission, XResultData rd, ArtifactToken artifact) {
      ensurePopulated();
      AccessResult result = new AccessResult(rd);
      if (artifact.isInvalid()) {
         rd.errorf("Artifact [%s] is InValid\n", artifact.getIdString());
         return result;
      }

      // FULL_ACCESS stops rest of baseline branch access checks; don't clutter main rd
      XResultData fullAccessRd =
         brchAclOps.hasBranchAclPermission(subject, artifact.getBranch(), PermissionEnum.FULLACCESS, new XResultData());
      boolean baselineBranch = isBaselineBranch(artifact.getBranch());
      if (baselineBranch && fullAccessRd.isSuccess()) {
         rd.merge(fullAccessRd);
         result.setFullAccess(true);
         return result;
      }

      // Has ACL branch permissions
      brchAclOps.hasBranchAclPermission(subject, artifact.getBranch(), permission, rd);
      if (rd.isErrors()) {
         return result;
      }

      // Has ACL artifact permissions
      artAclOps.hasArtifactAclPermission(subject, artifact, permission, rd);
      if (rd.isErrors()) {
         return result;
      }

      return result;
   }

   ////////////////////////////////////
   // Relation Permission
   ////////////////////////////////////

   @Override
   public XResultData hasRelationTypePermission(ArtifactToken artifact, RelationTypeToken relationType, Collection<? extends ArtifactToken> related, PermissionEnum permission, XResultData rd) {
      return hasRelationTypePermission(getUser(), artifact, relationType, related, permission, rd);
   }

   @Override
   public XResultData hasRelationTypePermission(ArtifactToken subject, ArtifactToken artifact, RelationTypeToken relationType, Collection<? extends ArtifactToken> related, PermissionEnum permission, XResultData rd) {
      Conditions.assertNotNull(related, "Related should be collection or empty collection, not null");
      ensurePopulated();
      if (rd == null) {
         rd = new XResultData();
      }

      BranchToken branch = artifact.getBranch();
      if (branch == null || branch.isInvalid()) {
         rd.errorf("Invalid branch for artifact %s", artifact.toStringWithId());
         return rd;
      }

      AccessResult ard = checkBaseBranchAndArtAcl(subject, permission, rd, artifact);
      if (rd.isErrors() || ard.isFullAccess()) {
         return rd;
      }

      artAclOps.hasArtifactAclPermission(artifact, permission, rd);
      for (ArtifactToken relArt : related) {
         artAclOps.hasArtifactAclPermission(relArt, permission, rd);
      }

      // Only need to check Context Access Control if requesting WRITE
      if (permission.matches(PermissionEnum.WRITE) || permission.matches(PermissionEnum.FULLACCESS)) {
         contextIdOps.hasRelationContextWriteAccess(subject, artifact, relationType, related, rd);
      } else {
         rd.logf("Context Id: Subject [%s] DOES have [%s] access for artifacts with permission [%s]\n",
            subject.getName(), permission, "Context Id: Only Checked for Write or Full Access");
      }

      return rd;
   }

   ////////////////////////////////////
   // Supporting Methods
   ////////////////////////////////////

   @Override
   abstract public boolean isInDb(ArtifactToken artifact);

   @Override
   abstract public ArtifactToken getUser();

   @Override
   public void reloadCache() {
      ensurePopulated.set(false);
   }

   @Override
   public synchronized void ensurePopulated() {
      if (ensurePopulated.compareAndSet(false, true)) {
         cache.initializeCaches();
         populateArtifactAccessControlList();
         populateBranchAccessControlList();
      }
   }

   abstract public void populateBranchAccessControlList();

   abstract public void populateArtifactAccessControlList();

   @Override
   abstract public void populateGroupMembers(ArtifactId groupId);

   @Override
   public void clearCaches() {
      cache.initializeCaches();
      ensurePopulated.set(false);
   }

   @Override
   public List<AccessControlData> getAccessControlList(Object object) {
      List<AccessControlData> datas = new LinkedList<>();
      AccessObject accessObject = null;

      try {
         accessObject = AccessObject.valueOf(object);

         if (accessObject == null) {
            return datas;
         }

         datas = generateAccessControlList(accessObject);

      } catch (Exception ex) {
         OseeLog.log(AbstractAccessControlService.class, Level.SEVERE, ex);
      }
      return datas;
   }

   public List<AccessControlData> generateAccessControlList(AccessObject accessObject) {
      ensurePopulated();
      List<AccessControlData> datas = new LinkedList<>();

      Collection<ArtifactId> subjects = cache.objectToSubjectCache.getValues(accessObject);
      if (subjects == null) {
         return datas;
      }

      for (ArtifactId subjectId : subjects) {
         ArtifactToken subject = getArtifactFromId(subjectId, COMMON);
         PermissionEnum permissionEnum = cache.accessControlListCache.get(subjectId.getId(), accessObject);
         AccessControlData accessControlData =
            new AccessControlData(subject, accessObject, permissionEnum, false, false);
         if (accessObject instanceof ArtifactAccessObject) {
            accessControlData.setArtifactPermission(permissionEnum);
            accessControlData.setBranchPermission(brchAclOps.getBranchPermission(subject, accessObject.getBranch()));
         } else if (accessObject instanceof BranchAccessObject) {
            accessControlData.setBranchPermission(brchAclOps.getBranchPermission(subject, accessObject.getBranch()));
         }
         datas.add(accessControlData);
      }

      return datas;
   }

   ////////////////////////////////////
   // Artifact Checks
   ////////////////////////////////////

   abstract public ArtifactToken getArtifactFromId(ArtifactId subjectId, BranchToken common);

   public Collection<ArtifactCheck> getArtifactChecks() {
      if (artifactChecks.isEmpty()) {
         for (IOseeAccessProvider provider : oseeAccessProviders) {
            artifactChecks.addAll(provider.getArtifactChecks());
         }
      }
      return artifactChecks;
   }

   @Override
   public XResultData isDeleteable(Collection<? extends ArtifactToken> artifacts, XResultData results) {
      for (ArtifactCheck check : getArtifactChecks()) {
         check.isDeleteable(artifacts, results);
      }
      return results;
   }

   @Override
   public XResultData isRenamable(Collection<? extends ArtifactToken> artifacts, XResultData results) {
      for (ArtifactCheck check : getArtifactChecks()) {
         check.isRenamable(artifacts, results);
      }
      return results;
   }

   @Override
   public XResultData isDeleteableRelation(ArtifactToken artifact, RelationTypeToken relationType, XResultData results) {
      for (ArtifactCheck check : getArtifactChecks()) {
         check.isDeleteableRelation(artifact, relationType, results);
      }
      return results;
   }

   @Override
   public Collection<IOseeAccessProvider> getOseeAccessProviders() {
      return oseeAccessProviders;
   }
}