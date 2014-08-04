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