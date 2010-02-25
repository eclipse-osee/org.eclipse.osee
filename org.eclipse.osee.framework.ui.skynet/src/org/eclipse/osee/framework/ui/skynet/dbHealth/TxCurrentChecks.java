/*
 * Created on Oct 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.operation.OperationReporter;
import org.eclipse.osee.framework.database.operation.InvalidTxCurrentsAndModTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;

/**
 * @author Ryan D. Brooks
 */
public class TxCurrentChecks extends DatabaseHealthOperation {

   public TxCurrentChecks() {
      super("All tx currents and mod types");
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      getResultsProvider().clearTabs();

      checkAndFix("osee_arts", "art_id", monitor);
      checkAndFix("osee_attribute", "attr_id", monitor);
      checkAndFix("osee_relation_link", "rel_link_id", monitor);
   }


   private void checkAndFix(String tableName, String columnName, IProgressMonitor monitor) throws Exception {
      ResultsEditorTableTab resultsTab = new ResultsEditorTableTab(tableName + " currents");
      getResultsProvider().addResultsTab(resultsTab);
      resultsTab.addColumn(new XViewerColumn("1", "Issue", 220, SWT.LEFT, true, SortDataType.String, false, ""));
      resultsTab.addColumn(new XViewerColumn("2", "Branch Id", 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("3", columnName, 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("4", "Transaction Id", 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("5", "Gamma Id", 80, SWT.LEFT, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(new XViewerColumn("6", "Mod Type", 80, SWT.LEFT, true, SortDataType.String, false, ""));
      resultsTab.addColumn(new XViewerColumn("7", "TX Current", 80, SWT.LEFT, true, SortDataType.String, false, ""));

      doSubWork(new InvalidTxCurrentsAndModTypes(tableName, columnName, new ResultsReporter(resultsTab),
            isFixOperationEnabled()), monitor, 0.3);
   }
   
   private static class ResultsReporter implements OperationReporter {
      private final ResultsEditorTableTab resultsTab;

      private ResultsReporter(ResultsEditorTableTab resultsTab) {
         this.resultsTab = resultsTab;
      }

      @Override
      public void report(String... row) {
         resultsTab.addRow(new ResultsXViewerRow(row));
      }

      @Override
      public void report(Throwable th) {
         report(Lib.exceptionToString(th));
      }
   }

   @Override
   public String getCheckDescription() {
      return "Find versions of artifact, attributes, and relations that currents that ";
   }

   @Override
   public String getFixDescription() {
      return null;
   }
}