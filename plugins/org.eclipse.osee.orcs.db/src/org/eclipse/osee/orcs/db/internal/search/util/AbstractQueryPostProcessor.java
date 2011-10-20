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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
public abstract class AbstractQueryPostProcessor implements QueryPostProcessor {

   private final Log logger;
   private CriteriaAttributeKeyword criteria;

   protected AbstractQueryPostProcessor(Log logger) {
      super();
      this.logger = logger;
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

   @SuppressWarnings("unchecked")
   private <T> List<ReadableAttribute<T>> getAttributes(ReadableArtifact artifact) throws OseeCoreException {
      List<ReadableAttribute<T>> toReturn;

      Collection<? extends IAttributeType> toCheck = getTypes();
      if (toCheck != null && !toCheck.isEmpty()) {
         toReturn = new ArrayList<ReadableAttribute<T>>();
         for (IAttributeType attributeType : toCheck) {
            for (ReadableAttribute<?> attr : artifact.getAttributes(attributeType)) {
               toReturn.add((ReadableAttribute<T>) attr);
            }
         }
      } else {
         toReturn = artifact.getAttributes();
      }
      return toReturn;
   }

   @Override
   public List<ReadableArtifact> getMatching(List<ReadableArtifact> artifacts) throws OseeCoreException {
      List<ReadableArtifact> filtered = new ArrayList<ReadableArtifact>();
      for (ReadableArtifact artifact : artifacts) {
         for (ReadableAttribute<?> attribute : getAttributes(artifact)) {
            try {
               Tagger tagger = getTagger(attribute);
               List<MatchLocation> locations = tagger.find(attribute, getQuery(), getCaseType(), false);
               if (!locations.isEmpty()) {
                  filtered.add(artifact);
                  break;
               }
            } catch (Exception ex) {
               logger.error(ex, "Error processing: [%s]", attribute);
            }
         }
      }
      return filtered;
   }

   @Override
   public List<Match<ReadableArtifact, ReadableAttribute<?>>> getLocationMatches(List<ReadableArtifact> artifacts) throws OseeCoreException {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      Map<ReadableAttribute<?>, List<MatchLocation>> matchedAttributes = null;
      for (ReadableArtifact artifact : artifacts) {
         for (ReadableAttribute<?> attribute : getAttributes(artifact)) {
            try {
               Tagger tagger = getTagger(attribute);
               List<MatchLocation> locations = tagger.find(attribute, getQuery(), getCaseType(), true);
               if (!locations.isEmpty()) {
                  if (matchedAttributes == null) {
                     matchedAttributes = new HashMap<ReadableAttribute<?>, List<MatchLocation>>();
                  }
                  matchedAttributes.put(attribute, locations);
               }
            } catch (Exception ex) {
               logger.error(ex, "Error processing: [%s]", attribute);
            }
         }
         if (matchedAttributes != null && !matchedAttributes.isEmpty()) {
            results.add(new ArtifactMatch(artifact, matchedAttributes));
            matchedAttributes = null;
         }
      }
      return results;
   }

   protected abstract Tagger getTagger(ReadableAttribute<?> attribute) throws OseeCoreException;
}
