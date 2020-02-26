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
