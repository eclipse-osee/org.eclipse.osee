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
         addRow(new ResultsXViewerRow(
               new String[] {
                     valueOrBlank(coverageItem.getCoverageUnit().getParentCoverageUnit() == null ? "" : coverageItem.getCoverageUnit().getParentCoverageUnit().getName()),
                     //
                     valueOrBlank(coverageItem.getCoverageUnit().getName()),
                     //
                     valueOrBlank(coverageItem.getMethodNum()),
                     //
                     valueOrBlank(coverageItem.getExecuteNum()),
                     //
                     valueOrBlank(coverageItem.getLineNum()),
                     //
                     valueOrBlank(coverageItem.getCoverageMethod())}));
      }
   }

   private String valueOrBlank(Object value) {
      if (value == null)
         return "";
      else
         return value.toString();
   }
}
