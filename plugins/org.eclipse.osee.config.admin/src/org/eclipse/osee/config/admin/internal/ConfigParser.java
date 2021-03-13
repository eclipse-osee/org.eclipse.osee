/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.config.admin.internal;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.JaxRsApi;
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
   private final JaxRsApi jaxRsApi;

   public ConfigParser(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   public void process(ConfigWriter writer, String source) {
      if (Strings.isValid(source)) {
         JsonNode services = jaxRsApi.readTree(source).get("config");

         for (JsonNode serviceNode : services) {
            Hashtable<String, Object> properties = new Hashtable<>();

            for (Iterator<Entry<String, JsonNode>> kvPairs = serviceNode.fields(); kvPairs.hasNext();) {
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