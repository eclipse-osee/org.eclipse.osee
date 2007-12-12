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
package org.eclipse.osee.framework.ui.plugin.sql.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.sql.dataType.SqlDataType;
import org.eclipse.osee.framework.ui.plugin.util.db.data.ColumnMetadata;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement;
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement.ColumnFields;

/**
 * @author Roberto E. Escobar
 */
public class OracleSqlManager extends SqlManager {

   private static String SEQUENCE_IDENTIFIER = "_SEQ";

   public OracleSqlManager(SqlDataType sqlDataType) {
      super(ConfigUtil.getConfigFactory().getLogger(OracleSqlManager.class), sqlDataType);
   }

   private void createSequence(Connection connection, String sequenceName) throws SQLException, Exception {
      String sequenceCreate =
            SqlManager.CREATE_STRING + " SEQUENCE " + formatQuotedString(sequenceName, "\\.") + " MINVALUE 1" + " MAXVALUE 999999999999999999999999999" + " INCREMENT BY 1" + " NOCYCLE" + " NOORDER" + " NOCACHE";
      executeStatement(connection, sequenceCreate);
   }

   private String handleColumnCreationSection(Connection connection, Map<String, ColumnMetadata> columns) throws SQLException {
      List<String> lines = new ArrayList<String>();
      Set<String> keys = columns.keySet();
      for (String key : keys) {
         Map<ColumnFields, String> column = columns.get(key).getColumnFields();
         lines.add(columnDataToSQL(column));
      }
      String toExecute = StringFormat.listToValueSeparatedString(lines, ",\n");
      return toExecute;
   }

   @Override
   public void createTable(Connection connection, TableElement tableDef) throws SQLException, Exception {
      String toExecute =
            SqlManager.CREATE_STRING + " TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(), "\\.") + " ( \n";
      // String toExecute = "CREATE TABLE " + tableDef.getFullyQualifiedTableName() + " ( \n";
      toExecute += handleColumnCreationSection(connection, tableDef.getColumns());
      toExecute += handleConstraintCreationSection(tableDef.getConstraints(), tableDef.getName());
      toExecute += handleConstraintCreationSection(tableDef.getForeignKeyConstraints(), tableDef.getName());
      toExecute += " \n)\n";
      logger.log(Level.INFO, "Creating Table: [ " + tableDef.getFullyQualifiedTableName() + "]");
      executeStatement(connection, toExecute);

      List<String> sequences = getSequences(tableDef);
      for (String sequenceName : sequences) {
         createSequence(connection, sequenceName);
      }
   }

   private List<String> getSequences(TableElement tableDef) {
      List<String> sequences = new ArrayList<String>();
      Map<String, ColumnMetadata> columns = tableDef.getColumns();
      Set<String> keys = columns.keySet();
      for (String key : keys) {
         Map<ColumnFields, String> column = columns.get(key).getColumnFields();
         String identity = column.get(ColumnFields.identity);
         if (identity != null) {
            if (identity.toUpperCase().equals("TRUE")) {
               sequences.add(tableDef.getSchema() + ".SKYNET_" + column.get(ColumnFields.id) + SEQUENCE_IDENTIFIER);
            }
         }
      }
      return sequences;
   }

   @Override
   public void dropTable(Connection connection, TableElement tableDef) throws SQLException, Exception {
      List<String> sequences = getSequences(tableDef);
      for (String sequenceName : sequences) {
         String toExecute = SqlManager.DROP_STRING + " SEQUENCE " + formatQuotedString(sequenceName, "\\.");
         logger.log(Level.INFO, "Dropping Sequence: [ " + sequenceName + "]");
         try {
            executeStatement(connection, toExecute);
         } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
         }

      }
      String toExecute =
            SqlManager.DROP_STRING + " TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(), "\\.");
      logger.log(Level.INFO, "Dropping Table: [ " + tableDef.getFullyQualifiedTableName() + "]");
      executeStatement(connection, toExecute);
   }
}
