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
package org.eclipse.osee.database.schema.internal.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.database.schema.internal.data.AppliesToClause;
import org.eclipse.osee.database.schema.internal.data.ColumnMetadata;
import org.eclipse.osee.database.schema.internal.data.ConstraintElement;
import org.eclipse.osee.database.schema.internal.data.ForeignKey;
import org.eclipse.osee.database.schema.internal.data.IndexElement;
import org.eclipse.osee.database.schema.internal.data.ReferenceClause;
import org.eclipse.osee.database.schema.internal.data.TableElement;
import org.eclipse.osee.database.schema.internal.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.database.schema.internal.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.database.schema.internal.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Andrew M. Finkbeiner
 */
public class PostgreSqlManager extends SqlManagerImpl {

   public PostgreSqlManager(Log logger, SqlDataType sqlDataType) {
      super(logger, sqlDataType);
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

   public void createTable(OseeConnection connection, TableElement tableDef) throws OseeCoreException {
      String toExecute = "CREATE TABLE " + tableDef.getFullyQualifiedTableName() + " ( \n";
      toExecute += handleColumnCreationSection(connection, tableDef.getColumns());
      toExecute += handleConstraintCreationSection(tableDef.getConstraints(), tableDef.getFullyQualifiedTableName());
      toExecute +=
         handleConstraintCreationSection(tableDef.getForeignKeyConstraints(), tableDef.getFullyQualifiedTableName());
      toExecute += " \n)\n";
      getLogger().debug("Creating Table: [%s]", tableDef.getFullyQualifiedTableName());
      ConnectionHandler.runPreparedUpdate(connection, toExecute);
   }

   @Override
   public void dropTable(TableElement tableDef) throws OseeCoreException {
      String toExecute = "DROP TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(), "\\.") + " CASCADE";
      getLogger().debug("Dropping Table: [%s]", tableDef.getFullyQualifiedTableName());
      ConnectionHandler.runPreparedUpdate(toExecute);
   }

   @Override
   protected String formatQuotedString(String value, String splitAt) {
      String[] array = value.split(splitAt);
      for (int index = 0; index < array.length; index++) {
         array[index] = array[index];
      }
      return join(array, splitAt.replaceAll("\\\\", ""));
   }

   public void dropIndex(OseeConnection connection, TableElement tableDef) throws OseeCoreException {
      List<IndexElement> tableIndices = tableDef.getIndexData();
      String tableName = tableDef.getFullyQualifiedTableName();
      for (IndexElement iData : tableIndices) {
         if (iData.ignoreMySql()) {
            continue;
         }
         getLogger().debug("Dropping Index: [%s] FROM [%s]\n", iData.getId(), tableName);
         if (iData.getId().equals("PRIMARY")) {
            ConnectionHandler.runPreparedUpdate(connection,
               "ALTER TABLE " + tableDef.getFullyQualifiedTableName() + " DROP PRIMARY KEY");
         } else {
            ConnectionHandler.runPreparedUpdate(connection,
               "ALTER TABLE " + tableDef.getFullyQualifiedTableName() + " DROP INDEX " + iData.getId());
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

                  if (constraint.isDeferrable()) {
                     toReturn.append(" DEFERRABLE");
                  }
               }

               else {
                  getLogger().warn("Skipping CONSTRAINT at Table: %s\n\t%s", tableID, fk.toString());
               }

            }
         }
      } else {
         getLogger().warn("Skipping CONSTRAINT at Table: %s\n\t%s", tableID, constraint.toString());
      }
      return toReturn.toString();
   }

   public void createIndex(OseeConnection connection, TableElement tableDef) throws OseeCoreException {
      List<IndexElement> tableIndices = tableDef.getIndexData();
      String indexId = null;
      StringBuilder appliesTo = new StringBuilder();
      String tableName = formatQuotedString(tableDef.getFullyQualifiedTableName(), "\\.");
      for (IndexElement iData : tableIndices) {
         if (iData.ignoreMySql()) {
            continue;
         }
         indexId = iData.getId();
         appliesTo.delete(0, appliesTo.length());

         List<AppliesToClause> appliesToList = iData.getAppliesToList();
         for (int index = 0; index < appliesToList.size(); index++) {
            AppliesToClause record = appliesToList.get(index);
            appliesTo.append(record.getColumnName());

            //            switch (record.getOrderType()) {
            //               case Ascending:
            //                  appliesTo.append(" ASC");
            //                  break;
            //               case Descending:
            //                  appliesTo.append(" DESC");
            //                  break;
            //               default:
            //                  break;
            //            }
            if (index + 1 < appliesToList.size()) {
               appliesTo.append(", ");
            }
         }
         String toExecute =
            String.format("%s %s INDEX %s ON %s (%s)", CREATE_STRING, iData.getIndexType(), indexId, tableName,
               appliesTo);
         getLogger().debug(toExecute);
         ConnectionHandler.runPreparedUpdate(connection, toExecute);
      }
   }

}
