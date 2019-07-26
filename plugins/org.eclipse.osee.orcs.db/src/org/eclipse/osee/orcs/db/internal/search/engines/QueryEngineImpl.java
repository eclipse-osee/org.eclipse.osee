/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.search.QueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.SelectiveArtifactSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {
   private final QueryCallableFactory artifactQueryEngineFactory;
   private final QuerySqlContextFactory branchSqlContextFactory;
   private final QuerySqlContextFactory txSqlContextFactory;
   private final QueryCallableFactory allQueryEngineFactory;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;
   private final SqlObjectLoader sqlObjectLoader;
   private final ArtifactTypes artifactTypes;
   private final AttributeTypes attributeTypes;
   private final KeyValueStore keyValue;
   private final SqlHandlerFactory handlerFactory;

   public QueryEngineImpl(QueryCallableFactory artifactQueryEngineFactory, QuerySqlContextFactory branchSqlContextFactory, QuerySqlContextFactory txSqlContextFactory, QueryCallableFactory allQueryEngineFactory, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, SqlHandlerFactory handlerFactory, SqlObjectLoader sqlObjectLoader, OrcsTypes orcsTypes, KeyValueStore keyValue) {
      this.artifactQueryEngineFactory = artifactQueryEngineFactory;
      this.branchSqlContextFactory = branchSqlContextFactory;
      this.txSqlContextFactory = txSqlContextFactory;
      this.allQueryEngineFactory = allQueryEngineFactory;
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
      this.sqlObjectLoader = sqlObjectLoader;
      this.artifactTypes = orcsTypes.getArtifactTypes();
      this.attributeTypes = orcsTypes.getAttributeTypes();
      this.keyValue = keyValue;
      this.handlerFactory = handlerFactory;
   }

   @Override
   public int getArtifactCount(QueryData queryData) {
      if (isPostProcessRequired(queryData)) {
         return artifactQueryEngineFactory.getArtifactCount(queryData);
      }
      Long[] count = new Long[1];
      selectiveArtifactLoad(queryData, stmt -> count[0] = stmt.getLong("art_count"));
      return count[0].intValue();
   }

   private boolean isPostProcessRequired(QueryData queryData) {
      return queryData.hasCriteriaType(CriteriaAttributeKeywords.class);
   }

   @Override
   public void runArtifactQuery(QueryData queryData, LoadDataHandler handler) throws Exception {
      artifactQueryEngineFactory.createQuery(null, queryData, handler).call();
      queryData.reset();
   }

   @Override
   public int getBranchCount(QueryData queryData) {
      return getCount(branchSqlContextFactory, queryData);
   }

   @Override
   public void runBranchQuery(QueryData queryData, List<? super Branch> branches) {
      QuerySqlContext queryContext = branchSqlContextFactory.createQueryContext(null, queryData, QueryType.SELECT);
      sqlObjectLoader.loadBranches(branches, queryContext);
      queryData.reset();
   }

   @Override
   public int getTxCount(QueryData queryData) {
      return getCount(txSqlContextFactory, queryData);
   }

   private int getCount(QuerySqlContextFactory sqlContextFactory, QueryData queryData) {
      QuerySqlContext queryContext = sqlContextFactory.createQueryContext(null, queryData, QueryType.COUNT);
      int count = sqlObjectLoader.getCount(queryContext);
      queryData.reset();
      return count;
   }

   @Override
   public void runTxQuery(QueryData queryData, List<? super TransactionReadable> txs) {
      QuerySqlContext queryContext = txSqlContextFactory.createQueryContext(null, queryData, QueryType.SELECT);
      sqlObjectLoader.loadTransactions(txs, queryContext);
      queryData.reset();
   }

   @Override
   public CancellableCallable<Integer> createQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return allQueryEngineFactory.createQuery(session, queryData, handler);
   }

   @Override
   public TupleQuery createTupleQuery() {
      return new TupleQueryImpl(jdbcClient, sqlJoinFactory, keyValue);
   }

   @Override
   public List<ArtifactToken> loadArtifactTokens(QueryData queryData) {
      List<ArtifactToken> tokens = new ArrayList<>(100);
      selectiveArtifactLoad(queryData, stmt -> tokens.add(ArtifactToken.valueOf(stmt.getLong("art_id"),
         stmt.getString("value"), queryData.getBranch(), artifactTypes.get(stmt.getLong("art_type_id")))));
      return tokens;
   }

   @Override
   public List<Map<String, Object>> asArtifactMaps(QueryData queryData) {
      List<Map<String, Object>> maps = new ArrayList<>(500);
      HashCollection<AttributeTypeToken, Object> attributes = new HashCollection<>();
      Long[] artifactId = new Long[] {Id.SENTINEL};

      Consumer<JdbcStatement> consumer = stmt -> {
         Long newArtId = stmt.getLong("art_id");
         if (artifactId[0].equals(Id.SENTINEL)) {
            artifactId[0] = newArtId;
         } else if (!artifactId[0].equals(newArtId)) {
            maps.add(createFieldMap(artifactId, attributes));
            attributes.clear();
            artifactId[0] = newArtId;
         }
         attributes.put(attributeTypes.get(stmt.getLong("attr_type_id")), stmt.getString("value"));
      };
      selectiveArtifactLoad(queryData, consumer);
      if (!artifactId[0].equals(Id.SENTINEL)) {
         maps.add(createFieldMap(artifactId, attributes));
      }
      return maps;
   }

   private Map<String, Object> createFieldMap(Long[] artifactId, HashCollection<AttributeTypeToken, Object> attributes) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("Artifact Id", artifactId[0].toString());
      map.put("Name", attributes.getValues(Name));

      List<AttributeTypeToken> attributeTypes = new ArrayList<>(attributes.keySet());
      Collections.sort(attributeTypes);
      for (AttributeTypeToken attributeType : attributeTypes) {
         List<Object> attributeValues = attributes.getValues(attributeType);
         if (attributeValues.size() == 1) {
            map.put(attributeType.getName(), attributeValues.get(0));
         } else {
            map.put(attributeType.getName(), attributeValues);
         }
      }
      return map;
   }

   @Override
   public Map<ArtifactId, ArtifactToken> loadArtifactTokenMap(QueryData queryData) {
      Map<ArtifactId, ArtifactToken> tokens = new HashMap<>(10000);
      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactToken token = ArtifactToken.valueOf(stmt.getLong("art_id"), stmt.getString("value"),
            queryData.getBranch(), artifactTypes.get(stmt.getLong("art_type_id")));
         tokens.put(token, token);
      };

      selectiveArtifactLoad(queryData, consumer);
      return tokens;
   }

   @Override
   public List<ArtifactId> loadArtifactIds(QueryData queryData) {
      List<ArtifactId> ids = new ArrayList<>(100);

      if (isPostProcessRequired(queryData)) {
         LoadDataHandlerAdapter handler = new LoadDataHandlerAdapter() {
            @Override
            public void onData(ArtifactData data) {
               ids.add(data);
            }
         };
         OptionsUtil.setLoadLevel(queryData.getOptions(), LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA);
         try {
            runArtifactQuery(queryData, handler);
            return ids;
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }

      selectiveArtifactLoad(queryData, stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))));
      return ids;
   }

   private void selectiveArtifactLoad(QueryData queryData, Consumer<JdbcStatement> consumer) {
      QueryData rootQueryData = queryData.getRootQueryData();
      new SelectiveArtifactSqlWriter(sqlJoinFactory, jdbcClient, rootQueryData).runSql(consumer, handlerFactory);
   }

   @Override
   public ApplicabilityDsQuery createApplicabilityDsQuery() {
      return new ApplicabilityDsQueryImpl(jdbcClient, sqlJoinFactory);
   }
}