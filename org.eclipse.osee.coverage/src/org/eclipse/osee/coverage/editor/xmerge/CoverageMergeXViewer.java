/*
 * Created on Oct 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor.xmerge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewer;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer.TableType;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageTestUnit;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoveragePackageImporter;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageMergeXViewer extends CoverageXViewer {

   public Map<ICoverage, Boolean> importChecked = new HashMap<ICoverage, Boolean>();
   public Map<ICoverage, XResultData> importError = new HashMap<ICoverage, XResultData>();
   Action toggleImport;
   private final CoveragePackageImporter coveragePackageImport;
   public static enum ImportType {
      Add, Replace, Folder, Error, None
   };

   public CoverageMergeXViewer(CoveragePackageImporter coveragePackageImport, Composite parent, int style, IXViewerFactory xViewerFactory, XCoverageMergeViewer xCoverageMergeViewer) {
      super(parent, style, xViewerFactory, xCoverageMergeViewer);
      this.coveragePackageImport = coveragePackageImport;
   }

   public ICoverage getPackageItemForImportItem(ICoverage importItem, boolean recurse) {
      return coveragePackageImport.getPackageCoverageItem(importItem, recurse);
   }

   public String getImportError(ICoverage importItem) {
      if (!importError.containsKey(importItem)) {
         XResultData rd = new XResultData(false);
         coveragePackageImport.validateItems(Collections.singleton(importItem), rd);
         importError.put(importItem, rd);
      }
      XResultData rd = importError.get(importItem);
      if (rd.getNumErrors() > 0) {
         return rd.getReport("").getText();
      }
      return "";
   }

   public ImportType getImportType(ICoverage importItem) {
      if (importItem instanceof CoverageItem) return ImportType.None;
      if (importItem instanceof CoverageTestUnit) return ImportType.None;

      if (!importError.containsKey(importItem)) {
         XResultData rd = new XResultData(false);
         coveragePackageImport.validateItems(Collections.singleton(importItem), rd);
         importError.put(importItem, rd);
      }
      if (importError.get(importItem).getNumErrors() > 0) {
         return ImportType.Error;
      }
      ICoverage packageItem = getPackageItemForImportItem(importItem, true);
      if (packageItem == null) {
         return ImportType.Add;
      } else if (importItem.isFolder()) {
         return ImportType.Folder;
      } else {
         return ImportType.Replace;
      }
   }

   public void setImportChecked(ICoverage coverage, boolean checked) {
      if (!(coverage instanceof ICoverage) || !isImportAllowed((ICoverage) coverage)) {
         return;
      }
      importChecked.put(coverage, checked);
      xCoverageViewer.getXViewer().update(coverage, null);
      // Check or un-check any children based on parent
      for (CoverageUnit childCoverageUnit : ((CoverageUnit) coverage).getCoverageUnits()) {
         setImportChecked(childCoverageUnit, checked);
      }
      // Check any parent based on child
      ICoverage parent = coverage.getParent();
      if (isImportAllowed(parent)) {
         // If child is cheked and parent not, check parent
         if (checked && !isImportChecked(parent)) {
            importChecked.put(parent, checked);
            xCoverageViewer.getXViewer().update(parent, null);
         }
         // If all children not checked and parent checked, uncheck parent
         boolean childChecked = false;
         for (Object child : parent.getChildren()) {
            if (child instanceof ICoverage && isImportAllowed((ICoverage) child) && isImportChecked((ICoverage) child)) {
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

   public Collection<ICoverage> getSelectedImportItems() {
      List<ICoverage> selected = new ArrayList<ICoverage>();
      for (Entry<ICoverage, Boolean> entry : importChecked.entrySet()) {
         if (entry.getValue()) selected.add(entry.getKey());
      }
      return selected;
   }

   public boolean isImportChecked(ICoverage coverageItem) {
      return importChecked.get(coverageItem) == null ? false : importChecked.get(coverageItem);
   }

   public boolean isImportAllowed(ICoverage coverageItem) {
      if (coverageItem instanceof CoverageUnit) return true;
      return false;
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      if (xCol.equals(CoverageMergeXViewerFactoryImport.Import)) {
         ICoverage coverageItem = (ICoverage) treeItem.getData();
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
      for (ICoverage item : xCoverageViewer.getSelectedCoverageItems()) {
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
            for (ICoverage coverageItem : xCoverageViewer.getSelectedCoverageItems()) {
               setImportChecked(coverageItem, !isImportChecked(coverageItem));
            }
         }
      };
   }
}
