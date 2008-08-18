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

package org.eclipse.osee.framework.database.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;

/**
 * @author Andrew M. Finkbeiner
 */
public class GroupSelection {
   private static final GroupSelection instance = new GroupSelection();
   private final Map<String, List<String>> initGroups = new LinkedHashMap<String, List<String>>();
   private String choice = null;

   /**
    * @param initGroups
    */
   private GroupSelection() {
      super();
      populateDbInitChoices();
   }

   public static GroupSelection getInstance() {
      return instance;
   }

   private void populateDbInitChoices() {
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(DatabaseActivator.getInstance(), "AddDbInitChoice", "dbInitChoice");

      for (IConfigurationElement element : elements) {
         String choiceClass = element.getAttribute("classname");
         try {
            IAddDbInitChoice choice =
                  (IAddDbInitChoice) Platform.getBundle(element.getContributor().getName()).loadClass(choiceClass).newInstance();
            choice.addDbInitChoice(this);
         } catch (InstantiationException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
         } catch (IllegalAccessException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
         } catch (ClassNotFoundException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
         }
      }
   }

   private void addCommonChoices(List<String> dbInitTasks, boolean bareBones) {
      List<String> initTasks = new ArrayList<String>();
      initTasks.add("org.eclipse.osee.framework.skynet.core.SkynetDbInit");
      dbInitTasks.addAll(0, initTasks);
      dbInitTasks.add("org.eclipse.osee.framework.skynet.core.SkynetDbBranchDataImport");
      dbInitTasks.add("org.eclipse.osee.framework.database.PostDbInitializationProcess");
   }

   public void addChoice(String listName, List<String> dbInitTasks, boolean bareBones) {
      addCommonChoices(dbInitTasks, bareBones);
      initGroups.put(listName, dbInitTasks);
   }

   /**
    * Call to get DB initialization Tasks from choice made by User
    * 
    * @return initialization task list
    */
   public List<String> getDbInitTasks() {
      if (choice == null) {
         if (initGroups.keySet().size() == 1) {
            String[] keys = initGroups.keySet().toArray(new String[1]);
            choice = keys[0];
         } else {
            chooser("Select Init Group To Run.", new ArrayList<String>(initGroups.keySet()));
         }
      }
      return initGroups.get(choice);
   }

   /**
    * Call get get DB initialization Tasks from specified taskId
    * 
    * @param dbInitTaskId
    * @return initialization task list
    */
   public List<String> getDbInitTasks(String dbInitTaskId) {
      populateDbInitChoices();
      return initGroups.get(dbInitTaskId);
   }

   private String chooser(String message, List<String> choices) {
      String configChoice = OseeProperties.getInstance().getDbConfigInitChoice();
      int selection = -1;
      if (false != Strings.isValid(configChoice)) {
         selection = choices.indexOf(configChoice);
      }

      if (selection <= -1) {
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
      }
      choice = choices.get(selection);
      OseeLog.log(DatabaseActivator.class, Level.INFO, String.format("DB Config Choice Selected: [%s]", choice));
      return choice;
   }
}