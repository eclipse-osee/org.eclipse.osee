/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public interface JdbcStatement extends AutoCloseable {

   void runPreparedQuery(String query, Object... data);

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    * to 10,000
    */
   void runPreparedQuery(int fetchSize, String query, Object... data);

   boolean next();

   /**
    * The application must call close when it is done using this object; however, it is safe to use this same object
    * multiple times, for example calling runPreparedQuery() repeatedly, without any intermediate calls to close
    */
   @Override
   void close();

   void cancel();

   InputStream getBinaryStream(String columnName);

   InputStream getAsciiStream(String columnName);

   String getString(String columnName);

   float getFloat(String columnName);

   long getLong(String columnName);

   int getInt(String columnName);

   boolean getBoolean(String columnName);

   Timestamp getTimestamp(String columnName);

   BigDecimal getBigDecimal(String name);

   Time getTime(String name);

   double getDouble(String columnName);

   Date getDate(String columnName);

   boolean wasNull();

   int getColumnCount();

   String getColumnName(int columnIndex);

   int getColumnType(int columnIndex);

   String getColumnTypeName(int columnIndex);

   Object getObject(int columnIndex);

   Object getObject(String columnName);

   /**
    * Returns the number of rows in the result set. Once this method returns the result set will be pointing to the last
    * row
    *
    * @return the number of rows in the result set
    */
   int getRowCount();

   boolean isNullable(int columnIndex);

   double getCallableDouble(int columnIndex);

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   int getInt(int columnIndex);

   int getCallableInt(int columnIndex);

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   long getLong(int columnIndex);

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   String getString(int columnIndex);

   void updateObject(String columnName, Object value);

   void updateRow();

   IVariantData parse();

   default Object loadAttributeValue(AttributeTypeToken attributeType) {
      Object value;
      if (attributeType.isBoolean()) {
         value = getBoolean("value");
      } else if (attributeType.isDouble()) {
         value = getDouble("value");
      } else if (attributeType.isInteger()) {
         value = getInt("value");
      } else if (attributeType.isLong()) {
         value = getLong("value");
      } else if (attributeType.isArtifactId()) {
         String id = getString("value");
         value = ArtifactId.valueOf(id);
      } else if (attributeType.isBranchId()) {
         value = BranchId.valueOf(getString("value"));
      } else if (attributeType.isDate()) {
         value = new Date(getLong("value"));
      } else {
         value = getString("value");
         if (attributeType.isEnumerated()) {
            value = Strings.intern((String) value);
         }
      }
      return value;
   }
}