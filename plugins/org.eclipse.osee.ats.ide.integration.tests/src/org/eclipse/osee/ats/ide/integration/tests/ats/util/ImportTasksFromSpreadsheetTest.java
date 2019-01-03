/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import java.io.File;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.navigate.NavigateView;
import org.eclipse.osee.ats.ide.util.Import.ImportTasksFromSpreadsheet;
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
      importTasks.performImport(false, AtsTestUtil.getTeamWf(), file);

      Collection<IAtsTask> tasks = AtsClientService.get().getTaskService().getTasks(AtsTestUtil.getTeamWf());
      Assert.assertEquals(3, tasks.size());
      IAttributeResolver attrs = AtsClientService.get().getAttributeResolver();
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
            for (IAtsUser assignee : task.getAssignees()) {
               Assert.assertTrue(assignee.getUserId().equals(
                  DemoUsers.Joe_Smith.getUserId()) || assignee.getUserId().equals(DemoUsers.Kay_Jones.getUserId()));
            }
            Assert.assertEquals("", attrs.getSoleAttributeValue(task, AtsAttributeTypes.Category1, ""));
         }
      }
   }

}
