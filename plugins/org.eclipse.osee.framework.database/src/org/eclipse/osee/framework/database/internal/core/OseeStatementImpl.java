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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.database.core.SupportedDatabase;
import org.eclipse.osee.framework.database.internal.Activator;
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
   public void runPreparedQuery(String query, Object... data) throws OseeCoreException {
      runPreparedQuery(0, query, data);
   }

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    * to 10,000
    */
   @Override
   public void runPreparedQuery(int fetchSize, String query, Object... data) throws OseeCoreException {

      try {
         allowReuse();
         preparedStatement = connection.prepareStatement(query, resultSetType, resultSetConcurrency);
         preparedStatement.setFetchSize(Math.min(fetchSize, 10000));
         StatementUtil.populateValuesForPreparedStatement(preparedStatement, data);

         rSet = preparedStatement.executeQuery();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   /**
    * Invokes a stored procedure parameters of type SQL3DataType are registered as Out parameters and all others are set
    * as in parameters
    */
   @Override
   public void runCallableStatement(String query, Object... data) throws OseeCoreException {

      try {
         allowReuse();
         callableStatement = connection.prepareCall(query, resultSetType, resultSetConcurrency);

         for (int index = 0; index < data.length; index++) {
            if (data[index] instanceof SQL3DataType) {
               callableStatement.registerOutParameter(index + 1, ((SQL3DataType) data[index]).getSQLTypeNumber());
            }
         }
         StatementUtil.populateValuesForPreparedStatement(callableStatement, data);
         if (callableStatement.execute()) {
            rSet = callableStatement.getResultSet();
         }
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   public boolean next() throws OseeCoreException {
      if (rSet != null) {
         try {
            return rSet.next();
         } catch (SQLException ex) {
            OseeExceptions.wrapAndThrow(ex);
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
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   /**
    * allows for multiple uses of this object to have an open connection
    */
   private void allowReuse() throws OseeCoreException {
      if (connection == null) {
         connection = connectionPool.getConnection();
      }
      closePreviousResources();
   }

   private void closePreviousResources() throws OseeCoreException {
      try {
         if (rSet != null) {
            rSet.close();
         }
         if (preparedStatement != null) {
            preparedStatement.close();
         }
         if (callableStatement != null) {
            callableStatement.close();
         }
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   public InputStream getBinaryStream(String columnName) throws OseeCoreException {
      try {
         return rSet.getBinaryStream(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public InputStream getAsciiStream(String columnName) throws OseeCoreException {
      try {
         return rSet.getAsciiStream(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public String getString(String columnName) throws OseeCoreException {
      try {
         return rSet.getString(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public float getFloat(String columnName) throws OseeCoreException {
      try {
         return rSet.getFloat(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0.0f; // unreachable since wrapAndThrow() always throws an exceptions
      }
   }

   @Override
   public long getLong(String columnName) throws OseeCoreException {
      try {
         return rSet.getLong(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public int getInt(String columnName) throws OseeCoreException {
      try {
         return rSet.getInt(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public int getInt(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getInt(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public int getCallableInt(int columnIndex) throws OseeCoreException {
      try {
         return callableStatement.getInt(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public double getCallableDouble(int columnIndex) throws OseeCoreException {
      try {
         return callableStatement.getDouble(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   @Override
   public long getLong(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getLong(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   @Override
   public String getString(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getString(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public Timestamp getTimestamp(String columnName) throws OseeCoreException {
      try {
         return rSet.getTimestamp(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public BigDecimal getBigDecimal(String name) throws OseeCoreException {
      try {
         return rSet.getBigDecimal(name);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public Time getTime(String name) throws OseeCoreException {
      try {
         return rSet.getTime(name);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public double getDouble(String columnName) throws OseeCoreException {
      try {
         return rSet.getDouble(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public Date getDate(String columnName) throws OseeCoreException {
      try {
         return rSet.getDate(columnName);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public boolean wasNull() throws OseeCoreException {
      try {
         return rSet.wasNull();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return false; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public int getColumnCount() throws OseeCoreException {
      try {
         return rSet.getMetaData().getColumnCount();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public String getColumnName(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getMetaData().getColumnName(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public int getColumnType(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getMetaData().getColumnType(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public String getColumnTypeName(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getMetaData().getColumnTypeName(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public Object getObject(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getObject(columnIndex);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   /**
    * Returns the number of rows in the result set. Once this method returns the result set will be pointing to the last
    * row
    * 
    * @return the number of rows in the result set
    */
   @Override
   public int getRowCount() throws OseeCoreException {
      try {
         rSet.last();
         return rSet.getRow();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return 0; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public boolean isNullable(int columnIndex) throws OseeCoreException {
      try {
         return rSet.getMetaData().isNullable(columnIndex) == ResultSetMetaData.columnNullable;
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return false; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   @Override
   public String getComplementSql() throws OseeCoreException {
      allowReuse();
      return SupportedDatabase.getComplementSql(connection.getMetaData());
   }

   @Override
   public boolean isDatabaseType(SupportedDatabase type) throws OseeCoreException {
      return SupportedDatabase.isDatabaseType(connection.getMetaData(), type);
   }

   @Override
   public void updateObject(String columnName, Object value) throws OseeCoreException {
      try {
         rSet.updateObject(columnName, value);
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   @Override
   public void updateRow() throws OseeCoreException {
      try {
         rSet.updateRow();
      } catch (SQLException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }
}