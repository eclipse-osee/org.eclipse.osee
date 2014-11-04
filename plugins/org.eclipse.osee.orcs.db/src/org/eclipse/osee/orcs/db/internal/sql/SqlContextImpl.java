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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.ResultObjectDescription;

/**
 * @author Roberto E. Escobar
 */
public class SqlContextImpl implements SqlContext {

   private String sql;
   private ResultObjectDescription data;
   private final List<Object> parameters = new ArrayList<Object>();
   private final List<AbstractJoinQuery> joinTables = new ArrayList<AbstractJoinQuery>();
   private final OrcsSession session;
   private final Options options;

   public SqlContextImpl(OrcsSession session, Options options) {
      this.session = session;
      this.options = options;
   }

   @Override
   public Options getOptions() {
      return options;
   }

   @Override
   public OrcsSession getSession() {
      return session;
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
   public ResultObjectDescription getObjectDescription() {
      return data;
   }

   @Override
   public void setObjectDescription(ResultObjectDescription data) {
      this.data = data;
   }

   @Override
   public void clear() {
      setSql(null);
      setObjectDescription(null);
      parameters.clear();
      joinTables.clear();
   }

   @Override
   public String toString() {
      return "SqlContextImpl [session=" + session + ", sql=" + (Strings.isValid(sql) ? sql.replaceAll("\n", "") : "") + ", parameters=" + parameters + ", joinTables=" + joinTables + ", options=" + options + "]";
   }

}
