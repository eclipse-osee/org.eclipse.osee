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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.action.DeleteCoverUnitAction;
import org.eclipse.osee.coverage.action.EditAssigneesAction;
import org.eclipse.osee.coverage.action.EditCoverageMethodAction;
import org.eclipse.osee.coverage.action.EditCoverageNotesAction;
import org.eclipse.osee.coverage.action.EditRationaleAction;
import org.eclipse.osee.coverage.action.IRefreshable;
import org.eclipse.osee.coverage.action.ISelectedCoverageEditorItem;
import org.eclipse.osee.coverage.action.ViewSourceAction;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer.TableType;
import org.eclipse.osee.coverage.editor.xmerge.CoverageMergeXViewerFactory;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageXViewer extends XViewer implements ISelectedCoverageEditorItem, ISaveable, IRefreshable {

   protected final XCoverageViewer xCoverageViewer;
   Action editRationaleAction, editMethodAction, viewSourceAction, editAssigneesAction, editCoverageStatusAction,
         deleteCoverUnitAction;

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
      createMenuActions();
   }

   public void createMenuActions() {
      if (viewSourceAction == null) {
         viewSourceAction = new ViewSourceAction(this);
         editMethodAction = new EditCoverageMethodAction(this, this, this);
         editAssigneesAction = new EditAssigneesAction(this, this, this);
         editCoverageStatusAction = new EditCoverageNotesAction(this, this, this);
         editRationaleAction = new EditRationaleAction(this, this, this);
         deleteCoverUnitAction = new DeleteCoverUnitAction(this, this, this);
      }
   }

   private boolean isEditRationaleEnabled() {
      if (xCoverageViewer.getSelectedCoverageItems().size() == 0) return false;
      for (ICoverage item : xCoverageViewer.getSelectedCoverageItems()) {
         if (!(item instanceof CoverageItem)) {
            return false;
         }
      }
      return true;
   }

   private boolean isEditMethodEnabled() {
      if (xCoverageViewer.getSelectedCoverageItems().size() == 0) return false;
      for (ICoverage item : xCoverageViewer.getSelectedCoverageItems()) {
         if (!(item instanceof CoverageItem)) {
            return false;
         }
      }
      return true;
   }

   private boolean isDeleteCoverageUnitEnabled() {
      if (xCoverageViewer.getSelectedCoverageItems().size() == 0) return false;
      for (ICoverage item : xCoverageViewer.getSelectedCoverageItems()) {
         if (!(item instanceof CoverageUnit)) {
            return false;
         }
      }
      return true;
   }

   private boolean isEditMetricsEnabled() {
      if (xCoverageViewer.getSelectedCoverageItems().size() == 0) return false;
      for (ICoverage item : xCoverageViewer.getSelectedCoverageItems()) {
         if (!(item instanceof CoverageUnit)) {
            return false;
         }
      }
      return true;
   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();
      // EDIT MENU BLOCK
      if (xCoverageViewer.isType(TableType.Package)) {
         mm.insertBefore(MENU_GROUP_PRE, editRationaleAction);
         editRationaleAction.setEnabled(isEditRationaleEnabled());

         mm.insertBefore(MENU_GROUP_PRE, editMethodAction);
         editMethodAction.setEnabled(isEditMethodEnabled());

         mm.insertBefore(MENU_GROUP_PRE, editAssigneesAction);
         editAssigneesAction.setEnabled(isEditMetricsEnabled());

         mm.insertBefore(MENU_GROUP_PRE, editCoverageStatusAction);
         editCoverageStatusAction.setEnabled(isEditMetricsEnabled());

         mm.insertBefore(MENU_GROUP_PRE, deleteCoverUnitAction);
         editCoverageStatusAction.setEnabled(isDeleteCoverageUnitEnabled());

      }
      mm.insertBefore(MENU_GROUP_PRE, viewSourceAction);
      editMethodAction.setEnabled(isEditMethodEnabled());
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
      mm.insertBefore(MENU_GROUP_PRE, new org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction(
            xCoverageViewer.getXViewer(), true));
   }

   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
   }

   public ArrayList<ICoverage> getSelectedCoverageEditorItems() {
      ArrayList<ICoverage> arts = new ArrayList<ICoverage>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((ICoverage) item.getData());
         }
      }
      return arts;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!xCoverageViewer.isEditable()) {
         return;
      }
      ArrayList<ICoverage> coverageItems = new ArrayList<ICoverage>();
      for (TreeItem item : treeItems) {
         coverageItems.add((ICoverage) item.getData());
      }
      try {
         promptChangeData((XViewerColumn) treeColumn.getData(), coverageItems, isColumnMultiEditEnabled());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      return false;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!xCoverageViewer.isEditable()) {
         return false;
      }
      createMenuActions();
      try {
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         if (xCol.equals(CoverageMergeXViewerFactory.Assignees_Col)) {
            editAssigneesAction.run();
         } else if (xCol.equals(CoverageMergeXViewerFactory.Notes_Col)) {
            editCoverageStatusAction.run();
         } else if (xCol.equals(CoverageMergeXViewerFactory.Coverage_Method)) {
            editMethodAction.run();
         } else if (xCol.equals(CoverageMergeXViewerFactory.Coverage_Rationale)) {
            editRationaleAction.run();
         } else if (xCol.equals(CoverageMergeXViewerFactory.Name) || xCol.equals(CoverageMergeXViewerFactory.File_Contents)) {
            viewSourceAction.run();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public void handleDoubleClick() {
      createMenuActions();
      if (getSelectedCoverageEditorItems().size() > 0) {
         viewSourceAction.run();
      }
   }

   public Result isEditable(Collection<ICoverage> coverageItems) {
      for (ICoverage item : coverageItems) {
         if (item.isEditable().isFalse()) {
            return item.isEditable();
         }
      }
      return Result.TrueResult;
   }

   public boolean promptChangeData(XViewerColumn xCol, Collection<ICoverage> coverageItems, boolean colMultiEdit) throws OseeCoreException {
      boolean modified = false;
      if (coverageItems != null && !coverageItems.isEmpty()) {
         //         ICoverage coverageItem = (ICoverage) coverageItems.toArray()[0];

         if (isEditable(coverageItems).isFalse()) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Coverage Item",
                  "Read-Only Field - One or more selected Coverage Items is Read-Only");
         }
      }
      if (modified) {
         //         return executeTransaction(promoteItems);
      }
      return false;
   }

   @Override
   public void update(Object element) {
      xCoverageViewer.getXViewer().update(element, null);
   }

   @Override
   public Result isEditable() {
      return xCoverageViewer.getSaveable().isEditable();
   }

   @Override
   public Result save() throws OseeCoreException {
      return xCoverageViewer.getSaveable().save();
   }

   @Override
   public void setSelectedCoverageEditorItem(ICoverage item) {
      xCoverageViewer.getXViewer().setSelection(new StructuredSelection(item));
      xCoverageViewer.getXViewer().reveal(new StructuredSelection(item));
   }

}
