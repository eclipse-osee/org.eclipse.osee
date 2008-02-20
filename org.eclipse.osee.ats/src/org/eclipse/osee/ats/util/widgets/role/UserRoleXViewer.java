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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.EnumStringSingleSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XPromptChange;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
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

   private static String NAMESPACE = "osee.ats.UserRoleXViewer";
   private final XUserRoleViewer xUserRoleViewer;

   /**
    * @param parent
    * @param style
    */
   public UserRoleXViewer(Composite parent, int style, XUserRoleViewer xViewer) {
      this(parent, style, NAMESPACE, new UserRoleXViewerFactory(), xViewer);
   }

   public UserRoleXViewer(Composite parent, int style, String nameSpace, IXViewerFactory xViewerFactory, XUserRoleViewer xRoleViewer) {
      super(parent, style, nameSpace, xViewerFactory);
      this.xUserRoleViewer = xRoleViewer;
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
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions();
         }
      });
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      UserRoleColumn aCol = UserRoleColumn.getAtsXColumn((XViewerColumn) treeColumn.getData());
      XViewerColumn xCol = getCustomize().getCurrentCustData().getColumnData().getXColumn(aCol.getName());
      if (!xCol.isShow() || !aCol.isMultiColumnEditable()) return false;
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   public void updateEditMenuActions() {
      // MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
   }

   public void updateMenuActions() {
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
      ((UserRoleContentProvider) getContentProvider()).set(userRoles);
   }

   public void clear() {
      ((UserRoleContentProvider) getContentProvider()).clear();
   }

   /**
    * Release resources
    */
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its ressources
      getLabelProvider().dispose();
   }

   public ArrayList<UserRole> getSelectedUserRoleItems() {
      ArrayList<UserRole> arts = new ArrayList<UserRole>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((UserRole) item.getData());
      return arts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClickInIconArea(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      return handleAltLeftClick(treeColumn, treeItem);
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.viewer.XViewer#handleAltLeftClick(org.eclipse.swt.widgets.TreeColumn,
    *      org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         UserRoleColumn aCol = UserRoleColumn.getAtsXColumn(xCol);
         UserRole userRole = (UserRole) treeItem.getData();
         boolean modified = false;
         if (aCol == UserRoleColumn.Hours_Spent_Col) {
            String hours =
                  XPromptChange.promptChangeFloat(aCol.getName(),
                        userRole.getHoursSpent() == null ? 0 : userRole.getHoursSpent());
            if (hours != null) {
               modified = true;
               userRole.setHoursSpent(hours.equals("") ? 0 : (new Double(hours)).doubleValue());
            }
         } else if (aCol == UserRoleColumn.Num_Minor_Col || aCol == UserRoleColumn.Num_Major_Col || aCol == UserRoleColumn.Num_Issues_Col) {
            AWorkbench.popup("ERROR", "Field is calculated");
         } else if (aCol == UserRoleColumn.Completed_Col) {
            if (userRole.getHoursSpent() == null) {
               AWorkbench.popup("ERROR", "Must enter Hours Spent");
               return false;
            }
            modified = true;
            userRole.setCompleted(!userRole.isCompleted());
         } else if (aCol == UserRoleColumn.User_Col) {
            UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New User");
            int result = ld.open();
            if (result == 0) {
               User selectedUser = (User) ld.getSelection();
               if (selectedUser != null && userRole.getUser() != selectedUser) {
                  modified = true;
                  userRole.setUser(selectedUser);
               }
            }
         } else if (aCol == UserRoleColumn.Role_Col) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(aCol.getName(), Role.strValues(),
                        userRole.getRole().name());
            if (enumDialog != null) {
               if (enumDialog.getResult()[0] != null) {
                  modified = true;
                  userRole.setRole(Role.valueOf((String) enumDialog.getResult()[0]));
               }
            }
         } else
            throw new IllegalStateException("Unhandled user role column");

         if (modified) {
            xUserRoleViewer.getReviewArt().getUserRoleManager().addOrUpdateUserRole(userRole, false);
            xUserRoleViewer.notifyXModifiedListeners();
            update(userRole, null);
            return true;
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiDebug.class, ex, true);
      }
      return false;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XUserRoleViewer getXUserRoleViewer() {
      return xUserRoleViewer;
   }

}
