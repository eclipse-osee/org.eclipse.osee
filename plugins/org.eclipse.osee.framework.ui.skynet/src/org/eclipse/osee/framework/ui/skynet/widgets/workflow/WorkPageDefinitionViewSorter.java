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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Donald G. Dunne
 */
public class WorkPageDefinitionViewSorter extends ViewerSorter {

   public WorkPageDefinitionViewSorter() {
      super();
   }

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      WorkPageDefinition def1 = (WorkPageDefinition) e1;
      WorkPageDefinition def2 = (WorkPageDefinition) e2;
      if (def1.getWorkPageOrdinal() == def2.getWorkPageOrdinal()) {
         return compareByName(def1, def2);
      } else if (def1.getWorkPageOrdinal() < def2.getWorkPageOrdinal()) {
         return -1;
      } else {
         return 1;
      }
   }

   @SuppressWarnings("unchecked")
   private int compareByName(WorkPageDefinition def1, WorkPageDefinition def2) {
      return getComparator().compare(def1.getPageName(), def2.getPageName());
   }
}
