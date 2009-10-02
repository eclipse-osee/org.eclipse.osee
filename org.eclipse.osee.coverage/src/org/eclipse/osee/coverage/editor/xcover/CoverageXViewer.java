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
package org.eclipse.osee.coverage.editor.xcover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
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
public class CoverageXViewer extends XViewer {

   private final XCoverageViewer xCoverageViewer;
   Action editAction1;
   Action editAction2;
   Action editAction3;

   /**
    * @param parent
    * @param style
    */
   public CoverageXViewer(Composite parent, int style, XCoverageViewer xCoverageViewer) {
      this(parent, style, new CoverageXViewerFactory(), xCoverageViewer);
   }

   public CoverageXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, XCoverageViewer xCoverageViewer) {
      super(parent, style, xViewerFactory, false, false);
      this.xCoverageViewer = xCoverageViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            ((CoverageContentProvider) getContentProvider()).clear();
         }
      });
      createMenuActions();
   }

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());

      editAction1 = new Action("Edit 1", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("Not Implemented Yet");
         }
      };

      editAction2 = new Action("Edit 2", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("Not Implemented Yet");
         }
      };

      editAction3 = new Action("2", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("Not Implemented Yet");
         }
      };

   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();
      // EDIT MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, editAction1);
      editAction1.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editAction2);
      editAction2.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editAction3);
      editAction3.setEnabled(true);
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
      mm.insertBefore(MENU_GROUP_PRE, new org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction(
            xCoverageViewer.getXViewer(), true));
   }

   public Collection<ICoverageEditorItem> getLoadedItems() {
      return ((CoverageContentProvider) getContentProvider()).getRootSet();
   }

   public void add(Collection<ICoverageEditorItem> coverageEditorItems) {
      if ((CoverageContentProvider) getContentProvider() != null) {
         ((CoverageContentProvider) getContentProvider()).add(coverageEditorItems);
      }
   }

   public void set(Collection<? extends ICoverageEditorItem> coverageEditorItems) {
      if ((CoverageContentProvider) getContentProvider() != null) {
         ((CoverageContentProvider) getContentProvider()).set(coverageEditorItems);
      }
   }

   public void clear() {
      if ((CoverageContentProvider) getContentProvider() != null) {
         ((CoverageContentProvider) getContentProvider()).clear();
      }
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

   public ArrayList<CoverageItem> getSelectedDefectItems() {
      ArrayList<CoverageItem> arts = new ArrayList<CoverageItem>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((CoverageItem) item.getData());
         }
      }
      return arts;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!xCoverageViewer.isEditable()) {
         return;
      }
      ArrayList<ICoverageEditorItem> coverageItems = new ArrayList<ICoverageEditorItem>();
      for (TreeItem item : treeItems) {
         coverageItems.add((ICoverageEditorItem) item.getData());
      }
      try {
         promptChangeData((XViewerColumn) treeColumn.getData(), coverageItems, isColumnMultiEditEnabled());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      return handleAltLeftClick(treeColumn, treeItem);
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!xCoverageViewer.isEditable()) {
         return false;
      }
      try {
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         ICoverageEditorItem coverageItem = (ICoverageEditorItem) treeItem.getData();
         List<ICoverageEditorItem> coverageItems = new ArrayList<ICoverageEditorItem>();
         coverageItems.add(coverageItem);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public void handleDoubleClick() {
      if (getSelectedDefectItems().size() > 0) {
      }
   }

   private boolean setUser(Collection<ICoverageEditorItem> coverageItems, User user) {
      boolean modified = false;
      for (ICoverageEditorItem coverageItem : coverageItems) {
         if (!coverageItem.getUser().equals(user)) {
            coverageItem.setUser(user);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   public Result isEditable(Collection<ICoverageEditorItem> coverageItems) {
      for (ICoverageEditorItem item : coverageItems) {
         if (item.isEditable().isFalse()) {
            return item.isEditable();
         }
      }
      return Result.TrueResult;
   }

   public boolean promptChangeData(XViewerColumn xCol, Collection<ICoverageEditorItem> coverageItems, boolean colMultiEdit) throws OseeCoreException {
      boolean modified = false;
      if (coverageItems != null && !coverageItems.isEmpty()) {
         ICoverageEditorItem coverageItem = (ICoverageEditorItem) coverageItems.toArray()[0];

         if (isEditable(coverageItems).isFalse()) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Coverage Item",
                  "Read-Only Field - One or more selected Coverage Items is Read-Only");
         } else if (isEditable(coverageItems).isTrue() && xCol.equals(CoverageXViewerFactory.User_Col)) {
            UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New User");
            int result = ld.open();
            if (result == 0) {
               User selectedUser = ld.getSelection();
               if (selectedUser != null) {
                  modified = setUser(coverageItems, selectedUser);
               }
            }
         }
      }
      if (modified) {
         //         return executeTransaction(promoteItems);
      }
      return false;
   }

}
