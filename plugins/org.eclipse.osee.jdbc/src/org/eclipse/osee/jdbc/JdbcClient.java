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
package org.eclipse.osee.jdbc;

import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public interface JdbcClient {

   JdbcDbType getDatabaseType();

   JdbcClientConfig getConfig();

   JdbcStatement getStatement();

   JdbcStatement getStatement(int resultSetType, int resultSetConcurrency);

   void runQuery(JdbcProcessor processor, String query, Object... data);

   int runBatchUpdate(String query, Iterable<Object[]> dataList);

   int runPreparedUpdate(String query, Object... data);

   <T> T runPreparedQueryFetchObject(T defaultValue, String query, Object... data);

   Map<String, String> getStatistics();

   //////////  QUESTIONABLE? MAYBE ONLY FOR TX SUPPORT
   JdbcConnection getConnection();

   JdbcStatement getStatement(JdbcConnection connection, boolean autoClose);

   JdbcStatement getStatement(JdbcConnection connection);

   int runPreparedUpdate(JdbcConnection connection, String query, Object... data);

   int runBatchUpdate(JdbcConnection connection, String query, Iterable<Object[]> dataList);

   <T> T runPreparedQueryFetchObject(JdbcConnection connection, T defaultValue, String query, Object... data);

   //////////////////////////////////////////////////

   void runTransaction(JdbcTransaction transaction);

   void initSchema(JdbcSchemaOptions options, JdbcSchemaResource... schemaResources);

   void initSchema(JdbcSchemaOptions options, Iterable<JdbcSchemaResource> schemaResources);

   long getNextSequence(String sequenceName);

   void invalidateSequences();

}