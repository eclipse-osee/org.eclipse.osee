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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DefaultAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.AttributeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.junit.BeforeClass;

/**
 * Low-level OseeTypeCache Test - Does not require database access
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactTypeCacheTest extends AbstractOseeCacheTest<ArtifactType> {

   private static List<ArtifactType> artifactTypes;
   private static List<AttributeType> attributeTypes;

   private static IOseeTypeFactory factory;

   private static ArtifactTypeCache artCache;
   private static AttributeTypeCache attrCache;
   private static BranchCache branchCache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      factory = new OseeTypeFactory();

      artifactTypes = new ArrayList<ArtifactType>();
      attributeTypes = new ArrayList<AttributeType>();

      BranchDataAccessor branchData = new BranchDataAccessor();
      branchCache = new BranchCache(factory, branchData);

      AttributeDataAccessor attrData = new AttributeDataAccessor(attributeTypes);
      attrCache = new AttributeTypeCache(factory, attrData);

      ArtifactDataAccessor artData = new ArtifactDataAccessor(attrCache, artifactTypes, branchCache);
      artCache = new ArtifactTypeCache(factory, artData);

      artCache.ensurePopulated();
      Assert.assertTrue(attrData.wasLoaded());
      Assert.assertTrue(branchData.wasLoaded());
      Assert.assertTrue(artData.wasLoaded());
   }

   public ArtifactTypeCacheTest() {
      super(artifactTypes, artCache);
   }

   @org.junit.Test
   public void testArtifactInheritanceCycleDetect() throws OseeCoreException {
      ArtifactType baseType = artCache.getUniqueByName("BaseArtifactType");
      Assert.assertNotNull(baseType);

      Set<ArtifactType> baseSuperType = new HashSet<ArtifactType>();
      baseSuperType.add(baseType);

      try {
         artCache.cacheArtifactSuperType(baseType, baseSuperType);
         Assert.assertTrue(
               "This line should not be execute, an inheritance cycle should have been detected and an exception should have been thrown",
               false);
      } catch (OseeInvalidInheritanceException ex) {
         Assert.assertNotNull(ex);
      }
   }

   @org.junit.Test
   public void testArtifactInheritance() throws OseeCoreException {
      OseeTypesUtil.checkInheritance(artCache, "000", "000");// inherits from returns true if comparing against itself
      OseeTypesUtil.checkInheritance(artCache, "111", "000");
      OseeTypesUtil.checkInheritance(artCache, "222", "000");
      OseeTypesUtil.checkInheritance(artCache, "333", "000", "222");
      OseeTypesUtil.checkInheritance(artCache, "444", "000", "222", "333");
      OseeTypesUtil.checkInheritance(artCache, "555", "000", "444", "333", "222");
      OseeTypesUtil.checkInheritance(artCache, "666", "000", "333", "222");
   }

   @org.junit.Test
   public void testArtifactInheritanceDescendants() throws OseeCoreException {
      OseeTypesUtil.checkDescendants(artCache, "000", false, "111", "222");
      OseeTypesUtil.checkDescendants(artCache, "000", true, "111", "222", "333", "444", "555", "666");

      OseeTypesUtil.checkDescendants(artCache, "111", false);
      OseeTypesUtil.checkDescendants(artCache, "111", true);

      OseeTypesUtil.checkDescendants(artCache, "222", false, "333");
      OseeTypesUtil.checkDescendants(artCache, "222", true, "333", "444", "555", "666");

      OseeTypesUtil.checkDescendants(artCache, "333", false, "333", "444");
      OseeTypesUtil.checkDescendants(artCache, "333", true, "444", "555", "666");

      OseeTypesUtil.checkDescendants(artCache, "444", false, "555");
      OseeTypesUtil.checkDescendants(artCache, "444", true, "555");
   }

   @org.junit.Test
   public void testNullArtifactInheritanceCheck() throws OseeCoreException {
      ArtifactType baseType = artCache.getUniqueByName("BaseArtifactType");
      // Check for null inheritance
      Assert.assertFalse(baseType.inheritsFrom((ArtifactType) null));
   }

   @org.junit.Test
   public void testArtifactInheritanceByName() throws OseeCoreException {
      Assert.assertTrue(artCache.getByGuid("666").inheritsFrom("ArtifactType3")); // check inherits from using artifact name
   }

   @org.junit.Test
   public void testAddArtifactSuperTypeMethod() throws OseeCoreException {
      ArtifactType artifactType = factory.createArtifactType(artCache, "myGUID", false, "TestMethodCreated");
      artCache.cache(artifactType);

      ArtifactType baseType = artCache.getUniqueByName("BaseArtifactType");
      Assert.assertFalse(artifactType.inheritsFrom(baseType));
      Assert.assertEquals(0, artifactType.getSuperArtifactTypes().size());

      artifactType.setSuperType(new HashSet<ArtifactType>(Arrays.asList(baseType)));
      Assert.assertEquals(1, artifactType.getSuperArtifactTypes().size());
      Assert.assertTrue(artifactType.inheritsFrom(baseType));
      artCache.decache(artifactType);
   }

   @org.junit.Test
   public void testInheritedAttributeTypes() throws OseeCoreException {
      Branch branch1 = branchCache.getByGuid("ROOT");
      Branch branch2 = branchCache.getByGuid("TEST");
      Assert.assertNotNull(branch1);
      Assert.assertNotNull(branch2);
      OseeTypesUtil.checkAttributes(artCache, attrCache, "000", branch1, "AAA");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "111", branch1, "AAA", "BBB");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "222", branch1, "AAA", "CCC");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "333", branch1, "AAA", "DDD", "CCC");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "444", branch1, "AAA", "FFF", "CCC", "DDD");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "555", branch1, "AAA", "GGG", "FFF", "CCC", "DDD");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "666", branch1, "AAA", "HHH", "DDD", "CCC");

      OseeTypesUtil.checkAttributes(artCache, attrCache, "000", branch2, "AAA");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "111", branch2, "AAA", "BBB");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "222", branch2, "AAA", "CCC");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "333", branch2, "AAA", "DDD", "CCC", "EEE"); // EEE only visible on branch2
      OseeTypesUtil.checkAttributes(artCache, attrCache, "444", branch2, "AAA", "FFF", "CCC", "DDD", "EEE");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "555", branch2, "AAA", "GGG", "FFF", "CCC", "DDD", "EEE");
      OseeTypesUtil.checkAttributes(artCache, attrCache, "666", branch2, "AAA", "HHH", "DDD", "CCC", "EEE");
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.test.types.AbstractOseeCacheTest#testDirty()
    */
   @Override
   public void testDirty() throws OseeCoreException {
   }

   private final static class BranchDataAccessor extends OseeTestDataAccessor<Branch> {

      @Override
      public void load(AbstractOseeCache<Branch> cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
         Branch branch1 =
               OseeTypesUtil.createBranch(cache, factory, "ROOT", "Root Branch", BranchType.SYSTEM_ROOT,
                     BranchState.CREATED, false);
         branch1.setId(999);
         cache.cache(branch1);

         Branch branch2 =
               OseeTypesUtil.createBranch(cache, factory, "TEST", "Test Branch", BranchType.BASELINE,
                     BranchState.CREATED, false);
         branch2.setId(998);
         cache.cache(branch2);

         ((BranchCache) cache).setBranchParent(branch1, branch2);
      }

   }

   private final static class AttributeDataAccessor extends OseeTestDataAccessor<AttributeType> {

      private final List<AttributeType> attributeTypes;

      public AttributeDataAccessor(List<AttributeType> attributeTypes) {
         super();
         this.attributeTypes = attributeTypes;
      }

      private AttributeType createAttributeTypeHelper(AbstractOseeCache<AttributeType> cache, IOseeTypeFactory factory, String guid, String name) throws OseeCoreException {
         return factory.createAttributeType(cache, guid, name, "DummyBase", "DummyProvider", StringAttribute.class,
               DefaultAttributeDataProvider.class, "none", "none", null, 1, 1, "test data", null);
      }

      @Override
      public void load(AbstractOseeCache<AttributeType> cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "AAA", "Attribute1"));
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "BBB", "Attribute2"));
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "CCC", "Attribute3"));
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "DDD", "Attribute4"));
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "EEE", "Attribute5"));
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "FFF", "Attribute6"));
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "GGG", "Attribute7"));
         attributeTypes.add(createAttributeTypeHelper(cache, factory, "HHH", "Attribute8"));
         int typeId = 200;
         for (AttributeType type : attributeTypes) {
            type.setId(typeId++);
            cache.cache(type);
         }
      }
   }

   private final static class ArtifactDataAccessor extends OseeTestDataAccessor<ArtifactType> {
      private final AttributeTypeCache attributeCache;
      private final List<ArtifactType> artifactTypes;
      private final BranchCache branchCache;

      public ArtifactDataAccessor(AttributeTypeCache attributeCache, List<ArtifactType> artifactTypes, BranchCache branchCache) {
         super();
         this.attributeCache = attributeCache;
         this.artifactTypes = artifactTypes;
         this.branchCache = branchCache;
      }

      @Override
      public void load(AbstractOseeCache<ArtifactType> cache, IOseeTypeFactory factory) throws OseeCoreException {
         branchCache.ensurePopulated();
         attributeCache.ensurePopulated();
         super.load(cache, factory);
         artifactTypes.add(factory.createArtifactType(cache, "000", true, "BaseArtifactType"));
         artifactTypes.add(factory.createArtifactType(cache, "111", true, "ArtifactType1"));
         artifactTypes.add(factory.createArtifactType(cache, "222", false, "ArtifactType2"));
         artifactTypes.add(factory.createArtifactType(cache, "333", true, "ArtifactType3"));
         artifactTypes.add(factory.createArtifactType(cache, "444", false, "ArtifactType4"));
         artifactTypes.add(factory.createArtifactType(cache, "555", true, "ArtifactType5"));
         artifactTypes.add(factory.createArtifactType(cache, "666", false, "ArtifactType6"));
         int typeId = 100;
         for (ArtifactType type : artifactTypes) {
            type.setId(typeId++);
            cache.cache(type);
         }
         ArtifactTypeCache artCache = (ArtifactTypeCache) cache;
         setUpArtifactTypeInheritance(artCache);
         setUpTypeValidity(artCache);
      }

      private void setUpArtifactTypeInheritance(ArtifactTypeCache cache) throws OseeCoreException {
         ArtifactType baseType = cache.getUniqueByName("BaseArtifactType");

         Set<ArtifactType> baseSuperType = new HashSet<ArtifactType>();
         baseSuperType.add(baseType);
         // 0<-1
         cache.cacheArtifactSuperType(cache.getByGuid("111"), baseSuperType);
         // 0<-2
         cache.cacheArtifactSuperType(cache.getByGuid("222"), baseSuperType);
         // 2<-3
         cache.cacheArtifactSuperType(cache.getByGuid("333"), Arrays.asList(cache.getByGuid("222")));
         // 3<-4 
         cache.cacheArtifactSuperType(cache.getByGuid("444"), Arrays.asList(cache.getByGuid("333")));
         // 4<-5 
         cache.cacheArtifactSuperType(cache.getByGuid("555"), Arrays.asList(cache.getByGuid("444")));
         // 3<-6 
         cache.cacheArtifactSuperType(cache.getByGuid("666"), Arrays.asList(cache.getByGuid("333")));
      }

      private void setUpTypeValidity(ArtifactTypeCache cache) throws OseeCoreException {
         Branch branch1 = branchCache.getByGuid("ROOT");
         Branch branch2 = branchCache.getByGuid("TEST");
         Assert.assertNotNull(branch1);
         Assert.assertNotNull(branch2);
         cache.cacheTypeValidity(cache.getByGuid("000"), attributeCache.getByGuid("AAA"), branch1);
         cache.cacheTypeValidity(cache.getByGuid("111"), attributeCache.getByGuid("BBB"), branch1);
         cache.cacheTypeValidity(cache.getByGuid("222"), attributeCache.getByGuid("CCC"), branch1);
         cache.cacheTypeValidity(cache.getByGuid("333"), attributeCache.getByGuid("DDD"), branch1);
         cache.cacheTypeValidity(cache.getByGuid("333"), attributeCache.getByGuid("EEE"), branch2);
         cache.cacheTypeValidity(cache.getByGuid("444"), attributeCache.getByGuid("FFF"), branch1);
         cache.cacheTypeValidity(cache.getByGuid("555"), attributeCache.getByGuid("GGG"), branch1);
         cache.cacheTypeValidity(cache.getByGuid("666"), attributeCache.getByGuid("HHH"), branch1);
      }

   }
}
