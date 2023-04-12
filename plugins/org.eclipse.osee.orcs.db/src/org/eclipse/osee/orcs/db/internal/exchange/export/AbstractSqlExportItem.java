/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.ExchangeUtil;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportSql;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Torin Grenda, David Miller
 */
public abstract class AbstractSqlExportItem extends AbstractExportItem {

   private final String GET_COLUMN_NAMES =
      "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'" + this.getSource() + "' ORDER BY ORDINAL_POSITION";

   protected final JdbcClient jdbcClient;

   public AbstractSqlExportItem(Log logger, ExportItem id, JdbcClient jdbcClient) {
      super(logger, id);
      this.jdbcClient = jdbcClient;
   }

   @Override
   public final void executeWork() throws Exception {
      checkForCancelled();
      try (var writer = ExchangeUtil.createSqlWriter(getWriteLocation(), getFileName(), getBufferSize())) {
         if (writer != null) {
            StringBuilder columnNames = new StringBuilder(256);
            jdbcClient.runQueryWithMaxFetchSize(stmt -> getColumnNames(columnNames, stmt), GET_COLUMN_NAMES);
            ExportImportSql.openSqlInsert(writer, this.getSource(), columnNames.toString());
            StringBuilder tableData = new StringBuilder(256);
            try {
               checkForCancelled();
               doWork(tableData);
               writer.append(tableData.substring(1));
            } finally {
               ExportImportSql.closeSqlInsert(writer);
            }
         }
      }
   }

   private void getColumnNames(Appendable appendable, JdbcStatement chStmt) {
      try {
         if (!appendable.toString().equals("")) {
            appendable.append(",");
         }
         appendable.append(chStmt.getString(1));

      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Failed to fetch column names for :%s", GET_COLUMN_NAMES);
      }
   }

   protected abstract void doWork(Appendable appendable) throws Exception;

}
