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

package org.eclipse.osee.framework.database.init;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.init.internal.Activator;
import org.eclipse.osee.framework.database.init.internal.DbBootstrapTask;
import org.eclipse.osee.framework.database.init.internal.GroupSelection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInitializationOperation extends AbstractOperation {

   private final String preSelectedChoice;
   public static ArtifactToken MAPPING_ARTIFACT;

   private DatabaseInitializationOperation(String preSelectedChoice) {
      super("Database Initialization", Activator.PLUGIN_ID);
      this.preSelectedChoice = preSelectedChoice;
      MAPPING_ARTIFACT = ArtifactToken.valueOf(5443258, "AOkJ_kFNbEXCS7UjmfwA", "DataRightsFooters", BranchId.SENTINEL,
         CoreArtifactTypes.GeneralData);
   }

   public static void execute(IDbInitChoiceEnum choice) {
      IOperation operation = new DatabaseInitializationOperation(choice.name());
      Operations.executeWorkAndCheckStatus(operation);
   }

   private String getPreSelectedChoice() {
      return preSelectedChoice;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      checkServerPreconditions();

      String dbName = ClientSessionManager.getDataStoreName();
      if (ClientSessionManager.isProductionDataStore()) {
         throw new OseeArgumentException(
            String.format("You are not allowed to run config client against production: [%s].\nExiting.", dbName));
      }

      System.out.println("Begin Database Initialization...");

      OseeProperties.setInDbInit(true);
      try {
         processTasks();
         System.out.println("Database Initialization Complete");
      } catch (Exception ex) {
         OseeLog.log(DatabaseInitializationOperation.class, Level.SEVERE, ex);
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         OseeProperties.setInDbInit(false);
      }
   }

   private void checkValidExtension(IExtension extension, String pointId) {
      Conditions.checkNotNull(extension, "Extension", "Unable to locate extension [%s]", pointId);
      String extensionPointId = extension.getExtensionPointUniqueIdentifier();
      Conditions.checkExpressionFailOnTrue(!DefaultDbInitTasks.DB_INIT_TASK.getExtensionId().equals(extensionPointId),
         "Unknown extension id [%s] from extension [%s]", extensionPointId, pointId);
   }

   private void processTasks() {
      OseeLog.log(Activator.class, Level.INFO, "Configuring Database...");
      IDatabaseInitConfiguration configuration = getConfiguration();
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      for (String pointId : configuration.getTaskExtensionIds()) {
         IExtension extension = extensionRegistry.getExtension(pointId);
         checkValidExtension(extension, pointId);
         runDbInitTasks(configuration, extension);
      }
      OseeLog.log(Activator.class, Level.INFO, "Database Initialization Complete.");
   }

   /**
    * Call to get DB initialization Tasks from choice made by User
    *
    * @return initialization task list
    */
   private IDatabaseInitConfiguration getConfiguration() {
      String selectedChoice = null;
      GroupSelection selector = GroupSelection.getInstance();
      List<String> choices = selector.getChoices();
      if (choices.size() == 1) {
         selectedChoice = choices.get(0);
      } else {
         int selection = -1;
         if (Strings.isValid(getPreSelectedChoice())) {
            selection = choices.indexOf(getPreSelectedChoice());
         }
         if (selection <= -1) {
            selectedChoice = getInitChoiceFromUser("Select Init Group To Run.", choices);
         } else {
            selectedChoice = choices.get(selection);
         }
      }
      OseeLog.logf(Activator.class, Level.INFO, "DB Config Choice Selected: [%s]", selectedChoice);
      return selector.getDbInitConfiguration(selectedChoice);
   }

   private String getInitChoiceFromUser(String message, List<String> choices) {
      int selection = -1;
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
      while (selection == -1) {
         try {
            System.out.println(message);
            for (int i = 0; i < choices.size(); i++) {
               System.out.println("   " + i + ") " + choices.get(i));
            }
            System.out.println("Enter: 0 - " + (choices.size() - 1));
            String line = stdin.readLine();
            if (line != null) {
               selection = Integer.parseInt(line);
            }
            if (selection < 0 || selection >= choices.size()) {
               System.out.println("Invalid selection:  Index [" + selection + "] is out of range.");
               selection = -1;
            }
         } catch (Exception ex) {
            System.out.println("Invalid selection:  Index [" + selection + "] is out of range.");
            ex.printStackTrace();
         }
      }
      return choices.get(selection);
   }

   private void runDbInitTasks(IDatabaseInitConfiguration configuration, IExtension extension) {
      IConfigurationElement[] elements = extension.getConfigurationElements();
      String classname = null;
      String bundleName = null;
      String initRuleClassName = null;
      for (IConfigurationElement el : elements) {
         if ("DatabaseInitializationTask".equals(el.getName())) {
            classname = el.getAttribute("classname");
            bundleName = el.getContributor().getName();
            initRuleClassName = el.getAttribute("rule");
         }
      }

      try {
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            boolean isExecutionAllowed = true;
            if (Strings.isValid(initRuleClassName)) {
               isExecutionAllowed = false;

               IDbInitializationRule rule = createTask(bundle, initRuleClassName);
               isExecutionAllowed = rule.isAllowed();
            }

            OseeLog.logf(Activator.class, isExecutionAllowed ? Level.INFO : Level.WARNING,
               "%s [%s] execution rule [%s]", isExecutionAllowed ? "Starting" : "Skipping",
               extension.getUniqueIdentifier(), Strings.isValid(initRuleClassName) ? initRuleClassName : "Default");
            if (isExecutionAllowed) {
               IDbInitializationTask task = (IDbInitializationTask) bundle.loadClass(classname).newInstance();
               if (task instanceof DbBootstrapTask) {
                  ((DbBootstrapTask) task).setConfiguration(configuration);
               }
               task.run();
            }
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private IDbInitializationRule createTask(Bundle bundle, String initRuleClassName) {
      Class<?> taskClass = null;
      try {
         taskClass = bundle.loadClass(initRuleClassName);
      } catch (Exception ex) {
         Bundle dbInitBundle = FrameworkUtil.getBundle(DatabaseInitializationOperation.class);
         try {
            taskClass = dbInitBundle.loadClass(initRuleClassName);
         } catch (ClassNotFoundException ex1) {
            throw new OseeCoreException(ex, "Unable to find rule [%s] in bundle [%s] or in [%s]", initRuleClassName,
               bundle.getSymbolicName(), dbInitBundle.getSymbolicName());
         }
      }
      try {
         return (IDbInitializationRule) taskClass.newInstance();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private boolean isApplicationServerAlive(String applicationServerUrl) {
      boolean canConnection = false;
      try {
         URL url = new URL(applicationServerUrl);
         URLConnection connection = url.openConnection();
         connection.connect();
         canConnection = true;
      } catch (Exception ex) {
         // Do Nothing
      }
      return canConnection;
   }

   private void checkServerPreconditions() {
      String serverUrl = OseeClientProperties.getOseeApplicationServer();
      Conditions.checkNotNullOrEmpty(serverUrl, "Application Server Address",
         "Database initialization requires an application server to be set by default.");

      boolean serverOk = isApplicationServerAlive(serverUrl);
      Conditions.checkExpressionFailOnTrue(!serverOk,
         "Error connecting to application server [%s].\nPlease ensure server is running and try again.", serverUrl);
   }

}
