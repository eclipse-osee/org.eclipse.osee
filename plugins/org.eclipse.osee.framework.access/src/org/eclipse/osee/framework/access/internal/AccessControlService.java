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

package org.eclipse.osee.framework.access.internal;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.access.internal.data.ArtifactAccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event2.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.osgi.framework.Bundle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides access control for OSEE. <REM2>
 * 
 * @author Jeff C. Phillips
 */

public class AccessControlService implements IAccessControlService {
   private static final String ACCESS_POINT_ID = "osee.access.point";

   private final String INSERT_INTO_ARTIFACT_ACL =
      "INSERT INTO OSEE_ARTIFACT_ACL (art_id, permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?, ?)";
   private final String INSERT_INTO_BRANCH_ACL =
      "INSERT INTO OSEE_BRANCH_ACL (permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?)";

   private final String UPDATE_ARTIFACT_ACL =
      "UPDATE OSEE_ARTIFACT_ACL SET permission_id = ? WHERE privilege_entity_id =? AND art_id = ? AND branch_id = ?";
   private final String UPDATE_BRANCH_ACL =
      "UPDATE OSEE_BRANCH_ACL SET permission_id = ? WHERE privilege_entity_id =? AND branch_id = ?";

   private final String GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST =
      "SELECT aac1.*, art1.art_type_id FROM osee_artifact art1, osee_artifact_acl aac1 WHERE art1.art_id = aac1.privilege_entity_id";
   private final String GET_ALL_BRANCH_ACCESS_CONTROL_LIST =
      "SELECT bac1.*, art1.art_type_id FROM osee_artifact art1, osee_branch_acl bac1 WHERE art1.art_id = bac1.privilege_entity_id";

   private final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   private final String DELETE_BRANCH_ACL_FROM_BRANCH = "DELETE FROM OSEE_BRANCH_ACL WHERE branch_id =?";

   private final String USER_GROUP_MEMBERS =
      "SELECT b_art_id FROM osee_relation_link WHERE a_art_id =? AND rel_link_type_id =? ORDER BY b_art_id";

   private DoubleKeyHashMap<Integer, AccessObject, PermissionEnum> accessControlListCache;
   private HashCollection<AccessObject, Integer> objectToSubjectCache; // <subjectId, groupId>
   private HashCollection<Integer, Integer> subjectToGroupCache; // <groupId, subjectId>
   private HashCollection<Integer, Integer> groupToSubjectsCache; // <artId, branchId>
   private Map<Integer, Integer> objectToBranchLockCache; // object, subject
   private Map<Integer, Integer> lockedObjectToSubject; // subject, permission
   private HashCollection<Integer, PermissionEnum> subjectToPermissionCache;

   private final IOseeCachingService cachingService;
   private final IOseeDatabaseService databaseService;

   public AccessControlService(IOseeDatabaseService databaseService, IOseeCachingService cachingService) {
      super();
      this.databaseService = databaseService;
      this.cachingService = cachingService;
      try {
         reloadCache();
      } catch (OseeCoreException ex) {
         OseeLog.log(AccessControlService.class, Level.SEVERE, ex);
      }
   }

   private ArtifactTypeCache getArtifactTypeCache() {
      return cachingService.getArtifactTypeCache();
   }

   private RelationTypeCache getRelationTypeCache() {
      return cachingService.getRelationTypeCache();
   }

   private BranchCache getBranchCache() {
      return cachingService.getBranchCache();
   }

   private IOseeDatabaseService getDatabaseService() {
      return databaseService;
   }

   public synchronized void reloadCache() throws OseeCoreException {
      initializeCaches();
      populateAccessControlLists();
   }

   private void initializeCaches() {
      accessControlListCache = new DoubleKeyHashMap<Integer, AccessObject, PermissionEnum>();
      objectToSubjectCache = new HashCollection<AccessObject, Integer>();
      subjectToGroupCache = new HashCollection<Integer, Integer>();
      groupToSubjectsCache = new HashCollection<Integer, Integer>();
      objectToBranchLockCache = new HashMap<Integer, Integer>();
      lockedObjectToSubject = new HashMap<Integer, Integer>();
      subjectToPermissionCache = new HashCollection<Integer, PermissionEnum>();
   }

   private void populateAccessControlLists() throws OseeCoreException {
      populateArtifactAccessControlList();
      populateBranchAccessControlList();
   }

