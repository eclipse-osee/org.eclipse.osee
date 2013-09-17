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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsDataFactory;
import org.eclipse.osee.orcs.db.internal.loader.processor.ArtifactLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.AttributeLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.BranchLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.LoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.RelationLoadProcessor;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class SqlObjectLoader {

   private final BranchLoadProcessor branchProcessor;
   private final ArtifactLoadProcessor artifactProcessor;
   private final AttributeLoadProcessor attributeProcessor;
   private final RelationLoadProcessor relationProcessor;

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final SqlProvider sqlProvider;
   private final SqlHandlerFactory handlerFactory;

   public SqlObjectLoader(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, SqlHandlerFactory handlerFactory, OrcsObjectFactory objectFactory) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.sqlProvider = sqlProvider;
      this.handlerFactory = handlerFactory;

      artifactProcessor = new ArtifactLoadProcessor(objectFactory);
      attributeProcessor = new AttributeLoadProcessor(objectFactory);
      relationProcessor = new RelationLoadProcessor(objectFactory);
      branchProcessor = new BranchLoadProcessor(objectFactory);
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   private void checkCancelled(HasCancellation cancellation) throws CancellationException {
      if (cancellation != null) {
         cancellation.checkForCancelled();
      }
   }

   private boolean isAttributeLoadingAllowed(LoadLevel level) {
      return level != LoadLevel.SHALLOW && level != LoadLevel.RELATION;
   }

   private boolean isRelationLoadingAllowed(LoadLevel level) {
      return level != LoadLevel.SHALLOW && level != LoadLevel.ATTRIBUTE;
   }

   private void writeSql(Criteria criteria, LoadSqlContext context) throws OseeCoreException {
      context.clear();
      SqlHandler<?> handler = handlerFactory.createHandler(criteria);
      AbstractSqlWriter writer = new LoadSqlWriter(logger, dbService, sqlProvider, context);
      writer.build(handler);
   }

   public void loadArtifacts(HasCancellation cancellation, LoadDataHandler handler, ArtifactJoinQuery join, CriteriaOrcsLoad criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      logger.trace("Sql Artifact Load - artifactJoinQuery[%s] loadSqlContext[%s]", join, loadContext);
      if (!join.isEmpty()) {
         try {
            join.store();
            criteria.setQueryId(join.getQueryId());

            loadArtifacts(cancellation, handler, criteria, loadContext, fetchSize);
         } finally {
            join.delete();
         }
      } else {
         logger.trace("Sql Artifact Load - artifactJoinQuery was empty - skipping load - loadSqlContext[%s]",
            loadContext);
      }
   }

   public void loadBranches(HasCancellation cancellation, LoadDataHandler handler, QuerySqlContext context, int fetchSize) throws OseeCoreException {
      logger.trace("Sql Branch Load - loadContext[%s] fetchSize[%s]", context, fetchSize);
      checkCancelled(cancellation);

      LoadDescription description = createDescription(context.getSession(), context.getOptions());
      handler.onLoadDescription(description);

      OrcsDataHandler<BranchData> branchHandler = asBranchHandler(handler);
      load(branchProcessor, branchHandler, context, fetchSize);
   }

   private void loadArtifacts(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      checkCancelled(cancellation);
      loadDescription(handler, loadContext);

      checkCancelled(cancellation);
      loadArtifacts(handler, criteria.getArtifactCriteria(), loadContext, fetchSize);

      checkCancelled(cancellation);
      loadAttributes(handler, criteria.getAttributeCriteria(), loadContext, fetchSize);

      checkCancelled(cancellation);
      loadRelations(handler, criteria.getRelationCriteria(), loadContext, fetchSize);
   }

   protected void loadDescription(LoadDataHandler builder, final LoadSqlContext loadContext) throws OseeCoreException {
      OrcsSession session = loadContext.getSession();
      Options options = loadContext.getOptions();
      IOseeBranch branch = loadContext.getBranch();

      int transactionLoaded;
      if (OptionsUtil.isHeadTransaction(options)) {
         transactionLoaded = loadHeadTransactionId(branch);
      } else {
         transactionLoaded = OptionsUtil.getFromTransaction(options);
      }

      LoadDescription description = createDescription(session, options, branch, transactionLoaded);
      builder.onLoadDescription(description);
   }

   protected void loadArtifacts(LoadDataHandler handler, Criteria criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      OrcsDataHandler<ArtifactData> artHandler = asArtifactHandler(handler);
      writeSql(criteria, loadContext);
      load(artifactProcessor, artHandler, loadContext, fetchSize);
   }

   protected void loadAttributes(LoadDataHandler handler, Criteria criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      LoadLevel loadLevel = OptionsUtil.getLoadLevel(loadContext.getOptions());
      if (isAttributeLoadingAllowed(loadLevel)) {
         OrcsDataHandler<AttributeData> attrHandler = asAttributeHandler(handler);
         writeSql(criteria, loadContext);
         load(attributeProcessor, attrHandler, loadContext, fetchSize);
      }
   }

   protected void loadRelations(LoadDataHandler handler, Criteria criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      LoadLevel loadLevel = OptionsUtil.getLoadLevel(loadContext.getOptions());
      if (isRelationLoadingAllowed(loadLevel)) {
         OrcsDataHandler<RelationData> relHandler = asRelationHandler(handler);
         writeSql(criteria, loadContext);
         load(relationProcessor, relHandler, loadContext, fetchSize);
      }
   }

   protected int loadHeadTransactionId(IOseeBranch branch) throws OseeCoreException {
      String sql = sqlProvider.getSql(OseeSql.TX_GET_MAX_AS_LARGEST_TX_WITH_BRANCH_GUID);
      return getDatabaseService().runPreparedQueryFetchObject(RelationalConstants.TRANSACTION_SENTINEL, sql,
         branch.getGuid());
   }

   protected <D extends HasLocalId, F extends OrcsDataFactory> void load(LoadProcessor<D, F> processor, OrcsDataHandler<D> handler, SqlContext loadContext, int fetchSize) throws OseeCoreException {
      try {
         for (AbstractJoinQuery join : loadContext.getJoins()) {
            join.store();
         }
         IOseeStatement chStmt = getDatabaseService().getStatement();
         long startTime = System.currentTimeMillis();
         try {
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
         } finally {
            chStmt.close();
         }
      } finally {
         for (AbstractJoinQuery join : loadContext.getJoins()) {
            try {
               join.delete();
            } catch (Exception ex) {
               // Do nothing
            }
         }
      }
   }

   private static LoadDescription createDescription(final OrcsSession session, final Options options) {
      return createDescription(session, options, null, -1);
   }

   private static LoadDescription createDescription(final OrcsSession session, final Options options, final IOseeBranch branch, final int transactionLoaded) {
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
         public IOseeBranch getBranch() {
            return branch;
         }

         @Override
         public int getTransaction() {
            return transactionLoaded;
         }

         @Override
         public boolean isMultiBranch() {
            return getBranch() == null;
         }
      };
   }

   private static OrcsDataHandler<ArtifactData> asArtifactHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<ArtifactData>() {

         @Override
         public void onData(ArtifactData data) throws OseeCoreException {
            handler.onData(data);
         }
      };
   }

   private static OrcsDataHandler<AttributeData> asAttributeHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<AttributeData>() {

         @Override
         public void onData(AttributeData data) throws OseeCoreException {
            handler.onData(data);
         }
      };
   }

   private static OrcsDataHandler<RelationData> asRelationHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<RelationData>() {

         @Override
         public void onData(RelationData data) throws OseeCoreException {
            handler.onData(data);
         }
      };
   }

   private static OrcsDataHandler<BranchData> asBranchHandler(final LoadDataHandler handler) {
      return new OrcsDataHandler<BranchData>() {

         @Override
         public void onData(BranchData data) throws OseeCoreException {
            handler.onData(data);
         }
      };
   }
}
