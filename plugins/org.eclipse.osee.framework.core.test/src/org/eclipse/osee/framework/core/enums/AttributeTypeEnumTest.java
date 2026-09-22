/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType;
import org.eclipse.osee.framework.core.enums.token.PartitionAttributeType.PartitionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AttributeTypeEnum}
 *
 * @author Donald G. Dunne
 * @author David W. Miller
 */
public class AttributeTypeEnumTest {

   @Test
   public void testValueFromStorageString() {
      int initialCount = CoreAttributeTypes.Partition.getEnumValues().size();
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumValues().size());
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumStrValues().size());
      PartitionEnum enum1 = CoreAttributeTypes.Partition.valueFromStorageString("New Partition");
      Assert.assertNotNull(enum1);
      // New enum gets next sequential enum id; initialCount since first enum gets 0
      Assert.assertEquals(Long.valueOf(initialCount), enum1.getId());

      // Valid enums should not change
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumValues().size());
      Assert.assertEquals(initialCount, CoreAttributeTypes.Partition.getEnumStrValues().size());

      // Loading same value again should not add another enum, but return same
      PartitionEnum enum2 = CoreAttributeTypes.Partition.valueFromStorageString("New Partition");
      Assert.assertNotNull(enum2);
      Assert.assertEquals(enum1.getId(), enum2.getId());
   }

   /**
    * Regression guard for the storage-tolerance/valid-set collision: when a value was first minted by
    * {@link AttributeTypeEnum#valueFromStorageString(String)} into the non-valid tolerance bucket and
    * is later promoted with {@link AttributeTypeEnum#addDbLoadedValidEnum(String)}, the SAME token
    * must be promoted (preserving its ordinal and {@code ==}/{@code equals} identity) rather than a
    * second token with the same name and a different ordinal being minted. Also verifies the promoted
    * name becomes valid and the round-trip still returns the identical token afterward.
    */
   @Test
   public void testAddDbLoadedValidEnumPromotesExistingStorageToken() {
      PartitionAttributeType type = newSeededType();

      PartitionEnum stored = type.valueFromStorageString("Runtime Partition");
      Assert.assertFalse("storage-tolerance value must not be valid before promotion",
         type.isValidEnum("Runtime Partition"));

      PartitionEnum promoted = type.addDbLoadedValidEnum("Runtime Partition");

      Assert.assertEquals("promotion must reuse the existing storage token instance", stored, promoted);
      Assert.assertEquals("promotion must preserve the storage token ordinal", stored.getId(),
         promoted.getId());
      Assert.assertTrue("name must be valid after promotion", type.isValidEnum("Runtime Partition"));
      Assert.assertEquals("name must appear exactly once in the valid set", 1,
         frequency(type.getEnumStrValues(), "Runtime Partition"));

      PartitionEnum roundTripped = type.valueFromStorageString("Runtime Partition");
      Assert.assertEquals("round-trip after promotion must return the same token", promoted, roundTripped);
      Assert.assertEquals("round-trip after promotion must return the same ordinal", promoted.getId(),
         roundTripped.getId());
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

   /**
    * Uses a fresh {@link PartitionAttributeType} instance per test rather than a shared
    * {@code CoreAttributeTypes} singleton. {@code addDbLoadedValidEnum} permanently grows the valid
    * enum set for the life of the process, so mutating a shared type would pollute other tests (for
    * example {@link #testValueFromStorageString} asserts exact sizes on {@code CoreAttributeTypes.Partition}).
    * A fresh instance is fully seeded by the public no-arg constructor and is isolated and repeatable.
    */
   private static PartitionAttributeType newSeededType() {
      return new PartitionAttributeType();
   }

   @Test
   public void testAddDbLoadedValidEnumAddsNewValidValue() {
      PartitionAttributeType type = newSeededType();
      int initialCount = type.getEnumValues().size();
      Assert.assertFalse("New name should not be valid before it is added",
         type.isValidEnum("Runtime Partition"));

      PartitionEnum added = type.addDbLoadedValidEnum("Runtime Partition");

      Assert.assertNotNull("addDbLoadedValidEnum must return the new token", added);
      Assert.assertEquals("Returned token name must equal the requested name", "Runtime Partition",
         added.getName());
      Assert.assertTrue("New name must become a valid enum after being added",
         type.isValidEnum("Runtime Partition"));
      Assert.assertTrue("New name must appear in getEnumStrValues()",
         type.getEnumStrValues().contains("Runtime Partition"));
      Assert.assertEquals("Valid set size must grow by exactly one", initialCount + 1,
         type.getEnumValues().size());
      // First promoted value gets the next sequential ordinal, which equals the pre-call valid-set
      // size given no dbLoadedEnumTokens have been added (matches valueFromStorageString scheme).
      Assert.assertEquals("New token must get the next sequential ordinal", Long.valueOf(initialCount),
         added.getId());
   }

   @Test
   public void testAddDbLoadedValidEnumIsIdempotentForDuplicateName() {
      PartitionAttributeType type = newSeededType();

      PartitionEnum first = type.addDbLoadedValidEnum("Runtime Partition");
      int countAfterFirst = type.getEnumValues().size();

      PartitionEnum second = type.addDbLoadedValidEnum("Runtime Partition");

      Assert.assertEquals("Duplicate name must return the existing token id", first.getId(),
         second.getId());
      Assert.assertEquals("Duplicate name must return the same token instance", first, second);
      Assert.assertEquals("Duplicate name must leave the valid set size unchanged", countAfterFirst,
         type.getEnumValues().size());
   }

   @Test
   public void testAddDbLoadedValidEnumExistingSeedNameReturnsSeedToken() {
      PartitionAttributeType type = newSeededType();
      int initialCount = type.getEnumValues().size();

      // "Unspecified" is already a seeded valid enum on PartitionAttributeType (ordinal 6).
      PartitionEnum existing = type.addDbLoadedValidEnum("Unspecified");

      Assert.assertEquals("Adding an existing seed name must return its seed ordinal",
         type.getEnumOrdinal("Unspecified"), existing.getId());
      Assert.assertEquals("Adding an existing seed name must not grow the valid set", initialCount,
         type.getEnumValues().size());
   }

   @Test
   public void testAddDbLoadedValidEnumAssignsUniqueSequentialOrdinals() {
      PartitionAttributeType type = newSeededType();
      int initialCount = type.getEnumValues().size();

      PartitionEnum a = type.addDbLoadedValidEnum("Runtime A");
      PartitionEnum b = type.addDbLoadedValidEnum("Runtime B");
      PartitionEnum c = type.addDbLoadedValidEnum("Runtime C");

      Assert.assertEquals("First added value gets the next sequential ordinal",
         Long.valueOf(initialCount), a.getId());
      Assert.assertEquals("Second added value gets the following ordinal",
         Long.valueOf(initialCount + 1), b.getId());
      Assert.assertEquals("Third added value gets the following ordinal",
         Long.valueOf(initialCount + 2), c.getId());

      Assert.assertNotEquals("Ordinals of distinct names must be unique", a.getId(), b.getId());
      Assert.assertNotEquals("Ordinals of distinct names must be unique", b.getId(), c.getId());
      Assert.assertNotEquals("Ordinals of distinct names must be unique", a.getId(), c.getId());

      Assert.assertEquals("Valid set must grow by the number of distinct names added",
         initialCount + 3, type.getEnumValues().size());
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddDbLoadedValidEnumRejectsNullName() {
      newSeededType().addDbLoadedValidEnum(null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddDbLoadedValidEnumRejectsBlankName() {
      newSeededType().addDbLoadedValidEnum("   ");
   }

}
