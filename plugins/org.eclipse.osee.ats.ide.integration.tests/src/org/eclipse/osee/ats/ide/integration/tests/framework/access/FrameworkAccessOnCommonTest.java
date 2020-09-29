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
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class FrameworkAccessOnCommonTest {

   public static AtsApi atsApi;

   @Test
   public void testAccessPermission() {
      ensureLoaded();

      ArtifactToken softReqFolder =
         atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsTopFolder, CoreBranches.COMMON);

      Assert.assertNotNull(softReqFolder);

      // Joe Smith only has all
      XResultData rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasAttributeTypePermission(Collections.singleton(softReqFolder),
         CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      // Kay Jones has all
      rd = atsApi.getAccessControlService().hasAttributeTypePermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), CoreAttributeTypes.Name, PermissionEnum.READ, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasAttributeTypePermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), CoreAttributeTypes.Name, PermissionEnum.FULLACCESS, new XResultData());
      Assert.assertTrue(rd.isSuccess());

      rd = atsApi.getAccessControlService().hasAttributeTypePermission(DemoUsers.Kay_Jones,
         Collections.singleton(softReqFolder), CoreAttributeTypes.Name, PermissionEnum.WRITE, new XResultData());
      Assert.assertTrue(rd.isSuccess());

   }

   private void ensureLoaded() {
      atsApi = AtsApiService.get();
   }

}
