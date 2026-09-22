/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.core.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType.PartitionEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * Concurrency property test for {@link AttributeTypeEnum#addDbLoadedValidEnum(String)}.
 * <p>
 * Property 6 (thread-safe promotion): concurrent {@code addDbLoadedValidEnum} calls produce no
 * duplicate names and no ordinal collisions in the resulting valid enum set.
 * <p>
 * There is no property-based-testing library on the module classpath, so the property is exercised
 * as a randomized table test driven by a fixed-seed {@link Random} (see {@link #SEED}) so any
 * failure is reproducible. A fresh {@link PartitionAttributeType} is used per invocation to avoid
 * polluting the shared {@code CoreAttributeTypes} singletons, matching the isolation approach used
 * by {@link AttributeTypeEnumTest}. The seed and the offending detail are included in assertion
 * messages so a counterexample can be reconstructed.
 *
 * <b>Validates: Requirements 6.3</b>
 *
 * @author David W. Miller
 */
public class AttributeTypeEnumConcurrencyTest {

   private static final long SEED = 20250101L;
   private static final int THREAD_COUNT = 12;
   private static final int TASK_COUNT = 500;
   private static final int DISTINCT_NAME_UNIVERSE = 40;
   private static final long AWAIT_TIMEOUT_SECONDS = 60L;

   /**
    * Seed names already present on a freshly constructed {@link PartitionAttributeType} (ordinals
    * 0-6). Including these in the dispatched name pool exercises idempotency under contention.
    */
   private static final String[] SEED_NAMES = {
      "Aircraft Systems",
      "Graphics Handler",
      "Communication",
      "Flight Control",
      "Input/Output Processor",
      "Navigation",
      "Unspecified"};

   @Test
   public void testConcurrentAddDbLoadedValidEnumHasNoDuplicateNamesOrOrdinalCollisions()
      throws InterruptedException {
      PartitionAttributeType type = new PartitionAttributeType();
      int seedCount = type.getEnumValues().size();

      Random random = new Random(SEED);

      // A modest universe of distinct non-seed names, deliberately dispatched many times so the
      // same name is added concurrently by multiple threads.
      List<String> nonSeedUniverse = new ArrayList<>(DISTINCT_NAME_UNIVERSE);
      for (int i = 0; i < DISTINCT_NAME_UNIVERSE; i++) {
         nonSeedUniverse.add("Runtime Classification " + i);
      }

      // Build the dispatch pool with heavy overlap: each task draws a random name from either the
      // non-seed universe or the seed-name set (so idempotency under contention is exercised too).
      List<String> dispatch = new ArrayList<>(TASK_COUNT);
      Set<String> expectedNonSeedNames = new HashSet<>();
      for (int i = 0; i < TASK_COUNT; i++) {
         String name;
         if (random.nextInt(5) == 0) {
            name = SEED_NAMES[random.nextInt(SEED_NAMES.length)];
         } else {
            name = nonSeedUniverse.get(random.nextInt(nonSeedUniverse.size()));
            expectedNonSeedNames.add(name);
         }
         dispatch.add(name);
      }

      Collection<Throwable> workerFailures = new CopyOnWriteArrayList<>();
      CountDownLatch startGate = new CountDownLatch(1);
      CountDownLatch doneGate = new CountDownLatch(TASK_COUNT);
      ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
      try {
         for (String name : dispatch) {
            executor.execute(() -> {
               try {
                  startGate.await();
                  type.addDbLoadedValidEnum(name);
               } catch (Throwable th) {
                  workerFailures.add(th);
               } finally {
                  doneGate.countDown();
               }
            });
         }
         // Release all workers at once to maximize contention.
         startGate.countDown();
         Assert.assertTrue("workers did not finish within timeout (seed=" + SEED + ")",
            doneGate.await(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
      } finally {
         executor.shutdownNow();
      }

      Assert.assertTrue("a worker thread threw (seed=" + SEED + "): " + describe(workerFailures),
         workerFailures.isEmpty());

      List<String> validNames = type.getEnumStrValues();
      Collection<PartitionEnum> validTokens = type.getEnumValues();

      // No duplicate names in the valid set.
      Set<String> distinctNames = new HashSet<>(validNames);
      Assert.assertEquals(
         "duplicate names in the valid set (seed=" + SEED + "); names=" + validNames,
         validNames.size(), distinctNames.size());

      // No ordinal collisions across valid tokens.
      Set<Long> distinctOrdinals = new HashSet<>();
      for (PartitionEnum token : validTokens) {
         distinctOrdinals.add(token.getId());
      }
      Assert.assertEquals(
         "ordinal collisions in the valid set (seed=" + SEED + "); ordinals=" + ordinalsOf(validTokens),
         validTokens.size(), distinctOrdinals.size());

      // Every seed name is present exactly once and valid.
      for (String seedName : SEED_NAMES) {
         Assert.assertTrue("seed name missing from valid set (seed=" + SEED + "): " + seedName,
            type.isValidEnum(seedName));
         Assert.assertEquals("seed name appears more than once (seed=" + SEED + "): " + seedName, 1,
            frequency(validNames, seedName));
      }

      // Every distinct generated non-seed name is present exactly once and valid.
      for (String name : expectedNonSeedNames) {
         Assert.assertTrue("generated name missing from valid set (seed=" + SEED + "): " + name,
            type.isValidEnum(name));
         Assert.assertEquals("generated name appears more than once (seed=" + SEED + "): " + name, 1,
            frequency(validNames, name));
      }

      // The final valid-set size equals the seed count plus the number of distinct non-seed names
      // generated: no over- or under-counting from races.
      Assert.assertEquals(
         "final valid-set size does not match seed + distinct generated names (seed=" + SEED + ")",
         seedCount + expectedNonSeedNames.size(), validNames.size());

      // Ordinals are contiguous starting at 0. Given the shared ordinal scheme and no
      // valueFromStorageString calls here, promoted tokens continue the seed sequence with no gaps.
      Set<Long> expectedOrdinals = new HashSet<>();
      for (long i = 0; i < validTokens.size(); i++) {
         expectedOrdinals.add(i);
      }
      Assert.assertEquals(
         "ordinals are not contiguous from 0 (seed=" + SEED + "); ordinals=" + ordinalsOf(validTokens),
         expectedOrdinals, distinctOrdinals);
   }

   private static int frequency(List<String> names, String target) {
      int count = 0;
      for (String name : names) {
         if (name.equals(target)) {
            count++;
         }
      }
      return count;
   }

   private static List<Long> ordinalsOf(Collection<PartitionEnum> tokens) {
      List<Long> ordinals = new ArrayList<>(tokens.size());
      for (PartitionEnum token : tokens) {
         ordinals.add(token.getId());
      }
      return ordinals;
   }

   private static String describe(Collection<Throwable> failures) {
      StringBuilder sb = new StringBuilder();
      for (Throwable th : failures) {
         if (sb.length() > 0) {
            sb.append("; ");
         }
         sb.append(th.getClass().getName()).append(": ").append(th.getMessage());
      }
      return sb.toString();
   }
}
