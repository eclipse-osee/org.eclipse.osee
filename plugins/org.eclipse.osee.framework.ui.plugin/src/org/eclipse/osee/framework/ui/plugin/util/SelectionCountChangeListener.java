/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
   private final IStatusLineManager statusLineManager;

   public SelectionCountChangeListener(IViewSite viewSite) {
      this.statusLineManager = viewSite.getActionBars().getStatusLineManager();
   }

   public SelectionCountChangeListener(IStatusLineManager statusLineManager) {
      this.statusLineManager = statusLineManager;
   }

   @Override
   public void selectionChanged(SelectionChangedEvent event) {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();

      IStatusLineManager lineManager = statusLineManager;
      if (selection.size() > 1) {
         lineManager.setMessage(selection.size() + " items selected");
      } else {
         lineManager.setMessage("");
      }
   }
}
