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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.jdbc.JdbcConstants;

/**
 * @author Roberto E. Escobar
 */
public enum JdbcSvcCfgChangeType {
   NO_CHANGE,
   ALL_CHANGED,
   JDBC_PROPERTY,
   OSGI_BINDING,
   OTHER_CHANGE;

   public boolean isJdbcChange() {
      return JdbcSvcCfgChangeType.JDBC_PROPERTY == this;
   }

   public boolean isAllDifferent() {
      return JdbcSvcCfgChangeType.ALL_CHANGED == this;
   }

   public static JdbcSvcCfgChangeType getChangeType(Map<String, Object> original, Map<String, Object> other) {
      JdbcSvcCfgChangeType changeType = JdbcSvcCfgChangeType.NO_CHANGE;
      if (original != null && other != null) {
         if (original.size() != other.size()) {
            changeType = ALL_CHANGED;
         } else {
            if (Compare.isDifferent(original, other)) {
               if (isOsgiBindingDifferent(original, other)) {
                  changeType = OSGI_BINDING;
               } else if (Compare.isDifferent(jdbcEntries(original), jdbcEntries(other))) {
                  changeType = JDBC_PROPERTY;
               } else {
                  changeType = OTHER_CHANGE;
               }
            }
         }
      } else if (original == null && other == null) {
         changeType = NO_CHANGE;
      } else {
         changeType = ALL_CHANGED;
      }
      return changeType;
   }

   private static boolean isOsgiBindingDifferent(Map<String, Object> original, Map<String, Object> other) {
      Object object1 = original.get(JdbcConstants.JDBC_SERVICE__OSGI_BINDING);
      Object object2 = other.get(JdbcConstants.JDBC_SERVICE__OSGI_BINDING);
      boolean result = true;
      if (object1 != null && object2 != null) {
         result = !object1.equals(object2);
      } else if (object1 == null && object2 == null) {
         result = false;
      }
      return result;
   }

   private static Map<String, Object> jdbcEntries(Map<String, Object> original) {
      Map<String, Object> toReturn = new HashMap<>();
      for (Entry<String, Object> entry : original.entrySet()) {
         if (entry.getKey().startsWith(JdbcConstants.NAMESPACE)) {
            toReturn.put(entry.getKey(), entry.getValue());
         }
      }
      return toReturn;
   }
}