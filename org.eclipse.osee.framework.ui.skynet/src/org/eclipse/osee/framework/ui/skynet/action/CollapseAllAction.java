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
package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

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
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
