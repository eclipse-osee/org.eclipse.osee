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

import java.io.InputStream;
import java.util.List;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.db.internal.search.tagger.StreamMatcher;

/**
 * @author John Misinco
 */
public class SecondPassMatcher implements StreamMatcher {

   private final TokenOrderProcessorFactory processorFactory;

   public SecondPassMatcher(TokenOrderProcessorFactory processorFactory) {
      this.processorFactory = processorFactory;
   }

   private String normalizeCase(QueryOption caseType, String token) {
      return caseType == QueryOption.CASE__MATCH ? token : token.toLowerCase();
   }

   @Override
   public List<MatchLocation> findInStream(InputStream inputStream, String toSearch, boolean findAllMatchLocations, QueryOption... options) {
      CheckedOptions checkedOptions = new CheckedOptions(options);

      TokenOrderProcessor processor = processorFactory.createTokenProcessor(checkedOptions);

      parseSearchString(processor, toSearch, checkedOptions);
      searchStream(processor, inputStream, checkedOptions, findAllMatchLocations);

      return processor.getLocations();
   }

   private void parseSearchString(TokenOrderProcessor processor, String toSearch, CheckedOptions options) {
      SecondPassScanner toSearchScanner = new SecondPassScanner(toSearch, options.getDelimiter());
      try {
         while (toSearchScanner.hasNext()) {
            String next = toSearchScanner.next();
            next = normalizeCase(options.getCaseType(), next);
            processor.acceptTokenToMatch(next);
         }
      } finally {
         toSearchScanner.close();
      }
   }

   private void searchStream(TokenOrderProcessor processor, InputStream inputStream, CheckedOptions options, boolean findAllMatchLocations) {
      SecondPassScanner inputStreamScanner = new SecondPassScanner(inputStream, options.getDelimiter());
      try {
         int numTokensProcessed = 0;
         boolean isProcessorDone = false;
         while (inputStreamScanner.hasNext()) {
            ++numTokensProcessed;

            if (numTokensProcessed > processor.getTotalTokensToMatch() && QueryOption.TOKEN_COUNT__MATCH == options.getCountType()) {
               processor.getLocations().clear();
               break;
            }

            /**
             * the purpose of this here is to allow one more token to be read after the processor has signaled that it
             * is complete. The if statement above will catch the case when too many tokens are present.
             */
            if (isProcessorDone && !findAllMatchLocations) {
               break;
            }

            String next = inputStreamScanner.next();
            next = normalizeCase(options.getCaseType(), next);
            MatchLocation match = inputStreamScanner.match();
            isProcessorDone = processor.processToken(next, match);
         }

         // Clear if search did not complete
         if (!isProcessorDone) {
            processor.clearAllLocations();
         }
      } finally {
         inputStreamScanner.close();
      }
   }

}
