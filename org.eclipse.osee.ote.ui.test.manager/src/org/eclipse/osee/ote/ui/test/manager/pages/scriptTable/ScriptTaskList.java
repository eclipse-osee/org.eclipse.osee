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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class ScriptTaskList {

   private Set<ITaskListViewer> changeListeners = new HashSet<ITaskListViewer>();
   private Vector<ScriptTask> tasks = new Vector<ScriptTask>();
   private boolean isConnected = false;

   /**
    * Constructor
    */
   public ScriptTaskList() {
      super();
   }

   /**
    * @param viewer
    */
   public void addChangeListener(ITaskListViewer viewer) {
      changeListeners.add(viewer);
   }

   /**
    * Add a new task to the collection of tasks
    */
   public void addTask(ScriptTask inTask) {
      inTask.updateStatusOnConnected(this.isConnected);
      tasks.add(tasks.size(), inTask);
      Iterator<ITaskListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         (iterator.next()).addTask(inTask);
   }

   public void addTasks(List<ScriptTask> inTasks) {
      for (ScriptTask task : inTasks) {
         task.updateStatusOnConnected(isConnected);
      }
      ScriptTask[] array = new ScriptTask[inTasks.size()];
      this.tasks.addAll(inTasks);
      Iterator<ITaskListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         (iterator.next()).addTasks(this.tasks.toArray(array));
   }

   /**
    * Return the collection of ScriptTask
    */
   public Vector<ScriptTask> getTasks() {
      return tasks;
   }

   public void onConnected(boolean connected) {
      synchronized (tasks) {
         this.isConnected = connected;
         Iterator<ScriptTask> iter = tasks.iterator();
         while (iter.hasNext()) {
            ScriptTask task = iter.next();
            //        	 task.computeExists();
            task.updateStatusOnConnected(connected);
         }
      }
   }

   public void removeAllTasks() {
      this.tasks.removeAllElements();
   }

   /**
    * @param viewer
    */
   public void removeChangeListener(ITaskListViewer viewer) {
      changeListeners.remove(viewer);
   }

   /**
    * @param task
    */
   public void removeTask(ScriptTask task) {
      tasks.remove(task);
      Iterator<ITaskListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         (iterator.next()).removeTask(task);
   }

   /**
    * @param task
    */
   public void taskChanged(ScriptTask task) {
      Iterator<ITaskListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         (iterator.next()).updateTask(task);
   }

   public String toString() {
      String str = "";
      for (int i = 0; i < tasks.size(); i++) {
         ScriptTask task = (ScriptTask) tasks.get(i);
         str += "\nTask " + task.getName() + " Status: " + task.getStatus();
      }
      return str + "\n\n";
   }

   /**
    * @param newTask
    */
   public boolean contains(ScriptTask newTask) {
      for (ScriptTask task : tasks) {
         if (task.getPath().equals(newTask.getPath())) {
            return true;
         }
      }
      return false;
   }

}