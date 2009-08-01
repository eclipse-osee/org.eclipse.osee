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
package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.xscript;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ITaskListViewer;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTaskList;

/**
 * @author Ken J. Aguilar
 */
public class XScriptTableContentProvider implements IStructuredContentProvider, ITaskListViewer, ITreeContentProvider {

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   @Override
   public void addTask(ScriptTask task) {
   }

   @Override
   public void addTasks(ScriptTask[] tasks) {
   }

   @Override
   public void removeTask(ScriptTask task) {
   }

   @Override
   public void updateTask(ScriptTask task) {
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if(parentElement instanceof ScriptTaskList){
         return ((ScriptTaskList)parentElement).getTasks().toArray();
      }
      return null;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      Object[] children = getChildren(element);
      if(children != null && children.length > 0) return true;
      return false;
   }
}
