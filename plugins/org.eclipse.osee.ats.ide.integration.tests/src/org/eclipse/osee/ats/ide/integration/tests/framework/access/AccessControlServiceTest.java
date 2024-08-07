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
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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

      // Steven DOES NOT have modify access
      XResultData rd =
         atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Steven_Kohn, softReqFolder, null);
      Assert.assertTrue(rd.isErrors());

      // Kay DOES have modify access cause Branch FULL_ACCESS
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, softReqFolder, null);
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains("Branch FULL_ACCESS"));

      ArtifactToken virtualFixesSoftReq = atsApi.getQueryService().getArtifactByName(
         CoreArtifactTypes.SoftwareRequirementMsWord, "Virtual fixtures", reqWorkBrch);

      // Steven DOES NOT have modify access cause working branch without access set
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Steven_Kohn, virtualFixesSoftReq, null);
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

      // Steven DOES NOT have modify access
      XResultData rd =
         atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Steven_Kohn, DemoBranches.SAW_PL, null);
      Assert.assertTrue(rd.isErrors());

      // Kay DOES have modify access cause Branch FULL_ACCESS
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Kay_Jones, DemoBranches.SAW_PL, null);
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains("Branch FULL_ACCESS"));

      // Steven DOES NOT have modify access cause working branch without access set
      rd = atsApi.getAccessControlService().isModifyAccessEnabled(DemoUsers.Steven_Kohn, reqWorkBrch, null);
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