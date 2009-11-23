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
public interface IOseeStatement {

   void runPreparedQuery(String query, Object... data) throws OseeDataStoreException;

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    *           to 10,000
    * @param query
    * @param data
    * @throws OseeDataStoreException
    */
   void runPreparedQuery(int fetchSize, String query, Object... data) throws OseeDataStoreException;

   /**
    * Invokes a stored procedure parameters of type SQL3DataType are registered as Out parameters and all others are set
    * as in parameters
    * 
    * @param query
    * @param data
    * @throws OseeDataStoreException
    */
   void runCallableStatement(String query, Object... data) throws OseeDataStoreException;

   boolean next() throws OseeDataStoreException;

   /**
    * The application must call close when it is done using this object; however, it is safe to use this same object
    * multiple times, for example calling runPreparedQuery() repeatedly, without any intermediate calls to close
    */
   void close();

   InputStream getBinaryStream(String columnName) throws OseeDataStoreException;

   InputStream getAsciiStream(String columnName) throws OseeDataStoreException;

   String getString(String columnName) throws OseeDataStoreException;

   float getFloat(String columnName) throws OseeDataStoreException;

   long getLong(String columnName) throws OseeDataStoreException;

   int getInt(String columnName) throws OseeDataStoreException;

   Timestamp getTimestamp(String columnName) throws OseeDataStoreException;

   BigDecimal getBigDecimal(String name) throws OseeDataStoreException;

   Time getTime(String name) throws OseeDataStoreException;

   double getDouble(String columnName) throws OseeDataStoreException;

   Date getDate(String columnName) throws OseeDataStoreException;

   boolean wasNull() throws OseeDataStoreException;

   int getColumnCount() throws OseeDataStoreException;

   String getColumnName(int columnIndex) throws OseeDataStoreException;

   int getColumnType(int columnIndex) throws OseeDataStoreException;

   String getColumnTypeName(int columnIndex) throws OseeDataStoreException;

   Object getObject(int columnIndex) throws OseeDataStoreException;

   /**
    * Returns the number of rows in the result set. Once this method returns the result set will be pointing to the last
    * row
    * 
    * @return the number of rows in the result set
    * @throws OseeDataStoreException
    */
   int getRowCount() throws OseeDataStoreException;

   boolean isNullable(int columnIndex) throws OseeDataStoreException;

   double getCallableDouble(int columnIndex) throws OseeDataStoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   int getInt(int columnIndex) throws OseeDataStoreException;

   int getCallableInt(int columnIndex) throws OseeDataStoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   long getLong(int columnIndex) throws OseeDataStoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    * 
    * @param columnIndex
    * @return value
    * @throws OseeDataStoreException
    */
   String getString(int columnIndex) throws OseeDataStoreException;

   String getComplementSql() throws OseeDataStoreException;

   boolean isDatabaseType(SupportedDatabase type) throws OseeDataStoreException;

   void updateObject(String columnName, Object value) throws OseeDataStoreException;

   void updateRow() throws OseeDataStoreException;
}