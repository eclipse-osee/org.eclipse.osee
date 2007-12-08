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
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Robert A. Fisher
 */
public class Query {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(Query.class);

   /**
    * Builds a collection of items from an SQL statement from the basic DBConnection.
    * 
    * @param collection The collection to add the objects to.
    * @param sql The SQL statement to use to acquire a ResultSet.
    * @param processor The RsetProcessor used for providing and validating items.
    * @param <A> The type of object being placed into the collection.
    * @throws SQLException
    */
   public static <A extends Object> void acquireCollection(Collection<A> collection, RsetProcessor<A> processor, String sql, Object... data) throws SQLException {
      acquireCollection(collection, ConnectionHandler.runPreparedQuery(sql, data), processor);
   }

   /**
    * Builds a collection of items from an SQL statement from the basic DBConnection.
    * 
    * @param collection The collection to add the objects to.
    * @param sql The SQL statement to use to acquire a ResultSet.
    * @param processor The RsetProcessor used for providing and validating items.
    * @param <A> The type of object being placed into the collection.
    * @throws SQLException
    */
   public static <A extends Object> void acquireCollection(Collection<A> collection, String sql, RsetProcessor<A> processor) throws SQLException {
      ConnectionHandlerStatement chStmt = ConnectionHandler.runPreparedQuery(100, sql);
      acquireCollection(collection, chStmt, processor);
   }

   private static <A extends Object> void acquireCollection(Collection<A> collection, ConnectionHandlerStatement chStmt, RsetProcessor<A> processor) throws SQLException {
      A item;
      try {
         while (chStmt.next()) {
            try {
               item = processor.process(chStmt.getRset());
               if (processor.validate(item)) collection.add(item);
            } catch (IllegalStateException ex) {
               logger.log(Level.SEVERE, "Encountered Exception when trying to acquire a collection.", ex);
            }
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * Burns exactly one value from a database sequence and returns the value.
    * 
    * @param connection The connection to use for performing the query.
    * @param sequence The name of the sequence to burn the value from.
    * @return The next value that was available from the sequence.
    * @throws SQLException See {@link Statement#executeQuery(java.lang.String)}
    */
   public static int getNextSeqVal(Connection connection, String sequence) throws SQLException {
      return (int) OseeSequenceManager.getInstance().getNextSequence(sequence);
   }

   /**
    * Replaces all of the '?' characters with ':#' values, where # is in incrementing integer value starting at 1.
    * 
    * @param sql The sql string to perform the replacement on.
    */
   public static String replaceBindValues(String sql) {
      int count = 1;

      Matcher matcher = Pattern.compile("\\?").matcher(sql);
      while (matcher.find()) {
         sql = matcher.replaceFirst(":" + count++);
         matcher.reset(sql);
      }
      return sql;
   }

   public static int getInt(String columnName, String query, Object... data) throws SQLException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(query, data);

         if (chStmt.next()) {
            int value = chStmt.getRset().getInt(columnName);
            if (chStmt.next()) {
               throw new IllegalStateException("More than one value returned");
            }
            return value;
         }
         throw new IllegalArgumentException("No value returned");
      } finally {
         DbUtil.close(chStmt);
      }
   }
}
