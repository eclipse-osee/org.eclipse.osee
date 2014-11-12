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
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.internal.schema.JdbcWriter;
import org.eclipse.osee.jdbc.internal.schema.data.ColumnMetadata;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.ColumnFields;

/**
 * @author Roberto E. Escobar
 */
public class SqlManagerImpl extends SqlManager {

   public SqlManagerImpl(JdbcWriter client, SqlDataType sqlDataType) {
      super(client, sqlDataType);
   }

   private String handleColumnCreationSection(Map<String, ColumnMetadata> columns) {
      List<String> lines = new ArrayList<String>();
      Set<String> keys = columns.keySet();
      for (String key : keys) {
         Map<ColumnFields, String> column = columns.get(key).getColumnFields();
         lines.add(columnDataToSQL(column));
      }
      String toExecute = join(lines, ",\n");
      return toExecute;
   }

   @Override
   public void createTable(TableElement tableDef) throws OseeCoreException {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append(SqlManager.CREATE_STRING + " TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(),
         "\\.") + " ( \n");
      toExecute.append(handleColumnCreationSection(tableDef.getColumns()));
      toExecute.append(handleConstraintCreationSection(tableDef.getConstraints(), tableDef.getFullyQualifiedTableName()));
      toExecute.append(handleConstraintCreationSection(tableDef.getForeignKeyConstraints(),
         tableDef.getFullyQualifiedTableName()));
      toExecute.append(" \n)\n");
      debug("Creating Table: [%s]", tableDef.getFullyQualifiedTableName());
      client.runPreparedUpdate(toExecute.toString());
   }

   @Override
   public void dropTable(TableElement tableDef) throws OseeCoreException {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append(SqlManager.DROP_STRING + " TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(),
         "\\."));
      debug("Dropping Table: [%s]", tableDef.getFullyQualifiedTableName());
      client.runPreparedUpdate(toExecute.toString());
   }
}
