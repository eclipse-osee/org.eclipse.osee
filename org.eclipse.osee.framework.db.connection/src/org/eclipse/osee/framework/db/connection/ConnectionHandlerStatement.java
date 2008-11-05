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
package org.eclipse.osee.framework.db.connection;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.core.query.QueryRecord;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Statment object created by the ConnectionHandler. It contains: <li>ResultSet <li>Statement
 * 
 * @author Jeff C. Phillips
 */
public class ConnectionHandlerStatement {
   private ResultSet rSet;
   private PreparedStatement preparedStatement;
   private Connection connection;
   private final boolean autoClose;

   public ConnectionHandlerStatement(Connection connection) {
      this(connection, connection == null);
   }

   public ConnectionHandlerStatement(Connection connection, boolean autoClose) {
      this.autoClose = autoClose;
      this.connection = connection;
   }

   public ConnectionHandlerStatement() {
      this(null);
   }

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
   public void runPreparedQuery(int fetchSize, String query, Object... data) throws OseeDataStoreException {
      QueryRecord record = new QueryRecord(query, data);

      try {
         if (connection == null) {
            connection = OseeDbConnection.getConnection(); // this allows for multiple calls to this method to have an open connection
         }
         preparedStatement = connection.prepareStatement(query);
         preparedStatement.setFetchSize(Math.min(fetchSize, 10000));
         ConnectionHandler.populateValuesForPreparedStatement(preparedStatement, data);

         record.markStart();
         rSet = preparedStatement.executeQuery();
         record.markEnd();
      } catch (SQLException ex) {
         record.setSqlException(ex);
         throw new OseeDataStoreException(ex);
      }
   }

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
    * @param rset The rset to set.
    */
   @Deprecated
   public void setRset(ResultSet rset) {
      this.rSet = rset;
   }

   /**
    * @param statement The statement to set.
    */
   public void setStatement(PreparedStatement statement) {
      this.preparedStatement = statement;
   }

   public void close() {
      try {
         if (rSet != null) {
            rSet.close();
         }
         if (preparedStatement != null) {
            preparedStatement.close();
         }
         if (autoClose && connection != null) {
            connection.close();
            connection = null;// this allows for multiple calls to runPreparedQuery to have an open connection
         }
      } catch (SQLException ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
      }
   }

   public InputStream getBinaryStream(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getBinaryStream(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public InputStream getAsciiStream(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getAsciiStream(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public String getString(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getString(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public float getFloat(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getFloat(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public long getLong(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getLong(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public int getInt(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getInt(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return
    * @throws OseeDataStoreException
    */
   int getInt(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getInt(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return
    * @throws OseeDataStoreException
    */
   long getLong(int columnIndex) throws OseeDataStoreException {
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
    * @return
    * @throws OseeDataStoreException
    */
   String getString(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getString(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public Timestamp getTimestamp(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getTimestamp(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public BigDecimal getBigDecimal(String name) throws OseeDataStoreException {
      try {
         return rSet.getBigDecimal(name);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public Time getTime(String name) throws OseeDataStoreException {
      try {
         return rSet.getTime(name);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public double getDouble(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getDouble(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public Date getDate(String columnName) throws OseeDataStoreException {
      try {
         return rSet.getDate(columnName);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public boolean wasNull() throws OseeDataStoreException {
      try {
         return rSet.wasNull();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public int getColumnCount() throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnCount();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public String getColumnName(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnName(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public int getColumnType(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnType(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public String getColumnTypeName(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().getColumnTypeName(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public Object getObject(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getObject(columnIndex);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public boolean isNullable(int columnIndex) throws OseeDataStoreException {
      try {
         return rSet.getMetaData().isNullable(columnIndex) == ResultSetMetaData.columnNullable;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}