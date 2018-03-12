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
package org.eclipse.osee.orcs.core.internal.script.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.SelectSet;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptAssembler;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptExecutor;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptOutputHandler;
import org.eclipse.osee.orcs.core.internal.search.ArtifactQueryBuilderImpl;
import org.eclipse.osee.orcs.core.internal.search.BranchCriteriaFactory;
import org.eclipse.osee.orcs.core.internal.search.BranchQueryBuilderImpl;
import org.eclipse.osee.orcs.core.internal.search.CriteriaFactory;
import org.eclipse.osee.orcs.core.internal.search.TransactionCriteriaFactory;
import org.eclipse.osee.orcs.core.internal.search.TxQueryBuilderImpl;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.eclipse.osee.orcs.search.ArtifactQueryBuilder;
import org.eclipse.osee.orcs.search.BranchQueryBuilder;
import org.eclipse.osee.orcs.search.TxQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptAssemblerImpl implements OrcsScriptAssembler, OrcsScriptExecutor {

   private static final String SHOW_HIDDEN_FIELDS = "show.hidden.fields";
   private static final String UNKNOWN_VERSION = "0.0.0";

   private final TransactionCriteriaFactory factory1;
   private final BranchCriteriaFactory factory2;
   private final CriteriaFactory factory3;

   private final LinkedList<QueryData> queries = new LinkedList<>();
   private String version;
   private TxQueryBuilderImpl<?> txQuery;
   private ArtifactQueryBuilderImpl<?> artQuery;
   private BranchQueryBuilderImpl<?> branchQuery;

   private final DataModule dataModule;
   private final OrcsScriptOutputHandler output;

   private boolean errorDetected;

   public OrcsScriptAssemblerImpl(DataModule dataModule, OrcsTypes orcsTypes, OrcsScriptOutputHandler output) {
      super();
      this.dataModule = dataModule;
      this.output = output;
      factory1 = new TransactionCriteriaFactory();
      factory2 = new BranchCriteriaFactory();
      factory3 = new CriteriaFactory(orcsTypes.getArtifactTypes(), orcsTypes.getAttributeTypes());
   }

   private void reset() {
      errorDetected = false;
      queries.clear();
      version = UNKNOWN_VERSION;
      txQuery = null;
      artQuery = null;
      branchQuery = null;
   }

   @Override
   public void onCompileStart(OrcsScript model) {
      reset();
      output.onCompileStart(model);
   }

   @Override
   public void onCompileEnd() {
      onQueryEnd();
      output.onCompileEnd();
   }

   @Override
   public void onQueryStart() {
      queries.add(new QueryData());
   }

   @Override
   public void onQueryEnd() {
      resetTxQuery();
      resetBranchQuery();
      resetArtifactQuery();
   }

   @Override
   public void onScriptVersion(String version) {
      this.version = version;
   }

   @Override
   public TxQueryBuilder<?> newTxQuery() {
      this.txQuery = new TxQueryBuilderImpl<>(factory1, getLastQuery());
      return txQuery;
   }

   @Override
   public BranchQueryBuilder<?> newBranchQuery() {
      this.branchQuery = new BranchQueryBuilderImpl<>(factory2, getLastQuery());
      return branchQuery;
   }

   @Override
   public ArtifactQueryBuilder<?> newArtifactQuery() {
      this.artQuery = new ArtifactQueryBuilderImpl<>(factory3, getLastQuery());
      return artQuery;
   }

   @Override
   public TxQueryBuilder<?> getTxQuery() {
      return txQuery;
   }

   @Override
   public BranchQueryBuilder<?> getBranchQuery() {
      return branchQuery;
   }

   @Override
   public ArtifactQueryBuilder<?> getArtifactQuery() {
      if (artQuery == null) {
         newArtifactQuery();
      }
      return artQuery;
   }

   @Override
   public void resetTxQuery() {
      if (txQuery != null) {
         txQuery.build();
      }
      txQuery = null;
   }

   @Override
   public void resetBranchQuery() {
      if (branchQuery != null) {
         branchQuery.build();
      }
      branchQuery = null;
   }

   @Override
   public void resetArtifactQuery() {
      artQuery = null;
   }

   public String getVersion() {
      return version;
   }

   private QueryData getLastQuery() {
      return queries.getLast();
   }

   private SelectSet getLastSelect() {
      return getLastQuery().getSelectSet();
   }

   @Override
   public void addCollect(DynamicData data, long limit) {
      if (data != null) {
         SelectSet select = getLastSelect();
         if (select != null) {
            select.setLimit(limit);

            DynamicData oldData = select.getData();
            if (oldData != null) {
               DynamicObject obj = (DynamicObject) oldData;
               obj.addChild(data);
            } else {
               select.setData(data);
            }
         }
      }
   }

   @Override
   public void onError(Throwable error) {
      errorDetected = true;
      output.onError(error);
   }

   @Override
   public Object execute(OrcsSession session, Map<String, Object> parameters) {
      Object result = null;
      try {
         output.onExecutionStart(version);
         result = executionHelper(session, parameters);
      } catch (Exception ex) {
         output.onError(ex);
      } finally {
         output.onExecutionEnd();
      }
      return result;
   }

   private boolean showHiddenFields(Map<String, Object> parameters) {
      Object hiddenFields = parameters.get(SHOW_HIDDEN_FIELDS);
      return Boolean.parseBoolean(String.valueOf(hiddenFields));
   }

   private Object executionHelper(OrcsSession session, Map<String, Object> parameters) {
      int results = -1;
      if (!errorDetected) {
         boolean showHiddenFields = showHiddenFields(parameters);

         QueryEngine queryEngine = dataModule.getQueryEngine();
         for (QueryData queryData : queries) {
            try {
               OptionsUtil.setShowHiddenFields(queryData.getOptions(), showHiddenFields);
               output.onQueryStart(queryData);
               Callable<Integer> op = queryEngine.createQuery(session, queryData, output);
               results += op.call();
            } catch (Exception ex) {
               output.onError(ex);
            } finally {
               output.onQueryEnd();
            }
         }
      }
      return results;
   }

   @Override
   public int getSelectSetIndex() {
      int index = 0;
      QueryData lastQuery = getLastQuery();
      if (lastQuery != null) {
         List<SelectSet> selectSets = lastQuery.getSelectSets();
         index = selectSets.size() - 1;
      }
      return index;
   }

   protected LinkedList<QueryData> getQueries() {
      return queries;
   }

}