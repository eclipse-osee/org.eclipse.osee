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
package org.eclipse.osee.framework.database.initialize;

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.ui.plugin.util.db.data.SchemaData;

/**
 * @author Roberto E. Escobar
 */
public class DbComparator {

   private Map<String, SchemaData> db1;
   private Map<String, SchemaData> db2;

   public DbComparator(Map<String, SchemaData> db1, Map<String, SchemaData> db2) {
      this.db1 = db1;
      this.db2 = db2;
   }

   public boolean collectDifferences() {
      boolean matched = false;
      if (db1.equals(db2)) {
         matched = true;
      } else {
         matched = false;
         Set<String> db1Schemas = db1.keySet();
         Set<String> db2Schemas = db2.keySet();
         if (!db1Schemas.equals(db2Schemas)) {
            // TODO - Report here - Schemas Different looking at different items - Done Comparing.
         } else {
            // Schemas Equal compare SchemaData
            // List<SchemaData> db1DiffSchemaData = new ArrayList<SchemaData>();
            // List<SchemaData> db2DiffSchemaData = new ArrayList<SchemaData>();
            //
            // for (String key : db1Schemas) {
            // SchemaData db1SchemaData = db1.get(key);
            // SchemaData db2SchemaData = db2.get(key);
            // if (!db1SchemaData.equals(db2SchemaData)) {
            // //Build TableMaps so we compare tables correctly
            // Map<String, TableElement> db1TableMap = db1SchemaData.getTableMap();
            // Map<String, TableElement> db2TableMap = db2SchemaData.getTableMap();
            //                  
            // Set<String> thisKey1 = db1TableMap.keySet();
            // Set<String> thatKey2 = db2TableMap.keySet();

            matched = true;

            // if(thisKey1.equals(thatKey2)){
            // for(String key : thisKey1){
            // toReturn &= EqualsUtility.areEqual(thisTableMap.get(key), thatTableMap.get(key));
            // }
            // } else {
            // // Keys didn't equal -- Find key that didn't equal??
            // // compare other tables so you can see if anything else didn't match
            // toReturn = false;
            // }
         }
      }
      // }
      // }
      return matched;
   }
}
