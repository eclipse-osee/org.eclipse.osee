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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;

/**
 * The main plug-in class to be used in the desktop.
 * 
 * @author Ryan D. Brooks
 */
public class DatabaseActivator extends OseeActivator {

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

   public List<String> getProductionDbs() {
      List<String> productionDbs = new ArrayList<String>();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(DatabaseActivator.getInstance(), "ProductionDatabase",
                  "ProductionDatabase");
      for (IConfigurationElement element : elements) {
         productionDbs.add(element.getAttribute("databaseInstance"));
      }
      return productionDbs;
   }

   public boolean isProductionDb() {
      String dbServiceId =
            OseeDb.getDefaultDatabaseService().getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
      for (String productionDb : getProductionDbs()) {
         if (dbServiceId.equals(productionDb)) {
            return true;
         }
      }
      return false;
   }
}