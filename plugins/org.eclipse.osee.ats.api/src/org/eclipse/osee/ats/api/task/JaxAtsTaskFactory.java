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

import java.util.Date;
import org.eclipse.osee.ats.api.user.AtsUser;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsTaskFactory {

   private JaxAtsTaskFactory() {
      // utility class
   }

   public static JaxAtsTask get(String title, AtsUser createdBy, Date createdDate) {
      JaxAtsTask task = new JaxAtsTask();
      task.setCreatedByUserId(createdBy.getUserId());
      task.setCreatedDate(createdDate);
      task.setName(title);
      return task;
   }

   public static JaxAtsTask get(NewTaskData newTaskData, String title, AtsUser createdBy, Date createdDate) {
      JaxAtsTask task = get(title, createdBy, createdDate);
      newTaskData.getNewTasks().add(task);
      return task;
   }
}
