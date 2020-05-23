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

import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;

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
         treeViewer.expandToLevel(iter.next(), AbstractTreeViewer.ALL_LEVELS);
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
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
