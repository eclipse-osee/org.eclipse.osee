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
package org.eclipse.osee.framework.skynet.core.access.internal;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.access.AccessQueries;
import org.eclipse.osee.framework.core.access.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.access.object.AccessObject;
import org.eclipse.osee.framework.core.access.object.ArtifactAccessObject;
import org.eclipse.osee.framework.core.access.object.BranchAccessObject;
import org.eclipse.osee.framework.core.access.operation.AccessCache;
import org.eclipse.osee.framework.core.access.operation.IAccessStoreOperations;
import org.eclipse.osee.framework.core.client.AccessTopicEvent;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;

/**
 * Much of this code is a duplicate of the other AccessStoreOperation. Once more is shared, it can be merged into an
 * abstract instead.
 *
 * @author Donald G. Dunne
 */
public class AccessStoreOperations implements IAccessStoreOperations {

   private AccessCache cache;
   private final JdbcClient jdbcClient;

   public AccessStoreOperations(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   @Override
   public void removePermissions(BranchId branch) {
      jdbcClient.runPreparedUpdate(AccessQueries.DELETE_ARTIFACT_ACL_FROM_BRANCH, branch);
      jdbcClient.runPreparedUpdate(AccessQueries.DELETE_BRANCH_ACL_FROM_BRANCH, branch);

      try {
         AccessTopicEventPayload event = new AccessTopicEventPayload();
         event.setBranch(branch);
         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_BRANCH_MODIFIED);
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   @Override
   public void setPermission(ArtifactToken subject, BranchId branch, PermissionEnum permission) {
      Conditions.assertFalse(CoreBranches.COMMON.equals(branch), "Can not set permissions on Common branch.");

      AccessObject accessObject = AccessObject.valueOf(branch);

      boolean newAccessControlData = !cache.accessControlListCache.containsKey(subject.getId(), accessObject);

      if (newAccessControlData || permission != cache.accessControlListCache.get(subject.getId(), accessObject)) {
         AccessControlData data = new AccessControlData(subject, accessObject, permission, newAccessControlData);
         persistPermission(data);
      }
   }

   @Override
   public void setPermission(ArtifactToken subject, ArtifactToken artifact, PermissionEnum permission) {
      AccessObject accessObject = AccessObject.valueOf(artifact);

      boolean newAccessControlData = !cache.accessControlListCache.containsKey(subject.getId(), accessObject);

      if (newAccessControlData || permission != cache.accessControlListCache.get(subject.getId(), accessObject)) {
         AccessControlData data = new AccessControlData(subject, accessObject, permission, newAccessControlData);
         persistPermission(data);
      }
   }

   @Override
   public void persistPermission(AccessControlData data) {
      persistPermission(data, false);
   }

   @Override
   public void persistPermission(AccessControlData data, boolean recurse) {
      if (data.getObject() instanceof ArtifactAccessObject) {

         ArtifactAccessObject artifactAccessObject = (ArtifactAccessObject) data.getObject();

         AccessTopicEventPayload event = new AccessTopicEventPayload();
         event.setBranch(artifactAccessObject.getBranch());

         persistPermissionForArtifact(data, artifactAccessObject, recurse, event);
         cache.cacheAccessControlData(data);

         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_ARTIFACT_MODIFIED);

      } else if (data.getObject() instanceof BranchAccessObject) {

         BranchAccessObject branchAccessObject = (BranchAccessObject) data.getObject();

         org.eclipse.osee.framework.core.client.OseeClient client = ServiceUtil.getOseeClient();
         BranchEndpoint proxy = client.getBranchEndpoint();
         proxy.setBranchPermission(data.getSubject(), branchAccessObject.getBranch(), data.getPermission());

         cache.cacheAccessControlData(data);

         AccessTopicEventPayload event = new AccessTopicEventPayload();
         event.setBranch(branchAccessObject.getBranch());
         OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_BRANCH_MODIFIED);

      }
   }

   private void persistPermissionForArtifact(AccessControlData data, ArtifactAccessObject artifactAccessObject,
      boolean recurse, AccessTopicEventPayload event) {
      ArtifactToken subject = data.getSubject();
      PermissionEnum permission = data.getPermission();

      if (data.isDirty()) {
         data.setNotDirty();

         try {

            if (data.isBirth()) {
               jdbcClient.runPreparedUpdate(AccessQueries.INSERT_INTO_ARTIFACT_ACL, artifactAccessObject,
                  data.getPermission().getPermId(), data.getSubject().getId(), artifactAccessObject.getBranch());
            } else {
               jdbcClient.runPreparedUpdate(AccessQueries.UPDATE_ARTIFACT_ACL, data.getPermission().getPermId(),
                  data.getSubject().getId(), artifactAccessObject, artifactAccessObject.getBranch());
            }
            event.addArtifact(artifactAccessObject.getArtifact());

            if (recurse) {
               Artifact artifact = ArtifactQuery.getArtifactFromToken(artifactAccessObject);

               for (Artifact child : artifact.getChildren()) {
                  AccessControlData childAccessControlData = null;
                  AccessObject childAccessObject = AccessObject.valueOf(child);

                  if (cache.objectToSubjectCache.containsKey(childAccessObject)) {
                     Collection<ArtifactId> subjectIds = cache.objectToSubjectCache.getValues(childAccessObject);

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
            OseeLog.log(getClass(), Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) {

      AccessObject accessControlledObject = data.getObject();
      boolean isArtifact = accessControlledObject instanceof ArtifactAccessObject;
      if (removeFromDb) {
         removeFromDatabase(accessControlledObject, data.getSubject());
      }

      if (accessControlledObject instanceof ArtifactAccessObject) {
         accessControlledObject.removeFromCache();
      }
      cache.deCacheAccessControlData(data);

      AccessTopicEventPayload event = new AccessTopicEventPayload();
      event.setBranch(accessControlledObject.getBranch());
      if (isArtifact) {
         event.addArtifact(((ArtifactAccessObject) accessControlledObject).getArtifact());
      }

      OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_ARTIFACT_MODIFIED);

   }

   @Override
   public void removeFromDatabase(AccessObject accessControlledObject, ArtifactId subjectId) {
      if (accessControlledObject.isArtifact()) {
         final String DELETE_ARTIFACT_ACL =
            "DELETE FROM OSEE_ARTIFACT_ACL WHERE privilege_entity_id = ? AND art_id =? AND branch_id =?";
         jdbcClient.runPreparedUpdate(DELETE_ARTIFACT_ACL, subjectId,
            ArtifactId.valueOf(accessControlledObject.getId()), accessControlledObject.getBranch());
      } else if (accessControlledObject.isBranch()) {
         final String DELETE_BRANCH_ACL = "DELETE FROM OSEE_BRANCH_ACL WHERE privilege_entity_id = ? AND branch_id =?";
         jdbcClient.runPreparedUpdate(DELETE_BRANCH_ACL, subjectId, BranchId.valueOf(accessControlledObject.getId()));
      }
   }

   @Override
   public void setCache(AccessCache cache) {
      this.cache = cache;
   }

}