/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc.internal.osgi;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcServiceConfigParser {

   public Map<String, JdbcServiceConfig> parse(String source) {
      Map<String, JdbcServiceConfig> toReturn = new LinkedHashMap<>();
      if (Strings.isValid(source)) {
         try {
            JSONArray array = new JSONArray(source);
            for (int index = 0; index < array.length(); index++) {
               JSONObject object = array.getJSONObject(index);
               JdbcServiceConfig newConfig = asConfig(object);
               if (!newConfig.isEmpty()) {
                  checkId(newConfig);
                  JdbcServiceConfig oldConfig = toReturn.put(newConfig.getId(), newConfig);
                  checkUnique(oldConfig, newConfig);
               }
            }
         } catch (JSONException ex) {
            throw JdbcException.newJdbcException(ex, "Error parsing jdbc config [%s]", source);
         }
      }
      return toReturn;
   }

   private JdbcServiceConfig asConfig(JSONObject object) throws JSONException {
      JdbcServiceConfig toReturn = new JdbcServiceConfig();
      String[] names = JSONObject.getNames(object);
      for (String key : names) {
         Object value = object.get(key);
         if (value instanceof JSONArray) {
            value = asSet(key, (JSONArray) value);
         } else if (value instanceof JSONObject) {
            value = asDictionary((JSONObject) value);
         }
         toReturn.put(key, value);
      }
      return toReturn;
   }

   private Dictionary<String, Object> asDictionary(JSONObject object) throws JSONException {
      Dictionary<String, Object> toReturn = new Hashtable<>();
      String[] names = JSONObject.getNames(object);
      for (String key : names) {
         Object value = object.get(key);
         if (value instanceof JSONArray) {
            value = asSet(key, (JSONArray) value);
         } else if (value instanceof JSONObject) {
            value = asDictionary((JSONObject) value);
         }
         toReturn.put(key, value);
      }
      return toReturn;
   }

   private Set<String> asSet(String key, JSONArray array) throws JSONException {
      Set<String> toReturn = new TreeSet<>();
      for (int index = 0; index < array.length(); index++) {
         String name = array.getString(index);
         if (Strings.isValid(name)) {
            toReturn.add(name);
         }
      }
      return toReturn;
   }

   private void checkId(JdbcServiceConfig config) {
      String id = config.getId();
      if (!Strings.isValid(id)) {
         throw newError("id cannot be null or empty - config[%s]", config);
      }
   }

   private void checkUnique(JdbcServiceConfig oldConfig, JdbcServiceConfig newConfig) {
      if (oldConfig != null) {
         throw newError("duplicate service id detected - id[%s]", newConfig.getId());
      }
   }

   private RuntimeException newError(String msg, Object... args) {
      return newJdbcException("Jdbc Service configuration error - " + msg, args);
   }
}