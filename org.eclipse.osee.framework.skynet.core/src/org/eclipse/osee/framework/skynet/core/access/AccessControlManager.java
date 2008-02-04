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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.ArtifactLockStatusChanged;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * Provides access control for OSEE.
 * 
 * @author Jeff C. Phillips
 */

public class AccessControlManager implements PersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(AccessControlManager.class);
   private static final String INSERT_INTO_ARTIFACT_ACL =
         "INSERT INTO " + SkynetDatabase.ARTIFACT_TABLE_ACL + " (art_id, permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?, ?)";
   private static final String INSERT_INTO_BRANCH_ACL =
         "INSERT INTO " + SkynetDatabase.BRANCH_TABLE_ACL + " (permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?)";
   private static final String UPDATE_ARTIFACT_ACL =
         "UPDATE " + SkynetDatabase.ARTIFACT_TABLE_ACL + " SET permission_id = ? WHERE privilege_entity_id =? AND art_id = ? AND branch_id = ?";
   private static final String UPDATE_BRANCH_ACL =
         "UPDATE " + SkynetDatabase.BRANCH_TABLE_ACL + " SET permission_id = ? WHERE privilege_entity_id =? AND branch_id = ?";
   private static final String GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST =
         "SELECT t2.permission_id, t2.privilege_entity_id, t2.art_id, t2.branch_id, t3.name FROM " + SkynetDatabase.ARTIFACT_TABLE + " t1, " + SkynetDatabase.ARTIFACT_TABLE_ACL + " t2, " + SkynetDatabase.ARTIFACT_TYPE_TABLE + " t3 WHERE t1.art_id = t2.privilege_entity_id AND t1.art_type_id = t3.art_type_id";
   private static final String GET_ALL_BRANCH_ACCESS_CONTROL_LIST =
         "SELECT t2.permission_id,t2.privilege_entity_id, t2.branch_id, t3.name FROM " + SkynetDatabase.ARTIFACT_TABLE + " t1, " + SkynetDatabase.BRANCH_TABLE_ACL + " t2, " + SkynetDatabase.ARTIFACT_TYPE_TABLE + " t3 WHERE t1.art_id = t2.privilege_entity_id AND t1.art_type_id = t3.art_type_id";
   private static final String DELETE_ARTIFACT_ACL =
         "DELETE FROM " + SkynetDatabase.ARTIFACT_TABLE_ACL + " WHERE privilege_entity_id = ? AND art_id =? AND branch_id =?";
   private static final String DELETE_BRANCH_ACL =
         "DELETE FROM " + SkynetDatabase.BRANCH_TABLE_ACL + " WHERE privilege_entity_id = ? AND branch_id =?";
   private static final String DELETE_ARTIFACT_ACL_FROM_BRANCH =
         "DELETE FROM " + SkynetDatabase.ARTIFACT_TABLE_ACL + " WHERE  branch_id =?";
   private static final String DELETE_BRANCH_ACL_FROM_BRANCH =
         "DELETE FROM " + SkynetDatabase.BRANCH_TABLE_ACL + " WHERE branch_id =?";
   private static final String USER_GROUP_MEMBERS =
         "SELECT t2.b_art_id AS user_id FROM " + SkynetDatabase.RELATION_LINK_TYPE_TABLE + " t1, " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t2 WHERE t1.type_name = 'Users' AND t1.rel_link_type_id = t2.rel_link_type_id AND t2.a_art_id =? ORDER BY user_id";

   public static enum ObjectTypeEnum {
      ALL, BRANCH, REL_TYPE, ART_TYPE, ATTR_TYPE, ART;
   }

   private DoubleKeyHashMap<Integer, Object, PermissionEnum> accessControlListCache;
   // <objectId, branchId>
   private DoubleKeyHashMap<Integer, Integer, AccessObject> accessObjectCache;
   private Map<Integer, BranchAccessObject> branchAccessObjectCache;
   private HashCollection<AccessObject, Integer> objectToSubjectCache;
   // <subjectId, groupId>
   private HashCollection<Integer, Integer> subjectToGroupCache;
   // <groupId, subjectId>
   private HashCollection<Integer, Integer> groupToSubjectsCache;
   // <artId, branchId>
   private Map<Integer, Integer> objectToBranchLockCache;
   // object, subject
   private Map<Integer, Integer> lockedObjectToSubject;
   // subject, permission
   private HashCollection<Integer, PermissionEnum> subjectToPermissionCache;

   private ArtifactPersistenceManager artifactManager;
   private BranchPersistenceManager branchManager;
   private SkynetEventManager eventManager;
   private SkynetAuthentication skynetAuth;

   private static final AccessControlManager instance = new AccessControlManager();

   public static AccessControlManager getInstance() {
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.PersistenceManager#onManagerWebInit()
    */
   public void onManagerWebInit() throws Exception {
      artifactManager = ArtifactPersistenceManager.getInstance();
      branchManager = BranchPersistenceManager.getInstance();
      eventManager = SkynetEventManager.getInstance();
      skynetAuth = SkynetAuthentication.getInstance();

      // This can result in a call to SkynetAuthentication, so it must be here (instead of in the
      // constructor) to stop a cycle from occurring.
      populateAccessControlLists();
   }

   private AccessControlManager() {
      this.accessControlListCache = new DoubleKeyHashMap<Integer, Object, PermissionEnum>();
      this.accessObjectCache = new DoubleKeyHashMap<Integer, Integer, AccessObject>();
      this.objectToSubjectCache = new HashCollection<AccessObject, Integer>();
      this.branchAccessObjectCache = new HashMap<Integer, BranchAccessObject>();
      this.subjectToGroupCache = new HashCollection<Integer, Integer>();
      this.groupToSubjectsCache = new HashCollection<Integer, Integer>();
      this.objectToBranchLockCache = new HashMap<Integer, Integer>();
      this.lockedObjectToSubject = new HashMap<Integer, Integer>();
      this.subjectToPermissionCache = new HashCollection<Integer, PermissionEnum>();
   }

   /**
    * populates all of the access control lists.
    */
   private void populateAccessControlLists() {
      populateArtifactAccessControlList();
      populateBranchAccessControlList();
      // populateSubjectsAccessControlList();
   }

   // TODO:: Enable Subject Based Control
   // /**
   // * populates subjects access control list
   // */
   // private void populateSubjectsAccessControlList() {
   // ConnectionHandlerStatement chStmt = null;
   // // select * from table order by subject_id
   // // subject_id, permission_id
   // try {
   // while (chStmt.next()) {
   // subjectToPermissionCache.put(chStmt.getRset().getInt("subject_id"),
   // PermissionEnum.getPermission(chStmt.getRset().getInt("permission_id")));
   // }
   // }
   // catch (SQLException ex) {
   // logger.log(Level.SEVERE, ex.toString(), ex);
   // }
   // finally {
   // DbUtil.close(chStmt);
   // }
   // }

   /**
    * populates the branch access control list.
    */
   private void populateBranchAccessControlList() {
      ConnectionHandlerStatement chStmt = null;
      BranchAccessObject branchAccessObject = null;
      String subjectName;
      Integer subjectId;
      Integer branchId;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(GET_ALL_BRANCH_ACCESS_CONTROL_LIST);

         while (chStmt.next()) {
            subjectId = chStmt.getRset().getInt("privilege_entity_id");
            branchId = chStmt.getRset().getInt("branch_id");
            subjectName = chStmt.getRset().getString("name");
            PermissionEnum permission = PermissionEnum.getPermission(chStmt.getRset().getInt("permission_id"));
            branchAccessObject = getBranchAccessObject(branchId);

            accessControlListCache.put(subjectId, branchAccessObject, permission);
            objectToSubjectCache.put(branchAccessObject, subjectId);

            if (subjectName.equals("User Group")) {
               populateGroupMembers(subjectId);
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * popualtes the artifact access control list cache
    */
   private void populateArtifactAccessControlList() {
      ConnectionHandlerStatement chStmt = null;
      Integer subjectId = null;
      Integer objectId = null;
      Integer branchId = null;
      String subjectName = "";
      PermissionEnum permission;
      AccessObject accessObject;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST);

         while (chStmt.next()) {
            subjectId = chStmt.getRset().getInt("privilege_entity_id");
            objectId = chStmt.getRset().getInt("art_id");
            branchId = chStmt.getRset().getInt("branch_id");
            subjectName = chStmt.getRset().getString("name");
            permission = PermissionEnum.getPermission(chStmt.getRset().getInt("permission_id"));

            if (permission.equals(PermissionEnum.LOCK)) {
               objectToBranchLockCache.put(objectId, branchId);
               lockedObjectToSubject.put(objectId, subjectId);
            } else {
               accessObject = getArtifactAccessObject(objectId, branchId);
               cacheAccessObject(objectId, subjectId, permission, accessObject);

               if (subjectName.equals("User Group")) {
                  populateGroupMembers(subjectId);
               }
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void populateGroupMembers(Integer groupId) throws SQLException {
      if (!groupToSubjectsCache.containsKey(groupId)) {
         Integer groupMember;

         ConnectionHandlerStatement chStmt =
               ConnectionHandler.runPreparedQuery(USER_GROUP_MEMBERS, SQL3DataType.INTEGER, groupId);

         // get group members and populate subjectToGroupCache
         while (chStmt.next()) {
            groupMember = chStmt.getRset().getInt("user_id");
            subjectToGroupCache.put(groupMember, groupId);
            groupToSubjectsCache.put(groupId, groupMember);
         }
         DbUtil.close(chStmt);
      }
   }

   /**
    * Checks permission for the subject.
    * 
    * @param permission
    * @return true if the subject has permission.
    */
   public boolean checkSubjectPermission(PermissionEnum permission) {
      return checkObjectPermission(skynetAuth.getAuthenticatedUser(), permission);
   }

   /**
    * Checks permission for the subject.
    * 
    * @param subject
    * @param permission
    * @return true if the subject has permission.
    */
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

   public boolean checkObjectListPermission(List<?> objectList, PermissionEnum permission) {
      boolean isValid = true;

      if (objectList.isEmpty()) {
         isValid = false;
      }

      for (Object object : objectList) {
         isValid &= checkObjectPermission(object, permission);
      }
      return isValid;
   }

   /**
    * Check permissions for the currently authenticated user.
    * 
    * @param object
    * @param permission
    * @return true if the subject has permission for an object else false.
    */
   public boolean checkObjectPermission(Object object, PermissionEnum permission) {
      return skynetAuth.duringUserCreation() || checkObjectPermission(skynetAuth.getAuthenticatedUser(), object,
            permission);
   }

   public boolean checkCurrentUserObjectPermission(Object object, PermissionEnum permission) {
      return checkObjectPermission(SkynetAuthentication.getInstance().getAuthenticatedUser(), object, permission);
   }

   public PermissionEnum getObjectPermission(Artifact subject, Object object) {
      for (PermissionEnum permissionEnum : PermissionEnum.values()) {
         boolean result = checkObjectPermission(subject, object, permissionEnum);
         System.out.println("subject " + subject + " object " + object + " permission " + permissionEnum.name() + " -> " + result);
         if (result) return permissionEnum;
      }
      return PermissionEnum.FULLACCESS;
   }

   /**
    * @param subject
    * @param object
    * @param permission
    * @return true if the subject has permission for an object else false.
    */
   public boolean checkObjectPermission(Artifact subject, Object object, PermissionEnum permission) {
      PermissionEnum userPermission = null;
      PermissionEnum branchPermission = null;
      Branch branch = null;

      if (object instanceof Artifact) {
         Artifact artifact = (Artifact) object;
         branch = artifact.getBranch();
         userPermission = getArtifactPermission(subject, (Artifact) object, permission);
      } else if (object instanceof Branch) {
         branch = (Branch) object;
      } else
         throw new IllegalStateException("Unhandled object type for access control - " + object);

      branchPermission = getBranchPermission(subject, branch, permission);

      if (branchPermission != null && (branchPermission.equals(PermissionEnum.DENY) || (userPermission == null && userPermission != PermissionEnum.LOCK))) {
         userPermission = branchPermission;
      }

      if (permission == PermissionEnum.READ && userPermission == PermissionEnum.LOCK) {
         return true;
      }

      if (userPermission == null || userPermission == PermissionEnum.LOCK) {
         return false;
      }
      return userPermission.getRank() >= permission.getRank() && !userPermission.equals(PermissionEnum.DENY);
   }

   public PermissionEnum getBranchPermission(Artifact subject, Branch branch, PermissionEnum permission) {
      PermissionEnum userPermission = null;
      AccessObject accessObject = null;
      accessObject = branchAccessObjectCache.get(branch.getBranchId());

      if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else {
         userPermission = acquirePermissionRank(subject, accessObject, permission);
      }
      return userPermission;
   }

   public PermissionEnum getArtifactPermission(Artifact subject, Artifact artifact, PermissionEnum permission) {
      PermissionEnum userPermission = null;
      AccessObject accessObject = null;

      // The artifact is new and has not been persisted.
      if (artifact.getPersistenceMemo() == null) return PermissionEnum.FULLACCESS;

      Integer artId = artifact.getArtId();
      Integer branchId = artifact.getBranch().getBranchId();
      Integer lockedBranchId;

      accessObject = accessObjectCache.get(artId, branchId);

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

   /**
    * @return The subjects permission on an accessObject.
    */
   private PermissionEnum acquirePermissionRank(Artifact subject, AccessObject accessObject, PermissionEnum permission) {
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

   public void persistPermission(AccessControlData data) {
      persistPermission(data, false);
   }

   /**
    * Sets a permission on the object for the subject.
    * 
    * @param subject
    * @param object
    * @param permission
    */
   public void setPermission(Artifact subject, Object object, PermissionEnum permission) {
      AccessObject accessObject = getAccessObject(object);

      boolean newAccessControlData = !accessControlListCache.containsKey(subject.getArtId(), accessObject);

      if (newAccessControlData || permission != accessControlListCache.get(subject.getArtId(), accessObject)) {
         AccessControlData data = new AccessControlData(subject, accessObject, permission, newAccessControlData);
         data.persist();
      }
   }

   /**
    * persists an AccessControlData. This should be used internal to the AccessControl framework.
    * 
    * @param data
    */
   protected void persistPermission(AccessControlData data, boolean recurse) {
      Artifact subject = data.getSubject();
      PermissionEnum permission = data.getPermission();

      if (data.isDirty()) {
         data.setNotDirty();

         try {
            if (data.getObject() instanceof ArtifactAccessObject) {
               ArtifactAccessObject artifactAccessObject = (ArtifactAccessObject) data.getObject();

               if (data.isBirth()) {
                  ConnectionHandler.runPreparedUpdate(INSERT_INTO_ARTIFACT_ACL, SQL3DataType.INTEGER,
                        artifactAccessObject.getArtId(), SQL3DataType.INTEGER, data.getPermission().getPermId(),
                        SQL3DataType.INTEGER, data.getSubject().getArtId(), SQL3DataType.INTEGER,
                        artifactAccessObject.getBranchId());
               } else {
                  ConnectionHandler.runPreparedUpdate(UPDATE_ARTIFACT_ACL, SQL3DataType.INTEGER,
                        data.getPermission().getPermId(), SQL3DataType.INTEGER, data.getSubject().getArtId(),
                        SQL3DataType.INTEGER, artifactAccessObject.getArtId(), SQL3DataType.INTEGER,
                        artifactAccessObject.getBranchId());
               }

               if (recurse) {
                  Artifact artifact =
                        artifactManager.getArtifactFromId(artifactAccessObject.getArtId(),
                              branchManager.getBranch(artifactAccessObject.getBranchId()));
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
                  ConnectionHandler.runPreparedUpdate(INSERT_INTO_BRANCH_ACL, SQL3DataType.INTEGER,
                        data.getPermission().getPermId(), SQL3DataType.INTEGER, data.getSubject().getArtId(),
                        SQL3DataType.INTEGER, branchAccessObject.getBranchId());
               } else {
                  ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_ACL, SQL3DataType.INTEGER,
                        data.getPermission().getPermId(), SQL3DataType.INTEGER, data.getSubject().getArtId(),
                        SQL3DataType.INTEGER, branchAccessObject.getBranchId());
               }
            }
            cacheAccessControlData(data);
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   private void cacheAccessControlData(AccessControlData data) {
      AccessObject accessObject = data.getObject();
      int subjectId = data.getSubject().getArtId();
      PermissionEnum permission = data.getPermission();

      if (!permission.equals(PermissionEnum.LOCK)) {
         accessControlListCache.put(subjectId, accessObject, permission);
         objectToSubjectCache.put(accessObject, subjectId);

         try {
            populateGroupMembers(subjectId);
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   /**
    * @param object
    * @return collection of subjects and their permissions to access the object.
    */
   public List<AccessControlData> getAccessControlList(Object object) {
      List<AccessControlData> datas = new LinkedList<AccessControlData>();
      AccessObject accessObject = null;

      try {
         if (object instanceof Artifact) {
            Artifact artifact = (Artifact) object;
            accessObject =
                  (ArtifactAccessObject) accessObjectCache.get(artifact.getArtId(), artifact.getBranch().getBranchId());
         } else if (object instanceof Branch) {
            Branch branch = (Branch) object;
            accessObject = branchAccessObjectCache.get(branch.getBranchId());
         }

         if (accessObject == null) return datas;

         Collection<Integer> subjects = objectToSubjectCache.getValues(accessObject);
         if (subjects == null) {
            return datas;
         }

         for (int subjectId : subjects) {
            Artifact subject = artifactManager.getArtifactFromId(subjectId, branchManager.getCommonBranch());
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
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return datas;
   }

   private PermissionEnum getBranchPermission(Artifact subject, Object object) throws SQLException {
      Branch branch = null;
      if (object instanceof BranchAccessObject) {
         branch = BranchPersistenceManager.getInstance().getBranch(((BranchAccessObject) object).getBranchId());
      } else if (object instanceof ArtifactAccessObject) {
         branch = BranchPersistenceManager.getInstance().getBranch(((ArtifactAccessObject) object).getBranchId());
      }
      if (branch == null) return null;
      return AccessControlManager.getInstance().getBranchPermission(subject, branch, PermissionEnum.FULLACCESS);
   }

   /**
    * Remove an item from their access control list
    * 
    * @param data
    */
   public void removeAccessControlData(AccessControlData data) {
      try {
         if (data.getObject() instanceof ArtifactAccessObject) {
            ArtifactAccessObject object = (ArtifactAccessObject) data.getObject();
            ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_ACL, SQL3DataType.INTEGER,
                  data.getSubject().getArtId(), SQL3DataType.INTEGER, object.getArtId(), SQL3DataType.INTEGER,
                  object.getBranchId());

         } else if (data.getObject() instanceof BranchAccessObject) {
            BranchAccessObject object = (BranchAccessObject) data.getObject();
            ConnectionHandler.runPreparedUpdate(DELETE_BRANCH_ACL, SQL3DataType.INTEGER, data.getSubject().getArtId(),
                  SQL3DataType.INTEGER, object.getBranchId());
         }
         deCacheAccessControlData(data);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   /**
    * @param data
    */
   private void deCacheAccessControlData(AccessControlData data) {
      if (data == null) throw new IllegalArgumentException("Can not remove a null AccessControlData.");

      AccessObject accessObject = (AccessObject) data.getObject();
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
         if (accessObject instanceof ArtifactAccessObject) {
            ArtifactAccessObject artifactAccessObject = (ArtifactAccessObject) accessObject;
            accessObjectCache.remove(artifactAccessObject.getArtId(), artifactAccessObject.getBranchId());
         } else if (accessObject instanceof BranchAccessObject) {
            BranchAccessObject branchAccessObject = (BranchAccessObject) accessObject;
            branchAccessObjectCache.remove(branchAccessObject.getBranchId());
         }
      }
   }

   /**
    * @param object
    */
   public AccessObject getAccessObject(Object object) {
      AccessObject accessObject = null;

      if (object instanceof Branch) {
         accessObject = getBranchAccessObject(((Branch) object).getBranchId());
      } else if (object instanceof Artifact) {
         Artifact artifact = (Artifact) object;
         Integer objectId = artifact.getArtId();
         Integer branchId = artifact.getBranch().getBranchId();

         accessObject = getArtifactAccessObject(objectId, branchId);
      }
      return accessObject;
   }

   private ArtifactAccessObject getArtifactAccessObject(Integer objectId, Integer branchId) {
      ArtifactAccessObject accessObject = (ArtifactAccessObject) getAccessObjectIfCached(objectId, branchId);

      if (accessObject == null) {
         accessObject = new ArtifactAccessObject(objectId, branchId);
         accessObjectCache.put(objectId, branchId, accessObject);
      }
      return accessObject;
   }

   private AccessObject getAccessObjectIfCached(Integer objectId, Integer branchId) {
      AccessObject accessObject = null;

      if (accessObjectCache.containsKey(objectId, branchId)) {
         accessObject = accessObjectCache.get(objectId, branchId);
      }
      return accessObject;
   }

   private BranchAccessObject getBranchAccessObject(Integer branchId) {
      BranchAccessObject branchAccessObject;
      if (branchAccessObjectCache.containsKey(branchId)) {
         branchAccessObject = branchAccessObjectCache.get(branchId);
      } else {
         branchAccessObject = new BranchAccessObject(branchId);
         branchAccessObjectCache.put(branchId, branchAccessObject);
      }
      return branchAccessObject;
   }

   private void cacheAccessObject(Integer objectId, Integer subjectId, PermissionEnum permission, AccessObject accessObject) {
      accessControlListCache.put(subjectId, accessObject, permission);
      objectToSubjectCache.put(accessObject, subjectId);
   }

   public void lockObject(Artifact object, Artifact subject) {
      Integer objectArtId = object.getArtId();
      Integer subjectArtId = subject.getArtId();
      Integer objectBranchId = object.getBranch().getBranchId();

      if (!objectToBranchLockCache.containsKey(objectArtId)) {
         AccessObject accessObject = getAccessObject(object);
         new AccessControlData(subject, accessObject, PermissionEnum.LOCK, true).persist();
         objectToBranchLockCache.put(objectArtId, objectBranchId);
         lockedObjectToSubject.put(objectArtId, subjectArtId);

         eventManager.kick(new ArtifactLockStatusChanged(this, object));
      }
   }

   public void unLockObject(Artifact object, Artifact subject) {
      Integer objectArtId = object.getArtId();
      Integer branchId = object.getBranch().getBranchId();
      Integer lockedBranchId;

      if (objectToBranchLockCache.containsKey(objectArtId) && canUnlockObject(object, subject)) {
         lockedBranchId = objectToBranchLockCache.get(objectArtId);

         if (branchId.equals(lockedBranchId)) {
            AccessObject accessObject = getAccessObject(object);
            removeAccessControlData(new AccessControlData(subject, accessObject, PermissionEnum.LOCK, false));
            objectToBranchLockCache.remove(objectArtId);
            lockedObjectToSubject.remove(objectArtId);

            eventManager.kick(new ArtifactLockStatusChanged(this, object));
         }
      }
   }

   /**
    * Removes all locks from a branch
    * 
    * @throws SQLException
    */
   public void removeAllPermissionsFromBranch(final Branch branch) throws Exception {
      AbstractDbTxTemplate dbTxWrapper = new AbstractDbTxTemplate() {
         @Override
         protected void handleTxWork() throws Exception {
            ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, SQL3DataType.INTEGER,
                  branch.getBranchId());
            ConnectionHandler.runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, SQL3DataType.INTEGER,
                  branch.getBranchId());
         }

      };
      dbTxWrapper.execute();
   }

   /**
    * @return - Returns true if the object has been locked on any branch.
    */
   public boolean hasLock(Artifact object) {
      if (object.getPersistenceMemo() == null) return false;

      return objectToBranchLockCache.containsKey(object.getArtId());
   }

   /**
    * @return - Returns true if the subject locked the object else false.
    */
   public boolean canUnlockObject(Artifact object, Artifact subject) {
      Integer subjectId = lockedObjectToSubject.get(object.getArtId());
      return subjectId != null && subjectId.intValue() == subject.getArtId();
   }

   /**
    * @return - Returns the subject who has the artifact locked or null if the object is not locked.
    */
   public Artifact getSubjectFromLockedObject(Object object) {
      Artifact subject = null;

      if (object instanceof Artifact) {
         Integer subjectArtId = lockedObjectToSubject.get(((Artifact) object).getArtId());

         if (subjectArtId != null) {
            subject = skynetAuth.getUserByArtId(subjectArtId);
         }
      }
      return subject;
   }

   /**
    * @return Returns true if the object is being accessed on the same branch it was locked on.
    */
   public boolean hasLockAccess(Artifact object) {
      boolean hasAccess = false;

      if (object.getPersistenceMemo() == null) return true;

      if (hasLock(object)) {
         hasAccess = objectToBranchLockCache.get(object.getArtId()) == object.getBranch().getBranchId();
      }
      return hasAccess;
   }
}
