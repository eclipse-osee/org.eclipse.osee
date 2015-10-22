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
package org.eclipse.osee.ats.client.integration.tests.ats.ev;

import static org.junit.Assert.assertEquals;
import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.ev.JaxWorkPackageData;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Unit for {@link AtsWorkPackageEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsWorkPackageEndpointImplTest {

   public static Long SAW_Code_Team_WorkPackage_01 = 38512616L;
   private AtsWorkPackageEndpointApi workPackageEp;

   @Before
   public void setup() {
      workPackageEp = AtsClientService.getWorkPackageEndpoint();
   }

   @Test
   public void testGetWorkItems() {
      Collection<IAtsWorkItem> workItems = workPackageEp.getWorkItems(SAW_Code_Team_WorkPackage_01);
      assertEquals(2, workItems.size());
   }

   @Test
   public void testSetRemoveWorkPackageItems() {
      Collection<IAtsWorkItem> workItems = workPackageEp.getWorkItems(SAW_Code_Team_WorkPackage_01);
      assertEquals(2, workItems.size());
      IAtsWorkItem workItem = workItems.iterator().next();
      JaxWorkPackageData data = new JaxWorkPackageData();
      data.setAsUserId(AtsClientService.get().getUserService().getCurrentUserId());
      data.getWorkItemUuids().add(workItem.getUuid());

      workPackageEp.deleteWorkPackageItems(SAW_Code_Team_WorkPackage_01, data);
      assertEquals(1, workPackageEp.getWorkItems(SAW_Code_Team_WorkPackage_01).size());

      workPackageEp.setWorkPackage(SAW_Code_Team_WorkPackage_01, data);
      assertEquals(2, workPackageEp.getWorkItems(SAW_Code_Team_WorkPackage_01).size());
   }

}
