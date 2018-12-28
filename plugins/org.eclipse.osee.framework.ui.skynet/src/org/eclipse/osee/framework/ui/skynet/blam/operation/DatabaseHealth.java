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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOpsExtensionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Jeff C. Phillips
 */
public class DatabaseHealth extends AbstractBlam {
   private static final String CLEAN_ALL_PROMPT = "Run all the Cleanup Operations";
   private static final String SHOW_ALL_PROMPT = "Run all the Verification Operations";;

   @Override
   public String getName() {
      return "Database Health";
   }

   @Override
   public String getDescriptionUsage() {
      return "Runs Database Health Checks/Fixes.  Cursor over label to see descriptions.";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      boolean fixAll = variableMap.getBoolean(CLEAN_ALL_PROMPT);
      boolean verifyAll = variableMap.getBoolean(SHOW_ALL_PROMPT);

      MasterDbHealthOperation dbHealthOperation = new MasterDbHealthOperation(getName());

      for (String taskName : DatabaseHealthOpsExtensionManager.getFixOperationNames()) {
         if (fixAll || variableMap.getBoolean(taskName)) {
            dbHealthOperation.addOperation(DatabaseHealthOpsExtensionManager.getFixOperationByName(taskName), true);
         }
      }
      for (String taskName : DatabaseHealthOpsExtensionManager.getVerifyOperationNames()) {
         if (verifyAll || variableMap.getBoolean(taskName)) {
            dbHealthOperation.addOperation(DatabaseHealthOpsExtensionManager.getVerifyOperationByName(taskName), false);
         }
      }
      Operations.executeWorkAndCheckStatus(dbHealthOperation, monitor);
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CLEAN_ALL_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + SHOW_ALL_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Verification Operations to Run:\"/>");
      for (DatabaseHealthOperation healthOp : DatabaseHealthOpsExtensionManager.getVerifyOperations()) {
         builder.append(getOperationsCheckBoxes(healthOp.getVerifyTaskName(), healthOp.getCheckDescription()));
      }

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Clean Up Operations to Run:\"/>");
      for (DatabaseHealthOperation fixOp : DatabaseHealthOpsExtensionManager.getFixOperations()) {
         builder.append(getOperationsCheckBoxes(fixOp.getFixTaskName(), fixOp.getFixDescription()));
      }

      builder.append("</xWidgets>");
      return builder.toString();
   }

   private String getOperationsCheckBoxes(String taskName, String description) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(taskName);
      String toolTip = AXml.textToXml(description);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"  toolTip=\"" + toolTip + "\"/>");
      return builder.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin.Health");
   }

   private final class MasterDbHealthOperation extends AbstractOperation {

      private final Set<DatabaseHealthOperation> fixOperations = new HashSet<>();
      private final Set<DatabaseHealthOperation> verifyOperations = new HashSet<>();

      public MasterDbHealthOperation(String operationName) {
         super(operationName, Activator.PLUGIN_ID);
      }

      public void addOperation(DatabaseHealthOperation operation, boolean isFixOperation) {
         if (operation != null) {
            if (isFixOperation) {
               fixOperations.add(operation);
            } else {
               verifyOperations.add(operation);
            }
         }
      }

      private void executeOperation(IProgressMonitor monitor, DatabaseHealthOperation operation, double workPercentage, boolean isFix) throws Exception {
         checkForCancelledStatus(monitor);
         if (operation != null) {
            operation.setFixOperationEnabled(isFix);
            logf("\nProcessing: [%s]", operation.getName());
            doSubWork(operation, monitor, workPercentage);

            String detailedReport = operation.getDetailedReport().toString();
            if (Strings.isValid(detailedReport)) {
               XResultData result = new XResultData();
               result.addRaw(detailedReport.toString());
               XResultDataUI.report(result, operation.getName(), Manipulations.RAW_HTML);
            } else {
               ResultsEditor.open(operation.getResultsProvider());
            }
            logf("Completed:  [%s]", operation.getName());
         }
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         int totalTasks = fixOperations.size() + verifyOperations.size();
         double workPercentage = 1.0 / totalTasks;
         if (!AccessControlManager.isOseeAdmin()) {
            throw new OseeAccessDeniedException("Must be a Developer to run this BLAM");
         } else {
            for (DatabaseHealthOperation operation : fixOperations) {
               executeOperation(monitor, operation, workPercentage, true);
            }
            for (DatabaseHealthOperation operation : verifyOperations) {
               executeOperation(monitor, operation, workPercentage, false);
            }
         }
      }
   }
}