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
import java.util.Date;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XPromptChange;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XPromptChange.Option;
import org.eclipse.osee.coverage.internal.CoveragePlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
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
   Action editPromotedAction;
   Action editEngBuildIdAction;
   Action editPlannedCMBuildIdAction;

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
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActionsForTable();
         }
      });

      editPromotedAction = new Action("Edit Promoted", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("Not Implemented Yet");
         }
      };

      editEngBuildIdAction = new Action("Edit Engineering Build ID", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("Not Implemented Yet");
         }
      };

      editPlannedCMBuildIdAction = new Action("Edit Planned CM Build ID", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("Not Implemented Yet");
         }
      };

   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();
      // EDIT MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, editPromotedAction);
      editPromotedAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editEngBuildIdAction);
      editEngBuildIdAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editPlannedCMBuildIdAction);
      editPlannedCMBuildIdAction.setEnabled(true);
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public Collection<CoverageItem> getLoadedDefectItems() {
      return ((CoverageContentProvider) getContentProvider()).getRootSet();
   }

   public void add(Collection<CoverageItem> coverageItems) {
      if ((CoverageContentProvider) getContentProvider() != null) {
         ((CoverageContentProvider) getContentProvider()).add(coverageItems);
      }
   }

   public void set(Collection<? extends CoverageItem> coverageItems) {
      if ((CoverageContentProvider) getContentProvider() != null) {
         ((CoverageContentProvider) getContentProvider()).set(coverageItems);
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
      // Tell the label provider to release its ressources
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
      ArrayList<CoverageItem> coverageItems = new ArrayList<CoverageItem>();
      for (TreeItem item : treeItems) {
         coverageItems.add((CoverageItem) item.getData());
      }
      try {
         promptChangeData((XViewerColumn) treeColumn.getData(), coverageItems, isColumnMultiEditEnabled());
      } catch (OseeCoreException ex) {
         OseeLog.log(CoveragePlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
         CoverageItem coverageItem = (CoverageItem) treeItem.getData();
         List<CoverageItem> coverageItems = new ArrayList<CoverageItem>();
         coverageItems.add(coverageItem);
      } catch (Exception ex) {
         OseeLog.log(CoveragePlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public void handleDoubleClick() {
      if (getSelectedDefectItems().size() > 0) {
      }
   }

   private boolean setDate(Collection<CoverageItem> coverageItems, Date selDate) {
      boolean modified = false;
      for (CoverageItem coverageItem : coverageItems) {
         coverageItem.setDate(selDate);
         if (!modified) {
            modified = true;
         }
      }
      return modified;
   }

   private boolean setPromoted(Collection<CoverageItem> coverageItems, boolean closed) {
      boolean modified = false;
      for (CoverageItem coverageItem : coverageItems) {
         coverageItem.setPromoted(closed);
         if (closed) {
            coverageItem.setPromotedDate(new Date());
         }
         if (!modified) {
            modified = true;
         }
      }
      return modified;
   }

   private boolean setNotes(Collection<CoverageItem> coverageItems, String notes) {
      boolean modified = false;
      for (CoverageItem coverageItem : coverageItems) {
         coverageItem.setNotes(notes);
         if (!modified) {
            modified = true;
         }
      }
      return modified;
   }

   private boolean setViewComparison(Collection<CoverageItem> coverageItems, String viewComp) {
      boolean modified = false;
      for (CoverageItem coverageItem : coverageItems) {
         coverageItem.setViewComparison(viewComp);
         if (!modified) {
            modified = true;
         }
      }
      return modified;
   }

   private boolean setUser(Collection<CoverageItem> coverageItems, User user) {
      boolean modified = false;
      for (CoverageItem coverageItem : coverageItems) {
         if (!coverageItem.getUser().equals(user)) {
            coverageItem.setUser(user);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setEngBuildGuid(Collection<CoverageItem> coverageItems, String guid) {
      boolean modified = false;
      for (CoverageItem coverageItem : coverageItems) {
         if (!coverageItem.getEngBuildGuid().equals(guid)) {
            coverageItem.setEngBuildGuid(guid);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setPlanCmBuildGuid(Collection<CoverageItem> coverageItems, String guid) {
      boolean modified = false;
      for (CoverageItem coverageItem : coverageItems) {
         if (!coverageItem.getPlanCmBuildGuid().equals(guid)) {
            coverageItem.setPlanCmBuildGuid(guid);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   public boolean isEditable(Collection<CoverageItem> coverageItems) {
      for (CoverageItem item : coverageItems) {
         if (!item.isEditable()) {
            return false;
         }
      }
      return true;
   }

   public boolean promptChangeData(XViewerColumn xCol, Collection<CoverageItem> coverageItems, boolean colMultiEdit) throws OseeCoreException {
      boolean modified = false;
      if (coverageItems != null && !coverageItems.isEmpty()) {
         CoverageItem coverageItem = (CoverageItem) coverageItems.toArray()[0];

         if (!isEditable(coverageItems)) {
            if (xCol.equals(CoverageXViewerFactory.Date_Col) || xCol.equals(CoverageXViewerFactory.View_Compare_Col) || xCol.equals(CoverageXViewerFactory.User_Col)) {
               MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Promote Item",
                     "Read-Only Field - One or more selected Promote Items is flagged \"Promoted\" ");
            }
         }
         if (isEditable(coverageItems) && xCol.equals(CoverageXViewerFactory.Date_Col)) {
            Date selDate = XPromptChange.promptChangeDate(xCol.getName(), coverageItem.getDate());
            if (selDate != null) {
               modified = setDate(coverageItems, selDate);
            }
         } else if (xCol.equals(CoverageXViewerFactory.Promoted_Col)) {
            Boolean closed =
                  XPromptChange.promptChangeBoolean(xCol.getName(), xCol.getName(), coverageItem.isPromoted());
            if (closed != null && coverageItem.isPromoted() != closed) {
               modified = setPromoted(coverageItems, closed);
            }
         } else if (xCol.equals(CoverageXViewerFactory.Notes_Col)) {
            String notes =
                  XPromptChange.promptChangeString(xCol.getName(), coverageItem.getNotes(), null, Option.MULTI_LINE);
            if (notes != null && !coverageItem.getNotes().equals(notes)) {
               modified = setNotes(coverageItems, notes);
            }
         } else if (isEditable(coverageItems) && xCol.equals(CoverageXViewerFactory.View_Compare_Col)) {
            String viewComp =
                  XPromptChange.promptChangeString(xCol.getName(), coverageItem.getViewComparison(), null,
                        Option.MULTI_LINE);
            if (viewComp != null && !coverageItem.getViewComparison().equals(viewComp)) {
               modified = setViewComparison(coverageItems, viewComp);
            }
         } else if (isEditable(coverageItems) && xCol.equals(CoverageXViewerFactory.User_Col)) {
            UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New User");
            int result = ld.open();
            if (result == 0) {
               User selectedUser = ld.getSelection();
               if (selectedUser != null) {
                  modified = setUser(coverageItems, selectedUser);
               }
            }
         } else if (xCol.equals(CoverageXViewerFactory.Eng_Build_Id_Col)) {

         } else if (xCol.equals(CoverageXViewerFactory.Plan_CM_Build_Id_Col)) {

         }
      }
      if (modified) {
         //         return executeTransaction(promoteItems);
      }
      return false;
   }

}
