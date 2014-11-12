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
package org.eclipse.osee.jdbc.internal.schema.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.internal.schema.JdbcWriter;
import org.eclipse.osee.jdbc.internal.schema.data.AppliesToClause;
import org.eclipse.osee.jdbc.internal.schema.data.ColumnMetadata;
import org.eclipse.osee.jdbc.internal.schema.data.ConstraintElement;
import org.eclipse.osee.jdbc.internal.schema.data.ForeignKey;
import org.eclipse.osee.jdbc.internal.schema.data.IndexElement;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.jdbc.internal.schema.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.ColumnFields;

/**
 * @author Roberto E. Escobar
 */
public class HyperSqlManager extends SqlManagerImpl {

   public HyperSqlManager(JdbcWriter client, SqlDataType sqlDataType) {
      super(client, sqlDataType);
   }

   private String handleColumnCreationSection(Map<String, ColumnMetadata> columns) {
      List<String> lines = new ArrayList<String>();
      for (String key : columns.keySet()) {
         Map<ColumnFields, String> column = columns.get(key).getColumnFields();
         lines.add(columnDataToSQL(column));
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", lines);
   }

   @Override
   protected String handleConstraintCreationSection(List<? extends ConstraintElement> constraints, String tableId) {
      List<String> constraintStatements = new ArrayList<String>();
      for (ConstraintElement constraint : constraints) {
         constraintStatements.add(constraintDataToSQL(constraint, tableId));
      }
      StringBuilder toExecute = new StringBuilder();
      if (constraintStatements.isEmpty()) {
         toExecute.append("");
      } else {
         toExecute.append(",\n");
      }
      toExecute.append(join(constraintStatements, ",\n"));
      return toExecute.toString();
   }

   @Override
   public String constraintDataToSQL(ConstraintElement constraint, String tableID) {
      StringBuilder toReturn = new StringBuilder();
      String id = formatQuotedString(constraint.getId(), "\\.");
      String type = constraint.getConstraintType().toString();
      String appliesTo = formatQuotedString(constraint.getCommaSeparatedColumnsList(), ",");

      if (Strings.isValid(id) && Strings.isValid(appliesTo)) {
         toReturn.append("CONSTRAINT ").append(id).append(" ").append(type).append(" (").append(appliesTo).append(")");

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
                  toReturn.append(" REFERENCES ").append(refTable).append(" (").append(refColumns).append(")");
                  if (!onUpdate.equals("")) {
                     toReturn.append(" ").append(onUpdate);
                  }

                  if (!onDelete.equals("")) {
                     toReturn.append(" ").append(onDelete);
                  }

               }

               else {
                  warn("Skipping CONSTRAINT at Table: %s\n\t %s", tableID, fk);
               }
            }
         }
      } else {
         warn("Skipping CONSTRAINT at Table: %s\n\t %s", tableID, constraint);
      }
      return toReturn.toString();
   }

   @Override
   public void createIndex(TableElement tableDef) throws OseeCoreException {
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

            if (index + 1 < appliesToList.size()) {
               appliesTo.append(", ");
            }
         }
         String toExecute = String.format("%s INDEX %s ON %s (%s)", CREATE_STRING, indexId, tableName, appliesTo);
         toExecute = createIndexPostProcess(iData, toExecute);
         debug(toExecute);
         client.runPreparedUpdate(toExecute);
      }
   }

   @Override
   public void createTable(TableElement tableDef) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("CREATE TABLE ").append(tableDef.getName()).append(" ( \n");
      builder.append(handleColumnCreationSection(tableDef.getColumns()));
      builder.append(handleConstraintCreationSection(tableDef.getConstraints(), tableDef.getFullyQualifiedTableName()));
      builder.append(handleConstraintCreationSection(tableDef.getForeignKeyConstraints(),
         tableDef.getFullyQualifiedTableName()));
      builder.append(" \n)\n");
      debug("Creating Table: [%s]", tableDef.getFullyQualifiedTableName());
      client.runPreparedUpdate(builder.toString());
   }

   @Override
   public void dropIndex(TableElement tableDef) {
      // Do Nothing -- Indexes are dropped during table drop
   }

}
