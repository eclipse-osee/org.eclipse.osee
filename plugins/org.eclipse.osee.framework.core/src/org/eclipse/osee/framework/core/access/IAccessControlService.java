/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.core.access;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.access.event.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroupService;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Roberto E. Escobar
 */
public interface IAccessControlService extends ArtifactCheck {

   public static final String DEBUG_BRANCH_ACCESS = "DebugBranchAccess";

   ////////////////////////////////////
   // Store Permission
   ////////////////////////////////////

   void persistPermission(AccessControlData data);

   void persistPermission(AccessControlData data, boolean recurse);

   void setPermission(ArtifactToken subject, ArtifactToken artifact, PermissionEnum permission);

   void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data);

   ////////////////////////////////////
   // Branch Permission
   ////////////////////////////////////

   XResultData isModifyAccessEnabled(ArtifactToken subject, BranchToken branch, XResultData rd);

   PermissionEnum getPermission(BranchToken branch);

   void removePermissions(BranchId branch);

   void setPermission(ArtifactToken subject, BranchId branch, PermissionEnum permissionEnum);

   XResultData hasBranchPermission(BranchToken branch, PermissionEnum permission, XResultData rd);

   XResultData hasBranchPermission(ArtifactToken subject, BranchToken branch, PermissionEnum permission, XResultData rd);

   ////////////////////////////////////
   // Artifact Permission
   ////////////////////////////////////

   XResultData isModifyAccessEnabled(ArtifactToken subject, ArtifactToken artifact, XResultData rd);

   PermissionEnum getPermission(ArtifactToken artifact);

   /**
    * Check rollup permission for current user including Branch, ACL and Branch Context. Preferred method.
    */
   XResultData hasArtifactPermission(ArtifactToken artifact, PermissionEnum permission, XResultData rd);

   // Check rollup permission for current user including Branch ACL, Artifact ACL and Branch Context.
   XResultData hasArtifactPermission(Collection<? extends ArtifactToken> artifacts, PermissionEnum permission, XResultData rd);

   // Check rollup permission for current user including Branch ACL, Artifact ACL and Branch Context.
   XResultData hasArtifactPermission(ArtifactToken subject, Collection<? extends ArtifactToken> artifact, PermissionEnum permission, XResultData rd);

   // Return artifact or sentinel
   ArtifactToken getSubjectFromLockedObject(ArtifactToken artifact);

   void lockArtifacts(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts);

   void unLockArtifacts(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts);

   boolean canUnlockObject(ArtifactToken subject, ArtifactToken artifact);

   boolean hasLock(ArtifactToken artifact);

   ////////////////////////////////////
   // Attribute Type Permission
   ////////////////////////////////////

   /**
    * Check rollup permission for current user including Branch ACL, Artifact ACL and Branch Context for Attr Type
    */
   XResultData hasAttributeTypePermission(Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, PermissionEnum permission, XResultData rd);

   /**
    * Check rollup permission for current user including Branch ACL, Artifact ACL and Branch Context for Attr Type
    */
   XResultData hasAttributeTypePermission(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, PermissionEnum permission, XResultData rd);

   ////////////////////////////////////
   // Relation Permission
   ////////////////////////////////////

   /**
    * Check rollup permission for current user including Branch ACL, Artifact ACL and Branch Context for Rel Type
    *
    * @param related list to relate or null to check permission without specific artifacts
    */
   XResultData hasRelationTypePermission(ArtifactToken user, ArtifactToken artifact, RelationTypeToken relationType, Collection<? extends ArtifactToken> related, PermissionEnum permission, XResultData rd);

   /**
    * Check rollup permission for current user including Branch ACL, Artifact ACL and Branch Context for Rel Type
    *
    * @param related list to relate or null to check permission without specific artifacts
    */
   XResultData hasRelationTypePermission(ArtifactToken artifact, RelationTypeToken relationType, Collection<? extends ArtifactToken> related, PermissionEnum permission, XResultData rd);

   ////////////////////////////////////
   // Supporting Methods
   ////////////////////////////////////

   boolean isInDb(ArtifactToken artifact);

   boolean isReadOnly(ArtifactToken artifact);

   boolean isOseeAdmin();

   void ensurePopulated();

   void clearCaches();

   void reloadCache();

   List<AccessControlData> getAccessControlList(Object object);

   ArtifactToken getUserByArtId(ArtifactId subjectArtId);

   Collection<IOseeAccessProvider> getOseeAccessProviders();

   void kickAccessTopicEvent(AccessTopicEventPayload event);

   IUserGroupService getUserGroupService();

   Collection<ArtifactToken> getArtifactListFromType(ArtifactTypeToken artType, BranchToken branch);

   boolean isBaselineBranch(BranchToken branch);

   void populateGroupMembers(ArtifactId groupId);

   ArtifactToken getUser();

}
