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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the use cases for allow/deny users the ability to modify access control for brancehs and artifacts
 *
 * @author Donald G. Dunne
 */
public class AccessControlServiceTest {

   public static AtsApi atsApi;
   private BranchToken reqWorkBrch;

   @Test
   public void testArtifactAccessControlModifyPermission() {
      ensureLoaded();

      // Reset OseeAccessAdmin
      atsApi.userService().getUserGroup(CoreUserGroups.OseeAccessAdmin).removeMember(DemoUsers.Kay_Jones, true);

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, DemoBranches.SAW_PL);

      Assert.assertNotNull(softReqFolder);

      // Joe DOES NOT have modify access
      XResultData rd =
         atsApi.getAccessControlService().isModifyAccessEnabled(UserManager.getUser(), softReqFolder, null);
      Assert.assertTrue(rd.isErrors());

      // Kay DOES have modify access cause Branch FULL_ACCESS
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, softReqFolder, null);
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains("Branch FULL_ACCESS"));

      ArtifactToken virtualFixesSoftReq =
         atsApi.getQueryService().getArtifact(DemoArtifactToken.VirtualFixes, reqWorkBrch);

      // Joe DOES NOT have modify access cause working branch without access set
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(UserManager.getUser(), virtualFixesSoftReq, null);
      Assert.assertTrue(rd.isErrors());
      Assert.assertTrue(rd.toString().contains("Access Not Set"));

      // Kay DOES NOT have modify access cause working branch without access set
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, virtualFixesSoftReq, null);
      Assert.assertTrue(rd.isErrors());

      atsApi.userService().getUserGroup(CoreUserGroups.OseeAccessAdmin).addMember(DemoUsers.Kay_Jones, true);
      Assert.assertTrue(atsApi.userService().isUserMember(CoreUserGroups.OseeAccessAdmin, DemoUsers.Kay_Jones));

      // Kay DOES have modify cause now in OSEE Access Admin group
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, virtualFixesSoftReq, null);
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains(CoreUserGroups.OseeAccessAdmin.getName()));

      atsApi.userService().getUserGroup(CoreUserGroups.OseeAccessAdmin).removeMember(DemoUsers.Kay_Jones, true);
      Assert.assertFalse(atsApi.userService().isUserMember(CoreUserGroups.OseeAccessAdmin, DemoUsers.Kay_Jones));

   }

   @Test
   public void testBranchAccessControlModifyPermission() {
      ensureLoaded();

      // Reset OseeAccessAdmin
      atsApi.userService().getUserGroup(CoreUserGroups.OseeAccessAdmin).removeMember(DemoUsers.Kay_Jones, true);

      // Joe DOES NOT have modify access
      XResultData rd =
         atsApi.getAccessControlService().isModifyAccessEnabled(UserManager.getUser(), DemoBranches.SAW_PL, null);
      Assert.assertTrue(rd.isErrors());

      // Kay DOES have modify access cause Branch FULL_ACCESS
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, DemoBranches.SAW_PL, null);
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains("Branch FULL_ACCESS"));

      // Joe DOES NOT have modify access cause working branch without access set
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(UserManager.getUser(), reqWorkBrch, null);
      Assert.assertTrue(rd.isErrors());
      Assert.assertTrue(rd.toString().contains("Access Not Set"));

      // Kay DOES NOT have modify access cause working branch without access set
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, reqWorkBrch, null);
      Assert.assertTrue(rd.isErrors());

      atsApi.userService().getUserGroup(CoreUserGroups.OseeAccessAdmin).addMember(DemoUsers.Kay_Jones, true);
      Assert.assertTrue(atsApi.userService().isUserMember(CoreUserGroups.OseeAccessAdmin, DemoUsers.Kay_Jones));

      // Kay DOES have modify cause now in OSEE Access Admin group
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, reqWorkBrch, null);
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains(CoreUserGroups.OseeAccessAdmin.getName()));

      atsApi.userService().getUserGroup(CoreUserGroups.OseeAccessAdmin).removeMember(DemoUsers.Kay_Jones, true);
      Assert.assertFalse(atsApi.userService().isUserMember(CoreUserGroups.OseeAccessAdmin, DemoUsers.Kay_Jones));
   }

   private void ensureLoaded() {
      atsApi = AtsApiService.get();
      reqWorkBrch = FrameworkAccessTestUtil.getReqWorkBrch();
   }
}