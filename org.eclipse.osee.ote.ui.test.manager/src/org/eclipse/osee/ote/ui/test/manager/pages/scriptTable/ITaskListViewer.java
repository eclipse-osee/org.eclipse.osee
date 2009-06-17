/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable;

public interface ITaskListViewer {

   /**
    * Update the view to reflect the fact that a task was added to the task list
    * 
    * @param task
    */
   public void addTask(ScriptTask task);

   /**
    * Update the view to reflect the fact that multiple tasks were added to the task list
    * 
    * @param tasks
    */
   public void addTasks(ScriptTask[] tasks);

   /**
    * Update the view to reflect the fact that a task was removed from the task list
    * 
    * @param task
    */
   public void removeTask(ScriptTask task);

   /**
    * Update the view to reflect the fact that one of the tasks was modified
    * 
    * @param task
    */
   public void updateTask(ScriptTask task);
}
