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

import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class ExpandAllAction extends Action {

   private final IExpandAllHandler iExpandAllHandler;
   private TreeViewer treeViewer = null;
   private boolean selectedOnly;

   public static interface IExpandAllHandler {
      public void expandAllActionHandler();
   }

   public ExpandAllAction(TreeViewer treeViewer) {
      this(treeViewer, false);
      setText("Expand All");
   }

   public ExpandAllAction(TreeViewer treeViewer, boolean selectedOnly) {
      this((IExpandAllHandler) null);
      this.treeViewer = treeViewer;
      this.selectedOnly = selectedOnly;
      setText("Expand All");
   }

   public ExpandAllAction(IExpandAllHandler iRefreshActionHandler) {
      this.iExpandAllHandler = iRefreshActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EXPAND_ALL));
      setToolTipText("Expand All");
      this.selectedOnly = false;
      setText("Expand All");
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
   }

   @Override
   public void run() {
      try {
         if (treeViewer != null) {
            if (selectedOnly) {
               expandAll((IStructuredSelection) treeViewer.getSelection());
            } else {
               treeViewer.expandAll();
            }
         } else {
            iExpandAllHandler.expandAllActionHandler();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
