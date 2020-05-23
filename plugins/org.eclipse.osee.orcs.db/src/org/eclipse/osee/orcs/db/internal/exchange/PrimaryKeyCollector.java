/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.handler.BaseDbSaxHandler;

/**
 * @author Ryan D. Brooks
 */
public class PrimaryKeyCollector extends BaseDbSaxHandler {
   private String primaryKey;
   private final Map<Long, Boolean> primaryKeys = new HashMap<>(50000);
   private final HashCollection<String, Long> missingPrimaryKeys = new HashCollection<>();

   public void setPrimaryKey(String primaryKey) {
      this.primaryKey = primaryKey;
   }

   public PrimaryKeyCollector(Log logger, JdbcClient service) {
      super(logger, service, true, 0);
   }

   @Override
   protected void processData(Map<String, String> fieldMap) {
      Long id = Long.valueOf(fieldMap.get(primaryKey));
      primaryKeys.put(id, Boolean.FALSE);
   }

   public void markAsReferenced(String foreignKey, Long id) {
      if (primaryKeys.containsKey(id)) {
         primaryKeys.put(id, Boolean.TRUE);
      } else {
         missingPrimaryKeys.put(foreignKey, id);
      }
   }

   public HashCollection<String, Long> getMissingPrimaryKeys() {
      return missingPrimaryKeys;
   }

   public Set<Long> getUnreferencedPrimaryKeys() {
      Set<Long> unreferenced = new HashSet<>();
      for (Entry<Long, Boolean> entry : primaryKeys.entrySet()) {
         if (!entry.getValue()) {
            unreferenced.add(entry.getKey());
         }
      }
      return unreferenced;
   }
}