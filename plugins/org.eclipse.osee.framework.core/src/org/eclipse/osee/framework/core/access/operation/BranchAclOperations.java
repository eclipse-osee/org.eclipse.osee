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
package org.eclipse.osee.framework.core.access.operation;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.access.object.BranchAccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class BranchAclOperations {

   private final AccessCache cache;
   private final AccessRankOperations rankOps;
   private final IAccessControlService accessService;

   public BranchAclOperations(AccessCache cache, AccessRankOperations rankOps, IAccessControlService accessService) {
      this.cache = cache;
      this.rankOps = rankOps;
      this.accessService = accessService;
   }

   public XResultData hasBranchAclPermission(BranchToken branch, PermissionEnum permission, XResultData rd) {
      return hasBranchAclPermission(accessService.getUser(), branch, permission, rd);
   }

   public XResultData hasBranchAclPermission(ArtifactToken subject, BranchToken branch, PermissionEnum permission, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      if (branch.isInvalid()) {
         rd.errorf("Branch ACL: Branch [%s] is InValid", branch.getIdString());
         return rd;
      }
      if (subject.equals(SystemUser.BootStrap)) {
         rd.logf("Branch ACL: Subject [%s] DOES have [%s] access for branch %s with permission [BOOTSTRAP]\n",
            subject.getName(), permission, branch.toStringWithId());
         return rd;
      }

      PermissionEnum accessPermissionEnum = getBranchPermission(subject, branch);

      if (!accessPermissionEnum.equals(PermissionEnum.DENY) && permission.getRank() <= accessPermissionEnum.getRank()) {
         rd.logf("Branch ACL: Subject [%s] DOES have [%s] access for branch %s with permission [%s]\n",
            subject.getName(), permission, branch.toStringWithId(), accessPermissionEnum);
      } else {
         rd.errorf("Branch ACL: Subject [%s] DOES NOT have [%s] access for branch %s; permission [%s]\n",
            subject.getName(), permission, branch.toStringWithId(), accessPermissionEnum);
      }
      return rd;
   }

   public PermissionEnum getBranchPermission(ArtifactToken subject, BranchToken branch) {
      PermissionEnum userPermission = null;
      BranchAccessObject accessObject = BranchAccessObject.valueOf(branch);

      if (accessObject == null && branch.notEqual(COMMON) && accessService.isBaselineBranch(branch)) {
         userPermission = PermissionEnum.READ;
      } else if (accessObject == null) {
         userPermission = PermissionEnum.FULLACCESS;
      } else {
         userPermission = rankOps.acquirePermissionRank(subject, accessObject);
      }
      return userPermission;
   }

   public void populateBranchAccessControlListEntry(ArtifactId subjectId, ArtifactTypeToken subjectArtifactType, BranchToken branch, PermissionEnum permission) {
      BranchAccessObject branchAccessObject = BranchAccessObject.valueOf(branch);

      cache.accessControlListCache.put(subjectId.getId(), branchAccessObject, permission);
      cache.objectToSubjectCache.put(branchAccessObject, subjectId);

      if (subjectArtifactType.inheritsFrom(CoreArtifactTypes.UserGroup)) {
         accessService.populateGroupMembers(subjectId);
      }
   }

}
