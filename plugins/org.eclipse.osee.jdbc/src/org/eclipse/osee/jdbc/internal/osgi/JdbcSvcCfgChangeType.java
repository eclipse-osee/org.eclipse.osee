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
               if (Compare.isDifferent(jdbcEntries(original), jdbcEntries(other))) {
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