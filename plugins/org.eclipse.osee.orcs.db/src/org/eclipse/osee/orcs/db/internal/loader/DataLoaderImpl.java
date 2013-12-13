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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
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

public class DataLoaderImpl implements DataLoader {

   private final Collection<Integer> attributeIds = new HashSet<Integer>();
   private final Collection<IAttributeType> attributeTypes = new HashSet<IAttributeType>();

   private final Collection<Integer> relationIds = new HashSet<Integer>();
   private final Collection<IRelationType> relationTypes = new HashSet<IRelationType>();

   private final Log logger;
   private AbstractLoadExecutor loadExecutor;
   private final Options options;

   private final OrcsSession session;
   private final IOseeBranch branch;
   private final BranchCache branchCache;
   private final SqlObjectLoader sqlLoader;

   public DataLoaderImpl(Log logger, AbstractLoadExecutor loadExecutor, Options options, OrcsSession session, IOseeBranch branch, BranchCache branchCache, SqlObjectLoader sqlLoader) {
      this.logger = logger;
      this.loadExecutor = loadExecutor;
      this.options = options;
      this.session = session;
      this.branch = branch;
      this.branchCache = branchCache;
      this.sqlLoader = sqlLoader;
   }

   public DataLoaderImpl(Log logger, Collection<Integer> artifactIds, Options options, OrcsSession session, IOseeBranch branch, BranchCache branchCache, SqlObjectLoader sqlLoader) {
      this.logger = logger;
      this.options = options;
      this.session = session;
      this.branch = branch;
      this.branchCache = branchCache;
      this.sqlLoader = sqlLoader;

      withArtifactIds(artifactIds);
   }

   public DataLoaderImpl(Log logger, Options options, OrcsSession session, IOseeBranch branch, BranchCache branchCache, SqlObjectLoader sqlLoader, Collection<String> artifactIds) {
      this.logger = logger;
      this.options = options;
      this.session = session;
      this.branch = branch;
      this.branchCache = branchCache;
      this.sqlLoader = sqlLoader;

      withArtifactGuids(artifactIds);
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
   public DataLoader fromTransaction(int transactionId) {
      OptionsUtil.setFromTransaction(getOptions(), transactionId);
      return this;
   }

   @Override
   public int getFromTransaction() {
      return OptionsUtil.getFromTransaction(getOptions());
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
         new LoadExecutor(sqlLoader, sqlLoader.getDatabaseService(), branchCache, session, branch, artifactIds);
      return this;
   }

   private DataLoader withArtifactGuids(Collection<String> artifactGuids) {
      loadExecutor =
         new UuidsLoadExecutor(sqlLoader, sqlLoader.getDatabaseService(), branchCache, session, branch, artifactGuids);
      return this;
   }

   @Override
   public DataLoader withAttributeTypes(IAttributeType... attributeType) throws OseeCoreException {
      return withAttributeTypes(Arrays.asList(attributeType));
   }

   @Override
   public DataLoader withAttributeTypes(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      this.attributeTypes.addAll(attributeTypes);
      return this;
   }

   @Override
   public DataLoader withRelationTypes(IRelationType... relationType) throws OseeCoreException {
      return withRelationTypes(Arrays.asList(relationType));
   }

   @Override
   public DataLoader withRelationTypes(Collection<? extends IRelationType> relationTypes) throws OseeCoreException {
      this.relationTypes.addAll(relationTypes);
      return this;
   }

   @Override
   public DataLoader withAttributeIds(int... attributeIds) throws OseeCoreException {
      return withAttributeIds(toCollection(attributeIds));
   }

   @Override
   public DataLoader withAttributeIds(Collection<Integer> attributeIds) throws OseeCoreException {
      this.attributeIds.addAll(attributeIds);
      return this;
   }

   @Override
   public DataLoader withRelationIds(int... relationIds) throws OseeCoreException {
      return withRelationIds(toCollection(relationIds));
   }

   @Override
   public DataLoader withRelationIds(Collection<Integer> relationIds) throws OseeCoreException {
      this.relationIds.addAll(relationIds);
      return this;
   }

   private Collection<Integer> toCollection(int... ids) {
      Set<Integer> toReturn = new HashSet<Integer>();
      for (Integer id : ids) {
         toReturn.add(id);
      }
      return toReturn;
   }

   private <T> Collection<T> copy(Collection<T> source) {
      Collection<T> toReturn = new HashSet<T>();
      for (T item : source) {
         toReturn.add(item);
      }
      return toReturn;
   }

   ////////////////////// EXECUTE METHODS
   @Override
   public void load(LoadDataHandler handler) throws OseeCoreException {
      load(null, handler);
   }

   @Override
   public void load(HasCancellation cancellation, LoadDataHandler handler) throws OseeCoreException {
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
         OseeExceptions.wrapAndThrow(saveException);
      }
   }

   private CriteriaOrcsLoad createCriteria() {
      CriteriaArtifact artifactCriteria = new CriteriaArtifact();
      CriteriaAttribute attributeCriteria = new CriteriaAttribute(copy(attributeIds), copy(attributeTypes));
      CriteriaRelation relationCriteria = new CriteriaRelation(copy(relationIds), copy(relationTypes));
      return new CriteriaOrcsLoad(artifactCriteria, attributeCriteria, relationCriteria);
   }

   private void determineLoadExecutor() throws OseeCoreException {
      if (loadExecutor == null) {
         throw new OseeArgumentException("Either artifacts ID or Query Context must be specified");
      }
   }

}
