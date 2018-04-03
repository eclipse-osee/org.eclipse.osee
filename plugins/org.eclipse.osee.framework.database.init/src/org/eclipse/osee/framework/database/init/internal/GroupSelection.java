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

package org.eclipse.osee.framework.database.init.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.database.init.IAddDbInitChoice;
import org.eclipse.osee.framework.database.init.IDatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.IDbInitChoiceEnum;
import org.eclipse.osee.framework.database.init.IGroupSelector;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Andrew M. Finkbeiner
 */
public class GroupSelection implements IGroupSelector {
   private static final GroupSelection instance = new GroupSelection();
   private final Map<String, IDatabaseInitConfiguration> initGroups = new HashMap<>();

   private boolean wasInitialized;

   private GroupSelection() {
      super();
      wasInitialized = false;
   }

   public static GroupSelection getInstance() {
      instance.populateDbInitChoices();
      return instance;
   }

   private synchronized void populateDbInitChoices() {
      if (!wasInitialized) {
         wasInitialized = true;
         ExtensionDefinedObjects<IAddDbInitChoice> contributions = new ExtensionDefinedObjects<IAddDbInitChoice>(
            Activator.PLUGIN_ID + ".DatabaseInitializationConfiguration",
            "DatabaseInitializationConfiguration", "classname");
         for (IAddDbInitChoice dbInitChoice : contributions.getObjects()) {
            dbInitChoice.addDbInitChoice(this);
         }
      }
   }

   @Override
   public void addChoice(String listName, IDatabaseInitConfiguration configuration) {
      initGroups.put(listName, configuration);
   }

   public List<String> getChoices() {
      List<String> choices = new ArrayList<>(initGroups.keySet());
      Collections.sort(choices);
      return choices;
   }

   public IDatabaseInitConfiguration getDbInitConfiguration(String choice) {
      return initGroups.get(choice);
   }

   @Override
   public void addChoice(IDbInitChoiceEnum choice, IDatabaseInitConfiguration configuration) {
      addChoice(choice.name(), configuration);
   }
}