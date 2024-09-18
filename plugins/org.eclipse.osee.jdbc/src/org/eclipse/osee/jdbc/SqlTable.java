/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.jdbc;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.ChainingArrayList;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedBase;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class SqlTable extends NamedBase {

   private final String aliasPrefix;
   private final ObjectType objectType;
   private final ChainingArrayList<SqlColumn> columns;
   private final ArrayList<String> constraints;
   private final ArrayList<String> statements;
   private final int indexLevel;
   private String insertSql;
   private String tableExtras;
   public SqlTable(String tableName, String aliasPrefix) {
      this(tableName, aliasPrefix, -1);
   }

   public SqlTable(String tableName, String aliasPrefix, int indexLevel) {
      this(tableName, aliasPrefix, ObjectType.UNKNOWN, indexLevel);
   }

   public SqlTable(String tableName, String aliasPrefix, ObjectType objectType) {
      this(tableName, aliasPrefix, objectType, -1);
   }

   public SqlTable(String tableName, String aliasPrefix, ObjectType objectType, int indexLevel) {
      super(tableName);
      this.aliasPrefix = aliasPrefix;
      this.objectType = objectType;
      columns = new ChainingArrayList<>();
      constraints = new ArrayList<>();
      statements = new ArrayList<>();
      this.indexLevel = indexLevel;
   }

   public String getPrefix() {
      return aliasPrefix;
   }

   public String getTableExtras() {
      return tableExtras;
   }

   public ObjectType getObjectType() {
      return objectType;
   }

   public List<SqlColumn> getColumns() {
      return columns;
   }

   public List<String> getConstraints() {
      return constraints;
   }

   public List<String> getStatements() {
      return statements;
   }

   public int getIndexLevel() {
      return indexLevel;
   }

   public SqlColumn addColumn(String name, JDBCType type) {
      return columns.addAndReturn(new SqlColumn(this, name, type));
   }

   public SqlColumn addColumn(String name, JDBCType type, boolean isNull) {
      return columns.addAndReturn(new SqlColumn(this, name, type, isNull));
   }

   public SqlColumn addAutoIncrementColumn(String name, JDBCType type) {
      return columns.addAndReturn(new SqlColumn(this, name, type, false, true));
   }

   public SqlColumn addColumnWithValueConstraint(String name, JDBCType type, boolean isNull, String valueConstraint) {
      return columns.addAndReturn(new SqlColumn(this, name, type, isNull, 0, valueConstraint));
   };

   public SqlColumn addVarCharColumn(String name, int length) {
      return columns.addAndReturn(new SqlColumn(this, name, JDBCType.VARCHAR, true, length, Strings.EMPTY_STRING));
   }

   public SqlColumn addVarCharColumn(String name, int length, boolean isNull) {
      return columns.addAndReturn(new SqlColumn(this, name, JDBCType.VARCHAR, false, length, Strings.EMPTY_STRING));
   }

   public void setPrimaryKeyConstraint(SqlColumn... columns) {
      constraints.add(
         "CONSTRAINT " + getName() + "_PK PRIMARY KEY (" + Collections.toString(",", Arrays.asList(columns)) + ")");
   }

   public void setPrimaryKeyConstraint(String key, SqlColumn... columns) {
      constraints.add(
         "CONSTRAINT " + key + "_PK PRIMARY KEY (" + Collections.toString(",", Arrays.asList(columns)) + ")");
   }

   public void setForeignKeyConstraint(String constraintName, SqlColumn column, SqlTable refTable,
      SqlColumn refColumn) {
      constraints.add(
         "CONSTRAINT " + constraintName + " FOREIGN KEY (" + column + ") REFERENCES " + refTable + " (" + refColumn + ")");
   }

   public void setForeignKeyConstraintCascadeDelete(String constraintName, SqlColumn column, SqlTable refTable,
      SqlColumn refColumn) {
      constraints.add(
         "CONSTRAINT " + constraintName + " FOREIGN KEY (" + column + ") REFERENCES " + refTable + " (" + refColumn + ") ON DELETE CASCADE");
   }

   public void setUniqueKeyConstraint(String constraintName, String columnName) {
      constraints.add("CONSTRAINT " + constraintName + " UNIQUE (" + columnName + ")");
   }

   public void createIndex(String indexName, boolean hasIndexTablespace, SqlColumn... columns) {
      addStatement("CREATE INDEX " + indexName + " ON " + getName() + " (" + Collections.toString(", ",
         Arrays.asList(columns)) + ")");
   }

   public void addStatement(String statement) {
      statements.add(statement);
   }

   public void setTableExtras(String extras) {
      tableExtras = extras;
   }

   public String getInsertIntoSqlWithValues(Object... parameters) {
      StringBuilder strB = getInsertIntoSqlStart();

      for (int i = 0; i < parameters.length; i++) {
         if (i != 0) {
            strB.append(", ");
         }

         JDBCType type = columns.get(i).getType();
         if (type.equals(JDBCType.VARCHAR)) {
            strB.append("'");
            strB.append(parameters[i]);
            strB.append("'");
         } else if (parameters[i] instanceof Id) {
            strB.append(((Id) parameters[i]).getIdString());
         } else {
            strB.append(parameters[i].toString());
         }
      }

      strB.append(")");
      return strB.toString();
   }

   public String getInsertSql() {
      if (insertSql == null) {
         insertSql = generateInsertIntoSql();
      }
      return insertSql;
   }

   private String generateInsertIntoSql() {
      StringBuilder strB = getInsertIntoSqlStart();

      for (int i = 0; i < columns.size(); i++) {
         if (i != 0) {
            strB.append(", ");
         }
         strB.append("?");
      }
      strB.append(")");
      return strB.toString();
   }

   private StringBuilder getInsertIntoSqlStart() {
      StringBuilder strB = new StringBuilder("INSERT INTO ");
      strB.append(getName());
      strB.append(" (");

      Collections.appendToBuilder(columns, ", ", strB);
      strB.append(") VALUES (");
      return strB;
   }
   /*
    * getSelectInsertString will be used to construct recovery sql files when purging data
    * These will be select statements that result in insert statements to add to recovery files
    * E.g. select 'insert into table1(col1,col2) values (val1,val2)' from table1 where <being deleted>';
    * will result in the following line added to recovery files:
    * insert into table1(col1,col2) values (val1,val2);
    */
   public String getSelectInsertString(String whereClause) {
      String insertSelectSql = getInsertIntoSqlWithValues(columns.toArray());
      String firstString = "select '" + insertSelectSql.substring(0, insertSelectSql.lastIndexOf("(") + 1) + "'||";
      String params = insertSelectSql.substring(insertSelectSql.lastIndexOf("(") + 1, insertSelectSql.lastIndexOf(")"));
      boolean first = true;
      for (String string : params.split(",")) {
         if (first) {
            first = false;
         } else {
            firstString = firstString + "||','||";
         }
         if (string.contains("'")) {
            firstString = firstString + "''''||" + string.replace("'", "") + "||''''";
         } else {
            firstString = firstString + string;
         }
      }
      firstString = firstString + "||');' insertString from " + getName() + whereClause;
      return firstString;
   }
}