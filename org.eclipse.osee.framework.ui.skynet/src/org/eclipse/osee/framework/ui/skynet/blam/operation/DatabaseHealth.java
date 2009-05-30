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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.db.connection.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;

/**
 * @author Jeff C. Phillips
 */
public class DatabaseHealth extends AbstractBlam {
   private final Map<String, DatabaseHealthOperation> dbFix = new TreeMap<String, DatabaseHealthOperation>();
   private final Map<String, DatabaseHealthOperation> dbVerify = new TreeMap<String, DatabaseHealthOperation>();
   private static final String SHOW_DETAILS_PROMPT = "Show Details of Operations";
   private static final String CLEAN_ALL_PROMPT = "Run all the Cleanup Operations";
   private static final String SHOW_ALL_PROMPT = "Run all the Verification Operations";;

   public DatabaseHealth() {
      loadExtensions();
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

      for (String taskName : dbFix.keySet()) {
         if (fixAll || variableMap.getBoolean(taskName)) {
            dbHealthOperation.addOperation(dbFix.get(taskName), true);
         }
      }
      for (String taskName : dbVerify.keySet()) {
         if (verifyAll || variableMap.getBoolean(taskName)) {
            dbHealthOperation.addOperation(dbVerify.get(taskName), false);
         }
      }
      IStatus status = dbHealthOperation.run(monitor).getStatus();
      appendResultLine(status.getMessage());
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + SHOW_DETAILS_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CLEAN_ALL_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + SHOW_ALL_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Clean Up Operations to Run:\"/>");
      for (String taskName : dbFix.keySet()) {
         builder.append(getOperationsCheckBoxes(taskName));
      }
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Verification Operations to Run:\"/>");
      for (String taskName : dbVerify.keySet()) {
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

   private void loadExtensions() {
      ExtensionDefinedObjects<DatabaseHealthOperation> extensionDefinedObjects =
            new ExtensionDefinedObjects<DatabaseHealthOperation>(SkynetGuiPlugin.PLUGIN_ID + ".DBHealthTask",
                  "DBHealthTask", "class");
      for (DatabaseHealthOperation operation : extensionDefinedObjects.getObjects()) {
         if (Strings.isValid(operation.getVerifyTaskName())) {
            dbVerify.put(operation.getVerifyTaskName(), operation);
         }
         if (Strings.isValid(operation.getFixTaskName())) {
            dbFix.put(operation.getFixTaskName(), operation);
         }
      }
   }
   private final class MasterDbHealthOperation extends AbstractOperation {

      private boolean isShowDetailsEnabled;
      private Set<DatabaseHealthOperation> fixOperations;
      private Set<DatabaseHealthOperation> verifyOperations;

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
            operation.setAppendable(appendable);
            doSubWork(operation, monitor, workPercentage);
            setStatus(operation.getStatus());
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         monitor.beginTask(getName(), getTotalWorkUnits());
         int totalTasks = fixOperations.size() + verifyOperations.size();
         double workPercentage = totalTasks / getTotalWorkUnits();
         try {
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
         } finally {
            monitor.done();
         }
      }
   }
}
