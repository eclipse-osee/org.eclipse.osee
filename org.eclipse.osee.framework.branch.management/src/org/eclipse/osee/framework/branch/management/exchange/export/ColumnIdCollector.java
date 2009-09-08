/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.export;

import org.eclipse.osee.framework.branch.management.exchange.export.RelationalExportItem.IExportColumnListener;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.ExportImportJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public final class ColumnIdCollector implements IExportColumnListener {
   private final String columnToListenFor;
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
         this.joinQuery.add(chStmt.getLong(columnName), -1L);
      }
   }
}
