/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem.task.track;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.task.track.TaskTrackItem;
import org.eclipse.osee.ats.core.test.MockAtsUser;
import org.eclipse.osee.ats.core.test.MockTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for TaskTrackingOperation.getAssignees
 *
 * @author Donald G. Dunne
 */
public class TaskTrackingOperationUnitTest {

   @Test
   public void getAssignees() throws Exception {
      MockTeamWorkflow teamWf = new MockTeamWorkflow(234L, "Mock Wf");
      MockAtsUser kayJason = new MockAtsUser(DemoUsers.Kay_Jason);
      MockAtsUser joeSmith = new MockAtsUser(DemoUsers.Joe_Smith);
      MockAtsUser michaelAlex = new MockAtsUser(DemoUsers.Michael_Alex);
      teamWf.setAssignees(Arrays.asList(kayJason));
      teamWf.setImplementers(Arrays.asList(joeSmith));
      teamWf.setCompletedBy(michaelAlex);

      // Get from TaskTrackItem if specified
      TaskTrackItem taskItem = new TaskTrackItem();
      taskItem.setAssigneesArtIds(DemoUsers.Karmen_John.getIdString());
      boolean isAssigneesFromImplementers = false;

      List<ArtifactId> taskAssignees =
         TaskTrackingOperation.getTaskAssignees(isAssigneesFromImplementers, taskItem, teamWf, null);
      Assert.assertEquals(1, taskAssignees.size());
      Assert.assertEquals(DemoUsers.Karmen_John, taskAssignees.iterator().next());

      // Else, get from teamWf implementers if set
      taskItem = new TaskTrackItem();
      isAssigneesFromImplementers = true;

      taskAssignees = TaskTrackingOperation.getTaskAssignees(isAssigneesFromImplementers, taskItem, teamWf, null);
      Assert.assertEquals(1, taskAssignees.size());
      Assert.assertEquals(DemoUsers.Joe_Smith, taskAssignees.iterator().next());

      // Else, get from teamWf assignees if set
      taskItem = new TaskTrackItem();
      isAssigneesFromImplementers = false;

      taskAssignees = TaskTrackingOperation.getTaskAssignees(isAssigneesFromImplementers, taskItem, teamWf, null);
      Assert.assertEquals(1, taskAssignees.size());
      Assert.assertEquals(DemoUsers.Kay_Jason, taskAssignees.iterator().next());

      // Else, get from teamWf createdBy if no assignees
      taskItem = new TaskTrackItem();
      isAssigneesFromImplementers = false;
      teamWf.setAssignees(Collections.emptyList());

      taskAssignees = TaskTrackingOperation.getTaskAssignees(isAssigneesFromImplementers, taskItem, teamWf, null);
      Assert.assertEquals(1, taskAssignees.size());
      Assert.assertEquals(DemoUsers.Michael_Alex, taskAssignees.iterator().next());
   }
}
