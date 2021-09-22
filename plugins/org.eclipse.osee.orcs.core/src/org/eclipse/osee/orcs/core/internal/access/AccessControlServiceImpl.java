/*********************************************************************
 * Copyright (c) 2019 Boeing
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
package org.eclipse.osee.orcs.core.internal.access;

import java.util.Collection;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.access.AbstractAccessControlService;
import org.eclipse.osee.framework.core.access.AccessQueries;
import org.eclipse.osee.framework.core.access.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * This service in not implement (on the server) as an OSGi component so that OrcsApi can reference it even through
 * OrcsApi is also a dependency of this service.
 *
 * @author Donald G. Dunne
 */
public class AccessControlServiceImpl extends AbstractAccessControlService {

   protected JdbcClient jdbcClient;
   private final OrcsApi orcsApi;
   private final OrcsTokenService tokenService;

   public AccessControlServiceImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.tokenService = orcsApi.tokenService();
      this.jdbcClient = orcsApi.getJdbcService().getClient();
      setStoreOperations(new AccessStoreOperations(orcsApi));
   }

   // Caches not needed on server, load as needed
   @Override
   public synchronized void ensurePopulated() {
      cache.initializeCaches();
      populateArtifactAccessControlList();
      populateBranchAccessControlList();
   }

   @Override
   public void removePermissions(BranchId branch) {
      jdbcClient.runPreparedUpdate(AccessQueries.DELETE_ARTIFACT_ACL_FROM_BRANCH, branch);
      jdbcClient.runPreparedUpdate(AccessQueries.DELETE_BRANCH_ACL_FROM_BRANCH, branch);
      // NOTE: No events are produced, so the IDE clients will not be notified.  Fix this when servers talk to clients.
   }

   @Override
   public boolean isReadOnly(ArtifactToken artifact) {
      try {
         if (artifact instanceof ArtifactReadable) {
            boolean deleted = ((ArtifactReadable) artifact).isDeleted();
            boolean historical = ((ArtifactReadable) artifact).isHistorical();
            boolean hasPermission = hasArtifactPermission(artifact, PermissionEnum.WRITE, null).isSuccess();
            return deleted || historical || !hasPermission;
         }
         return true;
      } catch (OseeCoreException ex) {
         return true;
      }
   }

   @Override
   public boolean isOseeAdmin() {
      return orcsApi.userService().getUserGroup(CoreUserGroups.OseeAdmin).isCurrentUserMember();
   }

   @Override
   public ArtifactToken getUserByArtId(ArtifactId subjectArtId) {
      return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(subjectArtId).getArtifactOrNull();
   }

   @Override
   public void kickAccessTopicEvent(AccessTopicEventPayload event) {
      // do nothing
   }

   @Override
   public Collection<ArtifactToken> getArtifactListFromType(ArtifactTypeToken artType, BranchToken branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artType).asArtifactTokens();
   }

   @Override
   public boolean isBaselineBranch(BranchToken branch) {
      ResultSet<Branch> branches = orcsApi.getQueryFactory().branchQuery().andId(branch).getResults();
      if (branches.size() == 1) {
         return branches.iterator().next().getBranchType().isBaselineBranch();
      }
      return false;
   }

   @Override
   protected boolean isInDbInit() {
      return OseeProperties.isInDbInit();
   }

   @Override
   public boolean isInDb(ArtifactToken artifact) {
      return ((ArtifactReadable) artifact).getTransaction().equals(TransactionId.SENTINEL);
   }

   @Override
   public ArtifactToken getUser() {
      return orcsApi.userService().getUser();
   }

   @Override
   public ArtifactToken getArtifactFromId(ArtifactId subjectId, BranchToken branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andId(subjectId).asArtifactTokenOrSentinel();
   }

   @Override
   public void populateBranchAccessControlList() {
      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId subjectId = ArtifactId.valueOf(stmt.getLong("privilege_entity_id"));
         BranchId branchId = BranchId.valueOf(stmt.getLong("branch_id"));
         BranchToken branch = orcsApi.getQueryFactory().branchQuery().andId(branchId).getOneOrSentinel();
         if (branch.isValid()) {
            PermissionEnum permission = PermissionEnum.getPermission(stmt.getInt("permission_id"));
            ArtifactTypeToken subjectArtifactType = tokenService.getArtifactType(stmt.getLong("art_type_id"));
            brchAclOps.populateBranchAccessControlListEntry(subjectId, subjectArtifactType, branch, permission);
         }
      };
      jdbcClient.runQuery(consumer, AccessQueries.GET_ALL_BRANCH_ACCESS_CONTROL_LIST);
   }

   @Override
   public void populateArtifactAccessControlList() {
      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId subjectId = UserId.valueOf(stmt.getLong("privilege_entity_id"));
         ArtifactId artifactId = ArtifactId.valueOf(stmt.getLong("art_id"));
         BranchId branchId = BranchId.valueOf(stmt.getLong("branch_id"));
         PermissionEnum permission = PermissionEnum.getPermission(stmt.getInt("permission_id"));
         ArtifactTypeToken subjectArtifactType = tokenService.getArtifactType(stmt.getLong("art_type_id"));

         artAclOps.populateArtifactAccessControlListEntry(subjectId, artifactId, branchId, permission,
            subjectArtifactType);
      };

      jdbcClient.runQuery(consumer, AccessQueries.GET_ALL_ARTIFACT_ACCESS_CONTROL_LIST);
   }

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
