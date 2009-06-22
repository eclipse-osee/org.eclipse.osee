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
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.db.connection.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOpsExtensionManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Jeff C. Phillips
 */
public class DatabaseHealth extends AbstractBlam {
   private static final String SHOW_DETAILS_PROMPT = "Show Details of Operations";
   private static final String CLEAN_ALL_PROMPT = "Run all the Cleanup Operations";
   private static final String SHOW_ALL_PROMPT = "Run all the Verification Operations";;

   public DatabaseHealth() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Database Health";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      boolean isShowDetailsEnabled = variableMap.getBoolean(SHOW_DETAILS_PROMPT);
      boolean fixAll = variableMap.getBoolean(CLEAN_ALL_PROMPT);
      boolean verifyAll = variableMap.getBoolean(SHOW_ALL_PROMPT);

      MasterDbHealthOperation dbHealthOperation = new MasterDbHealthOperation(getName());
      dbHealthOperation.setShowDetails(isShowDetailsEnabled);

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
      Operations.executeWork(dbHealthOperation, monitor, -1);
      Operations.checkForErrorStatus(dbHealthOperation.getStatus());
      println(dbHealthOperation.getStatus().getMessage());
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + SHOW_DETAILS_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CLEAN_ALL_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + SHOW_ALL_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Verification Operations to Run:\"/>");
      for (String taskName : DatabaseHealthOpsExtensionManager.getVerifyOperationNames()) {
         builder.append(getOperationsCheckBoxes(taskName));
      }

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Clean Up Operations to Run:\"/>");
      for (String taskName : DatabaseHealthOpsExtensionManager.getFixOperationNames()) {
         builder.append(getOperationsCheckBoxes(taskName));
      }

      builder.append("</xWidgets>");
      return builder.toString();
   }

   private String getOperationsCheckBoxes(String checkboxName) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(checkboxName);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin.Health");
   }

   private final class MasterDbHealthOperation extends AbstractOperation {

      private boolean isShowDetailsEnabled;
      private final Set<DatabaseHealthOperation> fixOperations = new HashSet<DatabaseHealthOperation>();
      private final Set<DatabaseHealthOperation> verifyOperations = new HashSet<DatabaseHealthOperation>();

      public MasterDbHealthOperation(String operationName) {
         super(operationName, SkynetGuiPlugin.PLUGIN_ID);
         this.isShowDetailsEnabled = false;
      }

      public void setShowDetails(boolean isShowDetailsEnabled) {
         this.isShowDetailsEnabled = isShowDetailsEnabled;
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

      private void executeOperation(IProgressMonitor monitor, DatabaseHealthOperation operation, Appendable appendable, double workPercentage, boolean isFix) throws Exception {
         checkForCancelledStatus(monitor);
         if (operation != null) {
            operation.setFixOperationEnabled(isFix);
            operation.setShowDetailsEnabled(isShowDetailsEnabled);
            operation.setSummary(appendable);
            doSubWork(operation, monitor, workPercentage);
            setStatus(operation.getStatus());

            if (operation.isShowDetailsEnabled()) {
               String detailedReport = operation.getDetailedReport().toString();
               if (Strings.isValid(detailedReport)) {
                  XResultData result = new XResultData();
                  result.addRaw(detailedReport.toString());
                  result.report(operation.getName(), Manipulations.RAW_HTML);
               }
            }
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         int totalTasks = fixOperations.size() + verifyOperations.size();
         double workPercentage = totalTasks / getTotalWorkUnits();
         if (!AccessControlManager.isOseeAdmin()) {
            throw new OseeAccessDeniedException("Must be a Developer to run this BLAM");
         } else {
            StringBuilder builder = new StringBuilder();
            for (DatabaseHealthOperation operation : fixOperations) {
               executeOperation(monitor, operation, builder, workPercentage, true);
            }
            for (DatabaseHealthOperation operation : verifyOperations) {
               executeOperation(monitor, operation, builder, workPercentage, false);

            }

            setStatusMessage(builder.toString());
         }
      }
   }
}
