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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class FrameworkAccessByContextIdsTest {

   private AtsApi atsApi;
   private BranchToken reqWorkBrch;
   private BranchToken testWorkBrch;
   private IAccessControlService accessControlService;

   @Test
   public void testAccessPermissionForAtsWorkingBranchContextIds_SoftwareRequirementsAndChildren() {
      ensureLoaded();

      accessControlService.removePermissions(reqWorkBrch);
      atsApi.getAccessControlService().clearCaches();

      /**
       * Test artifact match context id
       */
      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SoftwareRequirementsFolder, reqWorkBrch);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith has FULL access cause no BCL, ACL and ContextIds are only checked on Write
      XResultData rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Success cause req contextids allow editing of Software Requirements folder
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Same
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      /**
       * Test artifact child / type match context id
       */
      ArtifactToken collabRobotEvents = atsApi.getQueryService().getArtifactByName(
         CoreArtifactTypes.SoftwareRequirementMsWord, "Collaborative robot events", reqWorkBrch);

      Assert.assertNotNull(collabRobotEvents);

      // Joe Smith has FULL access cause ContextIds are only checked on Write
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(collabRobotEvents),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Success cause req context ids allow editing of Software Requirements children of Software Req Art Types
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(collabRobotEvents),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Same
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(collabRobotEvents),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   @Test
   public void testAccessPermissionForAtsWorkingBranchContextIds_SystemRequirementsAndChildren() {
      ensureLoaded();

      accessControlService.removePermissions(reqWorkBrch);
      atsApi.getAccessControlService().clearCaches();

      /**
       * Test software artifact match context id
       */
      ArtifactToken systemReqFolder =
         atsApi.getQueryService().getArtifact(CoreArtifactTokens.SystemRequirementsFolder, reqWorkBrch);

      Assert.assertNotNull(systemReqFolder);

      // Joe Smith has FULL access cause no BCL, ACL and ContextIds are only checked on Write
      XResultData rd = atsApi.getAccessControlService().hasAttributeTypePermission(
         Collections.singleton(systemReqFolder), CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Failure cause req context ids dis-allow editing of System Requirements folder
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(systemReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Same
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(systemReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      /**
       * Test systems artifact child / type match context id
       */
      ArtifactToken performanceRequirements = atsApi.getQueryService().getArtifactByName(
         CoreArtifactTypes.SystemRequirementMsWord, "Performance Requirements", reqWorkBrch);

      Assert.assertNotNull(performanceRequirements);

      // Joe Smith has FULL access cause ContextIds are only checked on Write
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(performanceRequirements),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Fail cause req context ids allow editing of System Requirements children of System Req Art Types
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(performanceRequirements),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Same
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(performanceRequirements),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

   }

   @Test
   public void testAccessPermissionForAtsWorkingBranchContextIds_SoftwareReqTestWithQualAttrType() {
      ensureLoaded();

      accessControlService.removePermissions(testWorkBrch);
      atsApi.getAccessControlService().clearCaches();

      /**
       * Load Software Requirement which is readonly to Test Wfs except for Qualification attr type
       */
      ArtifactToken virtualFixesSwReq = atsApi.getQueryService().getArtifactByName(
         CoreArtifactTypes.SoftwareRequirementMsWord, "Virtual fixtures", testWorkBrch);

      Assert.assertNotNull(virtualFixesSwReq);

      // Joe Smith has FULL access to read Name and Qualification Method
      XResultData rd = atsApi.getAccessControlService().hasAttributeTypePermission(
         Collections.singleton(virtualFixesSwReq), CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(virtualFixesSwReq),
         CoreAttributeTypes.QualificationMethod, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Failure cause test context ids dis-allow editing of Software Requirements
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(virtualFixesSwReq),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Failure cause test context ids dis-allow editing of Software Requirements
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(virtualFixesSwReq),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Success cause test context ids allow editing of Software Requirements Qualification Method Attr Type
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(virtualFixesSwReq),
         CoreAttributeTypes.QualificationMethod, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Success cause test context ids allow editing of Software Requirements Qualification Method Attr Type
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(virtualFixesSwReq),
         CoreAttributeTypes.QualificationMethod, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   @Test
   public void testAccessPermissionForAtsWorkingBranchContextIds_SoftwareReqTestWithValidationRelType() {
      ensureLoaded();

      accessControlService.removePermissions(testWorkBrch);
      atsApi.getAccessControlService().clearCaches();

      /**
       * Load Software Requirement which is readonly to Test Wfs except for Verification rel type
       */
      ArtifactToken virtualFixesSwReq = atsApi.getQueryService().getArtifactByName(
         CoreArtifactTypes.SoftwareRequirementMsWord, "Virtual fixtures", testWorkBrch);

      Assert.assertNotNull(virtualFixesSwReq);

      // Test relation that should NOT have write/full access to

      // Joe Smith DOES have read access to DefaultHierarchy relation
      XResultData rd = atsApi.getAccessControlService().hasRelationTypePermission(virtualFixesSwReq,
         CoreRelationTypes.DefaultHierarchical_Child, Collections.emptyList(), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Joe Smith DOES NOT have write access DefaultHierarchy relation cause no write on Software Requirements
      rd = atsApi.getAccessControlService().hasRelationTypePermission(virtualFixesSwReq,
         CoreRelationTypes.DefaultHierarchical_Child, Collections.emptyList(), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isErrors());
      Assert.assertTrue(rd.toString().contains("Deny    - ArtAndChildArtTypes [allow=Deny, art=Software Requirements"));

      // Joe Smith DOES NOT have full access to DefaultHierarchy relation
      rd = atsApi.getAccessControlService().hasRelationTypePermission(virtualFixesSwReq,
         CoreRelationTypes.DefaultHierarchical_Child, Collections.emptyList(), PermissionEnum.FULLACCESS,
         new XResultData());
      Assert.assertTrue(rd.isErrors());

      // Test relation that should have write/full access to

      // Joe Smith has FULL access to read Verification relation
      rd = atsApi.getAccessControlService().hasRelationTypePermission(virtualFixesSwReq,
         CoreRelationTypes.Verification_Requirement, Collections.emptyList(), PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Joe Smith has FULL access to read Verification relation due to relation rule
      rd = atsApi.getAccessControlService().hasRelationTypePermission(virtualFixesSwReq,
         CoreRelationTypes.Verification_Requirement, Collections.emptyList(), PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());
      Assert.assertTrue(rd.toString().contains("Allow   - RelType [allow=Allow, relType=Verification,"));

      // Joe Smith has FULL access to read Verification relation
      rd = atsApi.getAccessControlService().hasRelationTypePermission(virtualFixesSwReq,
         CoreRelationTypes.Verification_Requirement, Collections.emptyList(), PermissionEnum.FULLACCESS,
         new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   private void ensureLoaded() {
      FrameworkAccessTestUtil.ensureLoaded();
      atsApi = FrameworkAccessTestUtil.getAtsApi();
      accessControlService = atsApi.getAccessControlService();
      reqWorkBrch = FrameworkAccessTestUtil.getReqWorkBrch();
      testWorkBrch = FrameworkAccessTestUtil.getTestWorkBrch();
   }
}