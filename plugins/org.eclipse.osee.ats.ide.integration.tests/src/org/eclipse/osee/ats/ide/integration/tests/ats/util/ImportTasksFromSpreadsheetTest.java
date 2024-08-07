/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import java.io.File;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.navigate.NavigateView;
import org.eclipse.osee.ats.ide.util.Import.ImportTasksFromSpreadsheet;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ImportTasksFromSpreadsheet}
 *
 * @author Donald G. Dunne
 */
public class ImportTasksFromSpreadsheetTest {

   @After
   public void cleanup() {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testPerformImport() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      ImportTasksFromSpreadsheet importTasks = new ImportTasksFromSpreadsheet();

      // Import files live in deployed ATS bundle cause they are used as examples for users
      File file = OseeInf.getResourceAsFile("atsImport/Task_Import.xml", NavigateView.class);
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      importTasks.performImport(false, false, teamWf, file);

      Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(teamWf);
      Assert.assertEquals(3, tasks.size());
      IAttributeResolver attrs = AtsApiService.get().getAttributeResolver();
      for (IAtsTask task : tasks) {
         if (task.getName().equals("2nd Task")) {
            Assert.assertEquals("Category", attrs.getSoleAttributeValue(task, AtsAttributeTypes.Category1, ""));
            Assert.assertEquals("Waiting", attrs.getSoleAttributeValue(task, AtsAttributeTypes.Resolution, ""));
            Assert.assertEquals("Implement", attrs.getSoleAttributeValue(task, AtsAttributeTypes.RelatedToState, ""));
            Assert.assertEquals(3.0, attrs.getSoleAttributeValue(task, AtsAttributeTypes.EstimatedHours, 0.0), 0.01);
            Assert.assertEquals(DemoUsers.Joe_Smith.getUserId(), task.getCreatedBy().getUserId());
            Assert.assertEquals(1, task.getAssignees().size());
            Assert.assertEquals(AtsCoreUsers.UNASSIGNED_USER.getUserId(),
               task.getAssignees().iterator().next().getUserId());
         } else if (task.getName().equals("3rd Task")) {
            Assert.assertEquals(DemoUsers.Kay_Jones.getUserId(), task.getCreatedBy().getUserId());
            Assert.assertEquals(2, task.getAssignees().size());
            for (AtsUser assignee : task.getAssignees()) {
               Assert.assertTrue(assignee.getUserId().equals(
                  DemoUsers.Joe_Smith.getUserId()) || assignee.getUserId().equals(DemoUsers.Kay_Jones.getUserId()));
            }
            Assert.assertEquals("", attrs.getSoleAttributeValue(task, AtsAttributeTypes.Category1, ""));
         }
      }
   }

}
