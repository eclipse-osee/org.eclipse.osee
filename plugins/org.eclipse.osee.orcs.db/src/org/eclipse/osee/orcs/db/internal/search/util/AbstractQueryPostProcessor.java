/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.WorkUtility;
import org.eclipse.osee.executor.admin.WorkUtility.PartitionFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractQueryPostProcessor extends QueryPostProcessor implements PartitionFactory<ArtifactReadable, Match<ArtifactReadable, AttributeReadable<?>>> {

   private final QueryOptions options;
   private final CriteriaAttributeKeywords criteria;
   private final ExecutorAdmin executorAdmin;
   private List<Future<Collection<Match<ArtifactReadable, AttributeReadable<?>>>>> futures;

   protected AbstractQueryPostProcessor(Log logger, ExecutorAdmin executorAdmin, CriteriaAttributeKeywords criteria, QueryOptions options) {
      super(logger);
      this.executorAdmin = executorAdmin;
      this.criteria = criteria;
      this.options = options;
   }

   protected Collection<? extends IAttributeType> getTypes() {
      return criteria.getTypes();
   }

   protected String getQuery() {
      return criteria.getValues().iterator().next();
   }

   protected QueryOptions getOptions() {
      return options;
   }

   @Override
   public List<Match<ArtifactReadable, AttributeReadable<?>>> innerCall() throws Exception {
      Conditions.checkNotNull(getItemsToProcess(), "Query first pass results");

      futures = WorkUtility.partitionAndScheduleWork(executorAdmin, this, getItemsToProcess());

      checkForCancelled();

      List<Match<ArtifactReadable, AttributeReadable<?>>> results =
         new ArrayList<Match<ArtifactReadable, AttributeReadable<?>>>();
      for (Future<Collection<Match<ArtifactReadable, AttributeReadable<?>>>> future : futures) {
         results.addAll(future.get());
         checkForCancelled();
      }
      return results;
   }

   @Override
   public void setCancel(boolean isCancelled) {
      super.setCancel(isCancelled);
      if (futures != null) {
         for (Future<?> future : futures) {
            future.cancel(true);
         }
      }
   }

   protected abstract Tagger getTagger(String taggerId) throws OseeCoreException;

   @Override
   public Callable<Collection<Match<ArtifactReadable, AttributeReadable<?>>>> createWorker(Collection<ArtifactReadable> toProcess) {
      return new PostProcessorWorker(toProcess);
   }

   private class PostProcessorWorker extends CancellableCallable<Collection<Match<ArtifactReadable, AttributeReadable<?>>>> {

      private final Collection<ArtifactReadable> artifacts;

      public PostProcessorWorker(Collection<ArtifactReadable> artifacts) {
         this.artifacts = artifacts;
      }

      @Override
      public Collection<Match<ArtifactReadable, AttributeReadable<?>>> call() throws Exception {
         List<Match<ArtifactReadable, AttributeReadable<?>>> results =
            new ArrayList<Match<ArtifactReadable, AttributeReadable<?>>>();

         AttributeTypes attributeTypes = getAttributeTypes();

         DeletionFlag includeDeleted = getOptions().getIncludeDeleted();
         Map<AttributeReadable<?>, List<MatchLocation>> matchedAttributes = null;
         for (ArtifactReadable artifact : artifacts) {
            checkForCancelled();
            for (AttributeReadable<Object> attribute : getAttributes(artifact, includeDeleted)) {
               checkForCancelled();
               try {
                  if (getTypes().contains(attribute.getAttributeType())) {
                     checkForCancelled();

                     String taggerId = attributeTypes.getTaggerId(attribute.getAttributeType());
                     Tagger tagger = getTagger(taggerId);
                     if (tagger != null) {
                        checkForCancelled();
                        List<MatchLocation> locations = tagger.find(attribute, getQuery(), true, criteria.getOptions());
                        if (!locations.isEmpty()) {
                           if (matchedAttributes == null) {
                              matchedAttributes = new HashMap<AttributeReadable<?>, List<MatchLocation>>();
                           }
                           matchedAttributes.put(attribute, locations);
                        }
                     }
                  }
               } catch (Exception ex) {
                  getLogger().error(ex, "Error processing: [%s]", attribute);
               }
            }
            if (matchedAttributes != null && !matchedAttributes.isEmpty()) {
               results.add(new ArtifactMatch(artifact, matchedAttributes));
               matchedAttributes = null;
            }
         }
         return results;
      }

      private List<AttributeReadable<Object>> getAttributes(ArtifactReadable artifact, DeletionFlag includeDeleted) throws OseeCoreException {
         List<AttributeReadable<Object>> toReturn;

         Collection<? extends IAttributeType> toCheck = getTypes();
         if (toCheck != null && !toCheck.isEmpty()) {
            toReturn = new ArrayList<AttributeReadable<Object>>();
            for (IAttributeType attributeType : toCheck) {
               for (AttributeReadable<Object> attr : artifact.getAttributes(attributeType, includeDeleted)) {
                  toReturn.add(attr);
                  checkForCancelled();
               }
            }
         } else {
            toReturn = artifact.getAttributes(includeDeleted);
         }
         return toReturn;
      }
   }
}
