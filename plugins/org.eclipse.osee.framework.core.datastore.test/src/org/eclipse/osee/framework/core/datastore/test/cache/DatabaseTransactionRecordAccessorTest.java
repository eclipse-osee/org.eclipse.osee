/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.datastore.test.cache;

import org.eclipse.osee.framework.core.datastore.cache.DatabaseTransactionRecordAccessor;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case For {@link DatabaseTransactionRecordAccessor}
 * 
 * @author Roberto E. Escobar
 */
public class DatabaseTransactionRecordAccessorTest {

   @Ignore
   @Test
   public void testTX() {

   }

   //   private final class MockConnectionHandlerStatement extends ConnectionHandlerStatement {
   //
   //      @Override
   //      public void runPreparedQuery(int fetchSize, String query, Object... data) throws OseeDataStoreException {
   //         runPreparedQuery(query, data);
   //      }
   //
   //      @Override
   //      public void runPreparedQuery(String query, Object... data) throws OseeDataStoreException {
   //
   //      }
   //
   //      @Override
   //      public int getInt(String columnName) throws OseeDataStoreException {
   //         return super.getInt(columnName);
   //      }
   //
   //      @Override
   //      public String getString(String columnName) throws OseeDataStoreException {
   //         return super.getString(columnName);
   //      }
   //
   //      @Override
   //      public Timestamp getTimestamp(String columnName) throws OseeDataStoreException {
   //         return super.getTimestamp(columnName);
   //      }
   //
   //   }
   //
   //   private final class Handler implements IConnectionHandlerStatementProvider {
   //
   //      @Override
   //      public ConnectionHandlerStatement getStatement() {
   //         return null;
   //      }
   //   }
}
