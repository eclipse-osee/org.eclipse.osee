/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.skynet.core.utility.InvalidTxCurrentsAndModTypes;
import org.eclipse.osee.framework.ui.skynet.results.ResultsTableLogger;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;

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

      checkAndFix("osee_artifact", "art_id", monitor);
      checkAndFix("osee_attribute", "attr_id", monitor);
      checkAndFix("osee_relation_link", "rel_link_id", monitor);
   }

   private void checkAndFix(String tableName, String columnName, IProgressMonitor monitor) throws Exception {
      ResultsEditorTableTab resultsTab = new ResultsEditorTableTab(tableName + " currents");
      getResultsProvider().addResultsTab(resultsTab);
      resultsTab.addColumn(
         new XViewerColumn("1", "Issue", 220, XViewerAlign.Left, true, SortDataType.String, false, ""));
      resultsTab.addColumn(
         new XViewerColumn("2", "Branch Uuid", 80, XViewerAlign.Left, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(
         new XViewerColumn("3", columnName, 80, XViewerAlign.Left, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(
         new XViewerColumn("4", "Transaction Id", 80, XViewerAlign.Left, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(
         new XViewerColumn("5", "Gamma Id", 80, XViewerAlign.Left, true, SortDataType.Integer, false, ""));
      resultsTab.addColumn(
         new XViewerColumn("6", "Mod Type", 80, XViewerAlign.Left, true, SortDataType.String, false, ""));
      resultsTab.addColumn(
         new XViewerColumn("7", "TX Current", 80, XViewerAlign.Left, true, SortDataType.String, false, ""));

      doSubWork(new InvalidTxCurrentsAndModTypes("TxCurrentChecks ", tableName, columnName,
         new ResultsTableLogger(resultsTab), isFixOperationEnabled(), true), monitor, 0.3);
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