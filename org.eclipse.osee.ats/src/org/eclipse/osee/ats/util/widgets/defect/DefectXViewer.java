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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XPromptChange;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XPromptChange.Option;
import org.eclipse.nebula.widgets.xviewer.util.EnumStringSingleSelectionDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.InjectionActivity;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
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

   /**
    * @param parent
    * @param style
    */
   DefectXViewer(Composite parent, int style, XDefectViewer xDefectViewer) {
      this(parent, style, new DefectXViewerFactory(), xDefectViewer);
   }

   public DefectXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, XDefectViewer xDefectViewer) {
      super(parent, style, xViewerFactory);
      this.xDefectViewer = xDefectViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
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
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions();
         }
      });

      editSeverityAction = new Action("Edit Severity", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Severity_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editClosedAction = new Action("Edit Closed ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Closed_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editCreatedDateAction = new Action("Edit Created Date ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Created_Date_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editDescriptionAction = new Action("Edit Description ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Description_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editDispositionAction = new Action("Edit Disposition ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Disposition_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editInjectionAction = new Action("Edit Injection ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Injection_Activity_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editLocationAction = new Action("Edit Location ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Location_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editResolutionAction = new Action("Edit Resolution ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.Resolution_Col, defectItems, columnMultiEdit);
            } catch (OseeCoreException ex) {
               OseeLog.log(DefectXViewer.class, OseeLevel.SEVERE_POPUP, ex.toString());
            }
         }
      };

      editUserAction = new Action("Edit User ", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            boolean columnMultiEdit = false;
            // grab the data, prompt change
            ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
            defectItems = getSelectedDefectItems();
            try {
               promptChangeData(DefectXViewerFactory.User_Col, defectItems, columnMultiEdit);
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
      editSeverityAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editClosedAction);
      editClosedAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editCreatedDateAction);
      editCreatedDateAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editDescriptionAction);
      editDescriptionAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editDispositionAction);
      editDispositionAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editInjectionAction);
      editInjectionAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editLocationAction);
      editLocationAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editResolutionAction);
      editResolutionAction.setEnabled(true);
      mm.insertBefore(MENU_GROUP_PRE, editUserAction);
      editUserAction.setEnabled(true);

   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();
      updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public Collection<DefectItem> getLoadedDefectItems() {
      return ((DefectContentProvider) getContentProvider()).getRootSet();
   }

   public void add(Collection<DefectItem> defectItems) {
      if (((DefectContentProvider) getContentProvider()) != null) ((DefectContentProvider) getContentProvider()).add(defectItems);
   }

   public void set(Collection<? extends DefectItem> defectItems) {
      if (((DefectContentProvider) getContentProvider()) != null) ((DefectContentProvider) getContentProvider()).set(defectItems);
   }

   public void clear() {
      if (((DefectContentProvider) getContentProvider()) != null) ((DefectContentProvider) getContentProvider()).clear();
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

   public ArrayList<DefectItem> getSelectedDefectItems() {
      ArrayList<DefectItem> arts = new ArrayList<DefectItem>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((DefectItem) item.getData());
      return arts;
   }

   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!xDefectViewer.isEditable()) {
         return;
      }
      ArrayList<DefectItem> defectItems = new ArrayList<DefectItem>();
      for (TreeItem item : treeItems) {
         defectItems.add((DefectItem) item.getData());
      }
      try {
         promptChangeData((XViewerColumn) treeColumn.getData(), defectItems, isColumnMultiEditEnabled());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClickInIconArea(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      if (xCol.equals(DefectXViewerFactory.User_Col) || xCol.equals(DefectXViewerFactory.Injection_Activity_Col) || xCol.equals(DefectXViewerFactory.Closed_Col) || xCol.equals(DefectXViewerFactory.Severity_Col)) {
         return handleAltLeftClick(treeColumn, treeItem);
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.viewer.XViewer#handleAltLeftClick(org.eclipse.swt.widgets.TreeColumn,
    *      org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!xDefectViewer.isEditable()) {
         return false;
      }
      try {
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         DefectItem defectItem = (DefectItem) treeItem.getData();
         List<DefectItem> defectItems = new ArrayList<DefectItem>();
         defectItems.add(defectItem);
         if (xCol.equals(DefectXViewerFactory.Severity_Col) || xCol.equals(DefectXViewerFactory.Disposition_Col) || xCol.equals(DefectXViewerFactory.Created_Date_Col) || xCol.equals(DefectXViewerFactory.Closed_Col) || xCol.equals(DefectXViewerFactory.Description_Col) || xCol.equals(DefectXViewerFactory.Resolution_Col) || xCol.equals(DefectXViewerFactory.Location_Col) || xCol.equals(DefectXViewerFactory.User_Col) || xCol.equals(DefectXViewerFactory.Injection_Activity_Col)) {
            return promptChangeData(xCol, defectItems, false);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiDebug.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private boolean setInjectionActivity(Collection<DefectItem> defectItems, InjectionActivity newInjectionActivity) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (!defectItem.getInjectionActivity().equals(newInjectionActivity)) {
            defectItem.setInjectionActivity(newInjectionActivity);
            // at least one in the list has been changed.
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setUser(Collection<DefectItem> defectItems, User user) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (!defectItem.getUser().equals(user)) {
            defectItem.setUser(user);
            // at least one in the list has been changed.
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setLocation(Collection<DefectItem> defectItems, String loc) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (!defectItem.getLocation().equals(loc)) {
            defectItem.setLocation(loc);
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setDescription(Collection<DefectItem> defectItems, String desc) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (!defectItem.equals(desc)) {
            defectItem.setDescription(desc);
            if (!modified) modified = true;
         }

      }
      return modified;
   }

   private boolean setClosed(Collection<DefectItem> defectItems, boolean closed) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (defectItem.isClosed() != closed) {
            defectItem.setClosed(closed);
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setSeverity(Collection<DefectItem> defectItems, Severity severity) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (!defectItem.getSeverity().equals(severity)) {
            defectItem.setSeverity(severity);
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setDisposition(Collection<DefectItem> defectItems, Disposition disposition) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (!defectItem.getDisposition().equals(disposition)) {
            defectItem.setDisposition(disposition);
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setDate(Collection<DefectItem> defectItems, Date date) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         if (!defectItem.getDate().equals(date)) {
            defectItem.setDate(date);
            if (!modified) modified = true;
         }
      }
      return modified;
   }

   private boolean setResolution(Collection<DefectItem> defectItems, String resolution) {
      boolean modified = false;
      for (DefectItem defectItem : defectItems) {
         defectItem.setResolution(resolution);
         if (!modified) modified = true;
      }
      return modified;
   }

   public boolean promptChangeData(XViewerColumn xCol, Collection<DefectItem> defectItems, boolean columnMultiEdit) throws OseeCoreException {
      boolean modified = false;
      if (defectItems != null && !defectItems.isEmpty()) {
         DefectItem defectItem = (DefectItem) defectItems.toArray()[0];
         if (xCol.equals(DefectXViewerFactory.Severity_Col)) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(), Severity.strValues(),
                        ((columnMultiEdit) ? null : defectItem.getSeverity().name()));
            if (enumDialog != null && enumDialog.getResult() != null) {
               modified = setSeverity(defectItems, Severity.valueOf((String) enumDialog.getResult()[0]));
            }
         } else if (xCol.equals(DefectXViewerFactory.Disposition_Col)) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(), Disposition.strValues(),
                        ((columnMultiEdit) ? null : defectItem.getDisposition().name()));
            if (enumDialog != null && enumDialog.getResult() != null) {
               modified = setDisposition(defectItems, Disposition.valueOf((String) enumDialog.getResult()[0]));
            }
         } else if (xCol.equals(DefectXViewerFactory.Created_Date_Col)) {
            Date selDate =
                  XPromptChange.promptChangeDate(xCol.getName(), ((columnMultiEdit) ? defectItem.getDate() : null));
            if (selDate != null) {
               modified = setDate(defectItems, selDate);
            }
         } else if (xCol.equals(DefectXViewerFactory.Closed_Col)) {
            Boolean closed =
                  XPromptChange.promptChangeBoolean(xCol.getName(), xCol.getName(),
                        ((columnMultiEdit) ? false : defectItem.isClosed()));
            if (closed != null) {
               modified = setClosed(defectItems, closed);
            }
         } else if (xCol.equals(DefectXViewerFactory.Description_Col)) {
            String desc =
                  XPromptChange.promptChangeString(xCol.getName(),
                        ((columnMultiEdit) ? null : defectItem.getDescription()), null, Option.MULTI_LINE);
            if (desc != null) {
               modified = setDescription(defectItems, desc);
            }
         } else if (xCol.equals(DefectXViewerFactory.Resolution_Col)) {
            String resolution =
                  XPromptChange.promptChangeString(xCol.getName(),
                        (columnMultiEdit ? null : defectItem.getResolution()), null, Option.MULTI_LINE);
            if (resolution != null) {
               modified = setResolution(defectItems, resolution);
            }
         } else if (xCol.equals(DefectXViewerFactory.Location_Col)) {
            String loc =
                  XPromptChange.promptChangeString(xCol.getName(),
                        ((columnMultiEdit) ? null : defectItem.getLocation()), null, Option.MULTI_LINE);
            if (loc != null) {
               modified = setLocation(defectItems, loc);
            }
         } else if (xCol.equals(DefectXViewerFactory.User_Col)) {
            UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New User");
            int result = ld.open();
            if (result == 0) {
               modified = setUser(defectItems, ld.getSelection());
            }
         } else if (xCol.equals(DefectXViewerFactory.Injection_Activity_Col)) {
            EnumStringSingleSelectionDialog enumDialog =
                  XPromptChange.promptChangeSingleSelectEnumeration(xCol.getName(), InjectionActivity.strValues(),
                        ((columnMultiEdit) ? null : defectItem.getInjectionActivity().name()));
            if (enumDialog != null && enumDialog.getResult() != null) {
               modified =
                     setInjectionActivity(defectItems, InjectionActivity.valueOf((String) enumDialog.getResult()[0]));
            }
         }
         if (modified) {
            return executeTransaction(defectItems);
         }
      }
      return false;
   }

   public boolean executeTransaction(Collection<DefectItem> defectItems) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(xDefectViewer.getReviewArt().getArtifact().getBranch());
      for (DefectItem defectItem : defectItems) {
         xDefectViewer.getReviewArt().getDefectManager().addOrUpdateDefectItem(defectItem, false, transaction);
         update(defectItem, null);
      }
      transaction.execute();
      xDefectViewer.notifyXModifiedListeners();
      return true;
   }
}
