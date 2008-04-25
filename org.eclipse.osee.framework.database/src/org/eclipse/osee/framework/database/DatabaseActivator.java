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
package org.eclipse.osee.framework.database;

import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * The main plug-in class to be used in the desktop.
 * 
 * @author Ryan D. Brooks
 */
public class DatabaseActivator extends OseeUiActivator {

   // The shared instance.
   private static DatabaseActivator plugin;

   /**
    * The constructor.
    */
   public DatabaseActivator() {
      plugin = this;
   }

   /**
    * Returns the shared instance.
    */
   public static DatabaseActivator getInstance() {
      return plugin;
   }

   public boolean isProductionDb() {
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(DatabaseActivator.getInstance(), "ProductionDatabase",
                  "ProductionDatabase");
      String dbServiceId =
            OseeDb.getDefaultDatabaseService().getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
      for (IConfigurationElement element : elements) {
         if (dbServiceId.equals(element.getAttribute("databaseInstance"))) {
            return true;
         }
      }
      return false;
   }
}