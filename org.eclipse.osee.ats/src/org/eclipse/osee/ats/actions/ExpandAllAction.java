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
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ExpandAllAction extends Action {

   private final IExpandAllHandler iExpandAllHandler;
   private TreeViewer treeViewer = null;

   public static interface IExpandAllHandler {
      public void expandAllActionHandler();
   }

   public ExpandAllAction(TreeViewer treeViewer) {
      this((IExpandAllHandler) null);
      this.treeViewer = treeViewer;
   }

   public ExpandAllAction(IExpandAllHandler iRefreshActionHandler) {
      this.iExpandAllHandler = iRefreshActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EXPAND_ALL));
      setToolTipText("Expand All");
   }

   @Override
   public void run() {
      try {
         if (treeViewer != null) {
            treeViewer.expandAll();
         } else {
            iExpandAllHandler.expandAllActionHandler();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
