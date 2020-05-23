/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.sql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.ResultObjectDescription;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class SqlContextImpl implements SqlContext {

   private String sql;
   private ResultObjectDescription data;
   private final List<Object> parameters = new ArrayList<>();
   private final List<AbstractJoinQuery> joinTables = new ArrayList<>();
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
      return "SqlContextImpl [session=" + session + ", sql=" + (Strings.isValid(sql) ? sql.replaceAll("\n",
         "") : "") + ", parameters=" + parameters + ", joinTables=" + joinTables + ", options=" + options + "]";
   }

}