   private void populateBranchAccessControlList() throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(GET_ALL_BRANCH_ACCESS_CONTROL_LIST);
         while (chStmt.next()) {
            Integer subjectId = chStmt.getInt("privilege_entity_id");
            Integer branchId = chStmt.getInt("branch_id");
            int subjectArtifactTypeId = chStmt.getInt("art_type_id");
            PermissionEnum permission = PermissionEnum.getPermission(chStmt.getInt("permission_id"));
            BranchAccessObject branchAccessObject = BranchAccessObject.getBranchAccessObject(branchId);

            accessControlListCache.put(subjectId, branchAccessObject, permission);
            objectToSubjectCache.put(branchAccessObject, subjectId);

            ArtifactType subjectArtifactType = getArtifactTypeCache().getById(subjectArtifactTypeId);
            ArtifactType toCheck = getArtifactTypeCache().get(CoreArtifactTypes.UserGroup);
            if (subjectArtifactType.inheritsFrom(toCheck)) {
               populateGroupMembers(subjectId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void populateArtifactAccessControlList() throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST);

         while (chStmt.next()) {
            Integer subjectId = chStmt.getInt("privilege_entity_id");
            Integer objectId = chStmt.getInt("art_id");
            Integer branchId = chStmt.getInt("branch_id");
            int subjectArtifactTypeId = chStmt.getInt("art_type_id");
            PermissionEnum permission = PermissionEnum.getPermission(chStmt.getInt("permission_id"));

            if (permission.equals(PermissionEnum.LOCK)) {
               objectToBranchLockCache.put(objectId, branchId);
               lockedObjectToSubject.put(objectId, subjectId);
            } else {
               AccessObject accessObject = ArtifactAccessObject.getArtifactAccessObject(objectId, branchId);
               cacheAccessObject(objectId, subjectId, permission, accessObject);

               ArtifactType subjectArtifactType = getArtifactTypeCache().getById(subjectArtifactTypeId);
               ArtifactType toCheck = getArtifactTypeCache().get(CoreArtifactTypes.UserGroup);
               if (subjectArtifactType.inheritsFrom(toCheck)) {
                  populateGroupMembers(subjectId);
               }
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void populateGroupMembers(Integer groupId) throws OseeCoreException {
      if (!groupToSubjectsCache.containsKey(groupId)) {
         Integer groupMember;

         IOseeStatement chStmt = getDatabaseService().getStatement();
         try {
            RelationType relationType = getRelationTypeCache().get(CoreRelationTypes.Users_User);
            chStmt.runPreparedQuery(USER_GROUP_MEMBERS, groupId, relationType.getId());

            // get group members and populate subjectToGroupCache
            while (chStmt.next()) {
               groupMember = chStmt.getInt("b_art_id");
               subjectToGroupCache.put(groupMember, groupId);
               groupToSubjectsCache.put(groupId, groupMember);
            }
         } finally {
            chStmt.close();
         }
      }
   }

   public boolean checkSubjectPermission(Artifact subject, PermissionEnum permission) {
      boolean isValid = false;

      if (subjectToPermissionCache.containsKey(subject.getArtId())) {
         for (PermissionEnum subjectPermission : subjectToPermissionCache.getValues(subject.getArtId())) {
            if (subjectPermission.getRank() >= permission.getRank()) {
               isValid = true;
            }
         }
      }
      return isValid;
   }

   public boolean hasPermission(Collection<?> objectList, PermissionEnum permission) throws OseeCoreException {
      boolean isValid = true;

      if (objectList.isEmpty()) {
         isValid = false;
      }

      for (Object object : objectList) {
         isValid &= hasPermission(object, permission);
      }
      return isValid;
   }

   @Override
   public boolean hasPermission(Object object, PermissionEnum permission) throws OseeCoreException {
      boolean result = true;
      if (!DbUtil.isDbInit()) {
         Collection<?> objectsToCheck = null;
         if (object instanceof Collection<?>) {
            objectsToCheck = (Collection<?>) object;
         } else if (object instanceof Array) {
            objectsToCheck = Arrays.asList((Array) object);
         } else {
            objectsToCheck = Collections.singletonList(object);
         }
         IBasicArtifact<?> subject = UserManager.getUser();
         AccessDataQuery accessQuery = getAccessData(subject, objectsToCheck);
         result = accessQuery.matchesAll(permission);
      }
      return result;
   }

   @Override
   public AccessDataQuery getAccessData(IBasicArtifact<?> userArtifact, Collection<?> objectsToCheck) throws OseeCoreException {
      ILifecycleService service = getLifecycleService();
      AccessData accessData = new AccessData();
      AbstractLifecycleVisitor<?> visitor = new AccessProviderVisitor(userArtifact, objectsToCheck, accessData);
      IStatus status = service.dispatch(new NullProgressMonitor(), visitor, ACCESS_POINT_ID);
      try {
         Operations.checkForErrorStatus(status);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return new AccessDataQuery(accessData);
   }

   private ILifecycleService getLifecycleService() throws OseeCoreException {
      Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
      ServiceTracker tracker = new ServiceTracker(bundle.getBundleContext(), ILifecycleService.class.getName(), null);
      tracker.open(true);
      Object serviceObject = tracker.getService();
      tracker.close();
      if (serviceObject instanceof ILifecycleService) {
         return (ILifecycleService) serviceObject;
      } else {
         throw new OseeStateException("Lifecycle service is unavailable");
      }
   }

   public PermissionEnum getBranchPermission(IBasicArtifact<?> subject, Branch branch) {
      PermissionEnum userPermission = null;
      AccessObject accessObject = BranchAccessObject.getBranchAccessObjectFromCache(branch);

      if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else {
         userPermission = acquirePermissionRank(subject, accessObject);
      }
      return userPermission;
   }

   public PermissionEnum getArtifactPermission(IBasicArtifact<?> subject, Artifact artifact) {
      PermissionEnum userPermission = null;
      AccessObject accessObject = null;

      // The artifact is new and has not been persisted.
      if (!artifact.isInDb()) {
         return PermissionEnum.FULLACCESS;
      }

      Integer artId = artifact.getArtId();
      Integer branchId = artifact.getBranch().getId();
      Integer lockedBranchId;

      //      accessObject = accessObjectCache.get(artId, branchId);
      accessObject = ArtifactAccessObject.getArtifactAccessObjectFromCache(artId, branchId);

      if (objectToBranchLockCache.containsKey(artId)) {
         lockedBranchId = objectToBranchLockCache.get(artId);

         // this object is locked under a different branch
         if (lockedBranchId.intValue() != branchId.intValue()) {
            userPermission = PermissionEnum.LOCK;
         }
      }

      if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else if (userPermission == null) {
         userPermission = acquirePermissionRank(subject, accessObject);
      }
      return userPermission;
   }

   private PermissionEnum acquirePermissionRank(IBasicArtifact<?> subject, AccessObject accessObject) {
      PermissionEnum userPermission = null;
      int subjectId = subject.getArtId();

      userPermission = accessControlListCache.get(subjectId, accessObject);

      if (subjectToGroupCache.containsKey(subjectId)) {
         for (int groupPermissionId : subjectToGroupCache.getValues(subjectId)) {
            PermissionEnum groupPermission = accessControlListCache.get(groupPermissionId, accessObject);

            if (groupPermission != null) {
               if (userPermission == null) {
                  userPermission = groupPermission;
               }

               if (groupPermission.getRank() > userPermission.getRank()) {
                  userPermission = groupPermission;
               }
            }

            if (userPermission == null) {
               userPermission = PermissionEnum.DENY;
            }
         }
      }
      return userPermission;
   }

   public void persistPermission(AccessControlData data) {
      persistPermission(data, false);
   }

   public void setPermission(Artifact subject, Object object, PermissionEnum permission) {
      AccessObject accessObject = getAccessObject(object);

      boolean newAccessControlData = !accessControlListCache.containsKey(subject.getArtId(), accessObject);

      if (newAccessControlData || permission != accessControlListCache.get(subject.getArtId(), accessObject)) {
         AccessControlData data = new AccessControlData(subject, accessObject, permission, newAccessControlData);
         persistPermission(data);
      }
   }

   public void persistPermission(AccessControlData data, boolean recurse) {
      Artifact subject = data.getSubject();
      PermissionEnum permission = data.getPermission();

      if (data.isDirty()) {
         data.setNotDirty();

         try {
            if (data.getObject() instanceof ArtifactAccessObject) {
               ArtifactAccessObject artifactAccessObject = (ArtifactAccessObject) data.getObject();

               if (data.isBirth()) {
                  getDatabaseService().runPreparedUpdate(INSERT_INTO_ARTIFACT_ACL, artifactAccessObject.getArtId(),
                     data.getPermission().getPermId(), data.getSubject().getArtId(), artifactAccessObject.getId());
               } else {
                  getDatabaseService().runPreparedUpdate(UPDATE_ARTIFACT_ACL, data.getPermission().getPermId(),
                     data.getSubject().getArtId(), artifactAccessObject.getArtId(), artifactAccessObject.getId());
               }

               if (recurse) {
                  Artifact artifact =
                     ArtifactQuery.getArtifactFromId(artifactAccessObject.getArtId(),
                        BranchManager.getBranch(artifactAccessObject.getId()));
                  AccessControlData childAccessControlData = null;

                  for (Artifact child : artifact.getChildren()) {
                     AccessObject accessObject = getAccessObject(child);

                     if (objectToSubjectCache.containsKey(accessObject)) {
                        Collection<Integer> subjectIds = objectToSubjectCache.getValues(accessObject);

                        for (int subjectId : subjectIds) {
                           if (subjectId == subject.getArtId()) {
                              childAccessControlData = new AccessControlData(subject, accessObject, permission, false);
                           }
                        }
                     }

                     if (childAccessControlData == null) {
                        childAccessControlData = new AccessControlData(subject, accessObject, permission, true);
                     }
                     persistPermission(childAccessControlData, true);
                  }
               }
            } else if (data.getObject() instanceof BranchAccessObject) {
               BranchAccessObject branchAccessObject = (BranchAccessObject) data.getObject();

               if (data.isBirth()) {
                  getDatabaseService().runPreparedUpdate(INSERT_INTO_BRANCH_ACL, data.getPermission().getPermId(),
                     data.getSubject().getArtId(), branchAccessObject.getId());
               } else {
                  getDatabaseService().runPreparedUpdate(UPDATE_BRANCH_ACL, data.getPermission().getPermId(),
                     data.getSubject().getArtId(), branchAccessObject.getId());
               }
            }
            cacheAccessControlData(data);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private void cacheAccessControlData(AccessControlData data) throws OseeCoreException {
      AccessObject accessObject = data.getObject();
      int subjectId = data.getSubject().getArtId();
      PermissionEnum permission = data.getPermission();

      if (!permission.equals(PermissionEnum.LOCK)) {
         accessControlListCache.put(subjectId, accessObject, permission);
         objectToSubjectCache.put(accessObject, subjectId);

         populateGroupMembers(subjectId);
      }
   }

   public List<AccessControlData> getAccessControlList(Object object) {
      List<AccessControlData> datas = new LinkedList<AccessControlData>();
      AccessObject accessObject = null;

      try {
         accessObject = AccessObject.getAccessObjectFromCache(object);

         if (accessObject == null) {
            return datas;
         }

         datas = generateAccessControlList(accessObject);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return datas;
   }

   public List<AccessControlData> generateAccessControlList(AccessObject accessObject) throws OseeCoreException {
      List<AccessControlData> datas = new LinkedList<AccessControlData>();

      Collection<Integer> subjects = objectToSubjectCache.getValues(accessObject);
      if (subjects == null) {
         return datas;
      }

      for (int subjectId : subjects) {
         Artifact subject = ArtifactQuery.getArtifactFromId(subjectId, BranchManager.getCommonBranch());
         PermissionEnum permissionEnum = accessControlListCache.get(subjectId, accessObject);
         AccessControlData accessControlData =
            new AccessControlData(subject, accessObject, permissionEnum, false, false);
         if (accessObject instanceof ArtifactAccessObject) {
            accessControlData.setArtifactPermission(permissionEnum);
            accessControlData.setBranchPermission(getBranchPermission(subject, accessObject));
         } else if (accessObject instanceof BranchAccessObject) {
            accessControlData.setBranchPermission(getBranchPermission(subject, accessObject));
         }
         datas.add(accessControlData);
      }

      return datas;
   }

   private PermissionEnum getBranchPermission(IBasicArtifact<?> subject, Object object) throws OseeCoreException {
      int branchId = ((AccessObject) object).getId();
      Branch branch = BranchManager.getBranch(branchId);

      return getBranchPermission(subject, branch);
   }

   public void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) throws OseeDataStoreException {
      int subjectId = data.getSubject().getArtId();
      AccessObject accessControlledObject = data.getObject();
      if (removeFromDb) {
         accessControlledObject.removeFromDatabase(subjectId);
      }

      if (accessControlledObject instanceof ArtifactAccessObject) {
         accessControlledObject.removeFromCache();
      }
      deCacheAccessControlData(data);
   }

   private void deCacheAccessControlData(AccessControlData data) {
      if (data == null) {
         throw new IllegalArgumentException("Can not remove a null AccessControlData.");
      }

      AccessObject accessObject = data.getObject();
      Integer subjectId = data.getSubject().getArtId();

      accessControlListCache.remove(subjectId, accessObject);
      objectToSubjectCache.removeValue(accessObject, subjectId);
      Collection<Integer> members = groupToSubjectsCache.getValues(subjectId);

      if (members != null) {
         for (Integer member : members) {
            subjectToGroupCache.removeValue(member, subjectId);
         }
      }
      if (!objectToSubjectCache.containsKey(accessObject)) {
         accessObject.removeFromCache();
      }
   }

   public AccessObject getAccessObject(Object object) {
      return AccessObject.getAccessObject(object);
   }

   private void cacheAccessObject(Integer objectId, Integer subjectId, PermissionEnum permission, AccessObject accessObject) {
      accessControlListCache.put(subjectId, accessObject, permission);
      objectToSubjectCache.put(accessObject, subjectId);
   }

   public void lockObjects(Collection<Artifact> objects, Artifact subject) {
      AccessControlEvent event = new AccessControlEvent();
      event.setEventType(AccessControlEventType.ArtifactsLocked);
      Set<Artifact> lockedArts = new HashSet<Artifact>();
      for (Artifact object : objects) {
         Integer objectArtId = object.getArtId();
         Integer subjectArtId = subject.getArtId();
         Integer objectBranchId = object.getBranch().getId();

         if (!objectToBranchLockCache.containsKey(objectArtId)) {
            AccessObject accessObject = getAccessObject(object);
            AccessControlData data = new AccessControlData(subject, accessObject, PermissionEnum.LOCK, true);
            persistPermission(data);
            objectToBranchLockCache.put(objectArtId, objectBranchId);
            lockedObjectToSubject.put(objectArtId, subjectArtId);
            event.getArtifacts().add(object.getBasicGuidArtifact());
            lockedArts.add(object);
         }
      }
      try {
         OseeEventManager.kickAccessControlArtifactsEvent(this, event, new LoadedArtifacts(lockedArts));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void unLockObjects(Collection<Artifact> objects, Artifact subject) throws OseeDataStoreException, OseeAuthenticationRequiredException {
      AccessControlEvent event = new AccessControlEvent();
      event.setEventType(AccessControlEventType.ArtifactsUnlocked);
      Set<Artifact> lockedArts = new HashSet<Artifact>();
      for (Artifact object : objects) {
         Integer objectArtId = object.getArtId();
         Integer branchId = object.getBranch().getId();
         Integer lockedBranchId;

         if (objectToBranchLockCache.containsKey(objectArtId) && canUnlockObject(object, subject)) {
            lockedBranchId = objectToBranchLockCache.get(objectArtId);

            if (branchId.equals(lockedBranchId)) {
               AccessObject accessObject = getAccessObject(object);
               removeAccessControlDataIf(true, new AccessControlData(subject, accessObject, PermissionEnum.LOCK, false));
               objectToBranchLockCache.remove(objectArtId);
               lockedObjectToSubject.remove(objectArtId);
               event.getArtifacts().add(object.getBasicGuidArtifact());
               lockedArts.add(object);
            }
         }
      }
      try {
         OseeEventManager.kickAccessControlArtifactsEvent(this, event, new LoadedArtifacts(lockedArts));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void removePermissions(IOseeBranch branch) throws OseeCoreException {
      Branch theBranch = getBranchCache().get(branch);
      getDatabaseService().runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, theBranch.getId());
      getDatabaseService().runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, theBranch.getId());
   }

   public boolean hasLock(Artifact object) {
      if (!object.isInDb()) {
         return false;
      }

      return objectToBranchLockCache.containsKey(object.getArtId());
   }

   public boolean canUnlockObject(Artifact object, Artifact subject) {
      Integer subjectId = lockedObjectToSubject.get(object.getArtId());
      return subjectId != null && subjectId.intValue() == subject.getArtId();
   }

   public Artifact getSubjectFromLockedObject(Object object) throws OseeCoreException {
      Artifact subject = null;

      if (object instanceof Artifact) {
         Integer subjectArtId = lockedObjectToSubject.get(((Artifact) object).getArtId());

         if (subjectArtId != null) {
            subject = UserManager.getUserByArtId(subjectArtId);
         }
      }
      return subject;
   }

   public boolean hasLockAccess(Artifact object) {
      boolean hasAccess = false;

      if (!object.isInDb()) {
         return true;
      }

      if (hasLock(object)) {
         hasAccess = objectToBranchLockCache.get(object.getArtId()) == object.getBranch().getId();
      }
      return hasAccess;
   }

   public boolean isOseeAdmin() throws OseeCoreException {
      return SystemGroup.OseeAdmin.isCurrentUserMember();
   }
}
