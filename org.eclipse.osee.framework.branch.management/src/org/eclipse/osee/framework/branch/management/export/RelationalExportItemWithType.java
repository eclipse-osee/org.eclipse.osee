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
package org.eclipse.osee.framework.branch.management.export;

import java.io.File;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class RelationalExportItemWithType extends RelationalExportItem {

   private RelationalExportItem typeExportItem;
   private TypeCollector typeCollector;

   public RelationalExportItemWithType(String name, int priority, String typeColumn, String regularQuery, String typeQuery) {
      super(name, priority, regularQuery);
      this.typeCollector = new TypeCollector(typeColumn);
      this.typeExportItem = new RelationalExportItem(name + ".type", priority * -1, typeQuery);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.AbstractExportItem#setOptions(org.eclipse.osee.framework.resource.management.Options)
    */
   @Override
   public void setOptions(Options options) {
      super.setOptions(options);
      this.typeExportItem.setOptions(options);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.AbstractExportItem#setWriteLocation(java.io.File)
    */
   @Override
   public void setWriteLocation(File writeLocation) {
      super.setWriteLocation(writeLocation);
      this.typeExportItem.setWriteLocation(writeLocation);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.RelationalExportItem#cleanUp()
    */
   @Override
   public void cleanUp() {
      super.cleanUp();
      this.typeExportItem.cleanUp();
      this.typeCollector.cleanUp();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.AbstractExportItem#run()
    */
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.branch.management.export.RelationalExportItem#doWork(java.io.Writer)
    */
   @Override
   protected void doWork(Writer writer) throws Exception {
      this.typeCollector.initialize();
      this.addExportColumnListener(typeCollector);

      super.doWork(writer);

      this.removeExportColumnListener(typeCollector);
      this.typeCollector.store();
      this.typeExportItem.setJoinQueryId(typeCollector.getQueryId());
      this.typeExportItem.run();
   }

   private final class TypeCollector implements IExportColumnListener {
      private String columnToListenFor;
      private ExportImportJoinQuery joinQuery;

      public TypeCollector(String columnToListenFor) {
         this.columnToListenFor = columnToListenFor;
         cleanUp();
      }

      public void initialize() {
         this.joinQuery = JoinUtility.createExportImportJoinQuery();
      }

      public void cleanUp() {
         this.joinQuery = null;
      }

      public String getColumnToListenFor() {
         return columnToListenFor;
      }

      public void store() throws SQLException {
         if (this.joinQuery != null) {
            Connection connection = null;
            try {
               connection = OseeDbConnection.getConnection();
               this.joinQuery.store(connection);
            } finally {
               if (connection != null) {
                  connection.close();
               }
            }
         }
      }

      public int getQueryId() {
         return this.joinQuery != null ? this.joinQuery.getQueryId() : -1;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.branch.management.export.RelationalExportItem.IExportColumnListener#onColumnExport(java.lang.String, java.sql.ResultSet)
       */
      @Override
      public void onColumnExport(String columnName, ResultSet resultSet) throws Exception {
         if (columnName.equals(getColumnToListenFor())) {
            this.joinQuery.add(resultSet.getInt(columnName), -1);
         }
      }
   }
}
