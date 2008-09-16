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
package org.eclipse.osee.ats.util.widgets.task;

import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.world.WorldContentProvider;

/**
 * @author Donald G. Dunne
 */
public class TaskContentProvider extends WorldContentProvider {

   private final TaskXViewer taskXViewer;

   public TaskContentProvider(TaskXViewer taskXViewer) {
      super(taskXViewer);
      this.taskXViewer = taskXViewer;
   }

   @Override
   public String toString() {
      return "TaskContentProvider: " + taskXViewer.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.WorldContentProvider#hasChildren(java.lang.Object)
    */
   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof TaskArtifact) return false;
      return super.hasChildren(element);
   }

}
