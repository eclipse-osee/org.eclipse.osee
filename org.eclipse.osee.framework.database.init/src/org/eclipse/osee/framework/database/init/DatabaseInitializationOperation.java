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
package org.eclipse.osee.framework.database.init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.init.internal.DatabaseInitActivator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInitializationOperation {

   private static final String INIT_TASK_EXTENSION_ID =
         "org.eclipse.osee.framework.database.init.DatabaseInitializationTask";

   private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

   private final String preSelectedChoice;
   private final boolean isPromptEnabled;

   private DatabaseInitializationOperation(String preSelectedChoice, boolean isPromptEnabled) {
      this.preSelectedChoice = preSelectedChoice;
      this.isPromptEnabled = isPromptEnabled;
   }

   private boolean isPromptingAllowed() {
      return isPromptEnabled;
   }

   private String getPreSelectedChoice() {
      return preSelectedChoice;
   }

   private void execute() throws OseeCoreException {
      boolean isConfigured = false;
      checkServerPreconditions();

      ClientSessionManager.authenticate(new BaseCredentialProvider() {
         @Override
         public OseeCredential getCredential() throws OseeCoreException {
            OseeCredential credential = super.getCredential();
            credential.setUserName(SystemUser.BootStrap.getName());
            return credential;
         }
      });
      String dbName = ClientSessionManager.getDataStoreName();
      String userName = ClientSessionManager.getDataStoreLoginName();
      String dbDriverName = ClientSessionManager.getDataStoreDriver();
      if (ClientSessionManager.isProductionDataStore()) {
         System.err.println(String.format(
               "You are not allowed to run config client against production: [%s].\nExiting.", dbName));
         return;
      }

      String line = null;
      if (isPromptEnabled) {
         System.out.println(String.format("\nAre you sure you want to configure: [%s:%s] - [%s]", dbName, userName,
               dbDriverName));
         line = waitForUserResponse();
      } else {
         line = "Y";
      }
      if (line.equalsIgnoreCase("Y")) {
         isConfigured = true;
         OseeLog.log(DatabaseInitActivator.class, Level.INFO, "Configuring Database...");
         long startTime = System.currentTimeMillis();

         try {
            processTasks();
         } finally {
            System.out.println(String.format("Database Configuration completed in [%s] ms",
                  Lib.getElapseString(startTime)));
         }
      }

      if (isConfigured != true) {
         System.out.println("Database will not be configured. ");
      }
   }

   private void checkValidExtension(IExtension extension, String pointId) throws OseeCoreException {
      Conditions.checkNotNull(extension, "Extension", "Unable to locate extension [" + pointId + "]");
      String extensionPointId = extension.getExtensionPointUniqueIdentifier();
      Conditions.checkExpressionFailOnTrue(!INIT_TASK_EXTENSION_ID.equals(extensionPointId),
            "Unknown extension id [%s] from extension [%s]", extensionPointId, pointId);
   }

   private void processTasks() throws OseeCoreException {
      OseeLog.log(DatabaseInitializationOperation.class, Level.INFO, "Begin Database Initialization...");
      DbInitConfiguration configuration = getConfiguration();
      for (String pointId : configuration.getTaskExtensionIds()) {
         IExtension extension = Platform.getExtensionRegistry().getExtension(pointId);
         checkValidExtension(extension, pointId);
         runDbInitTasks(configuration, extension);
      }
      OseeLog.log(DatabaseInitActivator.class, Level.INFO, "Database Initialization Complete.");
   }

   /**
    * Call to get DB initialization Tasks from choice made by User
    *
    * @return initialization task list
    */
   private DbInitConfiguration getConfiguration() {
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
      OseeLog.log(DatabaseInitActivator.class, Level.INFO, String.format("DB Config Choice Selected: [%s]",
            selectedChoice));
      return selector.getDbInitConfiguration(selectedChoice);
   }

   private static String getInitChoiceFromUser(String message, List<String> choices) {
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
            selection = Integer.parseInt(line);
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

   private static void runDbInitTasks(DbInitConfiguration configuration, IExtension extension) throws OseeCoreException {
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
               Class<?> taskClass = bundle.loadClass(initRuleClassName);
               IDbInitializationRule rule = (IDbInitializationRule) taskClass.newInstance();
               isExecutionAllowed = rule.isAllowed();
            }

            OseeLog.log(DatabaseInitActivator.class, isExecutionAllowed ? Level.INFO : Level.WARNING, String.format(
                  "%s [%s] execution rule [%s]", isExecutionAllowed ? "Starting" : "Skipping",
                  extension.getUniqueIdentifier(), Strings.isValid(initRuleClassName) ? initRuleClassName : "Default"));
            if (isExecutionAllowed) {
               IDbInitializationTask task = (IDbInitializationTask) bundle.loadClass(classname).newInstance();
               if (task instanceof DbBootstrapTask) {
                  ((DbBootstrapTask) task).setConfiguration(configuration);
               }
               task.run();
            }
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private String waitForUserResponse() {
      System.out.println("Enter: [Y|N]\n");
      String line = "N";

      if (!isPromptingAllowed()) {
         line = "Y";
      } else {
         try {
            line = stdin.readLine();
         } catch (IOException ex) {
            OseeLog.log(DatabaseInitActivator.class, Level.SEVERE, ex);
         }
      }
      return line;
   }

   private boolean isApplicationServerAlive(String applicationServerUrl) {
      boolean canConnection = false;
      try {
         URL url = new URL(applicationServerUrl);
         URLConnection connection = url.openConnection();
         connection.connect();
         canConnection = true;
      } catch (Exception ex) {
      }
      return canConnection;
   }

   private void checkServerPreconditions() throws OseeCoreException {
      String serverUrl = OseeClientProperties.getOseeApplicationServer();
      Conditions.checkNotNullOrEmpty(serverUrl, "Application Server Address",
            "Database initialization requires an application server to be set by default.");

      boolean serverOk = isApplicationServerAlive(serverUrl);
      Conditions.checkExpressionFailOnTrue(!serverOk,
            "Error connecting to application server [%s].\nPlease ensure server is running and try again.", serverUrl);
      System.out.println("OSEE Application Server Validation [PASSED]");
   }

   public static void executeWithoutPrompting(String choice) throws OseeCoreException {
      new DatabaseInitializationOperation(choice, false).execute();
   }

   public static void executeWithPromptsAndChoice(String choice) throws OseeCoreException {
      new DatabaseInitializationOperation(choice, false).execute();
   }

   public static void executeWithPrompts() throws OseeCoreException {
      new DatabaseInitializationOperation(null, true).execute();
   }

   public static void executeConfigureFromJvmProperties() throws OseeCoreException {
      boolean arePromptsAllowed = OseeClientProperties.promptOnDbInit();
      String predefinedChoice = OseeClientProperties.getChoiceOnDbInit();
      new DatabaseInitializationOperation(predefinedChoice, arePromptsAllowed).execute();
   }
}
