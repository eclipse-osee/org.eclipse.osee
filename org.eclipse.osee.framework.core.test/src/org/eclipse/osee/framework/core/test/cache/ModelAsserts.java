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

package org.eclipse.osee.framework.core.test.cache;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class ModelAsserts {

   private ModelAsserts() {

   }

   public static void createAlias(BranchCache cache, String branchGuid, String... aliases) throws OseeCoreException {
      Branch branch = cache.getByGuid(branchGuid);
      Assert.assertNotNull(branch);
      //      cache.setAliases(branch, Arrays.asList(aliases));
   }

   public static void checkMergeBranch(BranchCache cache, String expectedMergeBranchGuid, String sourceBranchGuid, String destinationBranchGuid) throws OseeCoreException {
      Branch sourceBranch = cache.getByGuid(sourceBranchGuid);
      Assert.assertNotNull(sourceBranch);
      Branch destionationBranch = cache.getByGuid(destinationBranchGuid);
      Assert.assertNotNull(destionationBranch);

      Branch actualMergeBranch = cache.findMergeBranch(sourceBranch, destionationBranch);
      if (expectedMergeBranchGuid == null) {
         Assert.assertNull(actualMergeBranch);
      } else {
         Branch mergeBranch = cache.getByGuid(expectedMergeBranchGuid);
         Assert.assertNotNull(mergeBranch);
         Assert.assertEquals(mergeBranch, actualMergeBranch);
      }
   }

   public static void createMergeBranch(BranchCache cache, String mergeBranchGuid, String sourceBranchGuid, String destinationBranchGuid) throws OseeCoreException {
      Branch mergeBranch = cache.getByGuid(mergeBranchGuid);
      Assert.assertNotNull(mergeBranch);
      Branch sourceBranch = cache.getByGuid(sourceBranchGuid);
      Assert.assertNotNull(sourceBranch);
      Branch destionationBranch = cache.getByGuid(destinationBranchGuid);
      Assert.assertNotNull(destionationBranch);
      Assert.assertTrue(mergeBranch instanceof MergeBranch);
      MergeBranch mBranch = (MergeBranch) mergeBranch;
      mBranch.setSourceBranch(sourceBranch);
      mBranch.setDestinationBranch(destionationBranch);
   }

   public static void checkHierarchy(BranchCache cache, String parentGuid, String... expected) throws OseeCoreException {
      Branch parentBranch = cache.getByGuid(parentGuid);
      Assert.assertNotNull(parentBranch);
      Collection<Branch> children = parentBranch.getChildren();
      Assert.assertEquals(expected.length, children.size());
      int index = 0;
      for (Branch child : children) {
         Branch expectedBranch = cache.getByGuid(expected[index]);
         Assert.assertNotNull(expectedBranch);
         Assert.assertEquals(expectedBranch, child);
         Assert.assertEquals(parentBranch, child.getParentBranch());
         index++;
      }
   }

   public static void createBranchHierarchy(BranchCache cache, String parentGuid, String... childrenGuids) throws OseeCoreException {
      Branch parentBranch = cache.getByGuid(parentGuid);
      Assert.assertNotNull(parentBranch);
      Assert.assertNotNull(childrenGuids);
      Assert.assertTrue(childrenGuids.length > 0);
      for (String childGuid : childrenGuids) {
         Branch childBranch = cache.getByGuid(childGuid);
         Assert.assertNotNull(childBranch);
         childBranch.setParentBranch(parentBranch);
      }
   }

   public static RelationType createRelationType(AbstractOseeCache<ArtifactType> artCache, String guid, String name, String aGUID, String bGUID, RelationTypeMultiplicity multiplicity) throws OseeCoreException {
      ArtifactType type1 = artCache.getByGuid(aGUID);
      ArtifactType type2 = artCache.getByGuid(bGUID);
      RelationType relationType =
            new RelationType(guid, name, name + "_A", name + "_B", type1, type2, multiplicity, "");
      Assert.assertNotNull(relationType);
      return relationType;
   }

   public static Branch createBranch(String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException {
      Branch branch;
      if (branchType.isMergeBranch()) {
         branch = new MergeBranch(guid, name, branchType, branchState, isArchived);
      } else {
         branch = new Branch(guid, name, branchType, branchState, isArchived);
      }
      Assert.assertNotNull(branch);
      return branch;
   }

   public static AttributeType createAttributeType(String guid, String name) throws OseeCoreException {
      AttributeType attributeType =
            new AttributeType(guid, name, "DummyBase", "DummyProvider", "none", "none", 1, 1, "test data", null);
      Assert.assertNotNull(attributeType);
      return attributeType;
   }

   public static void checkOseeEnumEntries(OseeEnumEntry[] actual, Object... entries) {
      Assert.assertEquals(entries.length / 2, actual.length);
      int index2 = 0;
      for (int index = 0; index < entries.length; index++, index2++) {
         String itemName = (String) entries[index];
         Integer ordinal = (Integer) entries[++index];

         Assert.assertEquals(itemName, actual[index2].getName());
         Assert.assertEquals((int) ordinal, actual[index2].ordinal());
      }
   }

   public static OseeEnumType createEnumType(IOseeTypeFactory factory, String guid, String name, Object... entries) throws OseeCoreException {
      OseeEnumType type = new OseeEnumType(guid, name);
      if (entries != null && entries.length > 0) {
         List<OseeEnumEntry> items = new ArrayList<OseeEnumEntry>();
         for (int index = 0; index < entries.length; index++) {
            String itemName = (String) entries[index];
            Integer ordinal = (Integer) entries[++index];
            items.add(new OseeEnumEntry(null, itemName, ordinal));
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
      //      assertEquals(parent, entry.getDeclaringClass());
      assertEquals(parent.getId(), entry.getId());
      assertEquals(parent.getName(), entry.getName());
      //      OseeEnumEntry[] expected = parent.values();
      //      //      OseeEnumEntry[] actual = entry.getDeclaringClass().values();
      //      assertEquals(expected.length, actual.length);
      //      for (int index = 0; index < expected.length; index++) {
      //         assertEquals(expected[index], actual[index]);
      //      }
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
      //      Assert.assertEquals(expected.getDeclaringClass(), actual.getDeclaringClass());
      Assert.assertEquals(expected.getGuid(), actual.getGuid());
   }

   public static void checkInheritance(AbstractOseeCache<ArtifactType> artCache, String artTypeGuid, String... superTypeGuids) throws OseeCoreException {
      ArtifactType target = artCache.getByGuid(artTypeGuid);
      Assert.assertNotNull(target);

      List<ArtifactType> expectedSuperTypes = new ArrayList<ArtifactType>();
      for (String superTyperGuid : superTypeGuids) {
         ArtifactType superArtifactType = artCache.getByGuid(superTyperGuid);
         Assert.assertNotNull(superArtifactType);
         expectedSuperTypes.add(superArtifactType);
      }

      for (ArtifactType testAgainstType : artCache.getAll()) {
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

   public static void checkDescendants(AbstractOseeCache<ArtifactType> artCache, String artTypeGuid, boolean isAllLevels, String... descendantGuids) throws OseeCoreException {
      ArtifactType target = artCache.getByGuid(artTypeGuid);
      Assert.assertNotNull(target);

      List<ArtifactType> expectedDescendants = new ArrayList<ArtifactType>();
      for (String type : descendantGuids) {
         ArtifactType childType = artCache.getByGuid(type);
         Assert.assertNotNull(childType);
         expectedDescendants.add(childType);
      }

      Collection<ArtifactType> descendants =
            isAllLevels ? target.getAllDescendantTypes() : target.getFirstLevelDescendantTypes();

      Assert.assertEquals(expectedDescendants.size(), descendants.size());
      for (ArtifactType child : descendants) {
         boolean result = expectedDescendants.contains(target);
         if (result) {
            Assert.assertTrue(String.format("[%s] inherits from [%s]", child.getName(), target.getName()), result);
         } else {
            Assert.assertFalse(String.format("[%s] does not inherit from [%s]", child.getName(), target.getName()),
                  result);
         }
         if (target.hasSuperArtifactTypes()) {
            Assert.assertEquals(true, child.inheritsFrom(target));
         }
      }
   }

   public static void checkAttributes(AbstractOseeCache<ArtifactType> artCache, AbstractOseeCache<AttributeType> attrCache, String artTypeGuid, Branch branch, String... attributeGuids) throws OseeCoreException {
      ArtifactType artifactType = artCache.getByGuid(artTypeGuid);
      Assert.assertNotNull(artifactType);

      List<AttributeType> expectedAttributes = new ArrayList<AttributeType>();
      for (String attrGuid : attributeGuids) {
         AttributeType attributeType = attrCache.getByGuid(attrGuid);
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

   public static void checkRelationTypeInheritance(AbstractOseeCache<RelationType> cache, AbstractOseeCache<ArtifactType> artCache, String relGuid, RelationSide relationSide, int maxValue, String... artifactTypesAllowed) throws OseeCoreException {
      RelationType relationType = cache.getByGuid(relGuid);
      Assert.assertNotNull(relationType);

      Assert.assertEquals(maxValue, relationType.getMultiplicity().getLimit(relationSide));
      Assert.assertEquals(maxValue == Integer.MAX_VALUE ? "n" : "1", relationType.getMultiplicity().asLimitLabel(
            relationSide));

      List<ArtifactType> allowedTypes = new ArrayList<ArtifactType>();
      for (String guid : artifactTypesAllowed) {
         ArtifactType type = artCache.getByGuid(guid);
         Assert.assertNotNull(type);
         allowedTypes.add(type);
      }

      for (ArtifactType artifactType : artCache.getAll()) {
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
