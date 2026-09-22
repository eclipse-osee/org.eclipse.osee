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

package org.eclipse.osee.define.operations.publisher.datarights;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.framework.core.publishing.RequiredIndicator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Property tests for {@link RequiredIndicatorRefresherImpl} (design Correctness Properties 2, 3, 4,
 * and 5).
 * <p>
 * No property-based library is on the module classpath (see task 1 of the spec), so each property is
 * exercised as a randomized JUnit 4 table test driven by a seeded {@link Random}. The fixed seed
 * makes any failure reproducible; the seed and the offending generated inputs are included in the
 * assertion failure messages so a counterexample can be reconstructed. The tests inject a mock
 * {@link DataRightsFootersIndicatorLoader} (so no database is touched) and a fresh
 * {@link DataRightsClassificationAttributeType} seeded from every {@link RequiredIndicator} member,
 * so the shared {@code CoreAttributeTypes.DataRightsClassification} singleton is never mutated.
 *
 * @author David W. Miller
 */

public class RequiredIndicatorRefresherPropertyTest {

   private static final long SEED = 0x0DA7A21617445L;

   private static final int ITERATIONS = 200;

   /**
    * Builds a fresh {@link DataRightsClassificationAttributeType} whose valid enum set is seeded from
    * every current {@link RequiredIndicator} member. The identifier is a throwaway value; only the
    * seeded valid set matters for these tests.
    */

   private static DataRightsClassificationAttributeType freshAttributeType() {
      return new DataRightsClassificationAttributeType(123456789L, "Data Rights Classification", "",
         TaggerTypeToken.PlainTextTagger, MediaType.TEXT_PLAIN, NamespaceToken.OSEE);
   }

   private static Set<String> seedNames() {
      return Stream.of(RequiredIndicator.values()).map(RequiredIndicator::getDisplayName).collect(
         Collectors.toCollection(LinkedHashSet::new));
   }

   private static RequiredIndicatorRefresherImpl refresher(DataRightsClassificationAttributeType attributeType,
      DataRightsFootersIndicatorLoader loader) {
      return new RequiredIndicatorRefresherImpl(loader, attributeType);
   }

   /**
    * Generates a footer name set that mixes footer-only names, previously added names, and
    * seed-overlapping names, so the additive and idempotence properties are exercised across the
    * relevant input space.
    */

   private static Set<String> generateNameSet(Random random, List<String> seedList, List<String> priorNames) {
      Set<String> names = new LinkedHashSet<>();
      int count = random.nextInt(5);
      for (int index = 0; index < count; index++) {
         switch (random.nextInt(3)) {
            case 0:
               names.add(seedList.get(random.nextInt(seedList.size())));
               break;
            case 1:
               if (!priorNames.isEmpty()) {
                  names.add(priorNames.get(random.nextInt(priorNames.size())));
                  break;
               }
               names.add(generateFooterOnlyName(random));
               break;
            default:
               names.add(generateFooterOnlyName(random));
               break;
         }
      }
      return names;
   }

   private static String generateFooterOnlyName(Random random) {
      StringBuilder builder = new StringBuilder("Footer-");
      int length = 1 + random.nextInt(8);
      for (int index = 0; index < length; index++) {
         char character = (char) ('a' + random.nextInt(26));
         if (random.nextInt(5) == 0) {
            character = ' ';
         }
         builder.append(character);
      }
      builder.append((char) ('A' + random.nextInt(26)));
      return builder.toString();
   }

   /**
    * <b>Validates: Requirements 5.1, 5.3</b>
    * <p>
    * Property 2 (refresh is additive): after each refresh in a sequence of refreshes on the same fresh
    * attribute type, (a) every seed name is still valid, (b) every name from this and all prior loaded
    * sets is valid, and (c) the valid set never shrank. Each refresh in the sequence uses a distinct
    * generated name set that mixes seed-overlapping, previously added, and footer-only names.
    */

   @Test
   public void testRefreshAdditivity() {
      Random random = new Random(SEED);
      List<String> seedList = new ArrayList<>(seedNames());

      for (int iteration = 0; iteration < ITERATIONS; iteration++) {

         DataRightsClassificationAttributeType attributeType = freshAttributeType();
         DataRightsFootersIndicatorLoader loader = mock(DataRightsFootersIndicatorLoader.class);
         RequiredIndicatorRefresherImpl refresher = refresher(attributeType, loader);

         Set<String> cumulativeLoaded = new LinkedHashSet<>();
         List<String> priorNames = new ArrayList<>();
         int previousValidSize = attributeType.getEnumStrValues().size();

         int sequenceLength = 1 + random.nextInt(4);
         List<Set<String>> appliedSets = new ArrayList<>();

         for (int step = 0; step < sequenceLength; step++) {

            Set<String> loaded = generateNameSet(random, seedList, priorNames);
            appliedSets.add(loaded);
            when(loader.loadClassificationNames(any())).thenReturn(loaded);

            refresher.refresh(mock(QueryBuilder.class));

            cumulativeLoaded.addAll(loaded);
            priorNames = new ArrayList<>(cumulativeLoaded);

            Set<String> valid = new HashSet<>(attributeType.getEnumStrValues());

            String message = String.format(
               "refresh-additivity violation [seed=0x%XL iteration=%d step=%d]%n  appliedSets=%s%n  validSet=%s",
               SEED, iteration, step, appliedSets, valid);

            for (String seedName : seedList) {
               Assert.assertTrue(message + "\n  missing seed name: " + seedName, valid.contains(seedName));
            }
            for (String loadedName : cumulativeLoaded) {
               Assert.assertTrue(message + "\n  missing loaded name: " + loadedName, valid.contains(loadedName));
            }
            Assert.assertTrue(message + "\n  valid set shrank from " + previousValidSize + " to " + valid.size(),
               valid.size() >= previousValidSize);

            previousValidSize = valid.size();
         }
      }
   }

