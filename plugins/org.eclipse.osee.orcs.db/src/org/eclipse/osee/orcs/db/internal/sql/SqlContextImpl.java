/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.DataPostProcessor;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class SqlContextImpl<T extends Options, P extends DataPostProcessor<?>> implements SqlContext<T, P> {

   private String sql;
   private final List<Object> parameters = new ArrayList<Object>();
   private final List<AbstractJoinQuery> joinTables = new ArrayList<AbstractJoinQuery>();
   private final List<P> processors = new ArrayList<P>();

   private final String sessionId;
   private final T options;

   public SqlContextImpl(String sessionId, T options) {
      this.sessionId = sessionId;
      this.options = options;
   }

   @Override
   public T getOptions() {
      return options;
   }

   @Override
   public String getSessionId() {
      return sessionId;
   }

   @Override
   public void setSql(String sql) {
      this.sql = sql;
   }

   @Override
   public String getSql() {
      return sql;
   }

   @Override
   public List<Object> getParameters() {
      return parameters;
   }

   @Override
   public List<AbstractJoinQuery> getJoins() {
      return joinTables;
   }

   @Override
   public List<P> getPostProcessors() {
      return processors;
   }

   @Override
   public void clear() {
      setSql(null);
      parameters.clear();
      joinTables.clear();
      processors.clear();
   }

   @Override
   public String toString() {
      return "SqlContextImpl [sql=" + sql + ", parameters=" + parameters + ", joinTables=" + joinTables + ", processors=" + processors + ", sessionId=" + sessionId + ", options=" + options + "]";
   }

}
