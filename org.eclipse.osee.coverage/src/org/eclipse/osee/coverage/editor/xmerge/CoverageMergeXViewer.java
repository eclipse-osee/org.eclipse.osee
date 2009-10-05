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
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewer;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageMergeXViewer extends CoverageXViewer {

   public Map<ICoverageEditorItem, Boolean> importChecked = new HashMap<ICoverageEditorItem, Boolean>();

   public CoverageMergeXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, XCoverageMergeViewer xCoverageMergeViewer) {
      super(parent, style, xViewerFactory, xCoverageMergeViewer);
   }

   public void setImportChecked(ICoverageEditorItem coverageItem, boolean checked) {
      importChecked.put(coverageItem, checked);
   }

   public Collection<ICoverageEditorItem> getSelectedImportItems() {
      List<ICoverageEditorItem> selected = new ArrayList<ICoverageEditorItem>();
      for (Entry<ICoverageEditorItem, Boolean> entry : importChecked.entrySet()) {
         if (entry.getValue()) selected.add(entry.getKey());
      }
      return selected;
   }

   public boolean isImportChecked(ICoverageEditorItem coverageItem) throws OseeCoreException {
      return importChecked.get(coverageItem) == null ? false : importChecked.get(coverageItem);
   }

   public boolean isImportAllowed(ICoverageEditorItem coverageItem) throws OseeCoreException {
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
         importChecked.put(coverageItem, checked);
         xCoverageViewer.getXViewer().update(coverageItem, null);
         return true;
      }
      return super.handleLeftClickInIconArea(treeColumn, treeItem);
   }

   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      return super.handleLeftClick(treeColumn, treeItem);
   }

}
