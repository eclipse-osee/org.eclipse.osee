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
import java.util.Iterator;
import java.util.Map.Entry;
import org.codehaus.jackson.JsonNode;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.framework.Constants;

/**
 * Parses JSON String with the following format:
 *
 * <pre>
 * { "config": [ { "service.pid": "service-1", "key1": "val1", "key2": "val2" }, { "service.pid": "service-2", "a":
 * "34242", "b": "hello" }, { "service.pid": "service-3" } ] };
 *
 * <pre/>
 *
 * @author Roberto E. Escobar
 */
public class ConfigParser {

   public void process(ConfigWriter writer, String source) {
      if (Strings.isValid(source)) {
         JsonNode services = JsonUtil.readTree(source).get("config");

         for (JsonNode serviceNode : services) {
            Hashtable<String, Object> properties = new Hashtable<>();

            for (Iterator<Entry<String, JsonNode>> kvPairs = serviceNode.getFields(); kvPairs.hasNext();) {
               Entry<String, JsonNode> entry = kvPairs.next();

               String key = entry.getKey();
               JsonNode value = entry.getValue();
               if (value.isValueNode()) {
                  properties.put(key, value.asText());
                  if (key.equalsIgnoreCase(Constants.SERVICE_PID)) {
                     writer.write(value.asText(), properties);
                  }
               } else {
                  properties.put(key, value.toString());
               }
            }
         }
      }
   }
}