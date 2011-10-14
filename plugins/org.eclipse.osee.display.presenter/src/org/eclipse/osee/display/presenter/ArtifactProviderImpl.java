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
import java.util.List;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author John Misinco
 */
public class ArtifactProviderImpl implements ArtifactProvider {

   protected final QueryFactory factory;

   protected static final List<String> notAllowed = new ArrayList<String>();
   static {
      notAllowed.add("Technical Approaches");
      notAllowed.add("Technical Performance Parameters");
      notAllowed.add("Recent Imports");
      notAllowed.add("Test");
      notAllowed.add("Interface Requirements");
      notAllowed.add("Test Procedures");
   }

   public ArtifactProviderImpl(QueryFactory factory) {
      this.factory = factory;
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
      return sanitizeResult(factory.fromArtifact(branch, token).build(LoadLevel.FULL).getOneOrNull());
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
      return sanitizeResult(factory.fromGuidOrHrid(branch, guid).build(LoadLevel.FULL).getOneOrNull());
   }

   @Override
   public List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();
      IAttributeType type;

      if (nameOnly) {
         type = CoreAttributeTypes.Name;
      } else {
         type = QueryBuilder.ANY_ATTRIBUTE_TYPE;
      }

      ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> resultSet =
         factory.fromBranch(branch).and(type, StringOperator.TOKENIZED_ANY_ORDER, CaseType.IGNORE_CASE, searchPhrase).buildMatches(
            LoadLevel.FULL);
      for (Match<ReadableArtifact, ReadableAttribute<?>> match : resultSet.getList()) {
         ReadableArtifact matchedArtifact = match.getItem();
         if (sanitizeResult(matchedArtifact) != null) {
            results.add(match);
         }
      }
      return results;
   }

   private ReadableArtifact sanitizeResult(ReadableArtifact result) {
      boolean allowed = true;
      ReadableArtifact current = result;
      while (current != null) {
         if (notAllowed.contains(current.getName())) {
            allowed = false;
            break;
         }
         current = current.hasParent() ? current.getParent() : null;
      }
      if (allowed) {
         return result;
      } else {
         return null;
      }
   }
}
