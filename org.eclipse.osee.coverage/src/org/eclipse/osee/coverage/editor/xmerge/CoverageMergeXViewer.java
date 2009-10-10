/*
 * Created on Oct 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor.xmerge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewer;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer.TableType;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageMergeXViewer extends CoverageXViewer {

   public Map<ICoverageEditorItem, Boolean> importChecked = new HashMap<ICoverageEditorItem, Boolean>();
   Action toggleImport;

   public CoverageMergeXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, XCoverageMergeViewer xCoverageMergeViewer) {
      super(parent, style, xViewerFactory, xCoverageMergeViewer);
   }

   public void setImportChecked(ICoverageEditorItem coverageItem, boolean checked) {
      importChecked.put(coverageItem, checked);
      xCoverageViewer.getXViewer().update(coverageItem, null);
      // Check or un-check any children based on parent
      for (CoverageUnit coverageUnit : ((CoverageUnit) coverageItem).getCoverageUnits()) {
         importChecked.put(coverageUnit, checked);
         xCoverageViewer.getXViewer().update(coverageUnit, null);
      }
      // Check any parent based on child
      ICoverageEditorItem parent = coverageItem.getParent();
      if (isImportAllowed(parent)) {
         // If child is cheked and parent not, check parent
         if (checked && !isImportChecked(parent)) {
            importChecked.put(parent, checked);
            xCoverageViewer.getXViewer().update(parent, null);
         }
         // If all children not checked and parent checked, uncheck parent
         boolean childChecked = false;
         for (Object child : parent.getChildren()) {
            if (child instanceof ICoverageEditorItem && isImportAllowed((ICoverageEditorItem) child) && isImportChecked((ICoverageEditorItem) child)) {
               childChecked = true;
               break;
            }
         }
         if (!childChecked && isImportChecked(parent)) {
            importChecked.put(parent, false);
            xCoverageViewer.getXViewer().update(parent, null);
         }
      }
   }

   public Collection<ICoverageEditorItem> getSelectedImportItems() {
      List<ICoverageEditorItem> selected = new ArrayList<ICoverageEditorItem>();
      for (Entry<ICoverageEditorItem, Boolean> entry : importChecked.entrySet()) {
         if (entry.getValue()) selected.add(entry.getKey());
      }
      return selected;
   }

   public boolean isImportChecked(ICoverageEditorItem coverageItem) {
      return importChecked.get(coverageItem) == null ? false : importChecked.get(coverageItem);
   }

   public boolean isImportAllowed(ICoverageEditorItem coverageItem) {
      if (coverageItem instanceof CoverageUnit) return true;
      return false;
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      if (xCol.equals(CoverageMergeXViewerFactoryImport.Import)) {
         ICoverageEditorItem coverageItem = (ICoverageEditorItem) treeItem.getData();
         Boolean checked = importChecked.get(coverageItem);
         if (checked == null)
            checked = true;
         else
            checked = !checked;
         setImportChecked(coverageItem, checked);
         return true;
      }
      return super.handleLeftClickInIconArea(treeColumn, treeItem);
   }

   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      return super.handleLeftClick(treeColumn, treeItem);
   }

   @Override
   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();
      if (xCoverageViewer.isType(TableType.Merge) && xCoverageViewer.isType(TableType.Import)) {
         mm.insertBefore(MENU_GROUP_PRE, toggleImport);
         toggleImport.setEnabled(isToggleImportEnabled());
      }
      super.updateEditMenuActions();
   }

   private boolean isToggleImportEnabled() {
      if (xCoverageViewer.getSelectedCoverageItems().size() == 0) return false;
      for (ICoverageEditorItem item : xCoverageViewer.getSelectedCoverageItems()) {
         if (item.isEditable().isFalse() && isImportAllowed(item)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      toggleImport = new Action("Toggle Import", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            for (ICoverageEditorItem coverageItem : xCoverageViewer.getSelectedCoverageItems()) {
               setImportChecked(coverageItem, !isImportChecked(coverageItem));
            }
         }
      };
   }
}
