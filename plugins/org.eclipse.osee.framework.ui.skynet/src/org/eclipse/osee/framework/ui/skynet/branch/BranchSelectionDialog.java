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

import java.util.Collection;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchOptionsEnum;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget.BranchSelectedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget.IBranchWidgetMenuListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.actions.SetAsFavoriteAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class BranchSelectionDialog extends MessageDialog implements IBranchWidgetMenuListener {

   private BranchId selected;
   private BranchId defaultSelected;
   private static BranchId lastSelectedBranch;
   private XBranchWidget branchWidget;
   private boolean allowOnlyWorkingBranches;
   private final Collection<? extends BranchId> branches;

   public BranchSelectionDialog(String title, boolean allowOnlyWorkingBranches) {
      this(title, null);
      this.allowOnlyWorkingBranches = allowOnlyWorkingBranches;
   }

   public BranchSelectionDialog(String title, Collection<? extends BranchId> branches) {
      super(Displays.getActiveShell(), title, null, null, MessageDialog.NONE, new String[] {"Ok", "Cancel"}, 0);
      allowOnlyWorkingBranches = false;
      selected = null;
      this.branches = branches;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public void setDefaultSelection(BranchId branch) {
      defaultSelected = branch;
   }

   public BranchId getSelection() {
      return selected;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      branchWidget = new XBranchWidget(true, true, defaultSelected, this);
      branchWidget.setDisplayLabel(false);
      branchWidget.createWidgets(container, 1);
      branchWidget.setBranchOptions(true, BranchOptionsEnum.FAVORITE_KEY, BranchOptionsEnum.FLAT_KEY);
      branchWidget.setBranchOptions(allowOnlyWorkingBranches, BranchOptionsEnum.SHOW_WORKING_BRANCHES_ONLY);
      branchWidget.addBranchSelectedListener(new BranchSelectedListener() {

         @Override
         public void onBranchSelected(BranchId branch) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            storeSelectedBranch();
         }

      });
      final XViewer viewer = branchWidget.getXViewer();
      viewer.getFilterDataUI().addFilterTextListener(new KeyListener() {

         @Override
         public void keyReleased(KeyEvent e) {
            Collection<TreeItem> visibleItems = viewer.getVisibleItems();
            if (visibleItems.size() == 1) {
               viewer.setSelection(new StructuredSelection(new Object[] {visibleItems.iterator().next().getData()}));
               getButton(IDialogConstants.OK_ID).setEnabled(true);
               storeSelectedBranch();
            }
         }

         @Override
         public void keyPressed(KeyEvent e) {
            // do nothing
         }
      });
      if (branches != null) {
         branchWidget.loadData(branches);
      } else {
         branchWidget.loadData();
      }

      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 500;
      gd.widthHint = 800;
      Tree viewersTree = viewer.getTree();
      viewersTree.setLayoutData(gd);
      viewersTree.addListener(SWT.MouseDoubleClick, new Listener() {
         @Override
         public void handleEvent(Event event) {
            handleDoubleClick();
         }
      });
      viewersTree.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            storeSelectedBranch();
         }

      });

      return branchWidget.getControl();
   }

   @Override
   public void updateMenuActionsForTable(MenuManager mm) {
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new SetAsFavoriteAction(branchWidget));
   }

   @Override
   protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
      Button selectionButton = super.createButton(parent, id, label, defaultButton);
      // default the ok button to disabled until the user selects a branch
      if (id == IDialogConstants.OK_ID) {
         selectionButton.setEnabled(false);
      }
      return selectionButton;
   }

   private void storeSelectedBranch() {
      List<BranchId> branches = branchWidget.getSelectedBranches();

      if (!branches.isEmpty()) {
         selected = branches.iterator().next();
         BranchManager.setLastBranch(selected);
         lastSelectedBranch = selected;
      }
   }

   private void handleDoubleClick() {
      storeSelectedBranch();
      okPressed();
   }

   private static BranchId createDialog(boolean allowOnlyWorkingBranches) {
      BranchId toReturn = null;
      BranchSelectionDialog branchSelection = new BranchSelectionDialog("Select Branch", allowOnlyWorkingBranches);
      if (lastSelectedBranch != null) {
         try {
            if (!BranchManager.isArchived(lastSelectedBranch)) {
               branchSelection.setDefaultSelection(lastSelectedBranch);
            }
         } catch (OseeCoreException ex) {
            //do nothing
         }
      }
      int result = branchSelection.open();
      if (result == Window.OK) {
         toReturn = branchSelection.getSelection();
      }
      return toReturn;
   }

   public static BranchId getBranchFromUser() {
      return createDialog(false);
   }

   public static BranchId getWorkingBranchFromUser() {
      return createDialog(true);
   }
}
