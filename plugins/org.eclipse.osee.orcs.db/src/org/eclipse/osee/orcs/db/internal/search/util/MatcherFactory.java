/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.search.util;

import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.db.internal.search.tagger.StreamMatcher;

/**
 * @author Roberto E. Escobar
 */
public class MatcherFactory {

   private MatcherFactory() {
      // Static factory
   }

   public static StreamMatcher createMatcher() {
      return new SecondPassMatcher(new TokenOrderProcessorFactoryImpl());
   }

   private static final class TokenOrderProcessorFactoryImpl implements TokenOrderProcessorFactory {
      @Override
      public TokenOrderProcessor createTokenProcessor(CheckedOptions options) {
         return options.getOrderType() == QueryOption.TOKEN_MATCH_ORDER__MATCH ? new MatchTokenOrderProcessor() : new AnyTokenOrderProcessor();
      }
   }
}