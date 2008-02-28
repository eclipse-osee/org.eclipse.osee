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
package org.eclipse.osee.ats.navigate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateViewItems;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateComposite extends XNavigateComposite {

   /**
    * @param parent
    * @param style
    */
   public AtsNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style) {
      super(navigateViewItems, parent, style);
      if (!ConnectionHandler.isConnected()) {
         (new Label(parent, SWT.NONE)).setText("DB Connection Unavailable");
         return;
      }
   }

   @Override
   protected void handleDoubleClick() {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) return;
      XNavigateItem item = (XNavigateItem) sel.iterator().next();
      if (item instanceof SearchNavigateItem) {
         WorldSearchItem worldSearchItem = ((SearchNavigateItem) item).getWorldSearchItem();
         if (worldSearchItem.getLoadView() == LoadView.WorldView)
            openWorld(worldSearchItem);
         else if (worldSearchItem.getLoadView() == LoadView.TaskEditor) openTaskEditor(worldSearchItem);
      } else
         super.handleDoubleClick();
   }

   public static void openWorld(WorldSearchItem searchItem) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         WorldView worldView = (WorldView) page.showView(WorldView.VIEW_ID);
         worldView.loadTable(searchItem, true);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch Search View " + e1.getMessage());
      }
   }

   public static void openTaskEditor(WorldSearchItem searchItem) {
      try {
         TaskEditor.loadTable(searchItem, false);
      } catch (Exception e1) {
         OSEELog.logException(AtsPlugin.class, null, true);
      }
   }

   public void refresh() {
      super.refresh();
      if (AtsPlugin.isAtsAdmin()) {
         for (XNavigateItem item : getItems()) {
            if (item.getName().equals("Admin")) {
               filteredTree.getViewer().expandToLevel(item, 1);
            }
         }
      }
   }

}
