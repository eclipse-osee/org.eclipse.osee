/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.estimates.demo;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.workflow.cr.estimates.sibling.operation.CreateSiblingOffTaskEstOperation;
import org.eclipse.osee.ats.ide.workflow.cr.sibling.XSiblingActionBar;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstSiblingDemoActionBar extends XSiblingActionBar {

   public XTaskEstSiblingDemoActionBar(XTaskEstSiblingWorldDemoWidget siblingWorldWidget) {
      super(siblingWorldWidget);
   }

   @Override
   protected void createPreToolbarItems(ToolBar toolBar) {

      ToolItem createSiblingOffTaskEst = new ToolItem(toolBar, SWT.PUSH);
      createSiblingOffTaskEst.setImage(ImageManager.getImage(AtsImage.WORKFLOW));
      String label = "Create Sibling Workflows off Estimating Tasks";
      createSiblingOffTaskEst.setText(label);
      createSiblingOffTaskEst.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (MessageDialog.openConfirm(Displays.getActiveShell(), label, label + "\n\nAre you sure?")) {
               XTaskEstDemoWidget taskEstWid = new XTaskEstDemoWidget();
               CreateSiblingOffTaskEstOperation op =
                  new CreateSiblingOffTaskEstOperation(teamWf, taskEstWid.getTaskEstDefs());
               XResultData rd = op.run();
               XResultDataUI.report(rd, "Create Sibling Workflows off Estimating Tasks");
            }
         }

      });
   }

}
