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
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IViewSite;

/**
 * @author Robert A. Fisher
 */
public class SelectionCountChangeListener implements ISelectionChangedListener {
   private IStatusLineManager statusLineManager;

   /**
    * @param viewSite
    */
   public SelectionCountChangeListener(IViewSite viewSite) {
      this.statusLineManager = viewSite.getActionBars().getStatusLineManager();
   }

   /**
    * @param statusLineManager
    */
   public SelectionCountChangeListener(IStatusLineManager statusLineManager) {
      this.statusLineManager = statusLineManager;
   }

   public void selectionChanged(SelectionChangedEvent event) {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();

      IStatusLineManager lineManager = statusLineManager;
      if (selection.size() > 1)
         lineManager.setMessage(selection.size() + " items selected");
      else
         lineManager.setMessage("");
   }
}
