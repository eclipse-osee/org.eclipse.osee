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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.access.internal.data.ArtifactAccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchUuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessArtifactLockTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEventPayload;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

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
      "SELECT b_art_id FROM osee_relation_link WHERE a_art_id = ? AND rel_link_type_id = ? ORDER BY b_art_id";

   private final DoubleKeyHashMap<Long, AccessObject, PermissionEnum> accessControlListCache = new DoubleKeyHashMap<>();
   private final HashCollection<AccessObject, ArtifactId> objectToSubjectCache = new HashCollection<>(true);
   private final HashCollection<Long, ArtifactId> subjectToGroupCache = new HashCollection<>(true); // <subjectId, groupId>
   private final HashCollection<Long, ArtifactId> groupToSubjectsCache = new HashCollection<>(true); // <groupId, subjectId>

   // branch_id, art_id, subject_id
   private final CompositeKeyHashMap<BranchId, Long, ArtifactId> artifactLockCache = new CompositeKeyHashMap<>();

   private final HashCollection<Integer, PermissionEnum> subjectToPermissionCache =
      new HashCollection<Integer, PermissionEnum>(true);

   private final Cache<Collection<String>, AccessData> accessDataCache =
      CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

   private final IOseeCachingService cachingService;
   private final JdbcClient jdbcClient;
   private final OseeEventService eventService;

   private IArtifactEventListener listener1;
   private IArtifactEventListener listener2;

   private final AtomicBoolean ensurePopulated = new AtomicBoolean(false);

   public AccessControlService(JdbcClient jdbcClient, IOseeCachingService cachingService, OseeEventService eventService) {

      super();
      this.jdbcClient = jdbcClient;
      this.cachingService = cachingService;
      this.eventService = eventService;
   }

   public void start() {
      listener1 = new AccessControlUpdateListener();
      if (eventService != null) {
         eventService.addListener(EventQosType.NORMAL, listener1);
      }
   }

   public void stop() {
      if (listener1 != null) {
         if (eventService != null) {
            eventService.removeListener(EventQosType.NORMAL, listener1);
         }
         listener1 = null;
      }
      if (listener2 != null) {
         if (eventService != null) {
            eventService.removeListener(EventQosType.NORMAL, listener2);
         }
         listener2 = null;
      }
   }

   private ArtifactTypeCache getArtifactTypeCache() {
      return cachingService.getArtifactTypeCache();
   }

   private JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   public void reloadCache() throws OseeCoreException {
      ensurePopulated.set(false);
   }

   private synchronized void ensurePopulated() {
      if (ensurePopulated.compareAndSet(false, true)) {
         initializeCaches();
         populateAccessControlLists();
      }
   }

   public synchronized void clearCache() {
      initializeCaches();
      ensurePopulated.set(false);
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
      ensurePopulated();
      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId subjectId = ArtifactId.valueOf(stmt.getLong("privilege_entity_id"));
         BranchId branch = BranchId.valueOf(stmt.getLong("branch_id"));
         ArtifactTypeId subjectArtifactTypeId = ArtifactTypeId.valueOf(stmt.getLong("art_type_id"));
         PermissionEnum permission = PermissionEnum.getPermission(stmt.getInt("permission_id"));
         BranchAccessObject branchAccessObject = BranchAccessObject.getBranchAccessObject(branch);

         accessControlListCache.put(subjectId.getId(), branchAccessObject, permission);
         objectToSubjectCache.put(branchAccessObject, subjectId);

         ArtifactType subjectArtifactType = getArtifactTypeCache().get(subjectArtifactTypeId);
         if (subjectArtifactType != null && subjectArtifactType.inheritsFrom(CoreArtifactTypes.UserGroup)) {
            populateGroupMembers(subjectId);
         }
      };
      getJdbcClient().runQuery(consumer, GET_ALL_BRANCH_ACCESS_CONTROL_LIST);
   }

   private void populateArtifactAccessControlList() throws OseeCoreException {
      ensurePopulated();

      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId subjectId = ArtifactId.valueOf(stmt.getLong("privilege_entity_id"));
         ArtifactId objectId = ArtifactId.valueOf(stmt.getLong("art_id"));
         BranchId branch = BranchId.valueOf(stmt.getLong("branch_id"));
         Long subjectArtifactTypeId = stmt.getLong("art_type_id");
         PermissionEnum permission = PermissionEnum.getPermission(stmt.getInt("permission_id"));

         if (permission != null) {
            if (permission.equals(PermissionEnum.LOCK)) {
               artifactLockCache.put(branch, objectId.getId(), subjectId);
            } else {
               AccessObject accessObject = ArtifactAccessObject.getArtifactAccessObject(objectId, branch);
               cacheAccessObject(subjectId, permission, accessObject);

               ArtifactType subjectArtifactType = getArtifactTypeCache().getById(subjectArtifactTypeId);
               if (subjectArtifactType != null && subjectArtifactType.inheritsFrom(CoreArtifactTypes.UserGroup)) {
                  populateGroupMembers(subjectId);
               }
            }
         }
      };

      getJdbcClient().runQuery(consumer, GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST);
   }

   private void populateGroupMembers(ArtifactId groupId) throws OseeCoreException {
      ensurePopulated();
      if (!groupToSubjectsCache.containsKey(groupId.getId())) {
         getJdbcClient().runQuery(stmt -> {
            ArtifactId groupMember = ArtifactId.valueOf(stmt.getLong("b_art_id"));
            subjectToGroupCache.put(groupMember.getId(), groupId);
            groupToSubjectsCache.put(groupId.getId(), groupMember);
         }, USER_GROUP_MEMBERS, groupId, CoreRelationTypes.Users_User);
      }
   }

   public boolean checkSubjectPermission(Artifact subject, PermissionEnum permission) {
      ensurePopulated();
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
      ArtifactToken subject = UserManager.getUser();
      AccessDataQuery accessQuery = getAccessData(subject, objectsToCheck);
      result = accessQuery.matchesAll(permission);
      return result;
   }

   private void addLockAccessControl(ArtifactToken userArtifact, Collection<?> objectsToCheck, AccessData accessData) throws OseeCoreException {
      for (Object obj : objectsToCheck) {
         Artifact subject = getSubjectFromLockedObject(obj);
         if (subject != null && !subject.equals(userArtifact)) {
            accessData.add(obj,
               new AccessDetail<ArtifactToken>((Artifact) obj, PermissionEnum.LOCK, Scope.createArtifactLockScope()));
         }
      }
   }

   @Override
   public AccessDataQuery getAccessData(ArtifactToken userArtifact, final Collection<?> objectsToCheck) throws OseeCoreException {
      ensurePopulated();
      List<String> key = new LinkedList<>();
      for (Object o : objectsToCheck) {
         if (o instanceof BranchId) {
            key.add(String.valueOf(((BranchId) o).getGuid()));
         } else if (o instanceof Artifact) {
            key.add(((Artifact) o).getGuid());
            key.add(String.valueOf(((Artifact) o).getBranch().getGuid()));
         } else {
            key.add(GUID.create());
         }
      }

      if (listener2 == null) {
         listener2 = new AccessControlUpdateListener(userArtifact);
         if (eventService != null) {
            eventService.addListener(EventQosType.NORMAL, listener2);
         }
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
         OseeCoreException.wrapAndThrow(ex);
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

   public PermissionEnum getBranchPermission(ArtifactToken subject, BranchId branch) throws OseeCoreException {
      PermissionEnum userPermission = null;
      AccessObject accessObject = BranchAccessObject.getBranchAccessObjectFromCache(branch);

      if (accessObject == null && (!CoreBranches.COMMON.getId().equals(branch.getId()) && BranchManager.getType(
         branch).isBaselineBranch())) {
         userPermission = PermissionEnum.READ;
      } else if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else {
         userPermission = acquirePermissionRank(subject, accessObject);
      }
      return userPermission;
   }

   public PermissionEnum getArtifactPermission(ArtifactToken subject, Artifact artifact) throws OseeCoreException {
      ensurePopulated();
      PermissionEnum userPermission = PermissionEnum.FULLACCESS;
      AccessObject accessObject = null;

      // The artifact is new and has not been persisted.
      if (!artifact.isInDb()) {
         return PermissionEnum.FULLACCESS;
      }

      Long artId = artifact.getId();
      BranchId branch = artifact.getBranch();

      accessObject = ArtifactAccessObject.getArtifactAccessObjectFromCache(artifact);

      if (artifactLockCache.containsKey(branch, artId)) {

         ArtifactId lockOwnerId = artifactLockCache.get(branch, artId);
         // this object is locked under a different branch
         if (subject.notEqual(lockOwnerId)) {
            userPermission = PermissionEnum.LOCK;
         }
      }

      if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else {
         userPermission = acquirePermissionRank(subject, accessObject);
      }
      return userPermission;
   }

   private PermissionEnum acquirePermissionRank(ArtifactToken subject, AccessObject accessObject) {
      ensurePopulated();
      PermissionEnum userPermission = accessControlListCache.get(subject.getId(), accessObject);
      if (subjectToGroupCache.containsKey(subject.getId())) {
         for (ArtifactId groupPermissionId : subjectToGroupCache.getValues(subject.getId())) {
            PermissionEnum groupPermission = accessControlListCache.get(groupPermissionId.getId(), accessObject);

            if (groupPermission != null) {
               if (userPermission == null) {
                  userPermission = groupPermission;
               } else if (groupPermission.getRank() > userPermission.getRank()) {
                  userPermission = groupPermission;
               }
            }
         }
      }
      // user does not have entry in the branch access control table for this branch
      if (userPermission == null) {
         // If there are any other access on this branch, it's locked for this user
         if (objectToSubjectCache.containsKey(accessObject)) {
            userPermission = PermissionEnum.DENY;
         } else {
            userPermission = PermissionEnum.FULLACCESS;
         }
      }

      return userPermission;
   }

   public void persistPermission(AccessControlData data) {
      ensurePopulated();
      persistPermission(data, false);
   }

   public void setPermission(Artifact subject, Object object, PermissionEnum permission) throws OseeCoreException {
      ensurePopulated();
      AccessObject accessObject = getAccessObject(object);

      boolean newAccessControlData = !accessControlListCache.containsKey(subject.getId(), accessObject);

      if (newAccessControlData || permission != accessControlListCache.get(subject.getId(), accessObject)) {
         AccessControlData data = new AccessControlData(subject, accessObject, permission, newAccessControlData);
         persistPermission(data);
      }
   }

   public void persistPermission(AccessControlData data, boolean recurse) {
      if (data.getObject() instanceof ArtifactAccessObject) {

         ArtifactAccessObject artifactAccessObject = (ArtifactAccessObject) data.getObject();

         AccessTopicEventPayload event = new AccessTopicEventPayload();
         event.setBranch(artifactAccessObject.getBranch());

         persistPermissionForArtifact(data, artifactAccessObject, recurse, event);
         cacheAccessControlData(data);

         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_ARTIFACT_MODIFIED);

      } else if (data.getObject() instanceof BranchAccessObject) {

         BranchAccessObject branchAccessObject = (BranchAccessObject) data.getObject();

         persistPermissionForBranch(data, branchAccessObject, recurse, null);
         cacheAccessControlData(data);

         AccessTopicEventPayload event = new AccessTopicEventPayload();
         event.setBranch(branchAccessObject.getBranch());
         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_BRANCH_MODIFIED);

      }
   }

   private void persistPermissionForArtifact(AccessControlData data, ArtifactAccessObject artifactAccessObject, boolean recurse, AccessTopicEventPayload event) {
      ensurePopulated();
      Artifact subject = data.getSubject();
      PermissionEnum permission = data.getPermission();

      if (data.isDirty()) {
         data.setNotDirty();

         try {

            if (data.isBirth()) {
               getJdbcClient().runPreparedUpdate(INSERT_INTO_ARTIFACT_ACL, artifactAccessObject.getArtId(),
                  data.getPermission().getPermId(), data.getSubject().getArtId(), artifactAccessObject.getBranch());
            } else {
               getJdbcClient().runPreparedUpdate(UPDATE_ARTIFACT_ACL, data.getPermission().getPermId(),
                  data.getSubject().getArtId(), artifactAccessObject.getArtId(), artifactAccessObject.getBranch());
            }
            event.addArtifact(artifactAccessObject.getArtId());

            if (recurse) {
               Artifact artifact =
                  ArtifactQuery.getArtifactFromId(artifactAccessObject.getArtId(), artifactAccessObject.getBranch());

               for (Artifact child : artifact.getChildren()) {
                  AccessControlData childAccessControlData = null;
                  AccessObject childAccessObject = getAccessObject(child);

                  if (objectToSubjectCache.containsKey(childAccessObject)) {
                     Collection<ArtifactId> subjectIds = objectToSubjectCache.getValues(childAccessObject);

                     for (ArtifactId subjectId : subjectIds) {
                        if (subjectId.equals(subject)) {
                           childAccessControlData =
                              new AccessControlData(subject, childAccessObject, permission, false);
                           break;
                        }
                     }
                  }

                  if (childAccessControlData == null) {
                     childAccessControlData = new AccessControlData(subject, childAccessObject, permission, true);
                  }
                  persistPermissionForArtifact(childAccessControlData, (ArtifactAccessObject) childAccessObject, true,
                     event);
               }
            }

         } catch (OseeCoreException ex) {
            OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
         }
      }
   }

   private void persistPermissionForBranch(AccessControlData data, BranchAccessObject branchAccessObject, boolean recurse, AccessTopicEventPayload event) {
      ensurePopulated();
      if (data.isDirty()) {
         data.setNotDirty();
         try {
            if (data.isBirth()) {
               getJdbcClient().runPreparedUpdate(INSERT_INTO_BRANCH_ACL, data.getPermission().getPermId(),
                  data.getSubject().getArtId(), branchAccessObject.getBranch());
            } else {
               getJdbcClient().runPreparedUpdate(UPDATE_BRANCH_ACL, data.getPermission().getPermId(),
                  data.getSubject().getArtId(), branchAccessObject.getBranch());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
         }
      }
   }

   private void cacheAccessControlData(AccessControlData data) throws OseeCoreException {
      ensurePopulated();
      AccessObject accessObject = data.getObject();
      PermissionEnum permission = data.getPermission();
      ArtifactId subject = data.getSubject();

      if (!permission.equals(PermissionEnum.LOCK)) {
         accessControlListCache.put(data.getSubject().getId(), accessObject, permission);
         objectToSubjectCache.put(accessObject, subject);

         populateGroupMembers(subject);
      }
   }

   public List<AccessControlData> getAccessControlList(Object object) {
      List<AccessControlData> datas = new LinkedList<>();
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
      ensurePopulated();
      List<AccessControlData> datas = new LinkedList<>();

      Collection<ArtifactId> subjects = objectToSubjectCache.getValues(accessObject);
      if (subjects == null) {
         return datas;
      }

      for (ArtifactId subjectId : subjects) {
         Artifact subject = ArtifactQuery.getArtifactFromId(subjectId, CoreBranches.COMMON);
         PermissionEnum permissionEnum = accessControlListCache.get(subjectId.getId(), accessObject);
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

   private PermissionEnum getBranchPermission(ArtifactToken subject, Object object) throws OseeCoreException {
      return getBranchPermission(subject, ((AccessObject) object).getBranch());
   }

   public void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) throws OseeCoreException {

      int subjectId = data.getSubject().getArtId();
      AccessObject accessControlledObject = data.getObject();
      boolean isArtifact = accessControlledObject instanceof ArtifactAccessObject;
      if (removeFromDb) {
         accessControlledObject.removeFromDatabase(subjectId);
      }

      if (accessControlledObject instanceof ArtifactAccessObject) {
         accessControlledObject.removeFromCache();
      }
      deCacheAccessControlData(data);

      AccessTopicEventPayload event = new AccessTopicEventPayload();
      event.setBranch(accessControlledObject.getBranch());
      if (isArtifact) {
         event.addArtifact(((ArtifactAccessObject) accessControlledObject).getArtId());
      }

      OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_ARTIFACT_MODIFIED);

   }

   private void deCacheAccessControlData(AccessControlData data) {
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

   public AccessObject getAccessObject(Object object) throws OseeCoreException {
      return AccessObject.getAccessObject(object);
   }

   private void cacheAccessObject(ArtifactId subjectId, PermissionEnum permission, AccessObject accessObject) {
      accessControlListCache.put(subjectId.getId(), accessObject, permission);
      objectToSubjectCache.put(accessObject, subjectId);
   }

   public void lockObjects(Collection<Artifact> objects, Artifact subject) throws OseeCoreException {
      Conditions.checkNotNull(subject, "subject");
      Conditions.checkNotNullOrEmpty(objects, "objects");
      ensurePopulated();
      AccessArtifactLockTopicEvent event = new AccessArtifactLockTopicEvent();
      event.setBranch(objects.iterator().next().getBranch());
      event.setLocked(true);
      Set<Artifact> lockedArts = new HashSet<>();
      for (Artifact object : objects) {
         Long objectArtId = object.getId();
         BranchId objectBranch = object.getBranch();

         if (!artifactLockCache.containsKey(objectBranch, objectArtId)) {
            AccessObject accessObject = getAccessObject(object);
            AccessControlData data = new AccessControlData(subject, accessObject, PermissionEnum.LOCK, true);
            persistPermission(data);
            artifactLockCache.put(objectBranch, objectArtId, subject);
            event.addArtifact(object.getUuid());
            lockedArts.add(object);
         }
      }
      try {
         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED);
      } catch (Exception ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
      }
   }

   public void unLockObjects(Collection<Artifact> objects, Artifact subject) throws OseeCoreException, OseeAuthenticationRequiredException {
      ensurePopulated();
      AccessArtifactLockTopicEvent event = new AccessArtifactLockTopicEvent();
      event.setBranch(objects.iterator().next().getBranch());
      event.setLocked(false);
      Set<Artifact> lockedArts = new HashSet<>();
      for (Artifact object : objects) {
         Long objectArtId = object.getId();
         BranchId branch = object.getBranch();

         if (artifactLockCache.containsKey(branch, objectArtId) && canUnlockObject(object, subject)) {
            AccessObject accessObject = getAccessObject(object);
            removeAccessControlDataIf(true, new AccessControlData(subject, accessObject, PermissionEnum.LOCK, false));
            artifactLockCache.removeAndGet(branch, objectArtId);
            event.addArtifact(object.getUuid());
            lockedArts.add(object);
         }
      }
      try {
         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED);
      } catch (Exception ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void removePermissions(BranchId branch) throws OseeCoreException {
      getJdbcClient().runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, branch);
      getJdbcClient().runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, branch);

      try {
         AccessTopicEventPayload event = new AccessTopicEventPayload();
         event.setBranch(branch);
         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_BRANCH_MODIFIED);
      } catch (Exception ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
      }
   }

   public boolean hasLock(Artifact object) throws OseeCoreException {
      ensurePopulated();
      if (!object.isInDb()) {
         return false;
      }

      return artifactLockCache.containsKey(object.getBranch(), object.getId());
   }

   public boolean canUnlockObject(Artifact object, Artifact subject) throws OseeCoreException {
      ensurePopulated();
      ArtifactId subjectId = artifactLockCache.get(object.getBranch(), object.getId());
      return subject.equals(subjectId);
   }

   public Artifact getSubjectFromLockedObject(Object object) throws OseeCoreException {
      ensurePopulated();
      Artifact subject = null;

      if (object instanceof Artifact) {
         Artifact art = (Artifact) object;
         ArtifactId subjectArtId = artifactLockCache.get(art.getBranch(), art.getId());

         if (subjectArtId != null) {
            subject = UserManager.getUserByArtId(subjectArtId);
         }
      }
      return subject;
   }

   public boolean hasLockAccess(Artifact object) throws OseeCoreException {
      ensurePopulated();
      boolean hasAccess = false;

      if (!object.isInDb()) {
         return true;
      }

      if (hasLock(object)) {
         hasAccess = artifactLockCache.get(object.getBranch(), object.getId()).equals(UserManager.getUser());
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
         eventFilters = Arrays.asList(new ArtifactTypeEventFilter(CoreArtifactTypes.AccessControlModel),
            new BranchUuidEventFilter(CoreBranches.COMMON));
      }

      public AccessControlUpdateListener(ArtifactToken artifact) {
         eventFilters =
            Arrays.asList(new ArtifactEventFilter(artifact), new BranchUuidEventFilter(artifact.getBranch()));
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
