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
package org.eclipse.osee.framework.database.internal.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class DbDetailData implements Serializable {

   private static final long serialVersionUID = 4610179141542353247L;

   public enum ConfigPairField {
      key, value;
   }

   public enum ConfigField {
      DatabaseType, DatabaseName, DatabaseHome, Prefix, UserName, Password, Host, Port
   }

   public enum DescriptionField {
      id;
   }

   private final Map<ConfigField, Pair<String, String>> configFieldMap;
   private final Map<DescriptionField, String> descriptionMap;

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
         toReturn = pair.getSecond();
      }
      return toReturn;
   }

   @Override
   public String toString() {
      StringBuilder toReturn = new StringBuilder("DatabaseInfo: \n");
      Set<DescriptionField> descriptionMapkeys = descriptionMap.keySet();
      for (DescriptionField field : descriptionMapkeys) {
         String value = descriptionMap.get(field);
         toReturn.append(String.format("%s: [%s]\n", field, value));
      }

      toReturn.append("Fields: \n");
      Set<ConfigField> keys = configFieldMap.keySet();
      for (ConfigField field : keys) {
         Pair<String, String> pair = configFieldMap.get(field);
         toReturn.append(field + ": " + "[" + pair.getFirst() + "],[" + pair.getSecond() + "]\n");
      }
      toReturn.append("\n");
      return toReturn.toString();
   }

   public Map<ConfigField, Pair<String, String>> getConfigMap() {
      return configFieldMap;
   }
}