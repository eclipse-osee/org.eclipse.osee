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
public class WorkPageViewSorter extends ViewerSorter {

   public WorkPageViewSorter() {
      super();
   }

   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      return getComparator().compare(((WorkPage) e1).getName(), ((WorkPage) e2).getName());
   }

}
