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
package org.eclipse.osee.orcs.db.internal.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class SearchAsserts {

   private SearchAsserts() {
      // Test Utility Class
   }

   public static void assertTagsEqual(List<Pair<String, Long>> expected, List<Pair<String, Long>> actual) {
      Assert.assertEquals(expected.size(), actual.size());

      for (int index = 0; index < expected.size(); index++) {
         assertEquals(expected.get(index), actual.get(index));
      }
   }

   public static void assertEquals(List<MatchLocation> expected, List<MatchLocation> actual) {
      Assert.assertEquals(expected.size(), actual.size());

      for (int index = 0; index < expected.size(); index++) {
         assertEquals(expected.get(index), actual.get(index));
      }
   }

   public static void assertEquals(Pair<String, Long> expected, Pair<String, Long> actual) {
      Assert.assertEquals(expected.getFirst(), actual.getFirst());
      Assert.assertEquals(expected.getSecond(), actual.getSecond());
   }

   public static void assertEquals(MatchLocation expected, MatchLocation actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals(expected.getStartPosition(), actual.getStartPosition());
         Assert.assertEquals(expected.getEndPosition(), actual.getEndPosition());
         Assert.assertEquals(expected.toString(), actual.toString());
      }
   }

   public static List<Pair<String, Long>> asTags(String word, long... entries) {
      List<Pair<String, Long>> data = new ArrayList<>();
      for (int index = 0; index < entries.length; index++) {
         data.add(new Pair<>(word, entries[index]));
      }
      return data;
   }

   public static List<MatchLocation> asLocations(int... data) {
      List<MatchLocation> locations = new ArrayList<>();
      if (data != null && data.length > 0) {
         for (int index = 0; index < data.length; index++) {
            locations.add(new MatchLocation(data[index], data[++index]));
         }
      }
      return locations;
   }

}
