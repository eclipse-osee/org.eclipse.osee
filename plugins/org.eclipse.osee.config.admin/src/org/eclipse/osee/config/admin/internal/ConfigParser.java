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
package org.eclipse.osee.config.admin.internal;

import java.util.Hashtable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parses JSON String with the following format:
 * 
 * <pre>
 * {
 *    "config": 
 *      [
 *         {
 *            "serviceId": "service-1",
 *            "key1": "val1",
 *            "key2": "val2"
 *         },
 *         {
 *            "serviceId": "service-2",
 *            "a": "34242",
 *            "b": "hello"
 *         },
 *         {
 *            "serviceId": "service-3"
 *         }
 *      ]
 * };
 * 
 * <pre/>
 * 
 * @author Roberto E. Escobar
 */
public class ConfigParser {

   private static final String SERVICE_ID_FIELD = "serviceId";
   private static final String CONFIG_OBJECT = "config";

   public void process(ConfigWriter writer, String source) {
      if (Strings.isValid(source)) {
         try {
            JSONObject jsonObject = new JSONObject(source);
            JSONArray jsonArray = jsonObject.getJSONArray(CONFIG_OBJECT);
            for (int index = 0; index < jsonArray.length(); index++) {
               JSONObject object = jsonArray.getJSONObject(index);
               String serviceId = null;
               Hashtable<String, Object> properties = new Hashtable<String, Object>();
               String[] names = JSONObject.getNames(object);
               for (String key : names) {
                  String value = object.getString(key);
                  if (key.equalsIgnoreCase(SERVICE_ID_FIELD)) {
                     serviceId = value;
                  } else {
                     properties.put(key, value);
                  }
               }
               if (Strings.isValid(serviceId)) {
                  writer.write(serviceId, properties);
               }
            }
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }
   }
}
