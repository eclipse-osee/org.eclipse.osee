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
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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

   public static <O extends Object> void populateValuesForPreparedStatement(PreparedStatement preparedStatement, O... data) throws JdbcException {
      try {
         int preparedIndex = 0;
         for (Object dataValue : data) {
            preparedIndex++;
            if (dataValue instanceof String) {
               int length = ((String) dataValue).length();
               if (length > JDBC__MAX_VARCHAR_LENGTH) {
                  throw newJdbcException("SQL data value length must be <= %d not %d\nValue: %s",
                     JDBC__MAX_VARCHAR_LENGTH, length, dataValue);
               }
            }

            if (dataValue == null) {
               throw newJdbcException("instead of passing null for an query parameter, pass the corresponding SQL3DataType");
            } else if (dataValue instanceof SQL3DataType) {
               int dataTypeNumber = ((SQL3DataType) dataValue).getSQLTypeNumber();
               if (dataTypeNumber == java.sql.Types.BLOB) {
                  // TODO Need to check this - for PostgreSql, setNull for BLOB with the new JDBC driver gives the error "column
                  //  "content" is of type bytea but expression is of type oid"
                  preparedStatement.setBytes(preparedIndex, null);
               } else {
                  preparedStatement.setNull(preparedIndex, dataTypeNumber);
               }
            } else if (dataValue instanceof ByteArrayInputStream) {
               preparedStatement.setBinaryStream(preparedIndex, (ByteArrayInputStream) dataValue,
                  ((ByteArrayInputStream) dataValue).available());
            } else if (dataValue instanceof Date) {
               java.util.Date javaDate = (java.util.Date) dataValue;
               java.sql.Timestamp date = new java.sql.Timestamp(javaDate.getTime());
               preparedStatement.setTimestamp(preparedIndex, date);
            } else if (dataValue instanceof BigInteger) {
               BigInteger bigInt = (BigInteger) dataValue;
               preparedStatement.setLong(preparedIndex, bigInt.longValue());
            } else if (dataValue instanceof BigDecimal) {
               BigDecimal bigDec = (BigDecimal) dataValue;
               preparedStatement.setLong(preparedIndex, bigDec.longValue());
            } else {
               preparedStatement.setObject(preparedIndex, dataValue);
            }
         }
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   public static JdbcConnectionInfo newConnectionInfo(final String dbDriver, final String dbUri, final Properties dbProps, boolean appendPropsToUri) {
      checkNotNull(dbDriver, "database driver");
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

   private static void checkNotNullOrEmpty(String object, String objectName) throws OseeCoreException {
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
}
