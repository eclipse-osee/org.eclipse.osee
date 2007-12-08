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
package org.eclipse.osee.framework.ui.admin.dbtabletab;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class DbTaskList {

   private Vector<DbModel> tasks = new Vector<DbModel>();
   private Set<ITaskListViewer> changeListeners = new HashSet<ITaskListViewer>();

   /**
    * Constructor
    */
   public DbTaskList() {
      super();
   }

   /**
    * Return the collection of AnnotateTask
    */
   public Vector<DbModel> getTasks() {
      return tasks;
   }

   /**
    * Add a new task to the collection of tasks
    */
   public void addTask(DbModel model) {
      tasks.add(tasks.size(), model);
      Iterator<ITaskListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         iterator.next().addTask(model);
   }

   /**
    * @param task
    */
   public void removeTask(DbModel task) {
      tasks.remove(task);
      Iterator<ITaskListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         iterator.next().removeTask(task);
   }

   public String toString() {
      String str = "";
      for (int i = 0; i < tasks.size(); i++) {
         DbModel model = tasks.get(i);
         str += "\nTask " + model.toString();
      }
      return str + "\n\n";
   }

   /**
    * @param task
    */
   public void taskChanged(DbModel task) {
      Iterator<ITaskListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         iterator.next().updateTask(task);
   }

   /**
    * @param viewer
    */
   public void removeChangeListener(ITaskListViewer viewer) {
      changeListeners.remove(viewer);
   }

   /**
    * @param viewer
    */
   public void addChangeListener(ITaskListViewer viewer) {
      changeListeners.add(viewer);
   }

}