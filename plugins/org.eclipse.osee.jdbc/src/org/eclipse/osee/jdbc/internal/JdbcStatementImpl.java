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

package org.eclipse.osee.jdbc.internal;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.VariantData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public final class JdbcStatementImpl implements JdbcStatement {

   private final JdbcConnectionInfo dbInfo;
   private ResultSet rSet;
   private PreparedStatement preparedStatement;
   private CallableStatement callableStatement;
   private JdbcConnectionImpl connection;
   private final boolean autoClose;
   private final JdbcConnectionProvider connectionProvider;
   private final int resultSetType;
   private final int resultSetConcurrency;

   public JdbcStatementImpl(JdbcConnectionInfo dbInfo, JdbcConnectionProvider connectionProvider, JdbcConnectionImpl connection) {
      this(dbInfo, connectionProvider, connection, connection == null);
   }

   public JdbcStatementImpl(JdbcConnectionInfo dbInfo, JdbcConnectionProvider connectionProvider, JdbcConnectionImpl connection, boolean autoClose) {
      this(dbInfo, connectionProvider, connection, autoClose, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
   }

   public JdbcStatementImpl(JdbcConnectionInfo dbInfo, JdbcConnectionProvider connectionProvider) {
      this(dbInfo, connectionProvider, null);
   }

   public JdbcStatementImpl(JdbcConnectionInfo dbInfo, JdbcConnectionProvider connectionProvider, int resultSetType, int resultSetConcurrency) {
      this(dbInfo, connectionProvider, null, true, resultSetType, resultSetConcurrency);
   }

   public JdbcStatementImpl(JdbcConnectionInfo dbInfo, JdbcConnectionProvider connectionProvider, JdbcConnectionImpl connection, boolean autoClose, int resultSetType, int resultSetConcurrency) {
      this.dbInfo = dbInfo;
      this.autoClose = autoClose;
      this.connection = connection;
      this.connectionProvider = connectionProvider;
      this.resultSetType = resultSetType;
      this.resultSetConcurrency = resultSetConcurrency;
   }

   @Override
   public void runPreparedQuery(String query, Object... data) throws JdbcException {
      runPreparedQuery(0, query, data);
   }

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    * to 10,000
    */
   @Override
   public void runPreparedQuery(int fetchSize, String query, Object... data) throws JdbcException {
      try {
         allowReuse();
         preparedStatement = connection.prepareStatement(query, resultSetType, resultSetConcurrency);
         preparedStatement.setFetchSize(Math.min(fetchSize, JdbcConstants.JDBC__MAX_FETCH_SIZE));
         JdbcUtil.setInputParametersForStatement(preparedStatement, data);
         rSet = preparedStatement.executeQuery();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public boolean next() throws JdbcException {
      if (rSet != null) {
         try {
            return rSet.next();
         } catch (SQLException ex) {
            throw newJdbcException(ex);
         }
      }
      return false;
   }

   /**
    * The application must call close when it is done using this object; however, it is safe to use this same object
    * multiple times, for example calling runPreparedQuery() repeatedly, without any intermediate calls to close
    */
   @Override
   public void close() {
      closePreviousResources();
      if (autoClose && connection != null) {
         try {
            if (!connection.isClosed()) {
               connection.close();
            }
         } catch (Exception ex) {
            // do nothing
         } finally {
            connection = null;// this allows for multiple calls to runPreparedQuery to have an open connection
         }
      }
   }

   /**
    * allows for multiple uses of this object to have an open connection
    */
   private void allowReuse() throws JdbcException {
      closePreviousResources();
      if (connection == null) {
         connection = connectionProvider.getConnection(dbInfo);
      }
   }

   private void closePreviousResources() {
      try {
         // rSet.isClosed is not supported by some JDBC drivers so don't check it
         if (rSet != null) {
            rSet.close();
         }
      } catch (SQLException ex) {
         // do nothing
      } finally {
         rSet = null;
      }
      try {
         if (preparedStatement != null && !preparedStatement.isClosed()) {
            preparedStatement.close();
         }
      } catch (SQLException ex) {
         // do nothing
      } finally {
         preparedStatement = null;
      }

      try {
         if (callableStatement != null && !callableStatement.isClosed()) {
            callableStatement.close();
         }
      } catch (SQLException ex) {
         // do nothing
      } finally {
         callableStatement = null;
      }
   }

   @Override
   public InputStream getBinaryStream(String columnName) throws JdbcException {
      try {
         return rSet.getBinaryStream(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public InputStream getAsciiStream(String columnName) throws JdbcException {
      try {
         return rSet.getAsciiStream(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public String getString(String columnName) throws JdbcException {
      try {
         return rSet.getString(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public float getFloat(String columnName) throws JdbcException {
      try {
         return rSet.getFloat(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public long getLong(String columnName) throws JdbcException {
      try {
         return rSet.getLong(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public int getInt(String columnName) throws JdbcException {
      try {
         return rSet.getInt(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public boolean getBoolean(String columnName) throws JdbcException {
      try {
         return rSet.getBoolean(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public int getInt(int columnIndex) throws JdbcException {
      try {
         return rSet.getInt(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public int getCallableInt(int columnIndex) throws JdbcException {
      try {
         return callableStatement.getInt(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public double getCallableDouble(int columnIndex) throws JdbcException {
      try {
         return callableStatement.getDouble(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   @Override
   public long getLong(int columnIndex) throws JdbcException {
      try {
         return rSet.getLong(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   @Override
   public String getString(int columnIndex) throws JdbcException {
      try {
         return rSet.getString(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public Timestamp getTimestamp(String columnName) throws JdbcException {
      try {
         return rSet.getTimestamp(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public BigDecimal getBigDecimal(String name) throws JdbcException {
      try {
         return rSet.getBigDecimal(name);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public Time getTime(String name) throws JdbcException {
      try {
         return rSet.getTime(name);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public double getDouble(String columnName) throws JdbcException {
      try {
         return rSet.getDouble(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public Date getDate(String columnName) throws JdbcException {
      try {
         return rSet.getDate(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public boolean wasNull() throws JdbcException {
      try {
         return rSet.wasNull();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public int getColumnCount() throws JdbcException {
      try {
         return rSet.getMetaData().getColumnCount();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public String getColumnName(int columnIndex) throws JdbcException {
      try {
         return rSet.getMetaData().getColumnName(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public int getColumnType(int columnIndex) throws JdbcException {
      try {
         return rSet.getMetaData().getColumnType(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public String getColumnTypeName(int columnIndex) throws JdbcException {
      try {
         return rSet.getMetaData().getColumnTypeName(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public Object getObject(int columnIndex) throws JdbcException {
      try {
         return rSet.getObject(columnIndex);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public Object getObject(String columnName) throws JdbcException {
      try {
         return rSet.getObject(columnName);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   /**
    * Returns the number of rows in the result set. Once this method returns the result set will be pointing to the last
    * row
    *
    * @return the number of rows in the result set
    */
   @Override
   public int getRowCount() throws JdbcException {
      try {
         rSet.last();
         return rSet.getRow();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public boolean isNullable(int columnIndex) throws JdbcException {
      try {
         return rSet.getMetaData().isNullable(columnIndex) == ResultSetMetaData.columnNullable;
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public void updateObject(String columnName, Object value) throws JdbcException {
      try {
         rSet.updateObject(columnName, value);
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public void updateRow() throws JdbcException {
      try {
         rSet.updateRow();
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public void cancel() throws JdbcException {
      try {
         if (preparedStatement != null && !preparedStatement.isClosed()) {
            preparedStatement.cancel();
         }
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
      try {
         if (callableStatement != null && !callableStatement.isClosed()) {
            callableStatement.cancel();
         }
      } catch (SQLException ex) {
         throw newJdbcException(ex);
      }
   }

   @Override
   public IVariantData parse() {
      IVariantData toReturn = new VariantData();
      int numberOfColumns = getColumnCount() + 1;
      for (int index = 1; index < numberOfColumns; index++) {
         int type = getColumnType(index);
         String name = getColumnName(index);
         // Store name - all upper case
         String upperCasedName = name.toUpperCase();
         switch (type) {
            case Types.CLOB:
            case Types.BINARY:
               InputStream inputStream = getAsciiStream(name);
               toReturn.put(upperCasedName, streamToByteArray(inputStream));
               break;
            case Types.BLOB:
               InputStream blobStream = getBinaryStream(name);
               toReturn.put(upperCasedName, streamToByteArray(blobStream));
               break;
            case Types.TIMESTAMP:
               Timestamp timeStamp = getTimestamp(name);
               if (timeStamp != null) {
                  toReturn.put(upperCasedName, timeStamp.getTime());
               }
               break;
            case Types.DATE:
               Date date = getDate(name);
               if (date != null) {
                  toReturn.put(upperCasedName, date.getTime());
               }
               break;
            default:
               String value = getString(name);
               if (Strings.isValid(value) != false) {
                  value = value.trim();
               }
               toReturn.put(upperCasedName, getString(name));
               break;
         }
      }
      return toReturn;
   }

   private static byte[] streamToByteArray(InputStream inputStream) {
      byte[] toReturn;
      if (inputStream != null) {
         try {
            toReturn = Lib.inputStreamToBytes(inputStream);
         } catch (IOException ex) {
            throw newJdbcException(ex);
         }
      } else {
         toReturn = new byte[0];
      }
      return toReturn;
   }
}
