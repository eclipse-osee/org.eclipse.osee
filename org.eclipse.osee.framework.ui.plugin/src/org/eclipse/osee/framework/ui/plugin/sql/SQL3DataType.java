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
package org.eclipse.osee.framework.ui.plugin.sql;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Roberto E. Escobar
 */
public enum SQL3DataType {

   BIT(java.sql.Types.BIT, Boolean.class),
   TINYINT(java.sql.Types.TINYINT, Integer.class),
   SMALLINT(java.sql.Types.SMALLINT, Integer.class),
   INTEGER(java.sql.Types.INTEGER, Integer.class),
   BIGINT(java.sql.Types.BIGINT, BigInteger.class),
   FLOAT(java.sql.Types.FLOAT, Float.class),
   REAL(java.sql.Types.REAL, Double.class),
   DOUBLE(java.sql.Types.DOUBLE, Double.class),
   NUMERIC(java.sql.Types.NUMERIC, Double.class),
   DECIMAL(java.sql.Types.DECIMAL, Integer.class),
   CHAR(java.sql.Types.CHAR, Character.class),
   VARCHAR(java.sql.Types.VARCHAR, String.class),
   LONGVARCHAR(java.sql.Types.LONGVARCHAR, String.class),
   DATE(java.sql.Types.DATE, Date.class),
   TIME(java.sql.Types.TIME, Time.class),
   TIMESTAMP(java.sql.Types.TIMESTAMP, Object.class),
   BINARY(java.sql.Types.BINARY, Object.class),
   VARBINARY(java.sql.Types.VARBINARY, Object.class),
   LONGVARBINARY(java.sql.Types.LONGVARBINARY, Object.class),
   NULL(java.sql.Types.NULL, Object.class),
   OTHER(java.sql.Types.OTHER, Object.class),
   JAVA_OBJECT(java.sql.Types.JAVA_OBJECT, Object.class),
   DISTINCT(java.sql.Types.DISTINCT, Object.class),
   STRUCT(java.sql.Types.STRUCT, Object.class),
   ARRAY(java.sql.Types.ARRAY, Object.class),
   BLOB(java.sql.Types.BLOB, Object.class),
   CLOB(java.sql.Types.CLOB, Object.class),
   REF(java.sql.Types.REF, Object.class),
   DATALINK(java.sql.Types.DATALINK, Object.class),
   BOOLEAN(java.sql.Types.BOOLEAN, Boolean.class);

   @SuppressWarnings("unchecked")
   private Class javaClassEquivalent;
   private int sqlTypeNumber;
   @SuppressWarnings("unchecked")
   private static HashMap<Integer, Class> typeToClass;
   private static HashMap<Integer, SQL3DataType> typeToEnum;

   @SuppressWarnings("unchecked")
   private SQL3DataType(int sqlTypeNumber, Class classEquiv) {
      if (SQL3DataType.typeToClass == null) {
         SQL3DataType.typeToClass = new HashMap<Integer, Class>();
         SQL3DataType.typeToEnum = new HashMap<Integer, SQL3DataType>();
      }
      this.javaClassEquivalent = classEquiv;
      this.sqlTypeNumber = sqlTypeNumber;
      SQL3DataType.typeToClass.put(sqlTypeNumber, classEquiv);
      SQL3DataType.typeToEnum.put(sqlTypeNumber, this);
   }

   public int getSQLTypeNumber() {
      return sqlTypeNumber;
   }

   @SuppressWarnings("unchecked")
   public Class getJavaEquivalentClass() {
      return javaClassEquivalent;
   }

   public static SQL3DataType get(int type) {
      return typeToEnum.get(type);
   }
}
