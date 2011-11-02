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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractQueryPostProcessor extends QueryPostProcessor implements PartitionFactory<ReadableArtifact, Match<ReadableArtifact, ReadableAttribute<?>>> {

   private CriteriaAttributeKeyword criteria;
   private final ExecutorAdmin executorAdmin;
   private List<Future<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>>> futures;

   protected AbstractQueryPostProcessor(Log logger, ExecutorAdmin executorAdmin) {
      super(logger);
      this.executorAdmin = executorAdmin;
   }

   public void setCriteria(CriteriaAttributeKeyword criteria) {
      this.criteria = criteria;
   }

   protected CaseType getCaseType() {
      return criteria.getMatch();
   }

   protected Collection<? extends IAttributeType> getTypes() {
      return criteria.getTypes();
   }

   protected String getQuery() {
      return criteria.getValue();
   }

   @Override
   public List<Match<ReadableArtifact, ReadableAttribute<?>>> innerCall() throws Exception {
      Conditions.checkNotNull(getItemsToProcess(), "Query first pass results");

      futures = WorkUtility.partitionAndScheduleWork(executorAdmin, this, getItemsToProcess());

      checkForCancelled();

      List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();
      for (Future<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>> future : futures) {
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

   protected abstract Tagger getTagger(ReadableAttribute<?> attribute) throws OseeCoreException;

   @Override
   public Callable<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>> createWorker(Collection<ReadableArtifact> toProcess) {
      return new PostProcessorWorker(toProcess);
   }

   private class PostProcessorWorker extends CancellableCallable<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>> {

      private final Collection<ReadableArtifact> artifacts;

      public PostProcessorWorker(Collection<ReadableArtifact> artifacts) {
         this.artifacts = artifacts;
      }

      @Override
      public Collection<Match<ReadableArtifact, ReadableAttribute<?>>> call() throws Exception {
         List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
            new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();

         Map<ReadableAttribute<?>, List<MatchLocation>> matchedAttributes = null;
         for (ReadableArtifact artifact : artifacts) {
            checkForCancelled();
            for (ReadableAttribute<?> attribute : getAttributes(artifact)) {
               checkForCancelled();
               try {
                  if (getTypes().contains(attribute.getAttributeType())) {
                     checkForCancelled();
                     Tagger tagger = getTagger(attribute);
                     if (tagger != null) {
                        checkForCancelled();
                        List<MatchLocation> locations = tagger.find(attribute, getQuery(), getCaseType(), true);
                        if (!locations.isEmpty()) {
                           if (matchedAttributes == null) {
                              matchedAttributes = new HashMap<ReadableAttribute<?>, List<MatchLocation>>();
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

      @SuppressWarnings("unchecked")
      private <T> List<ReadableAttribute<T>> getAttributes(ReadableArtifact artifact) throws OseeCoreException {
         List<ReadableAttribute<T>> toReturn;

         Collection<? extends IAttributeType> toCheck = getTypes();
         if (toCheck != null && !toCheck.isEmpty()) {
            toReturn = new ArrayList<ReadableAttribute<T>>();
            for (IAttributeType attributeType : toCheck) {
               for (ReadableAttribute<?> attr : artifact.getAttributes(attributeType)) {
                  toReturn.add((ReadableAttribute<T>) attr);
                  checkForCancelled();
               }
            }
         } else {
            toReturn = artifact.getAttributes();
         }
         return toReturn;
      }
   }
}
