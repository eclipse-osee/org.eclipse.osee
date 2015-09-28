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
import org.osgi.framework.Constants;

/**
 * Parses JSON String with the following format:
 * 
 * <pre>
 * {
 *    "config": 
 *      [
 *         {
 *            "service.pid": "service-1",
 *            "key1": "val1",
 *            "key2": "val2"
 *         },
 *         {
 *            "service.pid": "service-2",
 *            "a": "34242",
 *            "b": "hello"
 *         },
 *         {
 *            "service.pid": "service-3"
 *         }
 *      ]
 * };
 * 
 * <pre/>
 * 
 * @author Roberto E. Escobar
 */
public class ConfigParser {

   private static final String CONFIG_OBJECT = "config";

   public void process(ConfigWriter writer, String source) {
      if (Strings.isValid(source)) {
         try {
            JSONObject jsonObject = new JSONObject(source);
            JSONArray jsonArray = jsonObject.getJSONArray(CONFIG_OBJECT);
            for (int index = 0; index < jsonArray.length(); index++) {
               JSONObject object = jsonArray.getJSONObject(index);
               String serviceId = null;
               Hashtable<String, Object> properties = new Hashtable<>();
               String[] names = JSONObject.getNames(object);
               for (String key : names) {
                  String value = object.getString(key);
                  if (key.equalsIgnoreCase(Constants.SERVICE_PID)) {
                     serviceId = value;
                     properties.put(Constants.SERVICE_PID, serviceId);
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
