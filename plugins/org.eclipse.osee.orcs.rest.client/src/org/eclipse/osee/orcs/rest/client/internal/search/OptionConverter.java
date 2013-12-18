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
package org.eclipse.osee.orcs.rest.client.internal.search;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.OptionVisitor;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.orcs.rest.client.internal.search.PredicateFactoryImpl.RestSearchOptions;
import org.eclipse.osee.orcs.rest.model.search.SearchFlag;

/**
 * @author Roberto E. Escobar
 */
public class OptionConverter implements OptionVisitor, RestSearchOptions {
   private final List<SearchFlag> flags = new LinkedList<SearchFlag>();
   private TokenDelimiterMatch delimiter;

   private StringBuilder buffer;

   public void accept(QueryOption[] options) {
      reset();
      for (QueryOption option : options) {
         option.accept(this);
      }
      if (delimiter == null) {
         delimiter = TokenDelimiterMatch.ANY;
      }
   }

   private void reset() {
      flags.clear();
      buffer = null;
   }

   @Override
   public void asCaseType(CaseType option) {
      if (option.isCaseSensitive()) {
         flags.add(SearchFlag.MATCH_CASE);
      }
   }

   @Override
   public void asTokenOrderType(TokenOrderType option) {
      if (option.isMatchOrder()) {
         flags.add(SearchFlag.MATCH_TOKEN_ORDER);
      }
   }

   @Override
   public void asMatchTokenCountType(MatchTokenCountType option) {
      if (option.isMatchTokenCount()) {
         flags.add(SearchFlag.MATCH_TOKEN_COUNT);
      }
   }

   @Override
   public void asTokenDelimiterMatch(TokenDelimiterMatch option) {
      if (buffer == null) {
         buffer = new StringBuilder();
      }
      buffer.append(option.name());
   }

   @Override
   public List<SearchFlag> getFlags() {
      return flags;
   }

   @Override
   public TokenDelimiterMatch getDelimiter() {
      return delimiter;
   }

};