/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.results;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class CoverageItemResultsTableTab extends ResultsEditorTableTab {

   private static enum Columns {
      Parent_Coverage_Unit, Coverage_Unit, Method_Number, Execution_Number, Line_Number, Coverage_Method;
   };

   public CoverageItemResultsTableTab() {
      super("Coverage Items");
      addColumn(new XViewerColumn(Columns.Parent_Coverage_Unit.name(), Columns.Parent_Coverage_Unit.name(), 80,
            SWT.LEFT, true, SortDataType.String, false, ""));
      addColumn(new XViewerColumn(Columns.Coverage_Unit.name(), Columns.Coverage_Unit.name(), 80, SWT.LEFT, true,
            SortDataType.String, false, ""));
      addColumn(new XViewerColumn(Columns.Method_Number.name(), Columns.Method_Number.name(), 80, SWT.LEFT, true,
            SortDataType.String, false, ""));
      addColumn(new XViewerColumn(Columns.Execution_Number.name(), Columns.Execution_Number.name(), 80, SWT.LEFT, true,
            SortDataType.String, false, ""));
      addColumn(new XViewerColumn(Columns.Line_Number.name(), Columns.Line_Number.name(), 80, SWT.LEFT, true,
            SortDataType.String, false, ""));
      addColumn(new XViewerColumn(Columns.Coverage_Method.name(), Columns.Coverage_Method.name(), 80, SWT.LEFT, true,
            SortDataType.String, false, ""));
   }

   public CoverageItemResultsTableTab(Collection<CoverageUnit> coverageUnits) {
      this();
      for (CoverageUnit coverageUnit : coverageUnits) {
         addRows(coverageUnit.getCoverageItems(true));
      }
   }

   private void addRows(Collection<CoverageItem> coverageItems) {
      for (CoverageItem coverageItem : coverageItems) {
         addRow(new ResultsXViewerRow(new String[] {coverageItem.getCoverageUnit().getParentCoverageUnit().getName(),
               coverageItem.getCoverageUnit().getName(), String.valueOf(coverageItem.getMethodNum()),
               String.valueOf(coverageItem.getExecuteNum()), String.valueOf(coverageItem.getLineNum()),
               String.valueOf(coverageItem.getCoverageMethod())}));
      }
   }
}
