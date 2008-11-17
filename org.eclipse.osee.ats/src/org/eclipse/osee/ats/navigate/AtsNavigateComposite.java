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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSearchItemProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSearchItemProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateViewItems;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         (new Label(parent, SWT.NONE)).setText(result.getText());
         return;
      }
   }

   @Override
   protected void handleDoubleClick() throws OseeCoreException {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) return;
      XNavigateItem item = (XNavigateItem) sel.iterator().next();
      handleDoubleClick(item);
   }

   @Override
   protected void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (item instanceof SearchNavigateItem) {
         WorldSearchItem worldSearchItem = ((SearchNavigateItem) item).getWorldSearchItem();
         if (worldSearchItem.getLoadView() == LoadView.WorldEditor) {
            WorldEditor.open(new WorldEditorSearchItemProvider(worldSearchItem.copy(), null, tableLoadOptions));
         } else if (worldSearchItem.getLoadView() == LoadView.TaskEditor) {
            TaskEditor.open(new TaskEditorSearchItemProvider(worldSearchItem.copy(), tableLoadOptions));
         }
      } else
         super.handleDoubleClick(item, tableLoadOptions);
   }

   @Override
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
