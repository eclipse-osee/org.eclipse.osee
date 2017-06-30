/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.access;

/********************************
 * @author Marc Potter
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ListDialog;

public class PolicyTableXviewer extends XViewer implements IMultiColumnEditProvider {
   private PermissionEnum maxPermission = PermissionEnum.FULLACCESS;
   private PolicyTableViewer tableViewer = null;

   public PolicyTableXviewer(Composite parent, int style, IXViewerFactory xViewerFactory) {
      super(parent, style, xViewerFactory);
   }

   public PolicyTableXviewer(Tree tree, IXViewerFactory xViewerFactory) {
      super(tree, xViewerFactory);
   }

   public PolicyTableXviewer(Composite parent, int style, IXViewerFactory xViewerFactory, boolean filterRealTime, boolean searchRealTime) {
      super(parent, style, xViewerFactory, filterRealTime, searchRealTime);
   }

   public void setMaxPermission(PermissionEnum maxPermission) {
      this.maxPermission = maxPermission;
   }

   public void setTableViewer(PolicyTableViewer viewer) {
      this.tableViewer = viewer;
   }

   @Override
   public void updateMenuActionsForTable() {
      AccessControlAction ac = new AccessControlAction(this);
      MenuManager mm = getMenuManager();
      mm.insertBefore(XViewer.MENU_GROUP_PRE, ac);
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (treeColumn.getText().equals(PolicyTableColumns.totalAccess.getLabel())) {
         updateAccess();
      }
   }

   public boolean updateAccess() {
      boolean toReturn = false;
      if (tableViewer == null || !PermissionEnum.getMostRestrictive(maxPermission, PermissionEnum.LOCK).equals(
         PermissionEnum.LOCK)) {
         // must be at least an owner to change access
         return false;
      }
      TreeSelection treeSelection = (TreeSelection) getSelection();
      Iterator<?> elements = treeSelection.iterator();
      ArrayList<AccessControlData> userData = new ArrayList<>();
      while (elements.hasNext()) {
         AccessControlData ac = (AccessControlData) elements.next();
         if (PermissionEnum.getMostRestrictive(ac.getPermission(), maxPermission).equals(ac.getPermission())) {
            userData.add(ac);
         } else {
            AWorkbench.popup("ERROR", "Cannot change permissions of user with higher permissions than yourself");
            return true;
         }
      }
      StringListDialog dialog = new StringListDialog("Select new access level", "Select new access level",
         Arrays.asList(PermissionEnum.getPermissionNames()));
      if (dialog.open() != Window.CANCEL) {
         String newState = dialog.getSelectedState();
         PermissionEnum newStateEnum = PermissionEnum.getPermission(newState);
         if (newStateEnum != null && PermissionEnum.getMostRestrictive(newStateEnum, maxPermission).equals(
            newStateEnum)) {
            for (AccessControlData data : userData) {
               tableViewer.modifyPermissionLevel(data, newStateEnum);
            }
         } else if (newStateEnum != null) {
            AWorkbench.popup("ERROR", "Cannot change permissions of user to higher permissions than yourself");
         }
         tableViewer.refresh();
         toReturn = true;
      }
      return toReturn;
   }
   class StringListDialog extends ListDialog {

      public StringListDialog(String title, String message, Collection<String> values) {
         super(Displays.getActiveShell());
         setInput(values);
         setTitle(title);
         setMessage(message);
         setContentProvider(new ArrayContentProvider());
         setLabelProvider(new StringLabelProvider());
      }

      @Override
      protected Control createDialogArea(Composite container) {
         Control control = super.createDialogArea(container);
         getTableViewer().setComparator(new ViewerComparator());
         return control;
      }

      public String getSelectedState() {
         if (getResult().length == 0) {
            return "";
         }
         return (String) getResult()[0];
      }
   }

   private class AccessControlAction extends Action {

      private final PolicyTableXviewer tableViewer;
      public String ID = "osee.skynet.gui.branchAccessViewer.AccesscontrolEdit";

      public AccessControlAction(PolicyTableXviewer tableViewer) {
         super("Edit Access Control");
         this.tableViewer = tableViewer;
      }

      @Override
      public String getId() {
         return ID;
      }

      @Override
      public void run() {
         tableViewer.updateAccess();
      }
   }
}