   /**
    * <b>Validates: Requirements 5.4</b>
    * <p>
    * Property 3 (refresh is idempotent): {@code refresh(q); refresh(q)} with the same loaded name set
    * yields the same total valid set as a single refresh, and the second call returns zero.
    */

   @Test
   public void testRefreshIdempotence() {
      Random random = new Random(SEED);
      List<String> seedList = new ArrayList<>(seedNames());

      for (int iteration = 0; iteration < ITERATIONS; iteration++) {

         DataRightsClassificationAttributeType attributeType = freshAttributeType();
         DataRightsFootersIndicatorLoader loader = mock(DataRightsFootersIndicatorLoader.class);
         RequiredIndicatorRefresherImpl refresher = refresher(attributeType, loader);

         Set<String> loaded = generateNameSet(random, seedList, new ArrayList<>());
         when(loader.loadClassificationNames(any())).thenReturn(loaded);

         refresher.refresh(mock(QueryBuilder.class));
         Set<String> afterFirst = new HashSet<>(attributeType.getEnumStrValues());

         int secondAdded = refresher.refresh(mock(QueryBuilder.class));
         Set<String> afterSecond = new HashSet<>(attributeType.getEnumStrValues());

         String message = String.format(
            "refresh-idempotence violation [seed=0x%XL iteration=%d]%n  loaded=%s%n  afterFirst=%s%n  afterSecond=%s",
            SEED, iteration, loaded, afterFirst, afterSecond);

         Assert.assertEquals(message + "\n  second call must add zero", 0, secondAdded);
         Assert.assertEquals(message + "\n  valid set must be unchanged by the second call", afterFirst, afterSecond);
      }
   }

   /**
    * <b>Validates: Requirements 5.5</b>
    * <p>
    * Property 4 (footer coverage): for every classification name returned by the loader, after refresh
    * the attribute type reports that name as a valid enum value.
    */

   @Test
   public void testFooterCoverageAfterRefresh() {
      Random random = new Random(SEED);
      List<String> seedList = new ArrayList<>(seedNames());

      for (int iteration = 0; iteration < ITERATIONS; iteration++) {

         DataRightsClassificationAttributeType attributeType = freshAttributeType();
         DataRightsFootersIndicatorLoader loader = mock(DataRightsFootersIndicatorLoader.class);
         RequiredIndicatorRefresherImpl refresher = refresher(attributeType, loader);

         Set<String> loaded = generateNameSet(random, seedList, new ArrayList<>());
         when(loader.loadClassificationNames(any())).thenReturn(loaded);

         refresher.refresh(mock(QueryBuilder.class));

         String message = String.format(
            "footer-coverage violation [seed=0x%XL iteration=%d]%n  loaded=%s%n  validSet=%s", SEED, iteration, loaded,
            attributeType.getEnumStrValues());

         for (String name : loaded) {
            Assert.assertTrue(message + "\n  name not valid after refresh: " + name, attributeType.isValidEnum(name));
         }
      }
   }

   /**
    * <b>Validates: Requirements 8.1, 8.2, 8.3</b>
    * <p>
    * Property 5 (storage round-trip preserved): for any stored value v, {@code valueFromStorageString(v)}
    * returns a token whose name equals v, whether or not v is in the valid set, both before and after
    * refresh. The generated values include names never present in any footer, and the refresh footer
    * set may or may not include v; the round trip must hold in every case via the storage tolerance
    * bucket.
    */

   @Test
   public void testStorageRoundTripPreservation() {
      Random random = new Random(SEED);
      List<String> seedList = new ArrayList<>(seedNames());

      for (int iteration = 0; iteration < ITERATIONS; iteration++) {

         DataRightsClassificationAttributeType attributeType = freshAttributeType();
         DataRightsFootersIndicatorLoader loader = mock(DataRightsFootersIndicatorLoader.class);
         RequiredIndicatorRefresherImpl refresher = refresher(attributeType, loader);

         String storedValue = generateFooterOnlyName(random);

         String beforeName = attributeType.valueFromStorageString(storedValue).getName();
         String beforeMessage = String.format(
            "storage-round-trip violation before refresh [seed=0x%XL iteration=%d]%n  storedValue=%s%n  tokenName=%s",
            SEED, iteration, storedValue, beforeName);
         Assert.assertEquals(beforeMessage, storedValue, beforeName);

         Set<String> loaded = generateNameSet(random, seedList, new ArrayList<>());
         when(loader.loadClassificationNames(any())).thenReturn(loaded);
         refresher.refresh(mock(QueryBuilder.class));

         String afterName = attributeType.valueFromStorageString(storedValue).getName();
         String afterMessage = String.format(
            "storage-round-trip violation after refresh [seed=0x%XL iteration=%d]%n  storedValue=%s%n  loaded=%s%n  tokenName=%s",
            SEED, iteration, storedValue, loaded, afterName);
         Assert.assertEquals(afterMessage, storedValue, afterName);
      }
   }

}
