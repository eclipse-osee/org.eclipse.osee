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
package org.eclipse.osee.framework.ui.skynet.branch;

import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchOptions;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class BranchSelectionDialog extends MessageDialog {

   Branch selected = null;
   XBranchWidget branchWidget;
   private final boolean allowOnlyWorkingBranches;

   public BranchSelectionDialog(String title, boolean allowOnlyWorkingBranches) {
      super(Display.getCurrent().getActiveShell(), title, null, null, MessageDialog.NONE,
            new String[] {"Ok", "Cancel"}, 0);
      this.allowOnlyWorkingBranches = allowOnlyWorkingBranches;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public Branch getSelection() {
      return selected;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      branchWidget = new XBranchWidget(true, true);
      branchWidget.setDisplayLabel(false);
      branchWidget.createWidgets(container, 1);
      branchWidget.setBranchOptions(BranchOptions.FAVORITES_FIRST, BranchOptions.FLAT);
      branchWidget.setShowWorkingBranchesOnly(allowOnlyWorkingBranches);
      branchWidget.loadData();
      
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      gd.widthHint = 800;
      branchWidget.getXViewer().getTree().setLayoutData(gd);
      branchWidget.getXViewer().getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         @Override
         public void handleEvent(Event event) {
            handleDoubleClick();
         }
      });
      branchWidget.getXViewer().getTree().addSelectionListener(new SelectionListener() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            storeSelectedBranch();
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });
      return branchWidget.getControl();
   }

   public Branch getSelected() {
      return selected;
   }

   private void storeSelectedBranch() {
      List<Branch> branches = branchWidget.getSelectedBranches();

      if (!branches.isEmpty()) {
         selected = branches.iterator().next();
         BranchManager.setLastBranch(selected);
      }
   }

   private void handleDoubleClick() {
      storeSelectedBranch();
      okPressed();
   }

   private static Branch createDialog(boolean allowOnlyWorkingBranches) {
      Branch toReturn = null;
      BranchSelectionDialog branchSelection = new BranchSelectionDialog("Select Branch", allowOnlyWorkingBranches);
      int result = branchSelection.open();
      if (result == Window.OK) {
         toReturn = branchSelection.getSelection();
      }
      return toReturn;
   }

   public static Branch getBranchFromUser() {
      return createDialog(false);
   }

   public static Branch getWorkingBranchFromUser() {
      return createDialog(true);
   }
}
