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
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.util.regex.Pattern;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class XNavigateViewFilter extends ViewerFilter {

   private boolean enabled = false;
   private String text;

   public XNavigateViewFilter(TreeViewer treeViewer) {
   }

   public void setFilterText(String text) {
      this.text = text;

   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (!enabled) return true;
      XNavigateItem item = (XNavigateItem) element;
      return Pattern.compile(text).matcher(item.getName()).find();
   }

   /**
    * @return the enabled
    */
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * @param enabled the enabled to set
    */
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

}
