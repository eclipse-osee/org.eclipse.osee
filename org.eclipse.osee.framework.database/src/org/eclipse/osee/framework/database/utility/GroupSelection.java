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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Andrew M. Finkbeiner
 */
public class GroupSelection {
   private static final GroupSelection instance = new GroupSelection();
   private final Map<String, List<String>> initGroups = new HashMap<String, List<String>>();

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

   private List<IConfigurationElement> getExtensionElements(Bundle bundle, String extensionPointName, String elementName) {
      return getExtensionElements(bundle.getSymbolicName() + "." + extensionPointName, elementName);
   }

   private List<IConfigurationElement> getExtensionElements(String extensionPointId, String elementName) {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      if (extensionRegistry == null) {
         throw new IllegalStateException("The extension registry is unavailable");
      }

      IExtensionPoint point = extensionRegistry.getExtensionPoint(extensionPointId);
      if (point == null) {
         throw new IllegalArgumentException("The extension point " + extensionPointId + " does not exist");
      }

      IExtension[] extensions = point.getExtensions();
      ArrayList<IConfigurationElement> elementsList = new ArrayList<IConfigurationElement>(extensions.length * 3);

      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement element : elements) {
            if (element.getName().equalsIgnoreCase(elementName)) {
               elementsList.add(element);
            }
         }
      }
      return elementsList;
   }

   private void populateDbInitChoices() {
      List<IConfigurationElement> elements =
            getExtensionElements(DatabaseActivator.getInstance().getBundle(), "AddDbInitChoice", "dbInitChoice");

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
      dbInitTasks.add("org.eclipse.osee.framework.skynet.core.PostDbUserCleanUp");
      dbInitTasks.add("org.eclipse.osee.framework.skynet.core.SkynetDbBranchDataImport");
      dbInitTasks.add("org.eclipse.osee.framework.database.PostDbInitializationProcess");
   }

   public void addChoice(String listName, List<String> dbInitTasks, boolean bareBones) {
      addCommonChoices(dbInitTasks, bareBones);
      initGroups.put(listName, dbInitTasks);
   }

   public List<String> getChoices() {
      List<String> choices = new ArrayList<String>(initGroups.keySet());
      Collections.sort(choices);
      return choices;
   }

   public List<String> getDbInitTasksByChoiceEntry(String choice) {
      return initGroups.get(choice);
   }
}