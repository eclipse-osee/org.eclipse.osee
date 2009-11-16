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
package org.eclipse.osee.framework.database.sql;

import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interesting information from a query. These are automatically added to the QueryLog if it is not full.
 * 
 * @author Robert A. Fisher
 */
public class QueryRecord {
   private static final QueryLog log = QueryLog.getInstance();
   private final Date date;
   private final String sql;
   private final Object[] bindVariables;
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
    * Replaces all of the '?' characters with ':#' values, where # is in incrementing integer value starting at 1.
    * 
    * @param sql The sql string to perform the replacement on.
    */
   private String replaceBindValues(String sql) {
      int count = 1;

      Matcher matcher = Pattern.compile("\\?").matcher(sql);
      while (matcher.find()) {
         sql = matcher.replaceFirst(":" + count++);
         matcher.reset(sql);
      }
      return sql;
   }

   /**
    * @param sql The sql text
    * @param bindVariables The bind variables, if any
    */
   public QueryRecord(String sql, Object... bindVariablesLocal) {
      if (sql == null) {
         throw new IllegalArgumentException("sql can not be null");
      }
      this.date = new Date();
      this.sql = replaceBindValues(sql);
      this.bindVariables = new Object[bindVariablesLocal.length];
      System.arraycopy(bindVariablesLocal, 0, bindVariables, 0, bindVariables.length);

      for (int i = 0; i < bindVariables.length; i++) {
         Object obj = bindVariables[i];
         if (obj != null) {
            if (obj instanceof String) {
               String str = (String) obj;
               if (str.length() > 80) {
                  bindVariables[i] = str.substring(0, 80);
               }
            } else if (!(obj instanceof Date || obj instanceof Integer || obj instanceof Long || obj instanceof Double)) {
               bindVariables[i] = "binary type";
            } else {
               bindVariables[i] = obj.toString();
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
   public Object[] getBindVariables() {
      return bindVariables;
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
