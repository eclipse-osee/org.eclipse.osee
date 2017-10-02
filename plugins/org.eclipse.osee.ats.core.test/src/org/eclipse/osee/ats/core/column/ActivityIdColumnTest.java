/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.column.ev.WorkPackageColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link WorkPackageColumn}
 * 
 * @author Donald G. Dunne
 */
public class ActivityIdColumnTest {

   // @formatter:off
   @Mock private IAtsAction action;
   @Mock private IAtsTeamWorkflow teamWf1;
   @Mock private IAtsWorkPackage workPkg1;
   @Mock private IAtsTask task1;
   
   @Mock private IAtsTeamWorkflow teamWf2;
   @Mock private IAtsWorkPackage workPkg2;
   

   @Mock private IAtsTeamWorkflow teamWf3;

   
   private IAtsEarnedValueServiceProvider earnedValueServiceProvider;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(action.getTeamWorkflows()).thenReturn(Arrays.asList(teamWf1, teamWf2));

      earnedValueServiceProvider = Mockito.mock(IAtsEarnedValueServiceProvider.class);
      IAtsEarnedValueService service = Mockito.mock(IAtsEarnedValueService.class);
      when(earnedValueServiceProvider.getEarnedValueService()).thenReturn(service);
      when(service.getWorkPackage(teamWf1)).thenReturn(workPkg1);
      when(service.getWorkPackage(teamWf2)).thenReturn(workPkg2);

      when(task1.getParentTeamWorkflow()).thenReturn(teamWf1);

      when(workPkg1.getActivityId()).thenReturn("ActId 1");
      when(workPkg1.getActivityName()).thenReturn("ActId 1 Name");
      when(workPkg2.getActivityId()).thenReturn("ActId 2");
      when(workPkg2.getActivityName()).thenReturn("ActId 2 Name");
   }

   @Test
   public void testGetWorkPackages_teamWf1() {
      WorkPackageColumn col = new WorkPackageColumn(earnedValueServiceProvider);

      Set<IAtsWorkPackage> workPackages = new HashSet<>();
      col.getWorkPackage(teamWf1);

      Assert.assertEquals(1, workPackages.size());
      Assert.assertEquals(workPkg1, workPackages.iterator().next());
   }

   @Test
   public void testGetWorkPackages_task() {
      WorkPackageColumn col = new WorkPackageColumn(earnedValueServiceProvider);

      Set<IAtsWorkPackage> workPackages = new HashSet<>();
      col.getWorkPackage(task1);

      // Each work flow stores it's own work package, so none should be returned
      Assert.assertEquals(0, workPackages.size());
   }

   @Test
   public void testGetWorkPackageStr() {
      WorkPackageColumn col = new WorkPackageColumn(earnedValueServiceProvider);

      Assert.assertEquals("ActId 1 - ActId 1 Name", col.getColumnText(teamWf1));
      String workPackageStr = col.getColumnText(action);
      Assert.assertTrue(
         workPackageStr.equals("ActId 1 - ActId 1 Name, ActId 2 - ActId 2 Name") || workPackageStr.equals(
            "ActId 2 - ActId 2 Name, ActId 1 - ActId 1 Name"));

      Assert.assertEquals("", col.getColumnText(teamWf3));
   }
}
