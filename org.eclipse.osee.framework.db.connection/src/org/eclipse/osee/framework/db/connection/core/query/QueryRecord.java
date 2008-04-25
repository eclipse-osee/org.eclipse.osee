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
package org.eclipse.osee.framework.db.connection.core.query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;

/**
 * Interesting information from a query. These are automatically added to the QueryLog if it is not full.
 * 
 * @author Robert A. Fisher
 */
public class QueryRecord {
   private static final QueryLog log = QueryLog.getInstance();
   private final Date date;
   private final String sql;
   private List<String> bindVariableImages;
   private SQLException sqlException;
   private Long runDurationMs;
   private long startTime;

   /**
    * @param sql The sql text
    */
   public QueryRecord(String sql) {
      this(sql, (Object[]) null);
   }

   /**
    * @param sql The sql text
    * @param bindVariables The bind variables, if any
    */
   public QueryRecord(String sql, Object... bindVariables) {
      if (sql == null) throw new IllegalArgumentException("sql can not be null");
      if (bindVariables != null && bindVariables.length % 2 != 0) throw new IllegalArgumentException(
            "bindvariables must be null, or have an even number of elements");

      this.date = new Date();
      this.sql = Query.replaceBindValues(sql);

      if (bindVariables == null) {
         this.bindVariableImages = new ArrayList<String>(0);
      } else {
         this.bindVariableImages = new ArrayList<String>(bindVariables.length);

         for (int i = 0; i < bindVariables.length; i += 2) {
            if (!(bindVariables[i] instanceof SQL3DataType)) {
               throw new IllegalArgumentException(
                     "bindVariables should have a " + SQL3DataType.class.getCanonicalName() + " reference at every even index");
            }

            SQL3DataType sqlType = (SQL3DataType) bindVariables[i];
            // Already checked that the list is even length, so it is safe to blindly call .next() here
            if (sqlType == SQL3DataType.BINARY || sqlType == SQL3DataType.BLOB || sqlType == SQL3DataType.CLOB || sqlType == SQL3DataType.DATALINK || sqlType == SQL3DataType.JAVA_OBJECT || sqlType == SQL3DataType.LONGVARBINARY || sqlType == SQL3DataType.REF || sqlType == SQL3DataType.STRUCT || sqlType == SQL3DataType.VARBINARY) {
               bindVariableImages.add(sqlType + ": <binary data>");
            } else {
               bindVariableImages.add(sqlType + ": " + bindVariables[i + 1]);
            }
         }
      }

      log.add(this);
   }

   /**
    * @return the runDurationMs
    */
   public Long getRunDurationMs() {
      return runDurationMs;
   }

   /**
    * Mark the start of the query being run
    */
   public void markStart() {
      startTime = System.currentTimeMillis();
   }

   /**
    * Mark the end of the query being run
    */
   public void markEnd() {
      runDurationMs = System.currentTimeMillis() - startTime;
   }

   /**
    * @return the sqlException
    */
   public SQLException getSqlException() {
      return sqlException;
   }

   /**
    * @param sqlException the sqlException to set
    */
   public void setSqlException(SQLException sqlException) {
      this.sqlException = sqlException;
   }

   /**
    * @return the bindVariables
    */
   public List<String> getBindVariableImages() {
      return bindVariableImages;
   }

   /**
    * @return the sql
    */
   public String getSql() {
      return sql;
   }

   /**
    * @return the date
    */
   public Date getDate() {
      return date;
   }

}
