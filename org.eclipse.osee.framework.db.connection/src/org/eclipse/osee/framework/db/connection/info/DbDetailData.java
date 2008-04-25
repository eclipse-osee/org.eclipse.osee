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
package org.eclipse.osee.framework.db.connection.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class DbDetailData {

   public enum ConfigPairField {
      key, value;
   }

   public enum ConfigField {
      DatabaseType, DatabaseName, DatabaseHome, Prefix, UserName, Password, Host, Port
   }

   public enum DescriptionField {
      id;
   }

   private Map<ConfigField, Pair<String, String>> configFieldMap;
   private Map<DescriptionField, String> descriptionMap;

   public DbDetailData() {
      super();
      configFieldMap = new HashMap<ConfigField, Pair<String, String>>();
      descriptionMap = new HashMap<DescriptionField, String>();
   }

   public String getId() {
      return descriptionMap.get(DescriptionField.id);
   }

   public void addDescription(DescriptionField field, String value) {
      descriptionMap.put(field, value);
   }

   public void addConfigField(ConfigField field, Pair<String, String> pair) {
      configFieldMap.put(field, pair);
   }

   public SupportedDatabase getDbType() {
      return SupportedDatabase.valueOf(getFieldValue(ConfigField.DatabaseType).toLowerCase());
   }

   public String getFieldValue(ConfigField field) {
      String toReturn = "";
      if (configFieldMap.containsKey(field)) {
         Pair<String, String> pair = configFieldMap.get(field);
         toReturn = pair.getValue();
      }
      return toReturn;
   }

   public String toString() {
      String toReturn = "DatabaseInfo: \n";
      Set<DescriptionField> descriptionMapkeys = descriptionMap.keySet();
      for (DescriptionField field : descriptionMapkeys) {
         String value = descriptionMap.get(field);
         toReturn += field + ": " + "[" + value + "]\n";
      }

      toReturn += "Fields: \n";
      Set<ConfigField> keys = configFieldMap.keySet();
      for (ConfigField field : keys) {
         Pair<String, String> pair = configFieldMap.get(field);
         toReturn += field + ": " + "[" + pair.getKey() + "],[" + pair.getValue() + "]\n";
      }
      return toReturn + "\n";
   }

   public Map<ConfigField, Pair<String, String>> getConfigMap() {
      return configFieldMap;
   }
}