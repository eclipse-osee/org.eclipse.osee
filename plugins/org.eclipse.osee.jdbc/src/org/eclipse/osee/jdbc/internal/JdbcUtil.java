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
package org.eclipse.osee.jdbc.internal;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_VARCHAR_LENGTH;
import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcConstants.PoolExhaustedAction;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcUtil {

   private JdbcUtil() {
      // Utility class
   }

   public static <O extends Object> void setInputParametersForStatement(PreparedStatement preparedStatement, O... data) throws JdbcException {
      setInputParametersForStatement(preparedStatement, 1, data);
   }

   public static <O extends Object> void setInputParametersForStatement(PreparedStatement preparedStatement, int intialIndex, O... data) throws JdbcException {
      int preparedIndex = intialIndex;
      for (Object dataValue : data) {
         setInputParameterForStatement(preparedStatement, dataValue, preparedIndex++);
      }
   }

   public static boolean isValidExtraParam(String key) {
      return Strings.isValid(key) //
         && !key.startsWith(JdbcConstants.NAMESPACE) //
         && !key.equalsIgnoreCase(JdbcConstants.JDBC_SERVICE__ID) //
         && !key.equalsIgnoreCase(JdbcConstants.JDBC_SERVICE__OSGI_BINDING) //
         && !key.equalsIgnoreCase("objectClass") //
         && !key.equalsIgnoreCase("component.id") //
         && !key.equalsIgnoreCase("component.name");
   }

   public static <O extends Object> void setInputParameterForStatement(PreparedStatement statement, O dataValue, int preparedIndex) throws JdbcException {
      try {
         if (dataValue instanceof String) {
            checkStringDataLength(dataValue);
         }

         if (dataValue == null) {
            throw newJdbcException(
               "instead of passing null for an input parameter, pass the corresponding SQL3DataType");
         } else if (dataValue instanceof SQL3DataType) {
            int dataTypeNumber = ((SQL3DataType) dataValue).getSQLTypeNumber();
            if (dataTypeNumber == java.sql.Types.BLOB) {
               // TODO Need to check this - for PostgreSql, setNull for BLOB with the new JDBC driver gives the error "column
               //  "content" is of type bytea but expression is of type oid"
               statement.setBytes(preparedIndex, null);
            } else {
               statement.setNull(preparedIndex, dataTypeNumber);
            }
         } else if (dataValue instanceof ByteArrayInputStream) {
            statement.setBinaryStream(preparedIndex, (ByteArrayInputStream) dataValue,
               ((ByteArrayInputStream) dataValue).available());
         } else if (dataValue instanceof Date) {
            java.util.Date javaDate = (java.util.Date) dataValue;
            java.sql.Timestamp date = new java.sql.Timestamp(javaDate.getTime());
            statement.setTimestamp(preparedIndex, date);
         } else if (dataValue instanceof BigInteger) {
            BigInteger bigInt = (BigInteger) dataValue;
            statement.setLong(preparedIndex, bigInt.longValue());
         } else if (dataValue instanceof BigDecimal) {
            BigDecimal bigDec = (BigDecimal) dataValue;
            statement.setLong(preparedIndex, bigDec.longValue());
         } else if (dataValue instanceof Id) {
            Id id = (Id) dataValue;
            statement.setLong(preparedIndex, id.getId());
         } else if (dataValue instanceof Identity<?>) {// remove once Id is fully utilized
            Identity<?> id = (Identity<?>) dataValue;
            statement.setInt(preparedIndex, (int) id.getGuid());
         } else {
            statement.setObject(preparedIndex, dataValue);
         }
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   private static void checkStringDataLength(Object dataValue) {
      int length = ((String) dataValue).length();
      if (length > JDBC__MAX_VARCHAR_LENGTH) {
         throw newJdbcException("SQL data value length must be <= %d not %d\nValue: %s", JDBC__MAX_VARCHAR_LENGTH,
            length, dataValue);
      }
   }

   public static JdbcConnectionInfo newConnectionInfo(final String dbDriver, final String dbUri, final Properties dbProps, boolean appendPropsToUri) {
      checkNotNullOrEmpty(dbDriver, "database driver");
      checkNotNullOrEmpty(dbUri, "database uri");

      final String connectionUri;
      if (appendPropsToUri && !dbProps.isEmpty()) {
         StringBuilder builder = new StringBuilder(dbUri);
         for (Entry<Object, Object> entry : dbProps.entrySet()) {
            builder.append(String.format(";%s=%s", entry.getKey(), entry.getValue()));
         }
         connectionUri = builder.toString();
      } else {
         connectionUri = dbUri;
      }

      StringBuilder builder = new StringBuilder();
      add(builder, dbDriver);
      add(builder, connectionUri);
      final String connectionId = builder.toString();
      return new JdbcConnectionInfo() {

         @Override
         public String getId() {
            return connectionId;
         }

         @Override
         public String getDriver() {
            return dbDriver;
         }

         @Override
         public String getUri() {
            return connectionUri;
         }

         @Override
         public Properties getProperties() {
            return dbProps;
         }

         @Override
         public String toString() {
            return "JdbcConnectionInfo [id=" + getId() + ", driver=" + getDriver() + ", uri=" + getUri() + ", props=" + getProperties();
         }

      };
   }

   private static void add(StringBuilder builder, String value) {
      if (Strings.isValid(value)) {
         if (builder.length() > 0) {
            builder.append('.');
         }
         builder.append(value);
      }
   }

   private static void checkNotNull(Object object, String objectName) {
      if (object == null) {
         throw newJdbcException("%s cannot be null", objectName);
      }
   }

   private static void checkNotNullOrEmpty(String object, String objectName)  {
      checkNotNull(object, objectName);
      if (object.length() == 0) {
         throw newJdbcException("%s cannot be empty", objectName);
      }
   }

   public static long getLong(Map<String, Object> props, String key, long defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Strings.isNumeric(toReturn) ? Long.parseLong(toReturn) : -1L;
   }

   public static int getInt(Map<String, Object> props, String key, int defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Strings.isNumeric(toReturn) ? Integer.parseInt(toReturn) : -1;
   }

   public static boolean getBoolean(Map<String, Object> props, String key, boolean defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Boolean.parseBoolean(toReturn);
   }

   public static String get(Map<String, Object> props, String key, String defaultValue) {
      String toReturn = defaultValue;
      Object object = props != null ? props.get(key) : null;
      if (object != null) {
         toReturn = String.valueOf(object);
      }
      return toReturn;
   }

   public static PoolExhaustedAction getExhaustedAction(Map<String, Object> props, String key, PoolExhaustedAction defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return PoolExhaustedAction.fromString(toReturn);
   }

   public static int getRandomPort() {
      PortUtil port = PortUtil.getInstance();
      try {
         return port.getValidPort();
      } catch (IOException ex) {
         throw newJdbcException(ex, "Error acquiring random port for JdbcServer");
      }
   }

   public static String getServiceId(Map<String, Object> data) {
      return get(data, JdbcConstants.JDBC_SERVICE__ID, null);
   }

   @SuppressWarnings("unchecked")
   public static Set<String> getBindings(Map<String, Object> data) {
      Set<String> bindings = (Set<String>) data.get(JdbcConstants.JDBC_SERVICE__OSGI_BINDING);
      return bindings != null ? bindings : Collections.<String> emptySet();
   }

   public static int calculateBatchUpdateResults(int[] updates) throws JdbcException {
      int returnCount = 0;
      for (int update : updates) {
         if (update >= 0) {
            returnCount += update;
         } else if (Statement.EXECUTE_FAILED == update) {
            throw newJdbcException("Batch update failed");
         } else if (Statement.SUCCESS_NO_INFO == update) {
            returnCount++;
         }
      }
      return returnCount;
   }

   public static void close(PreparedStatement stmt) {
      if (stmt != null) {
         try {
            stmt.close();
         } catch (SQLException ex) {
            // do nothing
         }
      }
   }
}
