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

package org.eclipse.osee.framework.skynet.core.access;

import static org.eclipse.osee.framework.core.enums.PermissionEnum.DENY;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.FULLACCESS;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.LOCK;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.READ;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleOperation;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.lifecycle.access.AccessManagerChkPoint;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * Provides access control for OSEE. <REM2>
 * 
 * @author Jeff C. Phillips
 */

public class AccessControlManager {
   private static final String INSERT_INTO_ARTIFACT_ACL =
         "INSERT INTO OSEE_ARTIFACT_ACL (art_id, permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?, ?)";
   private static final String INSERT_INTO_BRANCH_ACL =
         "INSERT INTO OSEE_BRANCH_ACL (permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?)";

   private static final String UPDATE_ARTIFACT_ACL =
         "UPDATE OSEE_ARTIFACT_ACL SET permission_id = ? WHERE privilege_entity_id =? AND art_id = ? AND branch_id = ?";
   private static final String UPDATE_BRANCH_ACL =
         "UPDATE OSEE_BRANCH_ACL SET permission_id = ? WHERE privilege_entity_id =? AND branch_id = ?";

   private static final String GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST =
         "SELECT aac1.*, art1.art_type_id FROM osee_artifact art1, osee_artifact_acl aac1 WHERE art1.art_id = aac1.privilege_entity_id";
   private static final String GET_ALL_BRANCH_ACCESS_CONTROL_LIST =
         "SELECT bac1.*, art1.art_type_id FROM osee_artifact art1, osee_branch_acl bac1 WHERE art1.art_id = bac1.privilege_entity_id";

   private static final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   private static final String DELETE_BRANCH_ACL_FROM_BRANCH = "DELETE FROM OSEE_BRANCH_ACL WHERE branch_id =?";
   private static final String USER_GROUP_MEMBERS =
         "SELECT b_art_id FROM osee_relation_link WHERE a_art_id =? AND rel_link_type_id =? ORDER BY b_art_id";

   public static enum ObjectTypeEnum {
      ALL,
      BRANCH,
      REL_TYPE,
      ART_TYPE,
      ATTR_TYPE,
      ART;
   }

   private static DoubleKeyHashMap<Integer, AccessObject, PermissionEnum> accessControlListCache;
   private static HashCollection<AccessObject, Integer> objectToSubjectCache; // <subjectId, groupId>
   private static HashCollection<Integer, Integer> subjectToGroupCache; // <groupId, subjectId>
   private static HashCollection<Integer, Integer> groupToSubjectsCache; // <artId, branchId>
   private static Map<Integer, Integer> objectToBranchLockCache; // object, subject
   private static Map<Integer, Integer> lockedObjectToSubject; // subject, permission
   private static HashCollection<Integer, PermissionEnum> subjectToPermissionCache;

   private static final AccessControlManager instance = new AccessControlManager();

   private AccessControlManager() {
      reload();
      OseeEventManager.addListener(new EventRelay());
   }

