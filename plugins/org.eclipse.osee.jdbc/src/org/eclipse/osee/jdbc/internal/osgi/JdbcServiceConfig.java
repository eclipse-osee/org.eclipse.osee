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

package org.eclipse.osee.jdbc.internal.osgi;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.jdbc.internal.JdbcUtil;

/**
 * @author Roberto E. Escobar
 */
public class JdbcServiceConfig {

   private final Map<String, Object> data = new LinkedHashMap<>();

   public String getId() {
      return JdbcUtil.getServiceId(data);
   }

   public boolean isEmpty() {
      return data.isEmpty();
   }

   public Map<String, Object> asMap() {
      return Collections.unmodifiableMap(data);
   }

   public Dictionary<String, Object> asDictionary() {
      Dictionary<String, Object> toReturn = new Hashtable<>();
      for (Entry<String, Object> entry : data.entrySet()) {
         toReturn.put(entry.getKey(), entry.getValue());
      }
      return toReturn;
   }

   public void put(String key, Object value) {
      data.put(key, value);
   }

   @Override
   public String toString() {
      return "JdbcServiceConfig [props=" + data + "]";
   }
}