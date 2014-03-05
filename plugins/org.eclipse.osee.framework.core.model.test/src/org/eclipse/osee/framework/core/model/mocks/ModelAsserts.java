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

package org.eclipse.osee.framework.core.model.mocks;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class ModelAsserts {

   private ModelAsserts() {
      // Utility Class
   }

   public static void assertTypeSetGet(AbstractOseeType<?> type, String fieldName, String getMethodName, String setMethodName, Object expectedValue, Object newValue) throws Exception {
      Method getMethod = type.getClass().getMethod(getMethodName);
      Method setMethod = type.getClass().getMethod(setMethodName, expectedValue.getClass());

      Assert.assertEquals(expectedValue, getMethod.invoke(type));

      type.clearDirty();
      Assert.assertFalse(type.isDirty());
      Assert.assertFalse(type.areFieldsDirty(fieldName));

      // Check reassign doesn't mark as dirty
      setMethod.invoke(type, expectedValue);
      Assert.assertFalse(type.areFieldsDirty(fieldName));

      setMethod.invoke(type, newValue);
      Assert.assertEquals(newValue, getMethod.invoke(type));

      Assert.assertTrue(type.isDirty());
      Assert.assertTrue(type.areFieldsDirty(fieldName));

      type.clearDirty();
      Assert.assertFalse(type.isDirty());
      Assert.assertFalse(type.areFieldsDirty(fieldName));

      setMethod.invoke(type, expectedValue);
      type.clearDirty();
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

   public static RelationType createRelationType(AbstractOseeCache<Long, ArtifactType> artCache, Long guid, String name, Long aGUID, Long bGUID, RelationTypeMultiplicity multiplicity) throws OseeCoreException {
      IArtifactType type1 = artCache.getByGuid(aGUID);
      IArtifactType type2 = artCache.getByGuid(bGUID);
      RelationType relationType =
         new RelationType(guid, name, name + "_A", name + "_B", type1, type2, multiplicity, "");
      Assert.assertNotNull(relationType);
      return relationType;
   }

   public static AttributeType createAttributeType(Long guid, String name) {
      AttributeType attributeType =
         new AttributeType(guid, name, "DummyBase", "DummyProvider", "none", "none", 1, 1, "test data", null,
            "mediaType");
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

   public static void checkEnumType(String expectedName, String[] expectedEntries, Integer[] expectedOrdinals, OseeEnumType actualEnumType) throws OseeCoreException {
      Assert.assertEquals(expectedName, actualEnumType.getName());
      OseeEnumEntry[] enumEntries = actualEnumType.values();
      Assert.assertEquals(expectedEntries.length, enumEntries.length);
      for (int index = 0; index < expectedEntries.length && index < expectedOrdinals.length; index++) {
         checkEnumEntry(expectedEntries[index], expectedOrdinals[index], actualEnumType, enumEntries[index]);
      }
   }

   public static void checkEnumEntry(String expectedName, int expectedOrdinal, OseeEnumType parent, OseeEnumEntry entry) {
      Assert.assertEquals(expectedName, entry.getName());
      Assert.assertEquals(expectedOrdinal, entry.ordinal());
      Assert.assertEquals(parent.getName(), entry.getName());
   }

   public static void checkEnumType(OseeEnumType expected, OseeEnumType actual) throws OseeCoreException {
      OseeEnumEntry[] expectedValues = expected.values();
      OseeEnumEntry[] actualValues = actual.values();
      Assert.assertEquals(expectedValues.length, actualValues.length);

      for (int index = 0; index < expectedValues.length; index++) {
         checkEnumEntry(expectedValues[index], actualValues[index]);
      }
   }

   public static void checkEnumEntry(OseeEnumEntry expected, OseeEnumEntry actual) {
      Assert.assertEquals(expected.getName(), actual.getName());
      Assert.assertEquals(expected.ordinal(), actual.ordinal());
   }

   public static void checkInheritance(AbstractOseeCache<Long, ArtifactType> artCache, Long artTypeGuid, Long... superTypeGuids) throws OseeCoreException {
      ArtifactType target = artCache.getByGuid(artTypeGuid);
      Assert.assertNotNull(target);

      List<ArtifactType> expectedSuperTypes = new ArrayList<ArtifactType>();
      for (Long superTyperGuid : superTypeGuids) {
         ArtifactType superArtifactType = artCache.getByGuid(superTyperGuid);
         Assert.assertNotNull(superArtifactType);
         expectedSuperTypes.add(superArtifactType);
      }

      for (ArtifactType testAgainstType : artCache.getAll()) {
         boolean result = target.inheritsFrom(testAgainstType);
         if (expectedSuperTypes.contains(testAgainstType) || target.equals(testAgainstType)) {
            Assert.assertTrue(
               String.format("[%s] does not inherit from [%s]", target.getName(), testAgainstType.getName()), result);
         } else {
            Assert.assertFalse(
               String.format("[%s] should not inherit from [%s]", target.getName(), testAgainstType.getName()), result);
         }
      }
   }

   public static void checkDescendants(AbstractOseeCache<Long, ArtifactType> artCache, Long artTypeGuid, boolean isAllLevels, Long... descendantGuids) throws OseeCoreException {
      ArtifactType target = artCache.getByGuid(artTypeGuid);
      Assert.assertNotNull(target);

      List<ArtifactType> expectedDescendants = new ArrayList<ArtifactType>();
      for (Long type : descendantGuids) {
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

   public static void checkAttributes(AbstractOseeCache<Long, ArtifactType> artCache, AbstractOseeCache<Long, AttributeType> attrCache, Long artTypeGuid, Branch branch, Long... attributeGuids) throws OseeCoreException {
      ArtifactType artifactType = artCache.getByGuid(artTypeGuid);
      Assert.assertNotNull(artifactType);

      List<IAttributeType> expectedAttributes = new ArrayList<IAttributeType>();
      for (Long attrGuid : attributeGuids) {
         IAttributeType attributeType = attrCache.getByGuid(attrGuid);
         Assert.assertNotNull(attributeType);
         expectedAttributes.add(attributeType);
      }

      Collection<IAttributeType> actualTypes = artifactType.getAttributeTypes(branch);
      Assert.assertEquals(String.format("ArtifactType [%s] - incorrect number of attributes actual - %s expected - %s",
         artTypeGuid, actualTypes, expectedAttributes), expectedAttributes.size(), actualTypes.size());

      Collection<IAttributeType> typesNotFound =
         org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(expectedAttributes, actualTypes);
      Assert.assertTrue(
         String.format("Artifact [%s] for branch [%s] did not have the following attributes [%s]",
            artifactType.getName(), branch.getName(), typesNotFound), typesNotFound.isEmpty());

      typesNotFound =
         org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(actualTypes, expectedAttributes);
      Assert.assertTrue(String.format("Artifact [%s] for branch [%s] the following additional attributes [%s]",
         artifactType.getName(), branch.getName(), typesNotFound), typesNotFound.isEmpty());
   }

   public static void checkRelationTypeInheritance(AbstractOseeCache<Long, RelationType> cache, AbstractOseeCache<Long, ArtifactType> artCache, Long relGuid, RelationSide relationSide, int maxValue, Long... artifactTypesAllowed) throws OseeCoreException {
      RelationType relationType = cache.getByGuid(relGuid);
      Assert.assertNotNull(relationType);

      Assert.assertEquals(maxValue, relationType.getMultiplicity().getLimit(relationSide));
      Assert.assertEquals(maxValue == Integer.MAX_VALUE ? "n" : "1",
         relationType.getMultiplicity().asLimitLabel(relationSide));

      List<ArtifactType> allowedTypes = new ArrayList<ArtifactType>();
      for (Long guid : artifactTypesAllowed) {
         ArtifactType type = artCache.getByGuid(guid);
         Assert.assertNotNull(type);
         allowedTypes.add(type);
      }

      for (ArtifactType artifactType : artCache.getAll()) {
         boolean result = relationType.isArtifactTypeAllowed(relationSide, artifactType);
         if (allowedTypes.contains(artifactType)) {
            Assert.assertTrue(String.format("ArtifactType [%s] was not allowed", artifactType), result);
         } else {
            Assert.assertFalse(
               String.format("ArtifactType [%s] was allowed even though it should not have been", artifactType), result);
         }
      }
   }

   public static void assertEquals(AccessDetail<?> expected, AccessDetail<?> actual) {
      Assert.assertEquals(expected, actual);
      Assert.assertEquals(expected.getPermission(), actual.getPermission());
      Assert.assertEquals(expected.getAccessObject(), actual.getAccessObject());
      Assert.assertEquals(expected.getReason(), actual.getReason());
   }
}
