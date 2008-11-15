/*
 * Created on Nov 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.export;

import org.eclipse.osee.framework.branch.management.exchange.export.RelationalExportItem.IExportColumnListener;
import org.eclipse.osee.framework.core.data.JoinUtility;
import org.eclipse.osee.framework.core.data.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public final class ColumnIdCollector implements IExportColumnListener {
   private String columnToListenFor;
   private ExportImportJoinQuery joinQuery;
   private OseeConnection connection;

   public ColumnIdCollector(String columnToListenFor) {
      this.columnToListenFor = columnToListenFor;
      this.connection = null;
      this.joinQuery = null;
   }

   public void initialize() {
      this.joinQuery = JoinUtility.createExportImportJoinQuery();
   }

   public void setConnection(OseeConnection connection) {
      this.connection = connection;
   }

   public void cleanUp() throws OseeDataStoreException {
      try {
         this.joinQuery.delete(connection);
      } finally {
         this.joinQuery = null;
      }
   }

   public String getColumnToListenFor() {
      return columnToListenFor;
   }

   public void store() throws OseeDataStoreException {
      if (this.joinQuery != null) {
         this.joinQuery.store(connection);
      }
   }

   public int getQueryId() {
      return this.joinQuery != null ? this.joinQuery.getQueryId() : -1;
   }

   @Override
   public void onColumnExport(String columnName, ConnectionHandlerStatement chStmt) throws Exception {
      if (columnName.equals(getColumnToListenFor())) {
         this.joinQuery.add(chStmt.getInt(columnName), -1);
      }
   }
}
