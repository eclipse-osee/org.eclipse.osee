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
package org.eclipse.osee.ats.util.widgets.defect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XPromptChange;
import org.eclipse.nebula.widgets.xviewer.XPromptChange.Option;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.EnumStringSingleSelectionDialog;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class DefectXViewer extends XViewer {

   private final XDefectViewer xDefectViewer;
   private Action editSeverityAction;
   private Action editDispositionAction;
   private Action editClosedAction;
   private Action editUserAction;
   private Action editCreatedDateAction;
   private Action editInjectionAction;
   private Action editDescriptionAction;
   private Action editLocationAction;
   private Action editResolutionAction;

   DefectXViewer(Composite parent, int style, XDefectViewer xDefectViewer, IOseeTreeReportProvider reportProvider) {
      this(parent, style, new DefectXViewerFactory(reportProvider), xDefectViewer);
   }

   public DefectXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, XDefectViewer xDefectViewer) {
      super(parent, style, xViewerFactory);
      this.xDefectViewer = xDefectViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         @Override
         public void widgetDisposed(DisposeEvent e) {
            ((DefectContentProvider) getContentProvider()).clear();
         }
      });
      createMenuActions();
   }

   public void createMenuActions() {
      setColumnMultiEditEnabled(true);
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());

      editSeverityAction = new Action("Edit Severity", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Severity_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editClosedAction = new Action("Edit Closed ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Closed_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editCreatedDateAction = new Action("Edit Created Date ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Created_Date_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editDescriptionAction = new Action("Edit Description ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Description_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editDispositionAction = new Action("Edit Disposition ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Disposition_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editInjectionAction = new Action("Edit Injection ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Injection_Activity_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editLocationAction = new Action("Edit Location ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Location_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editResolutionAction = new Action("Edit Resolution ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.Resolution_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editUserAction = new Action("Edit User ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(DefectXViewerFactory.User_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };
   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();
      // EDIT MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, editSeverityAction);
      editSeverityAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editClosedAction);
      editClosedAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editCreatedDateAction);
      editCreatedDateAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editDescriptionAction);
      editDescriptionAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editDispositionAction);
      editDispositionAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editInjectionAction);
      editInjectionAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editLocationAction);
      editLocationAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editResolutionAction);
      editResolutionAction.setEnabled(xDefectViewer.isEditable());
      mm.insertBefore(MENU_GROUP_PRE, editUserAction);
      editUserAction.setEnabled(xDefectViewer.isEditable());

   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public Collection<ReviewDefectItem> getLoadedDefectItems() {
      return ((DefectContentProvider) getContentProvider()).getRootSet();
   }

   public void add(Collection<ReviewDefectItem> defectItems) {
      if ((DefectContentProvider) getContentProvider() != null) {
         ((DefectContentProvider) getContentProvider()).add(defectItems);
      }
   }

   public void set(Collection<? extends ReviewDefectItem> defectItems) {
      if ((DefectContentProvider) getContentProvider() != null) {
         ((DefectContentProvider) getContentProvider()).set(defectItems);
      }
   }

   public void clear() {
      if ((DefectContentProvider) getContentProvider() != null) {
         ((DefectContentProvider) getContentProvider()).clear();
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

   public List<ReviewDefectItem> getSelectedDefectItems() {
      List<ReviewDefectItem> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((ReviewDefectItem) item.getData());
         }
      }
      return arts;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!xDefectViewer.isEditable()) {
         return;
      }
      ArrayList<ReviewDefectItem> defectItems = new ArrayList<>();
      for (TreeItem item : treeItems) {
         defectItems.add((ReviewDefectItem) item.getData());
      }
      try {
         promptChangeData((XViewerColumn) treeColumn.getData(), defectItems, isColumnMultiEditEnabled());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      if (xCol.equals(DefectXViewerFactory.User_Col) || xCol.equals(
         DefectXViewerFactory.Disposition_Col) || xCol.equals(
            DefectXViewerFactory.Injection_Activity_Col) || xCol.equals(
               DefectXViewerFactory.Closed_Col) || xCol.equals(DefectXViewerFactory.Severity_Col)) {
         return handleAltLeftClick(treeColumn, treeItem);
      }
      return false;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!xDefectViewer.isEditable()) {
         return false;
      }
      try {
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         ReviewDefectItem defectItem = (ReviewDefectItem) treeItem.getData();
         List<ReviewDefectItem> defectItems = new ArrayList<>();
         defectItems.add(defectItem);
         if (xCol.equals(DefectXViewerFactory.Severity_Col) || xCol.equals(
            DefectXViewerFactory.Disposition_Col) || xCol.equals(DefectXViewerFactory.Created_Date_Col) || xCol.equals(
               DefectXViewerFactory.Closed_Col) || xCol.equals(DefectXViewerFactory.Description_Col) || xCol.equals(
                  DefectXViewerFactory.Resolution_Col) || xCol.equals(DefectXViewerFactory.Location_Col) || xCol.equals(
                     DefectXViewerFactory.User_Col) || xCol.equals(DefectXViewerFactory.Injection_Activity_Col)) {
            return promptChangeData(xCol, defectItems, false);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private boolean setInjectionActivity(Collection<ReviewDefectItem> defectItems, InjectionActivity newInjectionActivity) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.getInjectionActivity().equals(newInjectionActivity)) {
            defectItem.setInjectionActivity(newInjectionActivity);
            // at least one in the list has been changed.
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setUser(Collection<ReviewDefectItem> defectItems, User user) throws OseeCoreException {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.getUser().equals(user)) {
            defectItem.setUser(user);
            // at least one in the list has been changed.
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setLocation(Collection<ReviewDefectItem> defectItems, String loc) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.getLocation().equals(loc)) {
            defectItem.setLocation(loc);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setDescription(Collection<ReviewDefectItem> defectItems, String desc) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.toString().equals(desc)) {
            defectItem.setDescription(desc);
            if (!modified) {
               modified = true;
            }
         }

      }
      return modified;
   }

   private boolean setClosed(Collection<ReviewDefectItem> defectItems, boolean closed) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (defectItem.isClosed() != closed) {
            defectItem.setClosed(closed);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setSeverity(Collection<ReviewDefectItem> defectItems, Severity severity) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.getSeverity().equals(severity)) {
            defectItem.setSeverity(severity);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setDisposition(Collection<ReviewDefectItem> defectItems, Disposition disposition) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.getDisposition().equals(disposition)) {
            defectItem.setDisposition(disposition);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setDate(Collection<ReviewDefectItem> defectItems, Date date) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.getDate().equals(date)) {
            defectItem.setDate(date);
            if (!modified) {
               modified = true;
            }
         }
      }
      return modified;
   }

   private boolean setResolution(Collection<ReviewDefectItem> defectItems, String resolution) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         defectItem.setResolution(resolution);
         if (!modified) {
            modified = true;
         }
      }
      return modified;
   }

   public boolean promptChangeData(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit) throws OseeCoreException {
      boolean modified = false;
      if (defectItems != null && !defectItems.isEmpty()) {
         ReviewDefectItem defectItem = (ReviewDefectItem) defectItems.toArray()[0];
         if (xCol.equals(DefectXViewerFactory.Severity_Col)) {
            modified = handleSeverityCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(DefectXViewerFactory.Disposition_Col)) {
            modified = handleDispositionCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(DefectXViewerFactory.Created_Date_Col)) {
            modified = handleCreatedDateCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(DefectXViewerFactory.Closed_Col)) {
            modified = handleClosedCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(DefectXViewerFactory.Description_Col)) {
            modified = handleDescriptionCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(DefectXViewerFactory.Resolution_Col)) {
            modified = handleResolutionCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(DefectXViewerFactory.Location_Col)) {
            modified = handleLocationCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(DefectXViewerFactory.User_Col)) {
            modified = handleUserCol(defectItems, modified);
         } else if (xCol.equals(DefectXViewerFactory.Injection_Activity_Col)) {
            modified = handleInjectionActivityCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         }
         if (modified) {
            return executeTransaction(defectItems);
         }
      }
      return false;
   }

   private boolean handleInjectionActivityCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      EnumStringSingleSelectionDialog enumDialog = XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(),
         InjectionActivity.strValues(), columnMultiEdit ? null : defectItem.getInjectionActivity().name());
      if (enumDialog != null && enumDialog.getResult() != null) {
         modified = setInjectionActivity(defectItems, InjectionActivity.valueOf((String) enumDialog.getResult()[0]));
      }
      return modified;
   }

   private boolean handleUserCol(Collection<ReviewDefectItem> defectItems, boolean modified) throws OseeCoreException {
      UserListDialog ld = new UserListDialog(Displays.getActiveShell(), "Select New User",
         AtsClientService.get().getUserServiceClient().getOseeUsersSorted(Active.Active));
      int result = ld.open();
      if (result == 0) {
         modified = setUser(defectItems, ld.getSelection());
      }
      return modified;
   }

   private boolean handleLocationCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      String loc = XPromptChange.promptChangeString(xCol.getName(), columnMultiEdit ? null : defectItem.getLocation(),
         null, Option.MULTI_LINE);
      if (loc != null) {
         modified = setLocation(defectItems, loc);
      }
      return modified;
   }

   private boolean handleResolutionCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      String resolution = XPromptChange.promptChangeString(xCol.getName(),
         columnMultiEdit ? null : defectItem.getResolution(), null, Option.MULTI_LINE);
      if (resolution != null) {
         modified = setResolution(defectItems, resolution);
      }
      return modified;
   }

   private boolean handleDescriptionCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      String desc = XPromptChange.promptChangeString(xCol.getName(),
         columnMultiEdit ? null : defectItem.getDescription(), null, Option.MULTI_LINE);
      if (desc != null) {
         modified = setDescription(defectItems, desc);
      }
      return modified;
   }

   private boolean handleClosedCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      Boolean closed = XPromptChange.promptChangeBoolean(xCol.getName(), xCol.getName(),
         columnMultiEdit ? false : defectItem.isClosed());
      if (closed != null) {
         modified = setClosed(defectItems, closed);
      }
      return modified;
   }

   private boolean handleCreatedDateCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      Date selDate = XPromptChange.promptChangeDate(xCol.getName(), columnMultiEdit ? defectItem.getDate() : null);
      if (selDate != null) {
         modified = setDate(defectItems, selDate);
      }
      return modified;
   }

   private boolean handleDispositionCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      EnumStringSingleSelectionDialog enumDialog = XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(),
         Disposition.strValues(), columnMultiEdit ? null : defectItem.getDisposition().name());
      if (enumDialog != null && enumDialog.getResult() != null) {
         modified = setDisposition(defectItems, Disposition.valueOf((String) enumDialog.getResult()[0]));
      }
      return modified;
   }

   private boolean handleSeverityCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      EnumStringSingleSelectionDialog enumDialog = XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(),
         Severity.strValues(), columnMultiEdit ? null : defectItem.getSeverity().name());
      if (enumDialog != null && enumDialog.getResult() != null) {
         modified = setSeverity(defectItems, Severity.valueOf((String) enumDialog.getResult()[0]));
      }
      return modified;
   }

   public boolean executeTransaction(Collection<ReviewDefectItem> defectItems) throws OseeCoreException {
      SkynetTransaction transaction = TransactionManager.createTransaction(
         xDefectViewer.getReviewArt().getArtifact().getBranch(), "Modify Review Defects");
      for (ReviewDefectItem defectItem : defectItems) {
         xDefectViewer.getDefectManager().addOrUpdateDefectItem(defectItem);
         update(defectItem, null);
      }
      xDefectViewer.getDefectManager().saveToArtifact(xDefectViewer.getReviewArt());
      transaction.execute();
      xDefectViewer.notifyXModifiedListeners();
      return true;
   }
}
