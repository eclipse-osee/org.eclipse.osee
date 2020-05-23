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

import java.util.Map;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.handler.BaseDbSaxHandler;
import org.eclipse.osee.orcs.db.internal.exchange.handler.IExportItem;

/**
 * @author Ryan D. Brooks
 */
public class ForeignKeyReader extends BaseDbSaxHandler {
   private final String[] foreignKeys;
   private final PrimaryKeyCollector primaryKeyCollector;
   private final IExportItem foreignTable;

   public ForeignKeyReader(Log logger, JdbcClient jdbcClient, PrimaryKeyCollector primaryKeyCollector, IExportItem foreignTable, String... foreignKeys) {
      super(logger, jdbcClient, true, 0);
      this.primaryKeyCollector = primaryKeyCollector;
      this.foreignKeys = foreignKeys;
      this.foreignTable = foreignTable;
   }

   @Override
   protected void processData(Map<String, String> fieldMap) {
      for (String foreignKey : foreignKeys) {
         String value = fieldMap.get(foreignKey);
         if (value != null) {
            Long id = Long.valueOf(value);
            primaryKeyCollector.markAsReferenced(foreignTable + "." + foreignKey, id);
         }
      }
   }
}