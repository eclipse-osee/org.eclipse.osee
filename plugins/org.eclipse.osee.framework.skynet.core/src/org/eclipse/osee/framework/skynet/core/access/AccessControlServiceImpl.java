/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.skynet.core.access;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.access.AbstractAccessControlService;
import org.eclipse.osee.framework.core.access.AccessQueries;
import org.eclipse.osee.framework.core.access.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.client.AccessTopicEvent;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.internal.AccessStoreOperations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public class AccessControlServiceImpl extends AbstractAccessControlService {

   // for ReviewOsgiXml public void addOseeAccessProvider(IOseeAccessProvider provider)
   // for ReviewOsgiXml public void addArtifactCheck(ArtifactCheck artifactCheck)
   // for ReviewOsgiXml public void bindUserService(UserService userService) {

   private JdbcClient jdbcClient;
   private OrcsTokenService tokenService;

   public void bindJdbcService(JdbcService jdbcService) {
      this.jdbcClient = jdbcService.getClient();
   }

   public void bindTokenService(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   public void start() {
      setStoreOperations(new AccessStoreOperations(jdbcClient));
      storeOps.setCache(cache);
   }

   @Override
   public boolean isOseeAdmin() {
      return UserManager.getUser().isOseeAdmin();
   }

   @Override
   public ArtifactToken getUser() {
      return UserManager.getUser();
   }

   @Override
   public XResultData isDeleteable(Collection<? extends ArtifactToken> artifacts, XResultData results) {
      super.isDeleteable(artifacts, results);
      // Check for associated artifact
      for (ArtifactToken art : artifacts) {
         // Branches can only be associated with artifacts on common
         if (art.isOnBranch(CoreBranches.COMMON)) {
            Collection<BranchToken> branches = BranchManager.getBranchesByAssocArt(art);
            for (BranchToken branchTok : branches) {
               BranchState state = BranchManager.getState(branchTok);
               if (state.isCreated() || state.isModified() || state.isCommitted()) {
                  results.errorf("Cannot delete artId %s because it is the associated artifact of branch(es) %s\n",
                     art.toStringWithId(), branches.toString());
               }
            }
         }
      }
      return results;
   }

   @Override
   public boolean isBaselineBranch(BranchToken branch) {
      return BranchManager.getBranch(branch).getBranchType().isBaselineBranch();
   }

   @Override
   public ArtifactToken getUserByArtId(ArtifactId subjectArtId) {
      return UserManager.getUserByArtId(subjectArtId);
   }

   @Override
   protected boolean isInDbInit() {
      return OseeProperties.isInDbInit();
   }

   @Override
   public boolean isInDb(ArtifactToken artifact) {
      boolean isInDb = false;
      if (artifact instanceof Artifact) {
         isInDb = ((Artifact) artifact).isInDb();
      } else {
         Artifact art = ArtifactQuery.getArtifactFromToken(artifact);
         if (art != null) {
            isInDb = art.isInDb();
         }
      }
      return isInDb;
   }

   @Override
   public boolean isReadOnly(ArtifactToken artifact) {
      try {
         if (artifact instanceof Artifact) {
            boolean deleted = ((Artifact) artifact).isDeleted();
            boolean historical = ((Artifact) artifact).isHistorical();
            boolean hasPermission = hasArtifactPermission(artifact, PermissionEnum.WRITE, null).isSuccess();
            return deleted || historical || !hasPermission;
         }
         return true;
      } catch (OseeCoreException ex) {
         OseeLog.log(AbstractAccessControlService.class, Level.SEVERE, ex);
         return true;
      }
   }

   @Override
   public ArtifactToken getArtifactFromId(ArtifactId artifactId, BranchToken branch) {
      return ArtifactQuery.getArtifactTokenFromId(branch, artifactId);
   }

   @Override
   public void kickAccessTopicEvent(AccessTopicEventPayload payload) {
      try {
         OseeEventManager.kickAccessTopicEvent(this, payload, AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED);
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   @Override
   public Collection<ArtifactToken> getArtifactListFromType(ArtifactTypeToken artifactType, BranchToken branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromType(artifactType, branch));
   }

   /**
    * Branch Locks can be placed by User or by User Group</br>
    * </br>
    * subjectId / privilege_entity_id = user or user group id
    */
   @Override
   public void populateBranchAccessControlList() {
      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId subjectId = ArtifactId.valueOf(stmt.getLong("privilege_entity_id"));
         BranchId branchId = BranchId.valueOf(stmt.getLong("branch_id"));
         BranchToken branch = BranchManager.getBranch(branchId);
         PermissionEnum permission = PermissionEnum.getPermission(stmt.getInt("permission_id"));
         ArtifactTypeToken subjectArtifactType = tokenService.getArtifactType(stmt.getLong("art_type_id"));
         brchAclOps.populateBranchAccessControlListEntry(subjectId, subjectArtifactType, branch, permission);
      };
      jdbcClient.runQuery(consumer, AccessQueries.GET_ALL_BRANCH_ACCESS_CONTROL_LIST);
   }

   /**
    * Artifact Locks can be placed by User or by User Group</br>
    * </br>
    * subjectId / privilege_entity_id = user or user group id
    */
   @Override
   public void populateArtifactAccessControlList() {
      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId subjectId = UserId.valueOf(stmt.getLong("privilege_entity_id"));
         BranchToken branch = BranchManager.getBranchToken(stmt.getLong("branch_id"));
         PermissionEnum permission = PermissionEnum.getPermission(stmt.getInt("permission_id"));
         ArtifactTypeToken subjectArtifactType = tokenService.getArtifactType(stmt.getLong("art_type_id"));
         ArtifactToken artifact = ArtifactToken.valueOf(stmt.getLong("art_id"), branch);

         artAclOps.populateArtifactAccessControlListEntry(subjectId, artifact, permission, subjectArtifactType);
      };

      jdbcClient.runQuery(consumer, AccessQueries.GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST);
   }

   /**
    * Populate cache holding subjectIds for groupId
    */
   @Override
   public void populateGroupMembers(ArtifactId groupId) {
      if (!cache.groupToSubjectsCache.containsKey(groupId.getId())) {
         jdbcClient.runQuery(stmt -> {
            ArtifactId groupMember = ArtifactId.valueOf(stmt.getLong("b_art_id"));
            userGrpOps.populateGroupMembersEntry(groupId, groupMember);
         }, AccessQueries.USER_GROUP_MEMBERS, groupId, CoreRelationTypes.Users_User);
      }
   }

}
