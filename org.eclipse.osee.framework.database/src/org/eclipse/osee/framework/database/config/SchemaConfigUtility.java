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
package org.eclipse.osee.framework.database.config;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.data.TableElement;

/**
 * @author Roberto E. Escobar
 */
public class SchemaConfigUtility {

   /**
    * Reads files containing Schema Table information and groups tables by schema.
    */
   public static Map<String, SchemaData> getUserDefinedConfig(List<URL> files) {
      Map<String, SchemaData> userSpecifiedConfig = new HashMap<String, SchemaData>();

      for (URL file : files) {
         SchemaData schemaData;
         try {
            schemaData = TableConfigUtility.getInstance().getTableConfigData(file.openStream());
            List<TableElement> tables = schemaData.getTablesOrderedByDependency();
            for (TableElement table : tables) {
               String schemaAddress = table.getSchema();

               SchemaData schema = null;
               if (!userSpecifiedConfig.containsKey(schemaAddress)) {
                  schema = new SchemaData();
                  userSpecifiedConfig.put(schemaAddress, schema);
               } else {
                  schema = userSpecifiedConfig.get(schemaAddress);
               }
               schema.addTableDefinition(table);
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return userSpecifiedConfig;
   }
}
