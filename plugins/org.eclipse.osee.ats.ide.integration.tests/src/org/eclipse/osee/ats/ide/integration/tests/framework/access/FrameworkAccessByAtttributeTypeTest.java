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
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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
public class FrameworkAccessByAtttributeTypeTest {

   private AtsApi atsApi;
   private BranchToken reqWorkBrch;
   private IAccessControlService accessControlService;

   /**
    * With the ATS CM system, access control uses branch and artifact access control, but the context id based access
    * wins. Although this is an AttributeType test, most of these tests will fail/pass without checking context ids. The
    * real attribute type context id tests are in FrameworkAccessByContextIdsTest which tests when the attribute type is
    * provided in the access context definition.
    */
   @Test
   public void testAccessPermissionForAtsBranch() {
      ensureLoaded();

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, DemoBranches.SAW_PL);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith only has read
      XResultData rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Kay Jones has read
      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Kay Jones does have full or write cause Artifact ACL and Contexts Ids do not kick in
      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Same
      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   @Test
   public void testAccessPermissionForAtsWorkingBranchNoContextIds() {
      ensureLoaded();

      accessControlService.removePermissions(reqWorkBrch);
      ArtifactToken kayJones = UserManager.getUserByArtId(DemoUsers.Kay_Jones);
      accessControlService.setPermission(kayJones, reqWorkBrch, PermissionEnum.FULLACCESS);
      accessControlService.clearCaches();

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, reqWorkBrch);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith has NO access
      XResultData rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Kay Jones only has READ access cause no ATS Context Ids
      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Joe Smith has no access
      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Joe_Smith, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Joe_Smith, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Joe_Smith, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Add Everyone Read Access
      accessControlService.setPermission(CoreUserGroups.Everyone, reqWorkBrch, PermissionEnum.READ);
      accessControlService.clearCaches();

      // Joe Smith has read access cause Everyone Read
      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Joe_Smith, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Joe_Smith, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Joe_Smith, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

   }

   /**
    * With no CM system, the framework relies on just the branch access control and artifact access control
    */
   @Test
   public void testAccessPermissionForNonAtsBranch() {
      ensureLoaded();

      BranchToken branch = FrameworkAccessTestUtil.getOrCreateAccessBranch(accessControlService);

      Assert.assertNotNull(branch);
      Assert.assertTrue(branch.isValid());

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, branch);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith and Kay Jones have all access
      XResultData rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Change the access control
      accessControlService.removePermissions(branch);
      ArtifactToken kayJones = UserManager.getUserByArtId(DemoUsers.Kay_Jones);
      accessControlService.setPermission(kayJones, branch, PermissionEnum.FULLACCESS);
      accessControlService.setPermission(CoreUserGroups.Everyone, branch, PermissionEnum.READ);
      accessControlService.clearCaches();

      // Joe Smith should be read only
      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Kay has full access cause not an ATS branch so ATS context ids aren't in play and branch ACL wins
      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = accessControlService.hasAttributeTypePermission(DemoUsers.Kay_Jones, Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   @Test
   public void testAccessPermissionForAtsArtifactAcl() {
      ensureLoaded();

      // Clear Branch Access entries
      accessControlService.removePermissions(reqWorkBrch);
      accessControlService.clearCaches();

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, reqWorkBrch);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith has READ access
      XResultData rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Joe Smith has WRITE access
      rd = accessControlService.hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());
   }

   private void ensureLoaded() {
      FrameworkAccessTestUtil.ensureLoaded();
      atsApi = FrameworkAccessTestUtil.getAtsApi();
      accessControlService = atsApi.getAccessControlService();
      reqWorkBrch = FrameworkAccessTestUtil.getReqWorkBrch();
   }
}