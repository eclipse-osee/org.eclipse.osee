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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaArtifact;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaAttribute;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation;
import org.eclipse.osee.orcs.db.internal.loader.executors.AbstractLoadExecutor;
import org.eclipse.osee.orcs.db.internal.loader.executors.LoadExecutor;
import org.eclipse.osee.orcs.db.internal.loader.executors.UuidsLoadExecutor;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

public class DataLoaderImpl implements DataLoader {

   private final Collection<Integer> attributeIds = new HashSet<>();
   private final Collection<AttributeTypeId> attributeTypes = new HashSet<>();

   private final Collection<Integer> relationIds = new HashSet<>();
   private final Collection<IRelationType> relationTypes = new HashSet<>();

   private final Log logger;
   private AbstractLoadExecutor loadExecutor;
   private final Options options;

   private final OrcsSession session;
   private final BranchId branchId;
   private final SqlObjectLoader sqlLoader;
   private final SqlJoinFactory joinFactory;

   public DataLoaderImpl(Log logger, AbstractLoadExecutor loadExecutor, Options options, OrcsSession session, BranchId branch, SqlObjectLoader sqlLoader, SqlJoinFactory joinFactory) {
      this(logger, options, session, branch, sqlLoader, joinFactory);
      this.loadExecutor = loadExecutor;
   }

   public DataLoaderImpl(Log logger, Collection<Integer> artifactIds, Options options, OrcsSession session, BranchId branch, SqlObjectLoader sqlLoader, SqlJoinFactory joinFactory) {
      this(logger, options, session, branch, sqlLoader, joinFactory);
      withArtifactIds(artifactIds);
   }

   public DataLoaderImpl(Log logger, Options options, OrcsSession session, BranchId branch, SqlObjectLoader sqlLoader, Collection<String> artifactIds, SqlJoinFactory joinFactory) {
      this(logger, options, session, branch, sqlLoader, joinFactory);
      withArtifactGuids(artifactIds);
   }

   private DataLoaderImpl(Log logger, Options options, OrcsSession session, BranchId branch, SqlObjectLoader sqlLoader, SqlJoinFactory joinFactory) {
      this.logger = logger;
      this.options = options;
      this.session = session;
      this.branchId = branch;
      this.sqlLoader = sqlLoader;
      this.joinFactory = joinFactory;
   }

   public DataLoader resetToDefaults() {
      OptionsUtil.reset(getOptions());

      attributeIds.clear();
      attributeTypes.clear();

      relationIds.clear();
      relationTypes.clear();
      return this;
   }

   private Options getOptions() {
      return options;
   }

   @Override
   public DataLoader setOptions(Options source) {
      getOptions().setFrom(source);
      return this;
   }

   @Override
   public DataLoader includeDeletedArtifacts() {
      includeDeletedArtifacts(true);
      return this;
   }

   @Override
   public DataLoader includeDeletedArtifacts(boolean enabled) {
      OptionsUtil.setIncludeDeletedArtifacts(getOptions(), enabled);
      return this;
   }

   @Override
   public DataLoader includeDeletedAttributes() {
      return includeDeletedAttributes(true);
   }

   @Override
   public DataLoader includeDeletedAttributes(boolean enabled) {
      OptionsUtil.setIncludeDeletedAttributes(getOptions(), enabled);
      return this;
   }

   @Override
   public DataLoader includeDeletedRelations() {
      return includeDeletedRelations(true);
   }

   @Override
   public DataLoader includeDeletedRelations(boolean enabled) {
      OptionsUtil.setIncludeDeletedRelations(getOptions(), enabled);
      return this;
   }

   @Override
   public boolean areDeletedArtifactsIncluded() {
      return OptionsUtil.areDeletedArtifactsIncluded(getOptions());
   }

   @Override
   public boolean areDeletedAttributesIncluded() {
      return OptionsUtil.areDeletedAttributesIncluded(getOptions());
   }

   @Override
   public boolean areDeletedRelationsIncluded() {
      return OptionsUtil.areDeletedRelationsIncluded(getOptions());
   }

   @Override
   public DataLoader fromTransaction(TransactionId transactionId) {
      OptionsUtil.setFromTransaction(getOptions(), transactionId);
      return this;
   }

   @Override
   public DataLoader fromBranchView(ArtifactId viewId) {
      OptionsUtil.setFromBranchView(getOptions(), viewId);
      return this;
   }

   @Override
   public DataLoader fromHeadTransaction() {
      OptionsUtil.setHeadTransaction(getOptions());
      return this;
   }

