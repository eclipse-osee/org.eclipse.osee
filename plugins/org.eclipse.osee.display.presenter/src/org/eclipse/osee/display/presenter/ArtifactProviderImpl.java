/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.Graph;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author John Misinco
 */
public class ArtifactProviderImpl implements ArtifactProvider {

   private final OrcsApi oseeApi;

   private final ApplicationContext context;

   private final Graph graph;

   protected static final List<String> notAllowed = new ArrayList<String>();
   static {
      notAllowed.add("Technical Approaches");
      notAllowed.add("Technical Performance Parameters");
      notAllowed.add("Recent Imports");
      notAllowed.add("Test");
      notAllowed.add("Interface Requirements");
      notAllowed.add("Test Procedures");
   }

   public ArtifactProviderImpl(OrcsApi oseeApi, ApplicationContext context) {
      this.oseeApi = oseeApi;
      this.context = context;
      this.graph = oseeApi.getGraph(context);
   }

   protected QueryFactory getFactory() {
      return oseeApi.getQueryFactory(context);
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
      return sanitizeResult(getArtifactByGuid(branch, token.getGuid()));
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
      return sanitizeResult(getFactory().fromBranch(branch).andGuidsOrHrids(guid).getResults().getOneOrNull());
   }

   @Override
   public List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> filtered;

      IAttributeType type = nameOnly ? CoreAttributeTypes.Name : QueryBuilder.ANY_ATTRIBUTE_TYPE;

      QueryBuilder builder = getFactory().fromBranch(branch);
      builder.and(type, StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, searchPhrase);
      List<Match<ReadableArtifact, ReadableAttribute<?>>> results = builder.getMatches().getList();

      filtered = sanitizeSearchResults(results);

      return filtered;
   }

   private List<Match<ReadableArtifact, ReadableAttribute<?>>> sanitizeSearchResults(List<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize) {
      int numProcessors = Runtime.getRuntime().availableProcessors();
      int partitionSize = toSanitize.size() / numProcessors;
      int remainder = toSanitize.size() % numProcessors;
      ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
      int startIndex = 0;
      int endIndex = 0;
      List<ResultsCallable> workers = new LinkedList<ResultsCallable>();
      List<Match<ReadableArtifact, ReadableAttribute<?>>> toReturn =
         new LinkedList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      for (int i = 0; i < numProcessors; i++) {
         startIndex = endIndex;
         endIndex = startIndex + partitionSize;
         if (i == 0) {
            endIndex += remainder;
         }
         ResultsCallable worker = new ResultsCallable(toSanitize.subList(startIndex, endIndex));
         workers.add(worker);
      }

      try {
         for (Future<List<Match<ReadableArtifact, ReadableAttribute<?>>>> future : executor.invokeAll(workers)) {
            toReturn.addAll(future.get());
         }
      } catch (Exception ex) {
         //
      }

      return toReturn;

   }

   private ReadableArtifact sanitizeResult(ReadableArtifact result) throws OseeCoreException {
      boolean allowed = true;
      ReadableArtifact current = result;
      while (current != null) {
         if (notAllowed.contains(current.getName())) {
            allowed = false;
            break;
         }
         current = graph.getParent(current);
      }
      if (allowed) {
         return result;
      } else {
         return null;
      }
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return graph.getRelatedArtifacts(art, relationTypeSide);
   }

   @Override
   public ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return graph.getRelatedArtifact(art, relationTypeSide);
   }

   @Override
   public ReadableArtifact getParent(ReadableArtifact art) throws OseeCoreException {
      return getRelatedArtifact(art, CoreRelationTypes.Default_Hierarchical__Parent);
   }

   @Override
   public Collection<RelationType> getValidRelationTypes(ReadableArtifact art) throws OseeCoreException {
      Collection<IRelationTypeSide> existingRelationTypes = graph.getExistingRelationTypes(art);
      Set<RelationType> toReturn = new HashSet<RelationType>();
      for (IRelationTypeSide side : existingRelationTypes) {
         toReturn.add(graph.getFullRelationType(side));
      }
      return toReturn;
   }

   private class ResultsCallable implements Callable<List<Match<ReadableArtifact, ReadableAttribute<?>>>> {

      List<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize;

      public ResultsCallable(List<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize) {
         this.toSanitize = toSanitize;
      }

      @Override
      public List<Match<ReadableArtifact, ReadableAttribute<?>>> call() throws Exception {
         Iterator<Match<ReadableArtifact, ReadableAttribute<?>>> it = toSanitize.iterator();
         while (it.hasNext()) {
            Match<ReadableArtifact, ReadableAttribute<?>> match = it.next();
            ReadableArtifact matchedArtifact = match.getItem();
            if (sanitizeResult(matchedArtifact) == null) {
               it.remove();
            }
         }
         return toSanitize;
      }
   }
}
