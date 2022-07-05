/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.framework.core.data;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Ryan D. Brooks
 */
public class RandomIdUniquenessTest {
   private final ThreadLocalRandom random = ThreadLocalRandom.current();
   private final long[] ids = new long[1000000000];

   public RandomIdUniquenessTest() {
   }

   public static void main(String[] args) {
      RandomIdUniquenessTest app = new RandomIdUniquenessTest();
      app.test();
   }

   private void test() {
      int idCount = ids.length;

      for (int i = 0; i < idCount; i++) {
         ids[i] = generateLong();
      }

      Arrays.sort(ids);
      System.out.println(countDuplicates());
      Math.random();
   }

   private long generateLong() {
      long id = random.nextLong(Long.MAX_VALUE);
      if (id < 1) {
         return generateLong();
      }
      return id;
   }

   private int countDuplicates() {
      int duplicateCount = 0;
      int idCount = ids.length;
      long previousId = 0;

      for (int i = 0; i < idCount; i++) {
         if (ids[i] == previousId) {
            duplicateCount++;
         }
         previousId = ids[i];
      }
      return duplicateCount;
   }
}