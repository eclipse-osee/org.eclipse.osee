/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.framework.access;

import java.util.Collections;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.OseeApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class FrameworkAccessByArtifactTest {

   public static AtsApi atsApi;
   private static BranchToken reqWorkBrch;

   /**
    * With the ATS CM system, access control uses branch and artifact access control, but the context id based access
    * wins
    */
   @Test
   public void testAccessPermissionForAtsBranch() {
      ensureLoaded();

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, DemoBranches.SAW_PL);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith only has read
      XResultData rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Kay Jones has read
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Kay Jones does have full or write cause Artifact ACL and Contexts Ids do not kick in
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Same
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   @Test
   public void testAccessPermissionForAtsWorkingBranchNoContextIds() {
      ensureLoaded();

      OseeApiService.get().getAccessControlService().removePermissions(reqWorkBrch);
      ArtifactToken kayJones = UserManager.getUserByArtId(DemoUsers.Kay_Jones);
      OseeApiService.get().getAccessControlService().setPermission(kayJones, reqWorkBrch, PermissionEnum.FULLACCESS);
      atsApi.getAccessControlService().clearCaches();

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, reqWorkBrch);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith has NO access
      XResultData rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Kay Jones only has READ access cause no ATS Context Ids
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Joe Smith has no access
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(softReqFolder), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(softReqFolder), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(softReqFolder), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Add Everyone Read Access
      OseeApiService.get().getAccessControlService().setPermission(CoreUserGroups.Everyone, reqWorkBrch,
         PermissionEnum.READ);
      atsApi.getAccessControlService().clearCaches();

      // Joe Smith has read access cause Everyone Read
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(softReqFolder), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(softReqFolder), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(softReqFolder), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

   }

   /**
    * With no CM system, the framework relies on just the branch access control and artifact access control
    */
   @Test
   public void testAccessPermissionForNonAtsBranch() {
      ensureLoaded();

      BranchToken branch = FrameworkAccessByAtttributeTypeTest.getOrCreateAccessBranch();

      Assert.assertNotNull(branch);
      Assert.assertTrue(branch.isValid());

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, branch);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith and Kay Jones have all access
      XResultData rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Change the access control
      OseeApiService.get().getAccessControlService().removePermissions(branch);
      ArtifactToken kayJones = UserManager.getUserByArtId(DemoUsers.Kay_Jones);
      OseeApiService.get().getAccessControlService().setPermission(kayJones, branch, PermissionEnum.FULLACCESS);
      OseeApiService.get().getAccessControlService().setPermission(CoreUserGroups.Everyone, branch,
         PermissionEnum.READ);
      atsApi.getAccessControlService().clearCaches();

      // Joe Smith should be read only
      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = atsApi.getAccessControlService().hasArtifactPermission(Collections.singleton(softReqFolder),
         PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Kay has full access cause not an ATS branch so ATS context ids aren't in play and branch ACL wins
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   @Test
   public void testAccessPermissionForAtsArtifactAcl() {
      ensureLoaded();

      // Clear Branch Access entries
      OseeApiService.get().getAccessControlService().removePermissions(reqWorkBrch);
      atsApi.getAccessControlService().clearCaches();

      ArtifactToken virtualFixesSoftReq =
         atsApi.getQueryService().getArtifact(DemoArtifactToken.VirtualFixes, reqWorkBrch);

      Assert.assertNotNull(virtualFixesSoftReq);

      // Kay Jones has READ access
      XResultData rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Kay Jones has WRITE access
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Joe Smith does have READ access and NOT cause of lock
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());
      Assert.assertFalse(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      // Lock Software Req by Joe Smith
      atsApi.getAccessControlService().lockArtifacts(DemoUsers.Joe_Smith, Collections.singleton(virtualFixesSoftReq));
      atsApi.getAccessControlService().clearCaches();

      Assert.assertTrue(atsApi.getAccessControlService().hasLock(virtualFixesSoftReq));

      // Joe Smith does have READ access cause of USER_LOCK
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      // Joe Smith does have WRITE access cause of USER_LOCK
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      // Same
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      // Kay Jones does have READ access cause of USER_LOCK
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      // Kay Jones does NOT have WRITE access cause of USER_LOCK
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());
      Assert.assertTrue(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      // Same
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Kay_Jones,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());
      Assert.assertTrue(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      // Joe Smith SHOULD be able to unlock
      Assert.assertTrue(atsApi.getAccessControlService().canUnlockObject(DemoUsers.Joe_Smith, virtualFixesSoftReq));

      // Kay Jones SHOULD NOT be able to unlock
      Assert.assertFalse(atsApi.getAccessControlService().canUnlockObject(DemoUsers.Kay_Jones, virtualFixesSoftReq));

      // UnLock Software Req by Joe Smith
      atsApi.getAccessControlService().unLockArtifacts(DemoUsers.Joe_Smith, Collections.singleton(virtualFixesSoftReq));
      atsApi.getAccessControlService().clearCaches();

      // Joe Smith does have READ access and NOT cause of lock
      rd = atsApi.getAccessControlService().hasArtifactPermission(DemoUsers.Joe_Smith,
         Collections.singleton(virtualFixesSoftReq), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());
      Assert.assertFalse(rd.toString().contains(PermissionEnum.USER_LOCK.name()));

      Assert.assertFalse(atsApi.getAccessControlService().hasLock(virtualFixesSoftReq));
   }

   private void ensureLoaded() {
      FrameworkAccessTestUtil.ensureLoaded();
      atsApi = FrameworkAccessTestUtil.getAtsApi();
      reqWorkBrch = FrameworkAccessTestUtil.getReqWorkBrch();
   }

}
