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
package org.eclipse.osee.framework.core.datastore.schema.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.data.ColumnMetadata;
import org.eclipse.osee.framework.core.datastore.schema.data.ConstraintElement;
import org.eclipse.osee.framework.core.datastore.schema.data.ForeignKey;
import org.eclipse.osee.framework.core.datastore.schema.data.IndexElement;
import org.eclipse.osee.framework.core.datastore.schema.data.ReferenceClause;
import org.eclipse.osee.framework.core.datastore.schema.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.framework.core.datastore.schema.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class MysqlSqlManager extends SqlManagerImpl {

   /**
    * @param sqlDataType
    */
   public MysqlSqlManager(SqlDataType sqlDataType) {
      super(sqlDataType);
   }

   private String handleColumnCreationSection(OseeConnection connection, Map<String, ColumnMetadata> columns) {
      List<String> lines = new ArrayList<String>();
      Set<String> keys = columns.keySet();
      for (String key : keys) {
         Map<ColumnFields, String> column = columns.get(key).getColumnFields();
         lines.add(columnDataToSQL(column));
      }
      String toExecute = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", lines);
      return toExecute;
   }

   public void createTable(OseeConnection connection, TableElement tableDef) throws OseeDataStoreException {
      String toExecute = "CREATE TABLE " + tableDef.getFullyQualifiedTableName() + " ( \n";
      toExecute += handleColumnCreationSection(connection, tableDef.getColumns());
      toExecute += handleConstraintCreationSection(tableDef.getConstraints(), tableDef.getFullyQualifiedTableName());
      toExecute +=
         handleConstraintCreationSection(tableDef.getForeignKeyConstraints(), tableDef.getFullyQualifiedTableName());
      toExecute += " \n)\n";
      OseeLog.log(Activator.class, Level.INFO, "Creating Table: [ " + tableDef.getFullyQualifiedTableName() + "]");
      ConnectionHandler.runPreparedUpdate(connection, toExecute);
   }

   @Override
   public void dropTable(TableElement tableDef) throws OseeDataStoreException {
      String toExecute = "DROP TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(), "\\.");
      OseeLog.log(Activator.class, Level.INFO, "Dropping Table: [ " + tableDef.getFullyQualifiedTableName() + "]");
      ConnectionHandler.runPreparedUpdate(toExecute);
   }

   @Override
   protected String formatQuotedString(String value, String splitAt) {
      String[] array = value.split(splitAt);
      for (int index = 0; index < array.length; index++) {
         array[index] = array[index];
      }
      return StringUtils.join(array, splitAt.replaceAll("\\\\", ""));
   }

   @Override
   public void dropIndex(TableElement tableDef) throws OseeDataStoreException {
      List<IndexElement> tableIndices = tableDef.getIndexData();
      String tableName = tableDef.getFullyQualifiedTableName();
      for (IndexElement iData : tableIndices) {
         if (iData.ignoreMySql()) {
            continue;
         }
         OseeLog.log(Activator.class, Level.INFO,
            String.format("Dropping Index: [%s] FROM [%s]", iData.getId(), tableName));
         if (iData.getId().equals("PRIMARY")) {
            ConnectionHandler.runPreparedUpdate("ALTER TABLE " + tableDef.getFullyQualifiedTableName() + " DROP PRIMARY KEY");
         } else {
            ConnectionHandler.runPreparedUpdate("ALTER TABLE " + tableDef.getFullyQualifiedTableName() + " DROP INDEX " + iData.getId());
         }
      }
   }

   @Override
   public String constraintDataToSQL(ConstraintElement constraint, String tableID) {
      StringBuilder toReturn = new StringBuilder();
      String id = formatQuotedString(constraint.getId(), "\\.");
      String type = constraint.getConstraintType().toString();
      String appliesTo = formatQuotedString(constraint.getCommaSeparatedColumnsList(), ",");

      if (Strings.isValid(id) && Strings.isValid(appliesTo)) {
         toReturn.append("CONSTRAINT " + id + " " + type + " (" + appliesTo + ")");

         if (constraint instanceof ForeignKey) {
            ForeignKey fk = (ForeignKey) constraint;
            List<ReferenceClause> refs = fk.getReferences();

            for (ReferenceClause ref : refs) {
               String refTable = formatQuotedString(ref.getFullyQualifiedTableName(), "\\.");
               String refColumns = formatQuotedString(ref.getCommaSeparatedColumnsList(), ",");

               String onUpdate = "";
               if (!ref.getOnUpdateAction().equals(OnUpdateEnum.UNSPECIFIED)) {
                  onUpdate = "ON UPDATE " + ref.getOnUpdateAction().toString();
               }

               String onDelete = "";
               if (!ref.getOnDeleteAction().equals(OnDeleteEnum.UNSPECIFIED)) {
                  onDelete = "ON DELETE " + ref.getOnDeleteAction().toString();
               }

               if (refTable != null && refColumns != null && !refTable.equals("") && !refColumns.equals("")) {
                  toReturn.append(" REFERENCES " + refTable + " (" + refColumns + ")");
                  if (!onUpdate.equals("")) {
                     toReturn.append(" " + onUpdate);
                  }

                  if (!onDelete.equals("")) {
                     toReturn.append(" " + onDelete);
                  }

                  //                  if (constraint.isDeferrable()) {
                  //                     toReturn.append(" DEFERRABLE");
                  //                  }
               }

               else {
                  OseeLog.log(Activator.class, Level.WARNING,
                     "Skipping CONSTRAINT at Table: " + tableID + "\n\t " + fk.toString());
               }

            }
         }
      } else {
         OseeLog.log(Activator.class, Level.WARNING,
            "Skipping CONSTRAINT at Table: " + tableID + "\n\t " + constraint.toString());
      }
      return toReturn.toString();
   }
}
