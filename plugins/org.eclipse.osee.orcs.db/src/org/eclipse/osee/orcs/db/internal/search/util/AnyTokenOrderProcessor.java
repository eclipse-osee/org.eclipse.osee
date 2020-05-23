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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;

/**
 * @author John Misinco
 */
public class AnyTokenOrderProcessor implements TokenOrderProcessor {

   private final TrackingMap trackingSet;
   private final List<MatchLocation> locations;
   private int numTokensToMatch = 0;

   public AnyTokenOrderProcessor() {
      locations = new LinkedList<>();
      trackingSet = new TrackingMap();
   }

   @Override
   public int getTotalTokensToMatch() {
      return numTokensToMatch;
   }

   @Override
   public void acceptTokenToMatch(String token) {
      trackingSet.add(token);
      numTokensToMatch++;
   }

   @Override
   public boolean processToken(String token, MatchLocation match) {
      if (trackingSet.found(token)) {
         locations.add(createMatchLocation(match.getStartPosition(), match.getEndPosition()));
      }
      return trackingSet.areAllFound();
   }

   @Override
   public List<MatchLocation> getLocations() {
      return locations;
   }

   @Override
   public void clearAllLocations() {
      locations.clear();
   }

   private MatchLocation createMatchLocation(int start, int end) {
      return new MatchLocation(start + 1, end);
   }

   private static final class TrackingMap {

      private final Map<String, MutableInteger> map = new LinkedHashMap<>();
      private int total = 0;

      public void add(String value) {
         MutableInteger stored = map.get(value);
         if (stored == null) {
            map.put(value, new MutableInteger(1));
         } else {
            stored.getValueAndInc();
         }
         total++;
      }

      public boolean found(String value) {
         MutableInteger stored = map.get(value);
         if (stored != null) {
            stored.getValueAndInc(-1);
            if (stored.getValue() > -1) {
               total--;
            }
            return true;
         }
         return false;
      }

      public boolean areAllFound() {
         return total == 0;
      }
   }

}