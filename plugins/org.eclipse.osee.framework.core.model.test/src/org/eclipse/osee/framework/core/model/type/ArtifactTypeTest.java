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
package org.eclipse.osee.framework.core.model.type;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link ArtifactType}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class ArtifactTypeTest extends AbstractOseeTypeTest<Long, ArtifactType> {

   private final boolean isAbstract;

   public ArtifactTypeTest(ArtifactType type, Long guid, String name, boolean isAbstract) {
      super(type, guid, name);
      this.isAbstract = isAbstract;
   }

   @Test
   public void testSetGetIsAbstract()  {
      Assert.assertEquals(isAbstract, getType().isAbstract());
      getType().clearDirty();
      Assert.assertFalse(getType().isDirty());
      Assert.assertFalse(getType().areFieldsDirty(ArtifactType.ARTIFACT_IS_ABSTRACT_FIELD_KEY));

      // Check assigning the same value does not affect dirty
      getType().setAbstract(isAbstract);
      Assert.assertFalse(getType().isDirty());
      Assert.assertFalse(getType().areFieldsDirty(ArtifactType.ARTIFACT_IS_ABSTRACT_FIELD_KEY));

      getType().setAbstract(!isAbstract);
      Assert.assertEquals(!isAbstract, getType().isAbstract());
      Assert.assertTrue(getType().isDirty());
      Assert.assertTrue(getType().areFieldsDirty(ArtifactType.ARTIFACT_IS_ABSTRACT_FIELD_KEY));

      getType().setAbstract(isAbstract);
      getType().clearDirty();
   }

   @Ignore
   @Test
   public void test() {

      //      getType().compareTo(other);

      //      getType().getAttributeTypes(branch);
      //      getType().setAttributeTypes(attributeTypes, branch);

      //      getType().getFieldNames();

      //      getType().getFirstLevelDescendantTypes();
      //      getType().getAllDescendantTypes();

      //      getType().getLocalAttributeTypes();
      //      getType().getSuperArtifactTypes();

      //      getType().setSuperType(superType);
   }

   @Override
   public void testToString() {
      Assert.assertEquals(getExpectedName(), getType().toString());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();
      boolean isAbstract = true;
      for (int index = 1; index <= 3; index++) {
         Long guid = 0x00L;
         String name = "index: " + index;
         isAbstract ^= isAbstract;
         data.add(new Object[] {new ArtifactType(guid, name, isAbstract), guid, name, isAbstract});
      }
      return data;
   }

   //   @Override
   //   @Ignore
   //   @Test
   //   public void testPersist()  {
   //
   //      //      T item1 = data.get(0);
   //      //      T item2 = data.get(1);
   //      //
   //      //      cache.storeByGuid(guids);
   //      //      cache.storeItem(item);
   //      //      cache.storeItems(toStore);
   //      //      cache.storeItems(items);
   //      //      public void storeItem(AbstractOseeType item)  {
   //
   //      //      public void storeItems(T... items)  {
   //
   //      //      public void storeItems(Collection<T> toStore)  {
   //   }

   //   @org.junit.Test
   //   public void testArtifactInheritanceCycleDetect()  {
   //      ArtifactType baseType = artCache.getUniqueByName("BaseArtifactType");
   //      Assert.assertNotNull(baseType);
   //
   //      Set<ArtifactType> baseSuperType = new HashSet<>();
   //      baseSuperType.add(baseType);
   //
   //      try {
   //         artCache.cacheArtifactSuperType(baseType, baseSuperType);
   //         Assert.assertTrue(
   //               "This line should not be execute, an inheritance cycle should have been detected and an exception should have been thrown",
   //               false);
   //      } catch (OseeInvalidInheritanceException ex) {
   //         Assert.assertNotNull(ex);
   //      }
   //   }
   //
   //   @org.junit.Test
   //   public void testArtifactInheritance()  {
   //      OseeTypesUtil.checkInheritance(artCache, "000", "000");// inherits from returns true if comparing against itself
   //      OseeTypesUtil.checkInheritance(artCache, "111", "000");
   //      OseeTypesUtil.checkInheritance(artCache, "222", "000");
   //      OseeTypesUtil.checkInheritance(artCache, "333", "000", "222");
   //      OseeTypesUtil.checkInheritance(artCache, "444", "000", "222", "333");
   //      OseeTypesUtil.checkInheritance(artCache, "555", "000", "444", "333", "222");
   //      OseeTypesUtil.checkInheritance(artCache, "666", "000", "333", "222");
   //   }
   //
   //   @org.junit.Test
   //   public void testArtifactInheritanceDescendants()  {
   //      OseeTypesUtil.checkDescendants(artCache, "000", false, "111", "222");
   //      OseeTypesUtil.checkDescendants(artCache, "000", true, "111", "222", "333", "444", "555", "666");
   //
   //      OseeTypesUtil.checkDescendants(artCache, "111", false);
   //      OseeTypesUtil.checkDescendants(artCache, "111", true);
   //
   //      OseeTypesUtil.checkDescendants(artCache, "222", false, "333");
   //      OseeTypesUtil.checkDescendants(artCache, "222", true, "333", "444", "555", "666");
   //
   //      OseeTypesUtil.checkDescendants(artCache, "333", false, "333", "444");
   //      OseeTypesUtil.checkDescendants(artCache, "333", true, "444", "555", "666");
   //
   //      OseeTypesUtil.checkDescendants(artCache, "444", false, "555");
   //      OseeTypesUtil.checkDescendants(artCache, "444", true, "555");
   //   }
   //
   //   @org.junit.Test
   //   public void testNullArtifactInheritanceCheck()  {
   //      ArtifactType baseType = artCache.getUniqueByName("BaseArtifactType");
   //      // Check for null inheritance
   //      Assert.assertFalse(baseType.inheritsFrom((ArtifactType) null));
   //   }
   //
   //   @org.junit.Test
   //   public void testArtifactInheritanceByName()  {
   //      Assert.assertTrue(artCache.getByGuid("666").inheritsFrom("ArtifactType3")); // check inherits from using artifact name
   //   }
   //
   //   @org.junit.Test
   //   public void testAddArtifactSuperTypeMethod()  {
   //      ArtifactType artifactType = factory.createArtifactType(artCache, "myGUID", false, "TestMethodCreated");
   //      artCache.cache(artifactType);
   //
   //      ArtifactType baseType = artCache.getUniqueByName("BaseArtifactType");
   //      Assert.assertFalse(artifactType.inheritsFrom(baseType));
   //      Assert.assertEquals(0, artifactType.getSuperArtifactTypes().size());
   //
   //      artifactType.setSuperType(new HashSet<ArtifactType>(Arrays.asList(baseType)));
   //      Assert.assertEquals(1, artifactType.getSuperArtifactTypes().size());
   //      Assert.assertTrue(artifactType.inheritsFrom(baseType));
   //      artCache.decache(artifactType);
   //   }
   //
   //   @org.junit.Test
   //   public void testInheritedAttributeTypes()  {
   //      Branch branch1 = branchCache.getByGuid("ROOT");
   //      Branch branch2 = branchCache.getByGuid("TEST");
   //      Assert.assertNotNull(branch1);
   //      Assert.assertNotNull(branch2);
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "000", branch1, "AAA");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "111", branch1, "AAA", "BBB");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "222", branch1, "AAA", "CCC");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "333", branch1, "AAA", "DDD", "CCC");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "444", branch1, "AAA", "FFF", "CCC", "DDD");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "555", branch1, "AAA", "GGG", "FFF", "CCC", "DDD");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "666", branch1, "AAA", "HHH", "DDD", "CCC");
   //
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "000", branch2, "AAA");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "111", branch2, "AAA", "BBB");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "222", branch2, "AAA", "CCC");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "333", branch2, "AAA", "DDD", "CCC", "EEE"); // EEE only visible on branch2
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "444", branch2, "AAA", "FFF", "CCC", "DDD", "EEE");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "555", branch2, "AAA", "GGG", "FFF", "CCC", "DDD", "EEE");
   //      OseeTypesUtil.checkAttributes(artCache, attrCache, "666", branch2, "AAA", "HHH", "DDD", "CCC", "EEE");
   //   }
   //
   //   @Override
   //   public void testDirty()  {
   //   }
   //
   //   private final static class BranchDataAccessor extends MockOseeDataAccessor<Branch> {
   //
   //      @Override
   //      public void load(AbstractOseeCache<Branch> cache)  {
   //         super.load(cache);
   //         Branch branch1 =
   //               OseeTypesUtil.createBranch(cache, factory, "ROOT", "Root Branch", BranchType.SYSTEM_ROOT,
   //                     BranchState.CREATED, false);
   //         branch1.setId(999);
   //         cache.cache(branch1);
   //
   //         Branch branch2 =
   //               OseeTypesUtil.createBranch(cache, factory, "TEST", "Test Branch", BranchType.BASELINE,
   //                     BranchState.CREATED, false);
   //         branch2.setId(998);
   //         cache.cache(branch2);
   //
   //         ((BranchCache) cache).setBranchParent(branch1, branch2);
   //      }
   //
   //   }
   //
   //   private final static class AttributeDataAccessor extends MockOseeDataAccessor<AttributeType> {
   //
   //      private final List<AttributeType> attributeTypes;
   //
   //      public AttributeDataAccessor(List<AttributeType> attributeTypes) {
   //         super();
   //         this.attributeTypes = attributeTypes;
   //      }
   //
   //      private AttributeType createAttributeTypeHelper(AbstractOseeCache<AttributeType> cache, IOseeTypeFactory factory, String guid, String name)  {
   //         return factory.createAttributeType(cache, guid, name, "DummyBase", "DummyProvider", "none", "none", null, 1,
   //               1, "test data", null);
   //      }
   //
   //      @Override
   //      public void load(AbstractOseeCache<AttributeType> cache)  {
   //         super.load(cache);
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "AAA", "Attribute1"));
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "BBB", "Attribute2"));
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "CCC", "Attribute3"));
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "DDD", "Attribute4"));
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "EEE", "Attribute5"));
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "FFF", "Attribute6"));
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "GGG", "Attribute7"));
   //         attributeTypes.add(createAttributeTypeHelper(cache, factory, "HHH", "Attribute8"));
   //         int typeId = 200;
   //         for (AttributeType type : attributeTypes) {
   //            type.setId(typeId++);
   //            cache.cache(type);
   //         }
   //      }
   //   }
   //

   //      private void setUpArtifactTypeInheritance(ArtifactTypeCache cache)  {
   //         ArtifactType baseType = cache.getUniqueByName("BaseArtifactType");
   //
   //         Set<ArtifactType> baseSuperType = new HashSet<>();
   //         baseSuperType.add(baseType);
   //         // 0<-1
   //         cache.cacheArtifactSuperType(cache.getByGuid("111"), baseSuperType);
   //         // 0<-2
   //         cache.cacheArtifactSuperType(cache.getByGuid("222"), baseSuperType);
   //         // 2<-3
   //         cache.cacheArtifactSuperType(cache.getByGuid("333"), Arrays.asList(cache.getByGuid("222")));
   //         // 3<-4
   //         cache.cacheArtifactSuperType(cache.getByGuid("444"), Arrays.asList(cache.getByGuid("333")));
   //         // 4<-5
   //         cache.cacheArtifactSuperType(cache.getByGuid("555"), Arrays.asList(cache.getByGuid("444")));
   //         // 3<-6
   //         cache.cacheArtifactSuperType(cache.getByGuid("666"), Arrays.asList(cache.getByGuid("333")));
   //      }
   //
   //      private void setUpTypeValidity(ArtifactTypeCache cache)  {
   //         Branch branch1 = branchCache.getByGuid("ROOT");
   //         Branch branch2 = branchCache.getByGuid("TEST");
   //         Assert.assertNotNull(branch1);
   //         Assert.assertNotNull(branch2);
   //         cache.cacheTypeValidity(cache.getByGuid("000"), attributeCache.getByGuid("AAA"), branch1);
   //         cache.cacheTypeValidity(cache.getByGuid("111"), attributeCache.getByGuid("BBB"), branch1);
   //         cache.cacheTypeValidity(cache.getByGuid("222"), attributeCache.getByGuid("CCC"), branch1);
   //         cache.cacheTypeValidity(cache.getByGuid("333"), attributeCache.getByGuid("DDD"), branch1);
   //         cache.cacheTypeValidity(cache.getByGuid("333"), attributeCache.getByGuid("EEE"), branch2);
   //         cache.cacheTypeValidity(cache.getByGuid("444"), attributeCache.getByGuid("FFF"), branch1);
   //         cache.cacheTypeValidity(cache.getByGuid("555"), attributeCache.getByGuid("GGG"), branch1);
   //         cache.cacheTypeValidity(cache.getByGuid("666"), attributeCache.getByGuid("HHH"), branch1);
   //      }
}
