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
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateComposite extends XNavigateComposite {

   public AtsNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style) {
      super(navigateViewItems, parent, style);
      Result result = OseeUiActivator.areOSEEServicesAvailable();
      if (result.isFalse()) {
         new Label(parent, SWT.NONE).setText(result.getText());
         return;
      }
   }

   @Override
   protected void handleDoubleClick() throws OseeCoreException {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) {
         return;
      }
      XNavigateItem item = (XNavigateItem) sel.iterator().next();
      handleDoubleClick(item);
   }

   @Override
   protected void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      super.disposeTooltip();
      if (item.getChildren().size() > 0) {
         filteredTree.getViewer().setExpandedState(item, true);
      }
      AtsXNavigateItemLauncher.handleDoubleClick(item, tableLoadOptions);
   }

   @Override
   public void refresh() {
      super.refresh();
      if (AtsUtil.isAtsAdmin()) {
         for (XNavigateItem item : getInput()) {
            if (item.getName().equals("Admin")) {
               filteredTree.getViewer().expandToLevel(item, 1);
            }
         }
      }
      layout(true);
   }
}
