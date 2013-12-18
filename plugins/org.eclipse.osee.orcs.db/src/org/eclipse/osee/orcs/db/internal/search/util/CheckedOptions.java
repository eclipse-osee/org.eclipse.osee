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
package org.eclipse.osee.orcs.db.internal.search.util;

import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.OptionVisitor;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;

/**
 * @author John Misinco
 */
public class CheckedOptions implements OptionVisitor {

   private TokenOrderType orderType;
   private CaseType caseType;
   private MatchTokenCountType countType;
   private TokenDelimiterMatch delimiter;

   public CheckedOptions() {
      initialize();
   }

   private void initialize() {
      orderType = TokenOrderType.ANY_ORDER;
      caseType = CaseType.IGNORE_CASE;
      countType = MatchTokenCountType.IGNORE_TOKEN_COUNT;
      delimiter = TokenDelimiterMatch.ANY;
   }

   public TokenOrderType getOrderType() {
      return orderType;
   }

   public CaseType getCaseType() {
      return caseType;
   }

   public MatchTokenCountType getCountType() {
      return countType;
   }

   public TokenDelimiterMatch getDelimiter() {
      return delimiter;
   }

   public void accept(QueryOption... options) {
      initialize();
      for (QueryOption option : options) {
         option.accept(this);
      }
   }

   @Override
   public void asCaseType(CaseType option) {
      caseType = option;
   }

   @Override
   public void asTokenOrderType(TokenOrderType option) {
      orderType = option;
   }

   @Override
   public void asMatchTokenCountType(MatchTokenCountType option) {
      countType = option;
   }

   @Override
   public void asTokenDelimiterMatch(TokenDelimiterMatch delimiter) {
      this.delimiter = delimiter;
   }
};