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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
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
import com.google.common.collect.MapMaker;

/**
 * @author John Misinco
 */
public class ArtifactProviderImpl implements ArtifactProvider {

   private final OrcsApi oseeApi;
   private final ApplicationContext context;
   private final Graph graph;
   private final ConcurrentMap<ReadableArtifact, ReadableArtifact> parentCache;
   private final ArtifactSanitizer sanitizer;

   public ArtifactProviderImpl(OrcsApi oseeApi, ApplicationContext context) {
      this.oseeApi = oseeApi;
      this.context = context;
      this.graph = oseeApi.getGraph(context);
      this.parentCache = new MapMaker().initialCapacity(500).expiration(30, TimeUnit.MINUTES).makeMap();
      sanitizer = new ArtifactSanitizer(this);
   }

   protected QueryFactory getFactory() {
      return oseeApi.getQueryFactory(context);
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
      return getArtifactByGuid(branch, token.getGuid());
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
      return sanitizer.sanitizeArtifact(getFactory().fromBranch(branch).andGuidsOrHrids(guid).getResults().getOneOrNull());
   }

   @Override
   public List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> filtered;
      System.out.println("begin getSearchResults: " + new Date().toString());

      IAttributeType type = nameOnly ? CoreAttributeTypes.Name : QueryBuilder.ANY_ATTRIBUTE_TYPE;

      QueryBuilder builder = getFactory().fromBranch(branch);
      builder.and(type, StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, searchPhrase);
      List<Match<ReadableArtifact, ReadableAttribute<?>>> results = builder.getMatches().getList();

      System.out.println("end1 getSearchResults: " + new Date().toString());
      filtered = sanitizer.sanitizeSearchResults(results);

      System.out.println("end2 getSearchResults: " + new Date().toString());
      return filtered;
   }

   @Override
   public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return sanitizer.sanitizeArtifacts(graph.getRelatedArtifacts(art, relationTypeSide));
   }

   @Override
   public ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      return sanitizer.sanitizeArtifact(graph.getRelatedArtifact(art, relationTypeSide));
   }

   @Override
   public ReadableArtifact getParent(ReadableArtifact art) throws OseeCoreException {
      ReadableArtifact parent = null;
      if (parentCache.containsKey(art)) {
         parent = parentCache.get(art);
      } else {
         parent = getRelatedArtifact(art, CoreRelationTypes.Default_Hierarchical__Parent);
         if (parent != null) {
            parentCache.put(art, parent);
         }
      }
      return sanitizer.sanitizeArtifact(parent);
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

}