   private static synchronized void reload() {
      initializeCaches();
      try {
         populateAccessControlLists();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private static void initializeCaches() {
      accessControlListCache = new DoubleKeyHashMap<Integer, AccessObject, PermissionEnum>();
      objectToSubjectCache = new HashCollection<AccessObject, Integer>();
      subjectToGroupCache = new HashCollection<Integer, Integer>();
      groupToSubjectsCache = new HashCollection<Integer, Integer>();
      objectToBranchLockCache = new HashMap<Integer, Integer>();
      lockedObjectToSubject = new HashMap<Integer, Integer>();
      subjectToPermissionCache = new HashCollection<Integer, PermissionEnum>();
   }

   private static void populateAccessControlLists() throws OseeCoreException {
      populateArtifactAccessControlList();
      populateBranchAccessControlList();
   }

   private static void populateBranchAccessControlList() throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
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

            ArtifactType toCheck = ArtifactTypeManager.getType("User Group");
            if (ArtifactTypeManager.getType(subjectArtifactTypeId).inheritsFrom(toCheck)) {
               populateGroupMembers(subjectId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private static void populateArtifactAccessControlList() throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
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

               ArtifactType toCheck = ArtifactTypeManager.getType("User Group");
               if (ArtifactTypeManager.getType(subjectArtifactTypeId).inheritsFrom(toCheck)) {
                  populateGroupMembers(subjectId);
               }
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private static void populateGroupMembers(Integer groupId) throws OseeCoreException {
      if (!groupToSubjectsCache.containsKey(groupId)) {
         Integer groupMember;

         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(USER_GROUP_MEMBERS, groupId, RelationTypeManager.getType(
                  CoreRelationTypes.Users_User).getId());

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

   public static boolean checkSubjectPermission(Artifact subject, PermissionEnum permission) {
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

   public static boolean checkObjectListPermission(Collection<?> objectList, PermissionEnum permission) throws OseeCoreException {
      boolean isValid = true;

      if (objectList.isEmpty()) {
         isValid = false;
      }

      for (Object object : objectList) {
         isValid &= hasPermission(object, permission);
      }
      return isValid;
   }

   public static boolean hasPermission(Object object, PermissionEnum permission) throws OseeCoreException {
      return hasPermission(UserManager.getUser(), object, permission);
   }

   public static PermissionEnum getObjectPermission(Artifact subject, Object object) throws OseeCoreException {
      for (PermissionEnum permissionEnum : PermissionEnum.values()) {
         boolean result = hasPermission(subject, object, permissionEnum);
         System.out.println("subject " + subject + " object " + object + " permission " + permissionEnum.name() + " -> " + result);
         if (result) {
            return permissionEnum;
         }
      }
      return FULLACCESS;
   }

   private static boolean hasPermission(Artifact subject, Object object, PermissionEnum permission) throws OseeCoreException {
      ILifecycleService service = Activator.getInstance().getLifecycleServices();
      AccessCheckOperation accessCheckOperation = new AccessCheckOperation(service, subject, object, permission);
      Operations.executeWork(accessCheckOperation, new LogProgressMonitor(), -1.0);
      IStatus status = accessCheckOperation.getStatus();
      return accessCheckOperation.hasPermission();
   }

   private static PermissionEnum getBranchPermission(Artifact subject, Branch branch, PermissionEnum permission) {
      PermissionEnum userPermission = null;
      AccessObject accessObject = BranchAccessObject.getBranchAccessObjectFromCache(branch);

      if (accessObject == null) {
         userPermission = FULLACCESS;
      } else {
         userPermission = acquirePermissionRank(subject, accessObject, permission);
      }
      return userPermission;
   }

   public static PermissionEnum getArtifactPermission(Artifact subject, Artifact artifact, PermissionEnum permission) {
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

      if (userPermission == null && accessObject != null) {
         userPermission = acquirePermissionRank(subject, accessObject, permission);
      }
      return userPermission;
   }

   private static PermissionEnum acquirePermissionRank(Artifact subject, AccessObject accessObject, PermissionEnum permission) {
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
         }
      }
      return userPermission;
   }

   public static void persistPermission(AccessControlData data) {
      persistPermission(data, false);
   }

   public static void setPermission(Artifact subject, Object object, PermissionEnum permission) {
      AccessObject accessObject = getAccessObject(object);

      boolean newAccessControlData = !accessControlListCache.containsKey(subject.getArtId(), accessObject);

      if (newAccessControlData || permission != accessControlListCache.get(subject.getArtId(), accessObject)) {
         AccessControlData data = new AccessControlData(subject, accessObject, permission, newAccessControlData);
         data.persist();
      }
   }

   protected static void persistPermission(AccessControlData data, boolean recurse) {
      Artifact subject = data.getSubject();
      PermissionEnum permission = data.getPermission();

      if (data.isDirty()) {
         data.setNotDirty();

         try {
            if (data.getObject() instanceof ArtifactAccessObject) {
               ArtifactAccessObject artifactAccessObject = (ArtifactAccessObject) data.getObject();

               if (data.isBirth()) {
                  ConnectionHandler.runPreparedUpdate(INSERT_INTO_ARTIFACT_ACL, artifactAccessObject.getArtId(),
                        data.getPermission().getPermId(), data.getSubject().getArtId(), artifactAccessObject.getId());
               } else {
                  ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACT_ACL, data.getPermission().getPermId(),
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
                  ConnectionHandler.runPreparedUpdate(INSERT_INTO_BRANCH_ACL, data.getPermission().getPermId(),
                        data.getSubject().getArtId(), branchAccessObject.getId());
               } else {
                  ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_ACL, data.getPermission().getPermId(),
                        data.getSubject().getArtId(), branchAccessObject.getId());
               }
            }
            cacheAccessControlData(data);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private static void cacheAccessControlData(AccessControlData data) throws OseeCoreException {
      AccessObject accessObject = data.getObject();
      int subjectId = data.getSubject().getArtId();
      PermissionEnum permission = data.getPermission();

      if (!permission.equals(PermissionEnum.LOCK)) {
         accessControlListCache.put(subjectId, accessObject, permission);
         objectToSubjectCache.put(accessObject, subjectId);

         populateGroupMembers(subjectId);
      }
   }

   public static List<AccessControlData> getAccessControlList(Object object) {
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

   private static List<AccessControlData> generateAccessControlList(AccessObject accessObject) throws OseeCoreException {
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

   private static PermissionEnum getBranchPermission(Artifact subject, Object object) throws OseeCoreException {
      int branchId = ((AccessObject) object).getId();
      Branch branch = BranchManager.getBranch(branchId);

      return AccessControlManager.getBranchPermission(subject, branch, PermissionEnum.FULLACCESS);
   }

   public static void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) throws OseeDataStoreException {
      int subjectId = data.getSubject().getArtId();
      AccessObject accessControlledObject = data.getObject();
      if (removeFromDb) {
         accessControlledObject.removeFromDatabase(subjectId);
      }

      if (accessControlledObject instanceof ArtifactAccessObject) {
         accessControlledObject.removeFromCache();
         // accessControlledObject.removeFromCache(accessControlListCache);
      }
      // branchAccessObjectCache.remove(object.getId()); (commented out due to 3KJSW)
      deCacheAccessControlData(data);
   }

   private static void deCacheAccessControlData(AccessControlData data) {
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

   public static AccessObject getAccessObject(Object object) {
      return AccessObject.getAccessObject(object);
   }

   private static void cacheAccessObject(Integer objectId, Integer subjectId, PermissionEnum permission, AccessObject accessObject) {
      accessControlListCache.put(subjectId, accessObject, permission);
      objectToSubjectCache.put(accessObject, subjectId);
   }

   public static void lockObjects(Collection<Artifact> objects, Artifact subject) {
      AccessControlEvent event = new AccessControlEvent();
      event.setEventType(AccessControlEventType.ArtifactsLocked);
      Set<Artifact> lockedArts = new HashSet<Artifact>();
      for (Artifact object : objects) {
         Integer objectArtId = object.getArtId();
         Integer subjectArtId = subject.getArtId();
         Integer objectBranchId = object.getBranch().getId();

         if (!objectToBranchLockCache.containsKey(objectArtId)) {
            AccessObject accessObject = getAccessObject(object);
            new AccessControlData(subject, accessObject, PermissionEnum.LOCK, true).persist();
            objectToBranchLockCache.put(objectArtId, objectBranchId);
            lockedObjectToSubject.put(objectArtId, subjectArtId);
            event.getArtifacts().add(object.getBasicGuidArtifact());
            lockedArts.add(object);
         }
      }
      try {
         OseeEventManager.kickAccessControlArtifactsEvent(instance, event, new LoadedArtifacts(lockedArts));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void unLockObjects(Collection<Artifact> objects, Artifact subject) throws OseeDataStoreException, OseeAuthenticationRequiredException {
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
         OseeEventManager.kickAccessControlArtifactsEvent(instance, event, new LoadedArtifacts(lockedArts));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void removeAllPermissionsFromBranch(OseeConnection connection, Branch branch) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(connection, DELETE_ARTIFACT_ACL_FROM_BRANCH, branch.getId());
      ConnectionHandler.runPreparedUpdate(connection, DELETE_BRANCH_ACL_FROM_BRANCH, branch.getId());
   }

   public static boolean hasLock(Artifact object) {
      if (!object.isInDb()) {
         return false;
      }

      return objectToBranchLockCache.containsKey(object.getArtId());
   }

   public static boolean canUnlockObject(Artifact object, Artifact subject) {
      Integer subjectId = lockedObjectToSubject.get(object.getArtId());
      return subjectId != null && subjectId.intValue() == subject.getArtId();
   }

   public static Artifact getSubjectFromLockedObject(Object object) throws OseeCoreException {
      Artifact subject = null;

      if (object instanceof Artifact) {
         Integer subjectArtId = lockedObjectToSubject.get(((Artifact) object).getArtId());

         if (subjectArtId != null) {
            subject = UserManager.getUserByArtId(subjectArtId);
         }
      }
      return subject;
   }

   public static boolean hasLockAccess(Artifact object) {
      boolean hasAccess = false;

      if (!object.isInDb()) {
         return true;
      }

      if (hasLock(object)) {
         hasAccess = objectToBranchLockCache.get(object.getArtId()) == object.getBranch().getId();
      }
      return hasAccess;
   }

   public static boolean isOseeAdmin() throws OseeCoreException {
      return SystemGroup.OseeAdmin.isCurrentUserMember();
   }

   private static final class EventRelay implements IBranchEventListener, IArtifactsPurgedEventListener, IArtifactEventListener {

      private void reload() {
         AccessControlManager.reload();
      }

      @Override
      public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
         try {
            if (branchModType == BranchEventType.Deleted || sender.isLocal() && branchModType == BranchEventType.Purged) {
               BranchAccessObject branchAccessObject = BranchAccessObject.getBranchAccessObject(branchId);
               List<AccessControlData> acl = generateAccessControlList(branchAccessObject);
               for (AccessControlData accessControlData : acl) {
                  AccessControlManager.removeAccessControlDataIf(sender.isLocal(), accessControlData);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }

      }

      @Override
      public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
         try {
            for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
               ArtifactAccessObject artifactAccessObject = ArtifactAccessObject.getArtifactAccessObject(artifact);
               List<AccessControlData> acl = generateAccessControlList(artifactAccessObject);
               for (AccessControlData accessControlData : acl) {
                  AccessControlManager.removeAccessControlDataIf(sender.isLocal(), accessControlData);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
            if (guidArt.is(EventModType.Added) && guidArt.is(CoreArtifactTypes.User)) {
               reload();
            }
            if (guidArt.is(EventModType.Purged)) {
               try {
                  Artifact cacheArt = ArtifactCache.getActive(guidArt);
                  if (cacheArt != null) {
                     ArtifactAccessObject artifactAccessObject = ArtifactAccessObject.getArtifactAccessObject(cacheArt);
                     List<AccessControlData> acl = generateAccessControlList(artifactAccessObject);
                     for (AccessControlData accessControlData : acl) {
                        AccessControlManager.removeAccessControlDataIf(sender.isLocal(), accessControlData);
                     }
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }

            }
         }
      }
   }

   public static void main(String[] args) {
      PermissionEnum[] a = {DENY, FULLACCESS, null};
      PermissionEnum[] b = {LOCK, FULLACCESS, null};
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            PermissionEnum branchPermission = a[i];
            PermissionEnum userPermission = b[j];
            if (branchPermission == DENY || userPermission == null) {
               System.out.print("T");
            } else {
               System.out.print("F");
            }
         }
         System.out.println();
      }
   }

   private static class AccessCheckOperation extends AbstractLifecycleOperation {
      private boolean hasPermission;
      private final Artifact subject;
      private final Object object;
      private final PermissionEnum permission;

      public AccessCheckOperation(ILifecycleService service, Artifact subject, Object object, PermissionEnum permission) {
         super(service, new AccessManagerChkPoint(null, null), "Check access control", Activator.PLUGIN_ID);
         this.hasPermission = false;
         this.subject = subject;
         this.object = object;
         this.permission = permission;
      }

      public boolean hasPermission() {
         return hasPermission;
      }

      @Override
      protected void doCoreWork(IProgressMonitor monitor) throws Exception {
         PermissionEnum userPermission = null;
         PermissionEnum branchPermission = null;
         Branch branch = null;

         if (object instanceof Artifact) {
            Artifact artifact = (Artifact) object;
            branch = artifact.getBranch();
            userPermission = getArtifactPermission(subject, (Artifact) object, permission);
         } else if (object instanceof Branch) {
            branch = (Branch) object;
         } else {
            throw new IllegalStateException("Unhandled object type for access control - " + object);
         }

         branchPermission = getBranchPermission(subject, branch, permission);

         if (branchPermission == DENY || userPermission == null) {
            userPermission = branchPermission;
         }

         if (permission == READ && userPermission == LOCK) {
            hasPermission = true;
         } else if (userPermission == null || userPermission == LOCK) {
            hasPermission = false;
         } else {
            hasPermission = userPermission.getRank() >= permission.getRank() && !userPermission.equals(DENY);
         }
      }
   }
}
