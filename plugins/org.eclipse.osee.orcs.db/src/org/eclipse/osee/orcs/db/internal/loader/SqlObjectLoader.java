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
package org.eclipse.osee.orcs.db.internal.loader;

import java.util.concurrent.CancellationException;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.ResultObjectDescription;
import org.eclipse.osee.orcs.core.ds.TxOrcsData;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.loader.processor.AbstractLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.ArtifactLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.AttributeLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.BranchLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.DynamicLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.RelationLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.TransactionLoadProcessor;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.Id4JoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class SqlObjectLoader {

   private final BranchLoadProcessor branchProcessor;
   private final TransactionLoadProcessor txProcessor;
   private final ArtifactLoadProcessor artifactProcessor;
   private final AttributeLoadProcessor attributeProcessor;
   private final RelationLoadProcessor relationProcessor;
   private final DynamicLoadProcessor dynamicProcessor;

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final SqlHandlerFactory handlerFactory;

   public SqlObjectLoader(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, SqlHandlerFactory handlerFactory, OrcsObjectFactory objectFactory, DynamicLoadProcessor dynamicProcessor, AttributeTypes attributeTypes) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.handlerFactory = handlerFactory;
      this.dynamicProcessor = dynamicProcessor;

      artifactProcessor = new ArtifactLoadProcessor(objectFactory);
      attributeProcessor = new AttributeLoadProcessor(logger, objectFactory, attributeTypes);
      relationProcessor = new RelationLoadProcessor(logger, objectFactory);
      branchProcessor = new BranchLoadProcessor(objectFactory);
      txProcessor = new TransactionLoadProcessor(objectFactory);
   }

   public SqlHandlerFactory getFactory() {
      return handlerFactory;
   }

   public JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   private void checkCancelled(HasCancellation cancellation) throws CancellationException {
      if (cancellation != null) {
         cancellation.checkForCancelled();
      }
   }

   private boolean isAttributeLoadingAllowed(LoadLevel level) {
      return level != LoadLevel.ARTIFACT_DATA && level != LoadLevel.RELATION_DATA;
   }

   private boolean isRelationLoadingAllowed(LoadLevel level) {
      return level != LoadLevel.ARTIFACT_DATA && level != LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA;
   }

   private void writeSql(Criteria criteria, LoadSqlContext context)  {
      context.clear();
      SqlHandler<?> handler = handlerFactory.createHandler(criteria);
      AbstractSqlWriter writer = new LoadSqlWriter(logger, joinFactory, jdbcClient, context);
      writer.build(handler);
   }

   public void loadArtifacts(HasCancellation cancellation, LoadDataHandler handler, Id4JoinQuery join, CriteriaOrcsLoad criteria, LoadSqlContext loadContext, int fetchSize)  {
      logger.trace("Sql Artifact Load - artifactJoinQuery[%s] loadSqlContext[%s]", join, loadContext);
      try {
         if (!join.isEmpty()) {
            join.store();
            criteria.setQueryId(join.getQueryId());

            loadArtifacts(cancellation, handler, criteria, loadContext, fetchSize);

         } else {
            logger.trace("Sql Artifact Load - artifactJoinQuery was empty - skipping load - loadSqlContext[%s]",
               loadContext);
         }
      } finally {
         join.close();
      }
   }

   public void loadBranches(HasCancellation cancellation, LoadDataHandler handler, QuerySqlContext context, int fetchSize)  {
      logger.trace("Sql Branch Load - loadContext[%s] fetchSize[%s]", context, fetchSize);
      checkCancelled(cancellation);

      LoadDescription description = createDescription(context.getSession(), context.getOptions());
      handler.onLoadDescription(description);

      OrcsDataHandler<BranchData> branchHandler = asBranchHandler(handler);
      load(branchProcessor, branchHandler, context, fetchSize);
   }

   public void loadTransactions(HasCancellation cancellation, LoadDataHandler handler, QuerySqlContext context, int fetchSize)  {
      logger.trace("Sql Transaction Load - loadContext[%s] fetchSize[%s]", context, fetchSize);
      checkCancelled(cancellation);

      LoadDescription description = createDescription(context.getSession(), context.getOptions());
      handler.onLoadDescription(description);

      OrcsDataHandler<TxOrcsData> txHandler = asTransactionHandler(handler);
      load(txProcessor, txHandler, context, fetchSize);
   }

   public void loadDynamicObjects(HasCancellation cancellation, LoadDataHandler handler, QuerySqlContext context, int fetchSize)  {
      logger.trace("Sql Transaction Load - loadContext[%s] fetchSize[%s]", context, fetchSize);
      checkCancelled(cancellation);

      Options options = context.getOptions();
      options.put("sql", context.getSql());
      options.put("parameters", context.getParameters().toString());
      options.put("result.descriptor", context.getObjectDescription());
      LoadDescription description = createDescription(context.getSession(), options, context.getObjectDescription());
      handler.onLoadDescription(description);

      load(dynamicProcessor, handler, context, fetchSize);

      options.remove("result.descriptor");
   }

   private void loadArtifacts(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, LoadSqlContext loadContext, int fetchSize)  {
      checkCancelled(cancellation);
      loadDescription(handler, loadContext);

      checkCancelled(cancellation);
      loadArtifacts(handler, criteria.getArtifactCriteria(), loadContext, fetchSize);

      checkCancelled(cancellation);
      loadAttributes(handler, criteria.getAttributeCriteria(), loadContext, fetchSize);

      checkCancelled(cancellation);
      loadRelations(handler, criteria.getRelationCriteria(), loadContext, fetchSize);
   }

   protected void loadDescription(LoadDataHandler builder, final LoadSqlContext loadContext)  {
      OrcsSession session = loadContext.getSession();
      Options options = loadContext.getOptions();
      BranchId branch = loadContext.getBranch();

      TransactionId transactionLoaded;
      if (OptionsUtil.isHeadTransaction(options)) {
         transactionLoaded = loadHeadTransactionId(branch);
      } else {
         transactionLoaded = OptionsUtil.getFromTransaction(options);
      }

      LoadDescription description = createDescription(session, options, branch, transactionLoaded);
      builder.onLoadDescription(description);
   }

   protected void loadArtifacts(LoadDataHandler handler, Criteria criteria, LoadSqlContext loadContext, int fetchSize)  {
      OrcsDataHandler<ArtifactData> artHandler = asArtifactHandler(handler);
      writeSql(criteria, loadContext);
      load(artifactProcessor, artHandler, loadContext, fetchSize);
   }

   protected void loadAttributes(LoadDataHandler handler, Criteria criteria, LoadSqlContext loadContext, int fetchSize)  {
      LoadLevel loadLevel = OptionsUtil.getLoadLevel(loadContext.getOptions());
      if (isAttributeLoadingAllowed(loadLevel)) {
         OrcsDataHandler<AttributeData> attrHandler = asAttributeHandler(handler);
         writeSql(criteria, loadContext);
         load(attributeProcessor, attrHandler, loadContext, fetchSize);
      }
   }

   protected void loadRelations(LoadDataHandler handler, Criteria criteria, LoadSqlContext loadContext, int fetchSize)  {
      LoadLevel loadLevel = OptionsUtil.getLoadLevel(loadContext.getOptions());
      if (isRelationLoadingAllowed(loadLevel)) {
         OrcsDataHandler<RelationData> relHandler = asRelationHandler(handler);
         writeSql(criteria, loadContext);
         load(relationProcessor, relHandler, loadContext, fetchSize);
      }
   }

   protected TransactionId loadHeadTransactionId(BranchId branch)  {
      String sql = "SELECT max(transaction_id) FROM osee_tx_details WHERE branch_id = ?";
      return getJdbcClient().fetch(TransactionId.SENTINEL, sql, branch);
   }

   protected <H> void load(AbstractLoadProcessor<H> processor, H handler, SqlContext loadContext, int fetchSize)  {
      try {
         for (AbstractJoinQuery join : loadContext.getJoins()) {
            join.store();
         }

         long startTime = System.currentTimeMillis();
         try (JdbcStatement chStmt = getJdbcClient().getStatement()) {
            chStmt.runPreparedQuery(fetchSize, loadContext.getSql(), loadContext.getParameters().toArray());

            String processorName = null;
            if (logger.isTraceEnabled()) {
               processorName = processor.getClass().getSimpleName();
               logger.trace("Sql Artifact Load [%s] - [%s] fetchSize[%s] context[%s] ", Lib.getElapseString(startTime),
                  processorName, fetchSize, loadContext);
               startTime = System.currentTimeMillis();
            }

            int rowCount = processor.processResultSet(handler, chStmt, loadContext.getOptions());

            if (logger.isTraceEnabled()) {
               logger.trace("Sql Artifact Load [%s] - [%s] processed [%d] rows", Lib.getElapseString(startTime),
                  processorName, rowCount);
            }
         }
      } finally {
         for (AbstractJoinQuery join : loadContext.getJoins()) {
            try {
               join.close();
            } catch (Exception ex) {
               // Do nothing
            }
         }
      }
   }

   private static LoadDescription createDescription(final OrcsSession session, final Options options) {
      return createDescription(session, options, null, TransactionId.SENTINEL, null);
   }

   private static LoadDescription createDescription(final OrcsSession session, final Options options, final ResultObjectDescription data) {
      return createDescription(session, options, null, TransactionId.SENTINEL, data);
   }

   private static LoadDescription createDescription(final OrcsSession session, final Options options, final BranchId branch, final TransactionId transactionLoaded) {
      return createDescription(session, options, branch, transactionLoaded, null);
   }

   private static LoadDescription createDescription(final OrcsSession session, final Options options, final BranchId branch, final TransactionId transactionLoaded, final ResultObjectDescription data) {
      return new LoadDescription() {

         @Override
         public OrcsSession getSession() {
            return session;
         }

         @Override
         public Options getOptions() {
            return options;
         }

         @Override
         public BranchId getBranch() {
            return branch;
         }

         @Override
         public TransactionId getTransaction() {
            return transactionLoaded;
         }

         @Override
         public boolean isMultiBranch() {
            return getBranch() == null;
         }

         @Override
         public ResultObjectDescription getObjectDescription() {
            return data;
         }
      };
   }

   private static OrcsDataHandler<ArtifactData> asArtifactHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<ArtifactData>() {

         @Override
         public void onData(ArtifactData data)  {
            handler.onData(data);
         }
      };
   }

   private static OrcsDataHandler<AttributeData> asAttributeHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<AttributeData>() {

         @Override
         public void onData(AttributeData data)  {
            handler.onData(data);
         }
      };
   }

   private static OrcsDataHandler<RelationData> asRelationHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<RelationData>() {

         @Override
         public void onData(RelationData data)  {
            handler.onData(data);
         }
      };
   }

   private static OrcsDataHandler<BranchData> asBranchHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<BranchData>() {

         @Override
         public void onData(BranchData data)  {
            handler.onData(data);
         }
      };
   }

   private static OrcsDataHandler<TxOrcsData> asTransactionHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<TxOrcsData>() {

         @Override
         public void onData(TxOrcsData data)  {
            handler.onData(data);
         }
      };
   }

}
