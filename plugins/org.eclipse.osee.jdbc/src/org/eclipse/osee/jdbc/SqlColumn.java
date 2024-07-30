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
import org.eclipse.osee.framework.jdk.core.type.NamedBase;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class SqlColumn extends NamedBase {
   private final int length;
   private final JDBCType type;
   private final boolean isNull;
   private final SqlTable table;
   private final String valueConstraint;
   private final boolean isAutoIncrement;

   public SqlColumn(SqlTable table, String name, JDBCType type) {
      this(table, name, type, false, 0, Strings.EMPTY_STRING, false);
   }

   public SqlColumn(SqlTable table, String name, JDBCType type, boolean isNull) {
      this(table, name, type, isNull, 0, Strings.EMPTY_STRING, false);
   }

   public SqlColumn(SqlTable table, String name, JDBCType type, boolean isNull, boolean isAutoIncrement) {
      this(table, name, type, isNull, 0, Strings.EMPTY_STRING, isAutoIncrement);
   }

   public SqlColumn(SqlTable table, String name, JDBCType type, boolean isNull, int length, String valueConstraint) {
      this(table, name, type, isNull, length, valueConstraint, false);
   }

   public SqlColumn(SqlTable table, String name, JDBCType type, boolean isNull, int length, String valueConstraint, boolean isAutoIncrement) {
      super(name);
      this.table = table;
      this.length = length;
      this.type = type;
      this.isNull = isNull;
      this.valueConstraint = valueConstraint;
      this.isAutoIncrement = isAutoIncrement;
   }

   public JDBCType getType() {
      return type;
   }

   public int getLength() {
      return length;
   }

   public boolean isNull() {
      return isNull;
   }

   public SqlTable getTable() {
      return table;
   }

   public String getValueConstraint() {
      return valueConstraint;
   }

   public boolean isAutoIncrement() {
      return isAutoIncrement;
   }
}