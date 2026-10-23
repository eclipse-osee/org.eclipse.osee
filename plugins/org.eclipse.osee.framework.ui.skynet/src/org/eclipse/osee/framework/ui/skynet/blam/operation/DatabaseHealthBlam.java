/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOpsExtensionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author Jeff C. Phillips
 */
@Component(service = AbstractBlam.class, immediate = true)
public class DatabaseHealthBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Database Health";
   static final String CLEAN_ALL_PROMPT = "Run all the Cleanup Operations";
   static final String SHOW_ALL_PROMPT = "Run all the Verification Operations";;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public DatabaseHealthBlam() {
      super(BLAM_NAME, CoreImage.HEALTH);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andCheckBoxWidget(CLEAN_ALL_PROMPT);
      wb.andCheckBoxWidget(SHOW_ALL_PROMPT);
      wb.andSpaceWidget();
      wb.andLabelWidget("Select Verification Operations to Run:");
      for (DatabaseHealthOperation healthOp : DatabaseHealthOpsExtensionManager.getVerifyOperations()) {
         wb.andCheckBoxWidget(healthOp.getFixTaskName()).andToolTip(healthOp.getCheckDescription());
      }
      wb.andSpaceWidget();
      wb.andLabelWidget("Select Clean Up Operations to Run:");
      for (DatabaseHealthOperation fixOp : DatabaseHealthOpsExtensionManager.getFixOperations()) {
         wb.andCheckBoxWidget(fixOp.getFixTaskName()).andToolTip(fixOp.getFixDescription());
      }
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

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

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

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

      private void executeOperation(IProgressMonitor monitor, DatabaseHealthOperation operation, double workPercentage,
         boolean isFix) throws Exception {
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
         if (!ServiceUtil.accessControlService().isOseeAdmin()) {
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

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.OseeAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      return "Runs Database Health Checks/Fixes.  Cursor over label to see descriptions.";
   }

}
