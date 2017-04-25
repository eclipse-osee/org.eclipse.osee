/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.eclipse.osee.jdbc.internal.JdbcUtil;

/**
 * This API is used in cases that involve large numbers of rows and thus many calls to addToBatch. The non-variable
 * argument list methods are provided for better performance so that the parameters are not wrapped in an array each
 * time.
 *
 * @author Ryan D. Brooks
 */
public class OseePreparedStatement implements Closeable {
   private final PreparedStatement preparedStatement;
   private final int batchIncrementSize;
   private final boolean autoClose;
   private final JdbcConnection connection;
   private int currentBatchSize;
   private int resultCount;
   private int size;

   public OseePreparedStatement(PreparedStatement preparedStatement, int batchIncrementSize, JdbcConnection connection, boolean autoClose) {
      this.preparedStatement = preparedStatement;
      this.batchIncrementSize = batchIncrementSize;
      this.autoClose = autoClose;
      this.connection = connection;
   }

   public void addToBatch(Object... params) {
      for (int i = 0; i < params.length; i++) {
         JdbcUtil.setInputParameterForStatement(preparedStatement, params[i], i + 1);
      }
      finishAddToBatch();
   }

   public void addToBatch(Object param1) {
      JdbcUtil.setInputParameterForStatement(preparedStatement, param1, 1);
      finishAddToBatch();
   }

   public void addToBatch(Object param1, Object param2) {
      JdbcUtil.setInputParameterForStatement(preparedStatement, param1, 1);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param2, 2);
      finishAddToBatch();
   }

   public void addToBatch(Object param1, Object param2, Object param3) {
      JdbcUtil.setInputParameterForStatement(preparedStatement, param1, 1);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param2, 2);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param3, 3);
      finishAddToBatch();
   }

   public void addToBatch(Object param1, Object param2, Object param3, Object param4) {
      JdbcUtil.setInputParameterForStatement(preparedStatement, param1, 1);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param2, 2);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param3, 3);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param4, 4);
      finishAddToBatch();
   }

   public void addToBatch(Object param1, Object param2, Object param3, Object param4, Object param5) {
      JdbcUtil.setInputParameterForStatement(preparedStatement, param1, 1);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param2, 2);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param3, 3);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param4, 4);
      JdbcUtil.setInputParameterForStatement(preparedStatement, param5, 5);
      finishAddToBatch();
   }

   private void finishAddToBatch() {
      try {
         preparedStatement.addBatch();
         preparedStatement.clearParameters();
         currentBatchSize++;
         size++;
         if (currentBatchSize >= batchIncrementSize) {
            int[] updates = preparedStatement.executeBatch();
            resultCount += JdbcUtil.calculateBatchUpdateResults(updates);
            currentBatchSize = 0;
         }
      } catch (SQLException ex) {
         throw JdbcException.newJdbcException(ex);
      }
   }

   public int execute() {
      try {
         if (currentBatchSize == 0) {
            return resultCount;
         } else {
            return resultCount += JdbcUtil.calculateBatchUpdateResults(preparedStatement.executeBatch());
         }
      } catch (SQLException ex) {
         throw JdbcException.newJdbcException(ex);
      } finally {
         close();
      }
   }

   public int size() {
      return size;
   }

   @Override
   public void close() {
      JdbcUtil.close(preparedStatement);
      if (autoClose) {
         connection.close();
      }
   }
}