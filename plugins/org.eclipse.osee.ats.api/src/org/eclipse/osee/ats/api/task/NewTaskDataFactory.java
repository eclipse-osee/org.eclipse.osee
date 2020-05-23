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

package org.eclipse.osee.ats.api.task;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public class NewTaskDataFactory {

   private NewTaskDataFactory() {
      // utility class
   }

   public static NewTaskData get(String comment, String asUserId, Long teamWfId) {
      NewTaskData newTaskData = new NewTaskData();
      newTaskData.setAsUserId(asUserId);
      newTaskData.setCommitComment(comment);
      newTaskData.setTeamWfId(teamWfId);
      return newTaskData;
   }

   public static NewTaskData get(String comment, AtsUser atsUser, IAtsTeamWorkflow teamWf) {
      NewTaskData newTaskData = new NewTaskData();
      newTaskData.setAsUserId(atsUser.getUserId());
      newTaskData.setCommitComment(comment);
      newTaskData.setTeamWfId(teamWf.getId());
      return newTaskData;
   }
}
