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

package org.eclipse.osee.framework.skynet.core.test.types;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesUtil {

   private OseeTypesUtil() {

   }

   public static OseeEnumType createEnumType(OseeTypeCache cache, IOseeTypeFactory factory, String guid, String name, Object... entries) throws OseeCoreException {
      OseeEnumTypeCache eCache = cache.getEnumTypeCache();
      OseeEnumType type = factory.createEnumType(eCache, guid, name);
      if (entries != null && entries.length > 0) {
         List<OseeEnumEntry> items = new ArrayList<OseeEnumEntry>();
         for (int index = 0; index < entries.length; index++) {
            String itemName = (String) entries[index];
            Integer ordinal = (Integer) entries[++index];
            items.add(factory.createEnumEntry(eCache, null, itemName, ordinal));
         }
         type.setEntries(items);
      }
      return type;
   }

   public static void checkEnumType(String expectedName, String[] expectedEntries, Integer[] expectedOrdinals, OseeEnumType actualEnumType) throws OseeCoreException {
      assertEquals(expectedName, actualEnumType.getName());
      OseeEnumEntry[] enumEntries = actualEnumType.values();
      assertEquals(expectedEntries.length, enumEntries.length);
      for (int index = 0; index < expectedEntries.length && index < expectedOrdinals.length; index++) {
         checkEnumEntry(expectedEntries[index], expectedOrdinals[index], actualEnumType, enumEntries[index]);
      }
   }

   public static void checkEnumEntry(String expectedName, int expectedOrdinal, OseeEnumType parent, OseeEnumEntry entry) throws OseeCoreException {
      assertEquals(expectedName, entry.getName());
      assertEquals(expectedOrdinal, entry.ordinal());
      assertEquals(parent, entry.getDeclaringClass());
      assertEquals(parent.getTypeId(), entry.getTypeId());
      assertEquals(parent.getName(), entry.getName());
      OseeEnumEntry[] expected = parent.values();
      OseeEnumEntry[] actual = entry.getDeclaringClass().values();
      assertEquals(expected.length, actual.length);
      for (int index = 0; index < expected.length; index++) {
         assertEquals(expected[index], actual[index]);
      }
   }

   public static void checkEnumType(OseeEnumType expected, OseeEnumType actual) throws OseeCoreException {
      OseeEnumEntry[] expectedValues = expected.values();
      OseeEnumEntry[] actualValues = actual.values();
      Assert.assertEquals(expectedValues.length, actualValues.length);

      for (int index = 0; index < expectedValues.length; index++) {
         checkEnumEntry(expectedValues[index], actualValues[index]);
      }
   }

   public static void checkEnumEntry(OseeEnumEntry expected, OseeEnumEntry actual) throws OseeCoreException {
      Assert.assertEquals(expected.getName(), actual.getName());
      Assert.assertEquals(expected.ordinal(), actual.ordinal());
      Assert.assertEquals(expected.getDeclaringClass(), actual.getDeclaringClass());
      Assert.assertEquals(expected.getGuid(), actual.getGuid());
   }

   public static void checkInheritance(OseeTypeCache typeCache, String artTypeGuid, String... superTypeGuids) throws OseeCoreException {
      ArtifactType target = typeCache.getArtifactTypeCache().getTypeByGuid(artTypeGuid);
      Assert.assertNotNull(target);

      List<ArtifactType> expectedSuperTypes = new ArrayList<ArtifactType>();
      for (String superTyperGuid : superTypeGuids) {
         ArtifactType superArtifactType = typeCache.getArtifactTypeCache().getTypeByGuid(superTyperGuid);
         Assert.assertNotNull(superArtifactType);
         expectedSuperTypes.add(superArtifactType);
      }

      for (ArtifactType testAgainstType : typeCache.getArtifactTypeCache().getAllTypes()) {
         boolean result = target.inheritsFrom(testAgainstType);
         if (expectedSuperTypes.contains(testAgainstType) || target.equals(testAgainstType)) {
            Assert.assertTrue(String.format("[%s] does not inherit from [%s]", target.getName(),
                  testAgainstType.getName()), result);
         } else {
            Assert.assertFalse(String.format("[%s] should not inherit from [%s]", target.getName(),
                  testAgainstType.getName()), result);
         }
      }
   }

   public static void checkAttributes(OseeTypeCache typeCache, String artTypeGuid, Branch branch, String... attributeGuids) throws OseeCoreException {
      ArtifactType artifactType = typeCache.getArtifactTypeCache().getTypeByGuid(artTypeGuid);
      Assert.assertNotNull(artifactType);

      List<AttributeType> expectedAttributes = new ArrayList<AttributeType>();
      for (String attrGuid : attributeGuids) {
         AttributeType attributeType = typeCache.getAttributeTypeCache().getTypeByGuid(attrGuid);
         Assert.assertNotNull(attributeType);
         expectedAttributes.add(attributeType);
      }

      Collection<AttributeType> actualTypes = artifactType.getAttributeTypes(branch);
      Assert.assertEquals(String.format("ArtifactType [%s] - incorrect number of attributes actual - %s expected - %s",
            artTypeGuid, actualTypes, expectedAttributes), expectedAttributes.size(), actualTypes.size());

      Collection<AttributeType> typesNotFound =
            org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(expectedAttributes, actualTypes);
      Assert.assertTrue(String.format("Artifact [%s] for branch [%s] did not have the following attributes [%s]",
            artifactType.getName(), branch.getName(), typesNotFound), typesNotFound.isEmpty());

      typesNotFound =
            org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(actualTypes, expectedAttributes);
      Assert.assertTrue(String.format("Artifact [%s] for branch [%s] the following additional attributes [%s]",
            artifactType.getName(), branch.getName(), typesNotFound), typesNotFound.isEmpty());
   }

   public static void checkInheritance(OseeTypeCache typeCache, String relGuid, RelationSide relationSide, int maxValue, String... artifactTypesAllowed) throws OseeCoreException {
      RelationType relationType = typeCache.getRelationTypeCache().getTypeByGuid(relGuid);
      Assert.assertNotNull(relationType);

      Assert.assertEquals(maxValue, relationType.getMultiplicity().getLimit(relationSide));
      Assert.assertEquals(maxValue == Integer.MAX_VALUE ? "n" : "1", relationType.getMultiplicity().asLimitLabel(
            relationSide));

      List<ArtifactType> allowedTypes = new ArrayList<ArtifactType>();
      for (String guid : artifactTypesAllowed) {
         ArtifactType type = typeCache.getArtifactTypeCache().getTypeByGuid(guid);
         Assert.assertNotNull(type);
         allowedTypes.add(type);
      }

      for (ArtifactType artifactType : typeCache.getArtifactTypeCache().getAllTypes()) {
         boolean result = relationType.isArtifactTypeAllowed(relationSide, artifactType);
         if (allowedTypes.contains(artifactType)) {
            Assert.assertTrue(String.format("ArtifactType [%s] was not allowed", artifactType), result);
         } else {
            Assert.assertFalse(String.format("ArtifactType [%s] was allowed even though it should not have been",
                  artifactType), result);
         }
      }
   }
}
