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
package org.eclipse.osee.framework.database.core;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public abstract class ConnectionHandlerStatement {

   protected ConnectionHandlerStatement() {
      super();
   }

   public abstract void runPreparedQuery(String query, Object... data) throws OseeDataStoreException;

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    *           to 10,000
    * @param query
    * @param data
    * @throws OseeDataStoreException
    */
   public abstract void runPreparedQuery(int fetchSize, String query, Object... data) throws OseeDataStoreException;

   /**
    * Invokes a stored procedure parameters of type SQL3DataType are registered as Out parameters and all others are set
    * as in parameters
    * 
    * @param query
    * @param data
    * @throws OseeDataStoreException
    */
   public abstract void runCallableStatement(String query, Object... data) throws OseeDataStoreException;

   public abstract boolean next() throws OseeDataStoreException;

   /**
    * The application must call close when it is done using this object; however, it is safe to use this same object
    * multiple times, for example calling runPreparedQuery() repeatedly, without any intermediate calls to close
    */
   public abstract void close();

   public abstract InputStream getBinaryStream(String columnName) throws OseeDataStoreException;

   public abstract InputStream getAsciiStream(String columnName) throws OseeDataStoreException;

   public abstract String getString(String columnName) throws OseeDataStoreException;

   public abstract float getFloat(String columnName) throws OseeDataStoreException;

   public abstract long getLong(String columnName) throws OseeDataStoreException;

   public abstract int getInt(String columnName) throws OseeDataStoreException;

   public abstract Timestamp getTimestamp(String columnName) throws OseeDataStoreException;

   public abstract BigDecimal getBigDecimal(String name) throws OseeDataStoreException;

   public abstract Time getTime(String name) throws OseeDataStoreException;

   public abstract double getDouble(String columnName) throws OseeDataStoreException;

   public abstract Date getDate(String columnName) throws OseeDataStoreException;

   public abstract boolean wasNull() throws OseeDataStoreException;

   public abstract int getColumnCount() throws OseeDataStoreException;

   public abstract String getColumnName(int columnIndex) throws OseeDataStoreException;

   public abstract int getColumnType(int columnIndex) throws OseeDataStoreException;

   public abstract String getColumnTypeName(int columnIndex) throws OseeDataStoreException;

   public abstract Object getObject(int columnIndex) throws OseeDataStoreException;

   /**
    * Returns the number of rows in the result set. Once this method returns the result set will be pointing to the last
    * row
    * 
    * @return the number of rows in the result set
    * @throws OseeDataStoreException
    */
   public abstract int getRowCount() throws OseeDataStoreException;

   public abstract boolean isNullable(int columnIndex) throws OseeDataStoreException;

   public abstract double getCallableDouble(int columnIndex) throws OseeDataStoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   public abstract int getInt(int columnIndex) throws OseeDataStoreException;

   public abstract int getCallableInt(int columnIndex) throws OseeDataStoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   public abstract long getLong(int columnIndex) throws OseeDataStoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   public abstract String getString(int columnIndex) throws OseeDataStoreException;

   public abstract String getComplementSql() throws OseeDataStoreException;

   public abstract boolean isDatabaseType(SupportedDatabase type) throws OseeDataStoreException;
}