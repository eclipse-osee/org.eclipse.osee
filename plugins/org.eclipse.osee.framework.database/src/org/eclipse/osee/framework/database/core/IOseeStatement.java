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

import java.io.Closeable;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 */
public interface IOseeStatement extends Closeable {

   void runPreparedQuery(String query, Object... data) throws OseeCoreException;

   /**
    * @param fetchSize hint as to the number of rows that should be fetched from the database at a time. will be limited
    * to 10,000
    */
   void runPreparedQuery(int fetchSize, String query, Object... data) throws OseeCoreException;

   /**
    * Invokes a stored procedure parameters of type SQL3DataType are registered as Out parameters and all others are set
    * as in parameters
    */
   void runCallableStatement(String query, Object... data) throws OseeCoreException;

   boolean next() throws OseeCoreException;

   /**
    * The application must call close when it is done using this object; however, it is safe to use this same object
    * multiple times, for example calling runPreparedQuery() repeatedly, without any intermediate calls to close
    */
   @Override
   void close();

   InputStream getBinaryStream(String columnName) throws OseeCoreException;

   InputStream getAsciiStream(String columnName) throws OseeCoreException;

   String getString(String columnName) throws OseeCoreException;

   float getFloat(String columnName) throws OseeCoreException;

   long getLong(String columnName) throws OseeCoreException;

   int getInt(String columnName) throws OseeCoreException;

   Timestamp getTimestamp(String columnName) throws OseeCoreException;

   BigDecimal getBigDecimal(String name) throws OseeCoreException;

   Time getTime(String name) throws OseeCoreException;

   double getDouble(String columnName) throws OseeCoreException;

   Date getDate(String columnName) throws OseeCoreException;

   boolean wasNull() throws OseeCoreException;

   int getColumnCount() throws OseeCoreException;

   String getColumnName(int columnIndex) throws OseeCoreException;

   int getColumnType(int columnIndex) throws OseeCoreException;

   String getColumnTypeName(int columnIndex) throws OseeCoreException;

   Object getObject(int columnIndex) throws OseeCoreException;

   /**
    * Returns the number of rows in the result set. Once this method returns the result set will be pointing to the last
    * row
    * 
    * @return the number of rows in the result set
    */
   int getRowCount() throws OseeCoreException;

   boolean isNullable(int columnIndex) throws OseeCoreException;

   double getCallableDouble(int columnIndex) throws OseeCoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   int getInt(int columnIndex) throws OseeCoreException;

   int getCallableInt(int columnIndex) throws OseeCoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   long getLong(int columnIndex) throws OseeCoreException;

   /**
    * should not be used by application code because it is less readable than using the column name
    */
   String getString(int columnIndex) throws OseeCoreException;

   String getComplementSql() throws OseeCoreException;

   boolean isDatabaseType(SupportedDatabase type) throws OseeCoreException;

   void updateObject(String columnName, Object value) throws OseeCoreException;

   void updateRow() throws OseeCoreException;
}