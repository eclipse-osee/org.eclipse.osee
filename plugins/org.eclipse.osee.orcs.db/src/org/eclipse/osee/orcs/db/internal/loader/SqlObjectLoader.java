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
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsDataHandler;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.loader.data.VersionObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.processor.ArtifactLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.AttributeLoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.LoadProcessor;
import org.eclipse.osee.orcs.db.internal.loader.processor.RelationLoadProcessor;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class SqlObjectLoader {

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

   public void loadArtifacts(HasCancellation cancellation, LoadDataHandler builder, ArtifactJoinQuery join, CriteriaOrcsLoad criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      logger.trace("Sql Artifact Load - artifactJoinQuery[%s] loadSqlContext[%s]", join, loadContext);
      if (!join.isEmpty()) {
         try {
            join.store();
            criteria.setQueryId(join.getQueryId());
            loadArtifacts(cancellation, builder, criteria, loadContext, fetchSize);
         } finally {
            join.delete();
         }
      } else {
         logger.trace("Sql Artifact Load - artifactJoinQuery was empty - skipping load - loadSqlContext[%s]",
            loadContext);
      }
   }

   public void loadArtifacts(HasCancellation cancellation, LoadDataHandler builder, CriteriaOrcsLoad criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      checkCancelled(cancellation);
      loadArtifacts(builder, criteria.getArtifactCriteria(), loadContext, fetchSize);

      checkCancelled(cancellation);
      loadAttributes(builder, criteria.getAttributeCriteria(), loadContext, fetchSize);

      checkCancelled(cancellation);
      loadRelations(builder, criteria.getRelationCriteria(), loadContext, fetchSize);
   }

   protected void loadArtifacts(LoadDataHandler builder, Criteria criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      ArtifactDataHandler artHandler = builder.getArtifactDataHandler();
      writeSql(criteria, loadContext);
      load(artifactProcessor, artHandler, loadContext, fetchSize);
   }

   protected void loadAttributes(LoadDataHandler builder, Criteria criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      LoadLevel loadLevel = OptionsUtil.getLoadLevel(loadContext.getOptions());
      if (isAttributeLoadingAllowed(loadLevel)) {
         AttributeDataHandler attrHandler = builder.getAttributeDataHandler();
         writeSql(criteria, loadContext);
         load(attributeProcessor, attrHandler, loadContext, fetchSize);
      }
   }

   protected void loadRelations(LoadDataHandler builder, Criteria criteria, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
      LoadLevel loadLevel = OptionsUtil.getLoadLevel(loadContext.getOptions());
      if (isRelationLoadingAllowed(loadLevel)) {
         RelationDataHandler relHandler = builder.getRelationDataHandler();
         writeSql(criteria, loadContext);
         load(relationProcessor, relHandler, loadContext, fetchSize);
      }
   }

   protected <D extends OrcsData, F extends VersionObjectFactory, H extends OrcsDataHandler<D>> void load(LoadProcessor<D, F, H> processor, H handler, LoadSqlContext loadContext, int fetchSize) throws OseeCoreException {
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
}
