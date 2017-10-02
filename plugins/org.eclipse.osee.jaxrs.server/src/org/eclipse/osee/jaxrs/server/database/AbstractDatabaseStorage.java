/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.SQL3DataType;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractDatabaseStorage<T> {

   private final Log logger;
   private final JdbcClient jdbcClient;

   public AbstractDatabaseStorage(Log logger, JdbcClient jdbcClient) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
   }

   protected Object asVarcharOrNull(String value) {
      return value != null ? value : SQL3DataType.VARCHAR;
   }

   protected abstract Object[] asInsert(T item);

   protected abstract Object[] asUpdate(T item);

   protected abstract Object[] asDelete(T item);

   protected abstract T readData(JdbcStatement chStmt);

   protected <R> R execute(Callable<R> callable) {
      try {
         return callable.call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   protected T selectOneOrNull(final String query, final Object... data) {
      return execute(select(query, data)).getOneOrNull();
   }

   protected ResultSet<T> selectItems(final String query, final Object... data) {
      return execute(select(query, data));
   }

   protected void insertItems(final String insertSql, final T... items) {
      insertItems(insertSql, Arrays.asList(items));
   }

   protected void insertItems(final String insertSql, final Iterable<T> items) {
      execute(insert(insertSql, items));
   }

   protected void deleteItems(final String deleteSql, final T... items) {
      deleteItems(deleteSql, Arrays.asList(items));
   }

   protected void deleteItems(final String deleteSql, final Iterable<T> items) {
      execute(delete(deleteSql, items));
   }

   protected void updateItems(final String insertSql, final T... items) {
      updateItems(insertSql, Arrays.asList(items));
   }

   protected void updateItems(final String updateSql, final Iterable<T> items) {
      execute(update(updateSql, items));
   }

   protected long countItems(final String countSql, final Object... data) {
      return execute(count(countSql, data));
   }

   private Callable<ResultSet<T>> select(final String query, final Object... data) {
      return new AbstractCallable<Object[], ResultSet<T>>(query, data) {

         @Override
         protected ResultSet<T> innerCall() throws Exception {
            List<T> list = new LinkedList<>();
            getJdbcClient().runQuery(stmt -> {
               T data = readData(stmt);
               list.add(data);
            }, query, data);
            return ResultSets.newResultSet(list);
         }
      };
   }

   private Callable<Long> count(final String query, final Object... data) {
      return new AbstractCallable<Object[], Long>(query, data) {

         @Override
         protected Long innerCall() throws Exception {
            return jdbcClient.fetch(-1L, query, data);
         }
      };
   }

   private Callable<Integer> insert(final String insertSql, final Iterable<T> items) {
      return new AbstractCallable<Iterable<T>, Integer>(insertSql, items) {

         @Override
         protected Integer innerCall() throws Exception {
            List<Object[]> data = new ArrayList<>();
            for (T item : items) {
               data.add(asInsert(item));
            }
            return jdbcClient.runBatchUpdate(insertSql, data);
         }
      };
   }

   private Callable<Integer> delete(final String deleteSql, final Iterable<T> items) {
      return new AbstractCallable<Iterable<T>, Integer>(deleteSql, items) {

         @Override
         protected Integer innerCall() throws Exception {
            List<Object[]> data = new ArrayList<>();
            for (T item : items) {
               data.add(asDelete(item));
            }
            return jdbcClient.runBatchUpdate(deleteSql, data);
         }
      };
   }

   private Callable<Integer> update(final String updateSql, final Iterable<T> items) {
      return new AbstractCallable<Iterable<T>, Integer>(updateSql, items) {

         @Override
         protected Integer innerCall() throws Exception {
            List<Object[]> data = new ArrayList<>();
            for (T item : items) {
               data.add(asUpdate(item));
            }
            return jdbcClient.runBatchUpdate(updateSql, data);
         }
      };
   }

   protected abstract class AbstractCallable<I, O> implements Callable<O> {

      protected final String query;
      protected final I data;

      public AbstractCallable(String query, I data) {
         super();
         this.query = query;
         this.data = data;
      }

      protected JdbcClient getJdbcClient() {
         return jdbcClient;
      }

      @Override
      public final O call() throws Exception {
         long startTime = System.currentTimeMillis();
         long endTime = startTime;
         O result = null;
         try {
            if (logger.isTraceEnabled()) {
               logger.trace("%s [start] - [%s] [%s]", getClass().getSimpleName(), query, data);
            }
            result = innerCall();
         } finally {
            endTime = System.currentTimeMillis() - startTime;
         }
         if (logger.isTraceEnabled()) {
            logger.trace("%s [finished] - [%s] [%s] [%s]", getClass().getSimpleName(), Lib.asTimeString(endTime), query,
               data);
         }
         return result;
      }

      protected abstract O innerCall() throws Exception;
   }

}