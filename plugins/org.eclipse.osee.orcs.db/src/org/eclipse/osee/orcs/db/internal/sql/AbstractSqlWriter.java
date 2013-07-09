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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.CharJoinQuery;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataPostProcessor;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.SqlProvider;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractSqlWriter<O extends Options> {

   protected static final String AND_WITH_NEWLINES = "\n AND \n";

   private final StringBuilder output = new StringBuilder();
   private final List<String> tableEntries = new ArrayList<String>();
   private final List<WithClause> withClauses = new ArrayList<WithClause>();
   private final SqlAliasManager aliasManager = new SqlAliasManager();

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final SqlProvider sqlProvider;
   private final SqlContext<O, ? extends DataPostProcessor<?>> context;

   public AbstractSqlWriter(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, SqlContext<O, ? extends DataPostProcessor<?>> context) {
      this.logger = logger;
      this.dbService = dbService;
      this.sqlProvider = sqlProvider;
      this.context = context;
   }

   public void build(SqlHandler<?, O>... handlers) throws OseeCoreException {
      build(Arrays.asList(handlers));
   }

   public void build(List<SqlHandler<?, O>> handlers) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(handlers, "SqlHandlers");
      output.delete(0, output.length());

      computeTables(handlers);
      computeWithClause(handlers);

      writeWithClause();
      writeSelect(handlers);
      write("\n FROM \n");
      writeTables();
      write("\n WHERE \n");
      writePredicates(handlers);
      writeGroupAndOrder();

      context.setSql(toString());

      if (logger.isTraceEnabled()) {
         logger.trace("Sql Writer - [%s]", context);
      }
   }

   private void writeWithClause() throws OseeCoreException {
      if (Conditions.hasValues(withClauses)) {
         write("WITH ");
         int size = withClauses.size();
         for (int i = 0; i < size; i++) {
            WithClause clause = withClauses.get(i);
            write(clause.getGeneratedAlias());
            write(" as (");
            write(clause.getEntry());
            write(")");
            if (i + 1 < size) {
               write(", \n");
            }
         }
         write("\n");
      }
   }

   public String addWithClause(WithClause clause) {
      String alias = getNextAlias(clause);
      clause.setGeneratedAlias(alias);
      withClauses.add(clause);
      return alias;
   }

   protected void computeWithClause(List<SqlHandler<?, O>> handlers) throws OseeCoreException {
      for (SqlHandler<?, O> handler : handlers) {
         handler.addWithTables(this);
      }
   }

   public String getNextAlias(AliasEntry table) {
      return getAliasManager().getNextAlias(table);
   }

   protected SqlAliasManager getAliasManager() {
      return aliasManager;
   }

   public SqlContext<O, ? extends DataPostProcessor<?>> getContext() {
      return context;
   }

   protected abstract void writeSelect(List<SqlHandler<?, O>> handlers) throws OseeCoreException;

   public abstract String getTxBranchFilter(String txsAlias) throws OseeCoreException;

   protected abstract void writeGroupAndOrder() throws OseeCoreException;

   protected void writeTables() throws OseeCoreException {
      int size = tableEntries.size();
      for (int i = 0; i < size; i++) {
         write(tableEntries.get(i));
         if (i + 1 < size) {
            write(", ");
         }
      }
   }

   protected void computeTables(List<SqlHandler<?, O>> handlers) throws OseeCoreException {
      for (SqlHandler<?, O> handler : handlers) {
         handler.addTables(this);
      }
   }

   protected void writePredicates(List<SqlHandler<?, O>> handlers) throws OseeCoreException {
      int size = handlers.size();
      for (int index = 0; index < size; index++) {
         SqlHandler<?, O> handler = handlers.get(index);
         boolean modified = handler.addPredicates(this);
         if (modified && index + 1 < size) {
            writeAndLn();
         }
      }
      removeDanglingSeparator(AND_WITH_NEWLINES);
   }

   public void writeAndLn() throws OseeCoreException {
      write(AND_WITH_NEWLINES);
   }

   private void removeDanglingSeparator(String token) {
      int length = output.length();
      int index = output.lastIndexOf(token);
      if (index == length - token.length()) {
         output.delete(index, length);
      }
   }

   public List<String> getAliases(TableEnum table) {
      return getAliasManager().getAliases(table);
   }

   public void addTable(String table) {
      tableEntries.add(table);
   }

   public String addTable(AliasEntry table) {
      String alias = getAliasManager().getNextAlias(table);
      tableEntries.add(String.format("%s %s", table.getEntry(), alias));
      return alias;
   }

   @SuppressWarnings("unused")
   public void write(String data, Object... params) throws OseeCoreException {
      if (params != null && params.length > 0) {
         output.append(String.format(data, params));
      } else {
         output.append(data);
      }
   }

   public void addParameter(Object data) {
      getContext().getParameters().add(data);
   }

   private void addJoin(AbstractJoinQuery join) {
      getContext().getJoins().add(join);
   }

   public CharJoinQuery writeCharJoin(Collection<String> ids) {
      CharJoinQuery joinQuery = JoinUtility.createCharJoinQuery(dbService, context.getSession().getGuid());
      for (String id : ids) {
         joinQuery.add(id);
      }
      addJoin(joinQuery);
      return joinQuery;
   }

   public IdJoinQuery writeIdJoin(Collection<Integer> ids) {
      IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery(dbService, context.getSession().getGuid());
      for (Integer id : ids) {
         joinQuery.add(id);
      }
      addJoin(joinQuery);
      return joinQuery;
   }

   public O getOptions() {
      return getContext().getOptions();
   }

   @SuppressWarnings("unchecked")
   public void addPostProcessor(DataPostProcessor<?> processor) {
      List<DataPostProcessor<?>> processors = (List<DataPostProcessor<?>>) getContext().getPostProcessors();
      processors.add(processor);
   }

   protected String getSqlHint() throws OseeCoreException {
      String hint = Strings.EMPTY_STRING;
      if (!Conditions.hasValues(withClauses)) {
         hint = sqlProvider.getSql(OseeSql.QUERY_BUILDER);
      }
      return hint;
   }

   @Override
   public String toString() {
      return output.toString();
   }

}
