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
package org.eclipse.osee.ote.ui.navigate;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;


/**
 * @author Donald G. Dunne
 */
public class OteNavigateViewerSorter extends ViewerSorter {

   /**
    * @param treeViewer
    */
   public OteNavigateViewerSorter() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
    */
   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      XNavigateItem item1 = (XNavigateItem)e1;
      XNavigateItem item2 = (XNavigateItem)e2;
      return getComparator().compare(item1.getName(), item2.getName());
   }

}
