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

package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CollapseAllAction extends Action {

   private final ICollapseAllHandler iCollapseAllHandler;
   private TreeViewer treeViewer;

   public static interface ICollapseAllHandler {
      public void expandAllActionHandler();
   }

   public CollapseAllAction(TreeViewer treeViewer) {
      this((ICollapseAllHandler) null);
      this.treeViewer = treeViewer;
   }

   public CollapseAllAction(ICollapseAllHandler iCollapseActionHandler) {
      this.iCollapseAllHandler = iCollapseActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.COLLAPSE_ALL));
      setToolTipText("Collapse All");
   }

   @Override
   public void run() {
      try {
         if (treeViewer != null) {
            treeViewer.collapseAll();
         } else {
            iCollapseAllHandler.expandAllActionHandler();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
