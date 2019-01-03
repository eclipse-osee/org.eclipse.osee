/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.ev;

import static org.junit.Assert.assertEquals;
import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.ev.JaxWorkPackageData;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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
      workPackageEp = AtsClientService.getWorkPackageEndpoint();
   }

   @Test
   public void testGetWorkItems() {
      Collection<IAtsWorkItem> workItems =
         workPackageEp.getWorkItems(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId());
      assertEquals(2, workItems.size());
   }

   @Test
   public void testGetEmptyWorkItems() {
      Collection<IAtsWorkItem> workItems =
         workPackageEp.getWorkItems(DemoArtifactToken.SAW_Test_AI_WorkPackage_0C.getId());
      assertEquals(0, workItems.size());
   }

   @Test
   public void testSetRemoveWorkPackageItems() {
      Collection<IAtsWorkItem> workItems =
         workPackageEp.getWorkItems(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId());
      assertEquals(2, workItems.size());
      IAtsWorkItem workItem = workItems.iterator().next();
      JaxWorkPackageData data = new JaxWorkPackageData();
      data.setAsUserId(AtsClientService.get().getUserService().getCurrentUserId());
      data.getWorkItemIds().add(workItem.getId());

      workPackageEp.deleteWorkPackageItems(0L, data);
      assertEquals(1, workPackageEp.getWorkItems(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId()).size());

      workPackageEp.setWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId(), data);
      assertEquals(2, workPackageEp.getWorkItems(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId()).size());
   }

}
