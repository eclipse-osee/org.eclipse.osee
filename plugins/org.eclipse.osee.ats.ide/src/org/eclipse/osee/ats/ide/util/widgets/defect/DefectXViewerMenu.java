/*********************************************************************
 * Copyright (c) 2016 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets.defect;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.XPromptChange;
import org.eclipse.nebula.widgets.xviewer.XPromptChange.Option;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.EnumStringSingleSelectionDialog;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewDefectManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class DefectXViewerMenu {

   private Action editSeverityAction;
   private Action editDispositionAction;
   private Action editClosedAction;
   private Action editUserAction;
   private Action editCreatedDateAction;
   private Action editInjectionAction;
   private Action editDescriptionAction;
   private Action editLocationAction;
   private Action editResolutionAction;
   private Action editNotesAction;
   private final DefectXViewer defectXViewer;
   private final IAtsPeerToPeerReview review;

   public DefectXViewerMenu(DefectXViewer defectXViewer, IAtsPeerToPeerReview review) {
      this.defectXViewer = defectXViewer;
      this.review = review;
   }

   private List<ReviewDefectItem> getSelectedDefectItems() {
      return defectXViewer.getSelectedDefectItems();
   }

   public void createMenuActions() {
      defectXViewer.setColumnMultiEditEnabled(true);
      MenuManager mm = defectXViewer.getMenuManager();
      mm.createContextMenu(defectXViewer.getControl());

      editSeverityAction = new Action("Edit Severity", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(PeerReviewDefectXViewerColumns.Severity_Col, getSelectedDefectItems(), columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.Closed_Col, getSelectedDefectItems(), columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.Created_Date_Col, getSelectedDefectItems(),
                  columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.Description_Col, getSelectedDefectItems(),
                  columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.Disposition_Col, getSelectedDefectItems(),
                  columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.Injection_Activity_Col, getSelectedDefectItems(),
                  columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.Location_Col, getSelectedDefectItems(), columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.Resolution_Col, getSelectedDefectItems(),
                  columnMultiEdit);
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
               promptChangeData(PeerReviewDefectXViewerColumns.User_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };
      editNotesAction = new Action("Edit Notes ", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            try {
               promptChangeData(PeerReviewDefectXViewerColumns.Notes_Col, getSelectedDefectItems(), columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };
   }

   public void updateEditMenuActions() {
      MenuManager mm = defectXViewer.getMenuManager();
      // EDIT MENU BLOCK
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editSeverityAction);
      editSeverityAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editClosedAction);
      editClosedAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editCreatedDateAction);
      editCreatedDateAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editDescriptionAction);
      editDescriptionAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editDispositionAction);
      editDispositionAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editInjectionAction);
      editInjectionAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editLocationAction);
      editLocationAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editResolutionAction);
      editResolutionAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editUserAction);
      editUserAction.setEnabled(isEditable());
      mm.insertBefore(XViewer.MENU_GROUP_PRE, editNotesAction);
      editNotesAction.setEnabled(isEditable());

   }

   private boolean isEditable() {
      return defectXViewer.isEditable();
   }

   public boolean promptChangeData(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit) {
      boolean modified = false;
      if (defectItems != null && !defectItems.isEmpty()) {
         ReviewDefectItem defectItem = (ReviewDefectItem) defectItems.toArray()[0];
         if (xCol.equals(PeerReviewDefectXViewerColumns.Severity_Col)) {
            modified = handleSeverityCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Disposition_Col)) {
            modified = handleDispositionCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Created_Date_Col)) {
            modified = handleCreatedDateCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Closed_Col)) {
            modified = handleClosedCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Description_Col)) {
            modified = handleDescriptionCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Resolution_Col)) {
            modified = handleResolutionCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Location_Col)) {
            modified = handleLocationCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.User_Col)) {
            modified = handleUserCol(defectItems, modified);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Injection_Activity_Col)) {
            modified = handleInjectionActivityCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
         } else if (xCol.equals(PeerReviewDefectXViewerColumns.Notes_Col)) {
            modified = handleNotesCol(xCol, defectItems, columnMultiEdit, modified, defectItem);
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

   private boolean handleUserCol(Collection<ReviewDefectItem> defectItems, boolean modified) {
      UserListDialog dialog = new UserListDialog(Displays.getActiveShell(), "Select New User", Active.Active, true);
      int result = dialog.open();
      if (result == 0) {
         modified = setUser(defectItems, dialog.getSelection());
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
      return setClosed(defectItems, !defectItem.isClosed());
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

   private boolean handleNotesCol(XViewerColumn xCol, Collection<ReviewDefectItem> defectItems, boolean columnMultiEdit, boolean modified, ReviewDefectItem defectItem) {
      String desc = XPromptChange.promptChangeString(xCol.getName(), columnMultiEdit ? null : defectItem.getNotes(),
         null, Option.MULTI_LINE);
      if (desc != null) {
         modified = setNotes(defectItems, desc);
      }
      return modified;
   }

   public boolean executeTransaction(Collection<ReviewDefectItem> defectItems) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Modify Review Defects");
      IAtsPeerReviewDefectManager defectManager = review.getDefectManager();
      for (ReviewDefectItem defectItem : defectItems) {
         defectManager.addOrUpdateDefectItem(defectItem);
         defectXViewer.update(defectItem, null);
      }
      defectManager.saveToArtifact(review, changes);
      changes.executeIfNeeded();
      return true;
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

   private boolean setUser(Collection<ReviewDefectItem> defectItems, AtsUser user) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.getUserId().equals(user.getUserId())) {
            defectItem.setUserId(user.getUserId());
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

   private boolean setNotes(Collection<ReviewDefectItem> defectItems, String desc) {
      boolean modified = false;
      for (ReviewDefectItem defectItem : defectItems) {
         if (!defectItem.toString().equals(desc)) {
            defectItem.setNotes(desc);
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

}
