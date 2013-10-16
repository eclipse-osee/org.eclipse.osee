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
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaArtifact;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaAttribute;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation;
import org.eclipse.osee.orcs.db.internal.loader.executors.AbstractLoadExecutor;

public class DataLoaderImpl implements DataLoader {

   private final Collection<Integer> attributeIds = new HashSet<Integer>();
   private final Collection<IAttributeType> attributeTypes = new HashSet<IAttributeType>();

   private final Collection<Integer> relationIds = new HashSet<Integer>();
   private final Collection<IRelationType> relationTypes = new HashSet<IRelationType>();

   private final Log logger;
   private final AbstractLoadExecutor loadExecutor;
   private final Options options;

   public DataLoaderImpl(Log logger, AbstractLoadExecutor loadExecutor, Options options) {
      this.logger = logger;
      this.loadExecutor = loadExecutor;
      this.options = options;
   }

   @Override
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
   public DataLoader includeDeleted() {
      includeDeleted(true);
      return this;
   }

   @Override
   public DataLoader includeDeleted(boolean enabled) {
      OptionsUtil.setIncludeDeleted(getOptions(), enabled);
      return this;
   }

   @Override
   public boolean areDeletedIncluded() {
      return OptionsUtil.areDeletedIncluded(getOptions());
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
   public DataLoader headTransaction() {
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
   public DataLoader setLoadLevel(LoadLevel loadLevel) {
      OptionsUtil.setLoadLevel(getOptions(), loadLevel);
      return this;
   }

   @Override
   public DataLoader loadAttributeType(IAttributeType... attributeType) throws OseeCoreException {
      return loadAttributeTypes(Arrays.asList(attributeType));
   }

   @SuppressWarnings("unused")
   @Override
   public DataLoader loadAttributeTypes(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      this.attributeTypes.addAll(attributeTypes);
      return this;
   }

   @Override
   public DataLoader loadRelationType(IRelationType... relationType) throws OseeCoreException {
      return loadRelationTypes(Arrays.asList(relationType));
   }

   @SuppressWarnings("unused")
   @Override
   public DataLoader loadRelationTypes(Collection<? extends IRelationType> relationTypes) throws OseeCoreException {
      this.relationTypes.addAll(relationTypes);
      return this;
   }

   @Override
   public DataLoader loadAttributeLocalId(int... attributeIds) throws OseeCoreException {
      return loadAttributeLocalIds(toCollection(attributeIds));
   }

   @SuppressWarnings("unused")
   @Override
   public DataLoader loadAttributeLocalIds(Collection<Integer> attributeIds) throws OseeCoreException {
      this.attributeIds.addAll(attributeIds);
      return this;
   }

   @Override
   public DataLoader loadRelationLocalId(int... relationIds) throws OseeCoreException {
      return loadRelationLocalIds(toCollection(relationIds));
   }

   @SuppressWarnings("unused")
   @Override
   public DataLoader loadRelationLocalIds(Collection<Integer> relationIds) throws OseeCoreException {
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
   public void load(HasCancellation cancellation, LoadDataHandler handler) throws OseeCoreException {
      long startTime = 0;

      final Options options = getOptions().clone();
      final CriteriaOrcsLoad criteria = createCriteria();
      if (logger.isTraceEnabled()) {
         startTime = System.currentTimeMillis();
         logger.trace("%s [start] - [%s] [%s]", getClass().getSimpleName(), criteria, options);
      }
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
}
