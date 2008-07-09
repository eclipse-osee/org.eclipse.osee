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
package org.eclipse.osee.framework.database.sql.datatype;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
public abstract class SqlDataType {

   public SqlDataType() {
      super();
   }

   protected abstract String getBooleanType();

   protected abstract String getBitType();

   protected abstract String getSmallIntType();

   protected abstract String getIntegerType();

   protected abstract String getDecimalType();

   protected abstract String getFloatType();

   protected abstract String getDoubleType();

   protected abstract String getRealType();

   protected abstract String getDateType();

   protected abstract String getCharType();

   protected abstract String getVarCharType();

   protected abstract String getClobType();

   protected abstract String getBlobType();

   protected abstract String getTimestamp();

   protected abstract String getTime();

   protected abstract String getBigInt();

   protected abstract String getLongVarCharType();

   protected abstract String getNumericType();

   public String getLimit(SQL3DataType dataType, String limit) {
      return limit;
   }

   public String getType(SQL3DataType dataType) {
      String toReturn = "";

      switch (dataType) {
         case BOOLEAN:
            toReturn = this.getBooleanType();
            break;
         case BIT:
            toReturn = this.getBitType();
            break;
         case SMALLINT:
            toReturn = this.getSmallIntType();
            break;
         case INTEGER:
            toReturn = this.getIntegerType();
            break;
         case REAL:
            toReturn = this.getRealType();
            break;
         case DECIMAL:
            toReturn = this.getDecimalType();
            break;
         case FLOAT:
            toReturn = this.getFloatType();
            break;
         case DOUBLE:
            toReturn = this.getDoubleType();
            break;
         case DATE:
            toReturn = this.getDateType();
            break;
         case CHAR:
            toReturn = this.getCharType();
            break;
         case VARCHAR:
            toReturn = this.getVarCharType();
            break;
         case CLOB:
            toReturn = this.getClobType();
            break;
         case TIMESTAMP:
            toReturn = this.getTimestamp();
            break;
         case TIME:
            toReturn = this.getTime();
            break;
         case BLOB:
            toReturn = this.getBlobType();
            break;
         case BIGINT:
            toReturn = this.getBigInt();
            break;
         case LONGVARCHAR:
            toReturn = this.getLongVarCharType();
            break;
         case NUMERIC:
            toReturn = this.getNumericType();
            break;
         default:
            toReturn = "ADDTHISTYPE:" + dataType.name();
            break;
      }
      return toReturn;
   }

   public String formatDataValueforDataType(String columnValue, SQL3DataType columnType) {
      String toReturn = "";
      switch (columnType) {
         case BIGINT:
         case BIT:
         case BOOLEAN:
         case DECIMAL:
         case DOUBLE:
         case FLOAT:
         case INTEGER:
         case REAL:
         case NUMERIC:
         case SMALLINT:
         case TINYINT:
            toReturn = ((columnValue != null && !columnValue.equals("")) ? columnValue : "0");
            break;
         case DATE:
            toReturn = "{d '" + formatDate(columnValue) + "'}";
            break;
         case TIMESTAMP:
            toReturn = "{ts '" + formatDate(columnValue) + "'}";
            break;
         case TIME:
            toReturn = "{t '" + columnValue + "'}";
            break;
         default:
            toReturn = "'" + columnValue + "'";
            break;
      }
      return toReturn;
   }

   private String formatDate(String input) {
      String[] sets = input.split(" ");
      String date = sets[0];
      String time = "";
      if (sets.length > 1) {
         time += " ";
         for (int i = 1; i < sets.length; i++) {
            time += sets[i];
         }
      }
      if (!date.contains("/") && !date.contains("-") && !date.contains(":")) {
         Pattern pattern = Pattern.compile("([0-9][0-9][0-9][0-9])([0-9][0-9])([0-9][0-9])");
         Matcher matcher = pattern.matcher(date);

         if (matcher.find()) {
            if (matcher.groupCount() == 3) {
               String year = matcher.group(1);
               String month = matcher.group(2);
               String day = matcher.group(3);

               date = year + "-" + month + "-" + day;
            }
         }
      } else {
         date = date.replaceAll("/", "-");
         date = date.replaceAll(":", "-");
      }
      return date + time;
   }

   public void preparedStatementHelper(PreparedStatement statement, int index, SQL3DataType columnType, String value) throws SQLException {
      switch (columnType) {
         case BINARY:
         case BIT:
            statement.setByte(index, (value != null && !value.equals("") ? Byte.parseByte(value) : 0));
            break;
         case TINYINT:
         case SMALLINT:
            statement.setShort(index, (value != null && !value.equals("") ? Short.valueOf(value) : 0));
            break;
         case INTEGER:
            statement.setInt(index, (value != null && !value.equals("") ? Integer.valueOf(value) : 0));
            break;
         case BIGINT:
            statement.setBigDecimal(index,
                  (value != null && !value.equals("") ? BigDecimal.valueOf(Double.valueOf(value)) : new BigDecimal(0)));
            break;
         case FLOAT:
            statement.setFloat(index, (value != null && !value.equals("") ? Float.valueOf(value) : 0.0f));
            break;
         case NUMERIC:
         case DECIMAL:
         case REAL:
         case DOUBLE:
            statement.setDouble(index, (value != null && !value.equals("") ? Double.valueOf(value) : 0.0));
            break;
         case CHAR:
         case VARCHAR:
         case LONGVARCHAR:
            statement.setString(index, value);
            break;
         case DATE:
            if (value == null || value.equals("")) {
               statement.setNull(index, SQL3DataType.DATE.getSQLTypeNumber());
            } else {
               statement.setDate(index, Date.valueOf(value));
            }
            break;
         case TIMESTAMP:
            statement.setTimestamp(index,
                  (value != null && !value.equals("") ? Timestamp.valueOf(value) : GlobalTime.GreenwichMeanTimestamp()));
            break;
         case TIME:
            if (value == null || value.equals("")) {
               statement.setNull(index, SQL3DataType.TIME.getSQLTypeNumber());
            } else {
               statement.setTime(index, Time.valueOf(value));
            }
            break;
         case VARBINARY:
         case LONGVARBINARY:
            statement.setBytes(index, value.getBytes());
            break;
         case BLOB:
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value.getBytes());
            BufferedInputStream bs = new BufferedInputStream(byteArrayInputStream);
            statement.setBinaryStream(index, bs, value.length());
            break;
         case CLOB:
            ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(value.getBytes());
            BufferedInputStream bs1 = new BufferedInputStream(byteArrayInputStream1);
            statement.setAsciiStream(index, bs1, value.length());
            break;
         case BOOLEAN:
            statement.setBoolean(index, (value != null && !value.equals("") ? Boolean.parseBoolean(value) : false));
            break;
         default:
            break;
      }
   }
}
