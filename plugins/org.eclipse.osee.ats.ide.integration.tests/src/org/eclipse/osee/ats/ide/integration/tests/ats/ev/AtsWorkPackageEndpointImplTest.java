/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.ev;

import static org.junit.Assert.assertEquals;
import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.ev.JaxWorkPackageData;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Unit for {@link AtsWorkPackageEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsWorkPackageEndpointImplTest {

   private AtsWorkPackageEndpointApi workPackageEp;

   @Before
   public void setup() {
      workPackageEp = AtsApiService.get().getServerEndpoints().getWorkPackageEndpoint();
   }

   @Test
   public void testGetEmptyWorkItems() {
      Collection<IAtsWorkItem> workItems =
         workPackageEp.getWorkItems(DemoArtifactToken.SAW_Test_AI_WorkPackage_0C.getId());
      assertEquals(0, workItems.size());
   }

   @Test
   public void testGetAndRemoveWorkPackageItems() {
      // Test Get
      Collection<IAtsWorkItem> workItems =
         workPackageEp.getWorkItems(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId());
      assertEquals(3, workItems.size());

      // Test Remove
      IAtsWorkItem workItem = workItems.iterator().next();
      JaxWorkPackageData data = new JaxWorkPackageData();
      data.setAsUserId(AtsApiService.get().getUserService().getCurrentUserId());
      data.getWorkItemIds().add(workItem.getId());
      XResultData rd = workPackageEp.deleteWorkPackageItems(0L, data);
      Assert.assertTrue(rd.toString(), rd.isSuccess());
      assertEquals(2, workPackageEp.getWorkItems(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId()).size());

      // Test Add
      workPackageEp.setWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId(), data);
      assertEquals(3, workPackageEp.getWorkItems(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId()).size());
   }

}
