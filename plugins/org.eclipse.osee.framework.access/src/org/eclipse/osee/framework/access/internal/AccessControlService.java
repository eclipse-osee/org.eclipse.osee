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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.access.internal.data.ArtifactAccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
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

   private final DoubleKeyHashMap<Integer, AccessObject, PermissionEnum> accessControlListCache =
      new DoubleKeyHashMap<Integer, AccessObject, PermissionEnum>();
   private final HashCollection<AccessObject, Integer> objectToSubjectCache =
      new HashCollection<AccessObject, Integer>(true); // <subjectId, groupId>
   private final HashCollection<Integer, Integer> subjectToGroupCache = new HashCollection<Integer, Integer>(true); // <groupId, subjectId>
   private final HashCollection<Integer, Integer> groupToSubjectsCache = new HashCollection<Integer, Integer>(true); // <artId, branchId>

   // branch_id, art_id, subject_id
   private final CompositeKeyHashMap<Integer, Integer, Integer> artifactLockCache =
      new CompositeKeyHashMap<Integer, Integer, Integer>();

   private final HashCollection<Integer, PermissionEnum> subjectToPermissionCache =
      new HashCollection<Integer, PermissionEnum>(true);

   private final Cache<Collection<String>, AccessData> accessDataCache = CacheBuilder.newBuilder().expireAfterAccess(1,
      TimeUnit.HOURS).build();

   private final IOseeCachingService cachingService;
   private final IOseeDatabaseService databaseService;
   private final IdentityService identityService;
   private final OseeEventService eventService;

   private IArtifactEventListener listener1;
   private IArtifactEventListener listener2;

   public AccessControlService(IOseeDatabaseService databaseService, IOseeCachingService cachingService, IdentityService identityService, OseeEventService eventService) {
      super();
      this.databaseService = databaseService;
      this.cachingService = cachingService;
      this.identityService = identityService;
      this.eventService = eventService;
   }

   public void start() {
      listener1 = new AccessControlUpdateListener();
      eventService.addListener(EventQosType.NORMAL, listener1);

      if (!DbUtil.isDbInit()) {
         try {
            reloadCache();
         } catch (OseeCoreException ex) {
            OseeLog.log(AccessControlService.class, Level.SEVERE, ex);
         }
      }
   }

   public void stop() {
      if (listener1 != null) {
         eventService.removeListener(EventQosType.NORMAL, listener1);
         listener1 = null;
      }
      if (listener2 != null) {
         eventService.removeListener(EventQosType.NORMAL, listener2);
         listener2 = null;
      }
   }

   private ArtifactTypeCache getArtifactTypeCache() {
      return cachingService.getArtifactTypeCache();
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

   public synchronized void clearCache() {
      initializeCaches();
   }

   private void initializeCaches() {
      accessDataCache.invalidateAll();
      accessControlListCache.clear();
      objectToSubjectCache.clear();
      subjectToGroupCache.clear();
      groupToSubjectsCache.clear();
      artifactLockCache.clear();
      subjectToPermissionCache.clear();
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
            if (subjectArtifactType.inheritsFrom(CoreArtifactTypes.UserGroup)) {
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
               artifactLockCache.put(branchId, objectId, subjectId);
            } else {
               AccessObject accessObject =
                  ArtifactAccessObject.getArtifactAccessObject(objectId, BranchManager.getBranch(branchId));
               cacheAccessObject(objectId, subjectId, permission, accessObject);

               ArtifactType subjectArtifactType = getArtifactTypeCache().getById(subjectArtifactTypeId);
               if (subjectArtifactType.inheritsFrom(CoreArtifactTypes.UserGroup)) {
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
            chStmt.runPreparedQuery(USER_GROUP_MEMBERS, groupId,
               identityService.getLocalId(CoreRelationTypes.Users_User));

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
      return result;
   }

   private void addLockAccessControl(IBasicArtifact<?> userArtifact, Collection<?> objectsToCheck, AccessData accessData) throws OseeCoreException {
      for (Object obj : objectsToCheck) {
         Artifact subject = getSubjectFromLockedObject(obj);
         if (subject != null && !subject.equals(userArtifact)) {
            accessData.add(
               obj,
               new AccessDetail<IBasicArtifact<?>>((Artifact) obj, PermissionEnum.LOCK, Scope.createArtifactLockScope()));
         }
      }
   }

   @Override
   public AccessDataQuery getAccessData(final IBasicArtifact<?> userArtifact, final Collection<?> objectsToCheck) throws OseeCoreException {
      List<String> key = new LinkedList<String>();
      for (Object o : objectsToCheck) {
         if (o instanceof Branch) {
            key.add(((Branch) o).getGuid());
         } else if (o instanceof Artifact) {
            key.add(((Artifact) o).getGuid() + ((Artifact) o).getBranchGuid());
         } else {
            key.add(GUID.create());
         }
      }

      if (listener2 == null) {
         listener2 = new AccessControlUpdateListener((Artifact) userArtifact.getFullArtifact());
         eventService.addListener(EventQosType.NORMAL, listener2);
      }

      AccessData accessData = null;
      try {
         accessData = accessDataCache.get(key, new Callable<AccessData>() {

            @Override
            public AccessData call() throws Exception {
               ILifecycleService service = getLifecycleService();
               AccessData accessData = new AccessData();
               if (!DbUtil.isDbInit()) {
                  AbstractLifecycleVisitor<?> visitor =
                     new AccessProviderVisitor(userArtifact, objectsToCheck, accessData);
                  IStatus status = service.dispatch(new NullProgressMonitor(), visitor, ACCESS_POINT_ID);
                  Operations.checkForErrorStatus(status);
               }
               return accessData;
            }
         });
      } catch (ExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      addLockAccessControl(userArtifact, objectsToCheck, accessData);
      return new AccessDataQuery(accessData);
   }

   private ILifecycleService getLifecycleService() throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      Conditions.checkNotNull(bundle, "bundle");
      BundleContext bundleContext = bundle.getBundleContext();
      Conditions.checkNotNull(bundleContext, "bundleContext");
      ServiceReference<ILifecycleService> reference = bundleContext.getServiceReference(ILifecycleService.class);
      ILifecycleService service = bundleContext.getService(reference);
      Conditions.checkNotNull(service, "ILifecycleService");
      return service;
   }

   public PermissionEnum getBranchPermission(IBasicArtifact<?> subject, IOseeBranch branch) throws OseeCoreException {
      PermissionEnum userPermission = null;
      AccessObject accessObject = BranchAccessObject.getBranchAccessObjectFromCache(branch);

      if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else {
         userPermission = acquirePermissionRank(subject, accessObject);
      }
      return userPermission;
   }

   public PermissionEnum getArtifactPermission(IBasicArtifact<?> subject, Artifact artifact) throws OseeCoreException {
      PermissionEnum userPermission = null;
      AccessObject accessObject = null;

      // The artifact is new and has not been persisted.
      if (!artifact.isInDb()) {
         return PermissionEnum.FULLACCESS;
      }

      Integer artId = artifact.getArtId();
      Branch branch = artifact.getFullBranch();
      Integer branchId = branch.getId();

      //      accessObject = accessObjectCache.get(artId, branchId);
      accessObject = ArtifactAccessObject.getArtifactAccessObjectFromCache(artId, branch);

      if (artifactLockCache.containsKey(branchId, artId)) {

         int lockOwnerId = artifactLockCache.get(branchId, artId);
         // this object is locked under a different branch
         if (lockOwnerId != subject.getArtId()) {
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

   public void setPermission(Artifact subject, Object object, PermissionEnum permission) throws OseeCoreException {
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
            OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
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
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
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

   public void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) throws OseeCoreException {
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
      groupToSubjectsCache.removeValues(subjectId);
      if (!objectToSubjectCache.containsKey(accessObject)) {
         accessObject.removeFromCache();
      }
   }

   public AccessObject getAccessObject(Object object) throws OseeCoreException {
      return AccessObject.getAccessObject(object);
   }

   private void cacheAccessObject(Integer objectId, Integer subjectId, PermissionEnum permission, AccessObject accessObject) {
      accessControlListCache.put(subjectId, accessObject, permission);
      objectToSubjectCache.put(accessObject, subjectId);
   }

   public void lockObjects(Collection<Artifact> objects, Artifact subject) throws OseeCoreException {
      AccessControlEvent event = new AccessControlEvent();
      event.setEventType(AccessControlEventType.ArtifactsLocked);
      Set<Artifact> lockedArts = new HashSet<Artifact>();
      for (Artifact object : objects) {
         Integer objectArtId = object.getArtId();
         Integer subjectArtId = subject.getArtId();
         Integer objectBranchId = object.getFullBranch().getId();

         if (!artifactLockCache.containsKey(objectBranchId, objectArtId)) {
            AccessObject accessObject = getAccessObject(object);
            AccessControlData data = new AccessControlData(subject, accessObject, PermissionEnum.LOCK, true);
            persistPermission(data);
            artifactLockCache.put(objectBranchId, objectArtId, subjectArtId);
            event.getArtifacts().add(object.getBasicGuidArtifact());
            lockedArts.add(object);
         }
      }
      try {
         eventService.send(this, event);
      } catch (Exception ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
      }
   }

   public void unLockObjects(Collection<Artifact> objects, Artifact subject) throws OseeCoreException, OseeAuthenticationRequiredException {
      AccessControlEvent event = new AccessControlEvent();
      event.setEventType(AccessControlEventType.ArtifactsUnlocked);
      Set<Artifact> lockedArts = new HashSet<Artifact>();
      for (Artifact object : objects) {
         Integer objectArtId = object.getArtId();
         Integer branchId = object.getFullBranch().getId();

         if (artifactLockCache.containsKey(branchId, objectArtId) && canUnlockObject(object, subject)) {
            AccessObject accessObject = getAccessObject(object);
            removeAccessControlDataIf(true, new AccessControlData(subject, accessObject, PermissionEnum.LOCK, false));
            artifactLockCache.remove(branchId, objectArtId);
            event.getArtifacts().add(object.getBasicGuidArtifact());
            lockedArts.add(object);
         }
      }
      try {
         eventService.send(this, event);
      } catch (Exception ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void removePermissions(IOseeBranch branch) throws OseeCoreException {
      Branch theBranch = getBranchCache().get(branch);
      getDatabaseService().runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, theBranch.getId());
      getDatabaseService().runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, theBranch.getId());
   }

   public boolean hasLock(Artifact object) throws OseeCoreException {
      if (!object.isInDb()) {
         return false;
      }

      return artifactLockCache.containsKey(object.getFullBranch().getId(), object.getArtId());
   }

   public boolean canUnlockObject(Artifact object, Artifact subject) throws OseeCoreException {
      Integer subjectId = artifactLockCache.get(object.getFullBranch().getId(), object.getArtId());
      return subjectId != null && subjectId.intValue() == subject.getArtId();
   }

   public Artifact getSubjectFromLockedObject(Object object) throws OseeCoreException {
      Artifact subject = null;

      if (object instanceof Artifact) {
         Artifact art = (Artifact) object;
         Integer subjectArtId = artifactLockCache.get(art.getFullBranch().getId(), art.getArtId());

         if (subjectArtId != null) {
            subject = UserManager.getUserByArtId(subjectArtId);
         }
      }
      return subject;
   }

   public boolean hasLockAccess(Artifact object) throws OseeCoreException {
      boolean hasAccess = false;

      if (!object.isInDb()) {
         return true;
      }

      if (hasLock(object)) {
         int branchId = object.getFullBranch().getId();
         hasAccess = artifactLockCache.get(branchId, object.getArtId()) == UserManager.getUser().getArtId();
      }
      return hasAccess;
   }

   private static Boolean isOseeAdmin = null;

   public boolean isOseeAdmin() throws OseeCoreException {
      if (isOseeAdmin == null) {
         isOseeAdmin = SystemGroup.OseeAdmin.isCurrentUserMember();
      }
      return isOseeAdmin;
   }

   private final class AccessControlUpdateListener implements IArtifactEventListener {

      private final List<? extends IEventFilter> eventFilters;

      public AccessControlUpdateListener() {
         eventFilters =
            Arrays.asList(new ArtifactTypeEventFilter(CoreArtifactTypes.AccessControlModel), new BranchGuidEventFilter(
               CoreBranches.COMMON));
      }

      public AccessControlUpdateListener(Artifact artifact) {
         eventFilters =
            Arrays.asList(new ArtifactEventFilter(artifact), new BranchGuidEventFilter(artifact.getBranch()));
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return eventFilters;
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         try {
            reloadCache();
         } catch (OseeCoreException ex) {
            OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
         }
      }
   }

}
