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
package org.eclipse.osee.framework.database.internal.core;

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
import java.util.logging.Level;

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.database.internal.InternalActivator;
import org.eclipse.osee.framework.database.sql.QueryRecord;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public final class OseeStatementImpl implements IOseeStatement {
   private ResultSet rSet;
   private PreparedStatement preparedStatement;
   private CallableStatement callableStatement;
   private OseeConnectionImpl connection;
   private final boolean autoClose;
   private final OseeConnectionPoolImpl connectionPool;
   private final int resultSetType;
   private final int resultSetConcurrency;

   public OseeStatementImpl(OseeConnectionPoolImpl connectionPool, OseeConnectionImpl connection) {
      this(connectionPool, connection, connection == null);
   }

   public OseeStatementImpl(OseeConnectionPoolImpl connectionPool, OseeConnectionImpl connection, boolean autoClose) {
      this(connectionPool, connection, autoClose, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
   }

   public OseeStatementImpl(OseeConnectionPoolImpl connectionPool) {
      this(connectionPool, null);
   }

   public OseeStatementImpl(OseeConnectionPoolImpl connectionPool, OseeConnectionImpl connection, boolean autoClose, int resultSetType, int resultSetConcurrency) {
      this.autoClose = autoClose;
      this.connection = connection;
      this.connectionPool = connectionPool;
      this.resultSetType = resultSetType;
      this.resultSetConcurrency = resultSetConcurrency;
   }

   @Override
   public void runPreparedQuery(String query, Object... data) throws OseeDataStoreException {
      runPreparedQuery(0, query, data);
   }

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    *           to 10,000
    * @param query
    * @param data
    * @throws OseeDataStoreException
    */
   @Override
   public void runPreparedQuery(int fetchSize, String query, Object... data) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord(query, data);

      try {
         allowReuse();
         preparedStatement = connection.prepareStatement(query, resultSetType, resultSetConcurrency);
         preparedStatement.setFetchSize(Math.min(fetchSize, 10000));
         StatementUtil.populateValuesForPreparedStatement(preparedStatement, data);

         record.markStart();
         rSet = preparedStatement.executeQuery();
         record.markEnd();
      } catch (SQLException ex) {
         record.setSqlException(ex);
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * Invokes a stored procedure parameters of type SQL3DataType are registered as Out parameters and all others are set
    * as in parameters
    * 
    * @param query
    * @param data
    * @throws OseeDataStoreException
    */
   @Override
   public void runCallableStatement(String query, Object... data) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord(query, data);

      try {
         allowReuse();
         callableStatement = connection.prepareCall(query, resultSetType, resultSetConcurrency);

         for (int index = 0; index < data.length; index++) {
            if (data[index] instanceof SQL3DataType) {
               callableStatement.registerOutParameter(index + 1, ((SQL3DataType) data[index]).getSQLTypeNumber());
            }
         }
         StatementUtil.populateValuesForPreparedStatement(callableStatement, data);

         record.markStart();
         if (callableStatement.execute()) {
            rSet = callableStatement.getResultSet();
         }
         record.markEnd();
      } catch (SQLException ex) {
         record.setSqlException(ex);
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public boolean next() throws OseeDataStoreException {
      if (rSet != null) {
         try {
            return rSet.next();
         } catch (SQLException ex) {
            throw new OseeDataStoreException(ex);
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
      try {
         closePreviousResources();
         if (autoClose && connection != null) {
            connection.close();
            connection = null;// this allows for multiple calls to runPreparedQuery to have an open connection
         }
      } catch (OseeDataStoreException ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
      }
   }

   /**
    * allows for multiple uses of this object to have an open connection
    * 
    * @throws SQLException
    * @throws OseeDataStoreException
    */
   private void allowReuse() throws OseeDataStoreException {
      if (connection == null) {
         connection = connectionPool.getConnection();
      }
      closePreviousResources();
   }

   private void closePreviousResources() throws OseeDataStoreException {
      try{
         if (rSet != null) {
               rSet.close();
         }
         if (preparedStatement != null) {
            preparedStatement.close();
         }
         if (callableStatement != null) {
            callableStatement.close();
         }
      }
      catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public InputStream getBinaryStream(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getBinaryStream(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public InputStream getAsciiStream(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getAsciiStream(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public String getString(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getString(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public float getFloat(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getFloat(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public long getLong(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getLong(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public int getInt(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getInt(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public int getInt(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getInt(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public int getCallableInt(int columnIndex) throws OseeDataStoreException {
      try {
         return callableStatement.getInt(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public double getCallableDouble(int columnIndex) throws OseeDataStoreException {
      try {
         return callableStatement.getDouble(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   @Override
   public long getLong(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getLong(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   @Override
   public String getString(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getString(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public Timestamp getTimestamp(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getTimestamp(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public BigDecimal getBigDecimal(String name) throws OseeDataStoreException {
      try {
         return rSet.getBigDecimal(name);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public Time getTime(String name) throws OseeDataStoreException {
      try {
         return rSet.getTime(name);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public double getDouble(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getDouble(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public Date getDate(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getDate(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public boolean wasNull() throws OseeDataStoreException {
      try {
         return rSet.wasNull();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public int getColumnCount() throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnCount();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public String getColumnName(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnName(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public int getColumnType(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnType(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public String getColumnTypeName(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnTypeName(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public Object getObject(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getObject(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * Returns the number of rows in the result set. Once this method returns the result set will be pointing to the last
    * row
    * 
    * @return the number of rows in the result set
    * @throws OseeDataStoreException
    */
   @Override
   public int getRowCount() throws OseeDataStoreException {
      try {
         rSet.last();
         return rSet.getRow();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public boolean isNullable(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().isNullable(columnIndex) == ResultSetMetaData.columnNullable;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public String getComplementSql() throws OseeDataStoreException {
      allowReuse();
      return SupportedDatabase.getComplementSql(connection.getMetaData());
   }

   @Override
   public boolean isDatabaseType(SupportedDatabase type) throws OseeDataStoreException {
      return SupportedDatabase.isDatabaseType(connection.getMetaData(), type);
   }

   @Override
   public void updateObject(String columnName, Object value) throws OseeDataStoreException {
      try {
         rSet.updateObject(columnName, value);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public void updateRow() throws OseeDataStoreException {
      try {
         rSet.updateRow();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}