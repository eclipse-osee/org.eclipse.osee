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
package org.eclipse.osee.ats.util.widgets.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XPromptChange;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.EnumStringSingleSelectionDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class UserRoleXViewer extends XViewer {

   private final XUserRoleViewer xUserRoleViewer;

   /**
    * @param parent
    * @param style
    */
   public UserRoleXViewer(Composite parent, int style, XUserRoleViewer xUserRoleViewer) {
      super(parent, style, new UserRoleXViewerFactory());
      this.xUserRoleViewer = xUserRoleViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            ((UserRoleContentProvider) getContentProvider()).clear();
         }
      });
      createMenuActions();
   }

   public void createMenuActions() {
      setColumnMultiEditEnabled(true);
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActionsForTable();
         }
      });
   }

   public void updateEditMenuActions() {
      // MenuManager mm = getMenuManager();
      // EDIT MENU BLOCK
   }

   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public Collection<UserRole> getLoadedUserRoleItems() {
      return ((UserRoleContentProvider) getContentProvider()).getRootSet();
   }

   public void add(Collection<UserRole> userRoles) {
      ((UserRoleContentProvider) getContentProvider()).add(userRoles);
   }

   public void set(Collection<? extends UserRole> userRoles) {
      if (((UserRoleContentProvider) getContentProvider()) != null) ((UserRoleContentProvider) getContentProvider()).set(userRoles);
   }

   public void clear() {
      ((UserRoleContentProvider) getContentProvider()).clear();
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
   }

   public ArrayList<UserRole> getSelectedUserRoleItems() {
      ArrayList<UserRole> arts = new ArrayList<UserRole>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((UserRole) item.getData());
      return arts;
   }

   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!xUserRoleViewer.isEditable()) {
         return;
      }
      ArrayList<UserRole> userRoles = new ArrayList<UserRole>();
      for (TreeItem item : treeItems) {
         userRoles.add((UserRole) item.getData());
      }
      try {
         promptChangeDate((XViewerColumn) treeColumn.getData(), userRoles, isColumnMultiEditEnabled());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      return handleAltLeftClick(treeColumn, treeItem);
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!xUserRoleViewer.isEditable()) {
         return false;
      }
      try {
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         XViewerColumn aCol = (XViewerColumn) treeColumn.getData();
         UserRole userRole = (UserRole) treeItem.getData();
         List<UserRole> userRoles = new ArrayList<UserRole>();
         userRoles.add(userRole);

         if (aCol.equals(UserRoleXViewerFactory.Completed_Col) || aCol.equals(UserRoleXViewerFactory.Hours_Spent_Col) || aCol.equals(UserRoleXViewerFactory.Num_Minor_Col) || aCol.equals(UserRoleXViewerFactory.Num_Major_Col) || aCol.equals(UserRoleXViewerFactory.Num_Issues_Col) || aCol.equals(UserRoleXViewerFactory.User_Col) || aCol.equals(UserRoleXViewerFactory.Role_Col)) {
            promptChangeDate(aCol, userRoles, false);
         } else
            throw new OseeStateException("Unhandled user role column");

      } catch (Exception ex) {
         OseeLog.log(SkynetGuiDebug.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private boolean setHoursSpent(Collection<UserRole> userRoles, String hours) {
      boolean modified = false;
      for (UserRole userRole : userRoles) {
         userRole.setHoursSpent(hours.equals("") ? 0 : (new Double(hours)).doubleValue());
         if (!modified) modified = true;
      }
      return modified;
   }

   private boolean setCompleted(Collection<UserRole> userRoles) {
      boolean modified = false;
      for (UserRole userRole : userRoles) {
         if (userRole.getHoursSpent() == null) {
            AWorkbench.popup("ERROR", "Must enter Hours Spent");
            return false;
         }
         userRole.setCompleted(!userRole.isCompleted());
         if (!modified) modified = true;
      }
      return modified;
   }

   private boolean setUser(Collection<UserRole> userRoles, User user) {
      boolean modified = false;
      for (UserRole userRole : userRoles) {
         if (user != null && userRole.getUser() != user) {
            userRole.setUser(user);
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setRole(Collection<UserRole> userRoles, String role) {
      boolean modified = false;
      for (UserRole userRole : userRoles) {
         userRole.setRole(Role.valueOf((String) role));
         if (!modified) modified = true;
      }
      return modified;
   }

   public boolean promptChangeDate(XViewerColumn xCol, Collection<UserRole> userRoles, boolean columnMultiEdit) throws OseeCoreException {
      boolean modified = false;
      if (userRoles != null && !userRoles.isEmpty()) {
         UserRole userRole = (UserRole) userRoles.toArray()[0];
         if (xCol.equals(UserRoleXViewerFactory.Hours_Spent_Col)) {
            String hours =
                  XPromptChange.promptChangeFloat(xCol.getName(),
                        (columnMultiEdit ? 0 : (userRole.getHoursSpent() == null ? 0 : userRole.getHoursSpent())));
            if (hours != null) {
               modified = setHoursSpent(userRoles, hours);
            }
         } else if (xCol.equals(UserRoleXViewerFactory.Num_Minor_Col) || xCol.equals(UserRoleXViewerFactory.Num_Major_Col) || xCol.equals(UserRoleXViewerFactory.Num_Issues_Col)) {
            AWorkbench.popup("ERROR", "Field is calculated");
         } else if (xCol.equals(UserRoleXViewerFactory.Completed_Col)) {
            modified = setCompleted(userRoles);
         } else if (xCol.equals(UserRoleXViewerFactory.User_Col)) {
            UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New User");
            int result = ld.open();
            if (result == 0) {
               modified = setUser(userRoles, ld.getSelection());
            }
         } else if (xCol.equals(UserRoleXViewerFactory.Role_Col)) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(), Role.strValues(),
                        (columnMultiEdit ? null : userRole.getRole().name()));
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = setRole(userRoles, (String) enumDialog.getResult()[0]);
               }
            }
         }
         if (modified) {
            return executeTransaction(userRoles);
         }
      }
      return false;
   }

   public boolean executeTransaction(Collection<UserRole> userRoles) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(xUserRoleViewer.getReviewArt().getArtifact().getBranch());
      for (UserRole userRole : userRoles) {
         xUserRoleViewer.getReviewArt().getUserRoleManager().addOrUpdateUserRole(userRole, false, transaction);
         update(userRole, null);
      }
      transaction.execute();
      xUserRoleViewer.refresh();
      xUserRoleViewer.notifyXModifiedListeners();
      return true;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XUserRoleViewer getXUserRoleViewer() {
      return xUserRoleViewer;
   }

}
