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
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchControlled;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchState;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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
   BranchListComposite branchListComposite;
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
      List<Branch> branches = null;
      try {
         if (allowOnlyWorkingBranches) {
            branches =
                  BranchManager.getBranches(BranchState.ACTIVE, BranchControlled.CHANGE_MANAGED, BranchType.WORKING);
         } else {
            branches = BranchManager.getNormalBranches();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      branchListComposite = new BranchListComposite(branches, container);
      branchListComposite.setPresentation(true);
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      gd.widthHint = 400;
      branchListComposite.getBranchTable().getTree().setLayoutData(gd);
      branchListComposite.getFilterText().setFocus();
      branchListComposite.getBranchTable().getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         @Override
         public void handleEvent(Event event) {
            handleDoubleClick();
         }
      });
      branchListComposite.getBranchTable().getTree().addSelectionListener(new SelectionListener() {
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
      return branchListComposite.getBranchTable().getControl();
   }

   public Branch getSelected() {
      return selected;
   }

   private void storeSelectedBranch() {
      IStructuredSelection sel = (IStructuredSelection) branchListComposite.getBranchTable().getSelection();
      if (!sel.isEmpty() && (sel.getFirstElement() instanceof JobbedNode)) {
         selected = (Branch) ((JobbedNode) sel.getFirstElement()).getBackingData();
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
