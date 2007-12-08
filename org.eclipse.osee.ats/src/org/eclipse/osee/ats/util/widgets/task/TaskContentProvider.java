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

import org.eclipse.osee.ats.world.WorldContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.Displays;

/**
 * @author Donald G. Dunne
 */
public class TaskContentProvider extends WorldContentProvider {

   private final TaskXViewer xViewer;

   /**
    */
   public TaskContentProvider(TaskXViewer taskXViewer) {
      super(taskXViewer);
      this.xViewer = taskXViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.WorldContentProvider#hasChildren(java.lang.Object)
    */
   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof TaskArtifactItem) return false;
      return super.hasChildren(element);
   }

   public void add(final TaskArtifactItem item) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.add(item);
            xViewer.refresh();
         };
      });
   }

}