   @Override
   public boolean isHeadTransaction() {
      return !OptionsUtil.isHistorical(getOptions());
   }

   @Override
   public LoadLevel getLoadLevel() {
      return OptionsUtil.getLoadLevel(getOptions());
   }

   @Override
   public DataLoader withLoadLevel(LoadLevel loadLevel) {
      OptionsUtil.setLoadLevel(getOptions(), loadLevel);
      return this;
   }

   private DataLoader withArtifactIds(Collection<Integer> artifactIds) {
      loadExecutor =
         new LoadExecutor(sqlLoader, sqlLoader.getJdbcClient(), joinFactory, session, branchId, artifactIds);
      return this;
   }

   private DataLoader withArtifactGuids(Collection<String> artifactGuids) {
      loadExecutor =
         new UuidsLoadExecutor(sqlLoader, sqlLoader.getJdbcClient(), joinFactory, session, branchId, artifactGuids);
      return this;
   }

   @Override
   public DataLoader withAttributeTypes(AttributeTypeId... attributeType) {
      return withAttributeTypes(Arrays.asList(attributeType));
   }

   @Override
   public DataLoader withAttributeTypes(Collection<? extends AttributeTypeId> attributeTypes) {
      this.attributeTypes.addAll(attributeTypes);
      return this;
   }

   @Override
   public DataLoader withRelationTypes(IRelationType... relationType) {
      return withRelationTypes(Arrays.asList(relationType));
   }

   @Override
   public DataLoader withRelationTypes(Collection<? extends IRelationType> relationTypes) {
      this.relationTypes.addAll(relationTypes);
      return this;
   }

   @Override
   public DataLoader withAttributeIds(int... attributeIds) {
      return withAttributeIds(toCollection(attributeIds));
   }

   @Override
   public DataLoader withAttributeIds(Collection<Integer> attributeIds) {
      this.attributeIds.addAll(attributeIds);
      return this;
   }

   @Override
   public DataLoader withRelationIds(int... relationIds) {
      return withRelationIds(toCollection(relationIds));
   }

   @Override
   public DataLoader withRelationIds(Collection<Integer> relationIds) {
      this.relationIds.addAll(relationIds);
      return this;
   }

   private Collection<Integer> toCollection(int... ids) {
      Set<Integer> toReturn = new HashSet<>();
      for (Integer id : ids) {
         toReturn.add(id);
      }
      return toReturn;
   }

   private <T> Collection<T> copy(Collection<T> source) {
      Collection<T> toReturn = new HashSet<>();
      for (T item : source) {
         toReturn.add(item);
      }
      return toReturn;
   }

   ////////////////////// EXECUTE METHODS
   @Override
   public void load(LoadDataHandler handler) {
      load(null, handler);
   }

   @Override
   public void load(HasCancellation cancellation, LoadDataHandler handler) {
      long startTime = 0;

      final Options options = getOptions().clone();
      final CriteriaOrcsLoad criteria = createCriteria();
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
         logger.trace("%s [start] - [%s] [%s]", getClass().getSimpleName(), criteria, options);
      }
      determineLoadExecutor();
      Exception saveException = null;
      try {
         handler.onLoadStart();
         loadExecutor.load(cancellation, handler, criteria, options);
      } catch (Exception ex) {
         saveException = ex;
      } finally {
         try {
            handler.onLoadEnd();
         } catch (OseeCoreException ex) {
            if (saveException == null) {
               saveException = ex;
            }
         } finally {
            if (logger.isTraceEnabled()) {
               logger.trace("%s [%s] - loaded [%s] [%s]", getClass().getSimpleName(), Lib.getElapseString(startTime),
                  criteria, options);
            }
         }
      }
      if (saveException != null) {
         OseeCoreException.wrapAndThrow(saveException);
      }
   }

   private CriteriaOrcsLoad createCriteria() {
      CriteriaArtifact artifactCriteria = new CriteriaArtifact();
      CriteriaAttribute attributeCriteria = new CriteriaAttribute(copy(attributeIds), copy(attributeTypes));
      CriteriaRelation relationCriteria = new CriteriaRelation(copy(relationIds), copy(relationTypes));
      return new CriteriaOrcsLoad(artifactCriteria, attributeCriteria, relationCriteria);
   }

   private void determineLoadExecutor() {
      if (loadExecutor == null) {
         throw new OseeArgumentException("Either artifacts ID or Query Context must be specified");
      }
   }

}
