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
package org.eclipse.osee.orcs.db.internal.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.QueryOptions;

/**
 * @author Roberto E. Escobar
 */
public class SqlContext {

   private final List<Object> parameters = new ArrayList<Object>();
   private final List<AbstractJoinQuery> joinTables = new ArrayList<AbstractJoinQuery>();
   private String sql;
   private int fetchSize = 100;

   private final String sessionId;
   private final QueryOptions options;

   public SqlContext(String sessionId, QueryOptions options) {
      this.sessionId = sessionId;
      this.options = options;
   }

   public String getSessionId() {
      return sessionId;
   }

   public void setSql(String sql) {
      this.sql = sql;
   }

   public String getSql() {
      return sql;
   }

   public List<Object> getParameters() {
      return parameters;
   }

   public List<AbstractJoinQuery> getJoins() {
      return joinTables;
   }

   public QueryOptions getOptions() {
      return options;
   }

   public int getFetchSize() {
      return fetchSize;
   }

   public void setFetchSize(int fetchSize) {
      this.fetchSize = fetchSize;
   }

   public void clear() {
      setSql(null);
      parameters.clear();
      joinTables.clear();
   }

}
