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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;

public class TaskCurrentStateFilter extends ViewerFilter {

   private final String stateName;

   public TaskCurrentStateFilter(String stateName) {
      this.stateName = stateName;
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      try {
         TaskArtifact taskArt = (TaskArtifact) element;
         if (taskArt.isDeleted()) return true;
         return (taskArt.getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "").equals(stateName));
      } catch (Exception ex) {
         // do nothing
      }
      return true;
   }
}
