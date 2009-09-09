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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DefaultAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.AttributeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.junit.BeforeClass;

/**
 * Low-level OseeTypeCache Test - check inheritance, artifact, attribute and relation type management
 * This test does not require database access
 * 
 * @author Roberto E. Escobar
 */
public class OseeTypeCacheTest {
   private static List<ArtifactType> artifactTypes;
   private static List<AttributeType> attributeTypes;
   private static List<RelationType> relationTypes;
   private static List<OseeEnumType> oseeEnumTypes;
   private static TestOseeTypeDataAccessor testAccessor;
   private static Branch branch1;
   private static Branch branch2;
   private static OseeTypeCache typeCache;
   private static IOseeTypeFactory factory;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      artifactTypes = new ArrayList<ArtifactType>();
      attributeTypes = new ArrayList<AttributeType>();
      relationTypes = new ArrayList<RelationType>();
      oseeEnumTypes = new ArrayList<OseeEnumType>();
      factory = new OseeTypeFactory();
      branch1 = createBranchHelper("ROOT", "Root Branch", 999, null, BranchType.SYSTEM_ROOT);
      branch2 = createBranchHelper("TEST", "Test Branch", 998, branch1, BranchType.BASELINE);

      testAccessor = new TestData(artifactTypes, attributeTypes, relationTypes, oseeEnumTypes, branch1, branch2);
      typeCache = new OseeTypeCache(testAccessor, factory);

      typeCache.getArtifactTypeCache().getAllTypes();
      Assert.assertTrue(testAccessor.isLoadAllArtifactTypes());
      Assert.assertTrue(testAccessor.isLoadAllAttributeTypes());
      Assert.assertTrue(testAccessor.isLoadAllRelationTypes());
      Assert.assertTrue(testAccessor.isLoadAllTypeValidity());
      Assert.assertTrue(testAccessor.isLoadAllOseeEnumTypes());
   }

   @org.junit.Test
   public void testAllArtifactTypes() throws OseeCoreException {
      List<ArtifactType> actualTypes = new ArrayList<ArtifactType>(typeCache.getArtifactTypeCache().getAllTypes());
      java.util.Collections.sort(actualTypes);
      java.util.Collections.sort(artifactTypes);
      Assert.assertEquals(artifactTypes.size(), actualTypes.size());
      for (int index = 0; index < artifactTypes.size(); index++) {
         Assert.assertEquals(artifactTypes.get(index), actualTypes.get(index));
      }
   }

   @org.junit.Test
   public void testArtifactTypesExistByGuid() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         Assert.assertTrue(typeCache.getArtifactTypeCache().existsByGuid(expected.getGuid()));
      }
      Assert.assertFalse(typeCache.getArtifactTypeCache().existsByGuid("notExist"));
   }

   @org.junit.Test
   public void testCacheArtifactTypesByGuid() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeCache().getTypeByGuid(expected.getGuid());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheArtifactTypesById() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeCache().getTypeById(expected.getTypeId());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheArtifactTypesByName() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeCache().getTypeByName(expected.getName());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testAllAttributeTypes() throws OseeCoreException {
      List<AttributeType> actualTypes = new ArrayList<AttributeType>(typeCache.getAttributeTypeCache().getAllTypes());
      java.util.Collections.sort(actualTypes);
      java.util.Collections.sort(attributeTypes);
      Assert.assertEquals(attributeTypes.size(), actualTypes.size());
      for (int index = 0; index < attributeTypes.size(); index++) {
         Assert.assertEquals(attributeTypes.get(index), actualTypes.get(index));
      }
   }

   @org.junit.Test
   public void testAttributeTypesExistByGuid() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         Assert.assertTrue(typeCache.getAttributeTypeCache().existsByGuid(expected.getGuid()));
      }
      Assert.assertFalse(typeCache.getAttributeTypeCache().existsByGuid("notExist"));
   }

   @org.junit.Test
   public void testCacheAttributeTypesByGuid() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeCache().getTypeByGuid(expected.getGuid());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheAttributeTypesById() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeCache().getTypeById(expected.getTypeId());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheAttributeTypesByName() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeCache().getTypeByName(expected.getName());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testAllRelationTypes() throws OseeCoreException {
      List<RelationType> actualTypes = new ArrayList<RelationType>(typeCache.getRelationTypeCache().getAllTypes());
      java.util.Collections.sort(actualTypes);
      java.util.Collections.sort(relationTypes);
      Assert.assertEquals(relationTypes.size(), actualTypes.size());
      for (int index = 0; index < relationTypes.size(); index++) {
         Assert.assertEquals(relationTypes.get(index), actualTypes.get(index));
      }
   }

   @org.junit.Test
   public void testRelationTypesExistByGuid() throws OseeCoreException {
      for (RelationType expected : relationTypes) {
         Assert.assertTrue(typeCache.getRelationTypeCache().existsByGuid(expected.getGuid()));
      }
      Assert.assertFalse(typeCache.getRelationTypeCache().existsByGuid("notExist"));
   }

   @org.junit.Test
   public void testCacheRelationTypesByGuid() throws OseeCoreException {
      for (RelationType expected : relationTypes) {
         RelationType actual = typeCache.getRelationTypeCache().getTypeByGuid(expected.getGuid());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheRelationTypesById() throws OseeCoreException {
      for (RelationType expected : relationTypes) {
         RelationType actual = typeCache.getRelationTypeCache().getTypeById(expected.getTypeId());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheRelationTypesByName() throws OseeCoreException {
      for (RelationType expected : relationTypes) {
         RelationType actual = typeCache.getRelationTypeCache().getTypeByName(expected.getName());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testAllOseeEnumTypes() throws OseeCoreException {
      List<OseeEnumType> actualTypes = new ArrayList<OseeEnumType>(typeCache.getEnumTypeCache().getAllTypes());
      java.util.Collections.sort(actualTypes);
      java.util.Collections.sort(oseeEnumTypes);
      Assert.assertEquals(oseeEnumTypes.size(), actualTypes.size());
      for (int index = 0; index < oseeEnumTypes.size(); index++) {
         TestOseeTypesUtil.checkEnumType(oseeEnumTypes.get(index), actualTypes.get(index));
      }
   }

   @org.junit.Test
   public void testOseeEnumTypesExistByGuid() throws OseeCoreException {
      for (OseeEnumType expected : oseeEnumTypes) {
         Assert.assertTrue(typeCache.getEnumTypeCache().existsByGuid(expected.getGuid()));
      }
      Assert.assertFalse(typeCache.getEnumTypeCache().existsByGuid("notExist"));
   }

   @org.junit.Test
   public void testCacheOseeEnumTypesByGuid() throws OseeCoreException {
      for (OseeEnumType expected : oseeEnumTypes) {
         OseeEnumType actual = typeCache.getEnumTypeCache().getTypeByGuid(expected.getGuid());
         TestOseeTypesUtil.checkEnumType(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheOseeEnumTypesById() throws OseeCoreException {
      for (OseeEnumType expected : oseeEnumTypes) {
         OseeEnumType actual = typeCache.getEnumTypeCache().getTypeById(expected.getTypeId());
         TestOseeTypesUtil.checkEnumType(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheOseeEnumTypesByName() throws OseeCoreException {
      for (OseeEnumType expected : oseeEnumTypes) {
         OseeEnumType actual = typeCache.getEnumTypeCache().getTypeByName(expected.getName());
         TestOseeTypesUtil.checkEnumType(expected, actual);
      }
   }

   @org.junit.Test
   public void testArtifactInheritanceCycleDetect() throws OseeCoreException {
      ArtifactType baseType = typeCache.getArtifactTypeCache().getTypeByName("BaseArtifactType");

      Set<ArtifactType> baseSuperType = new HashSet<ArtifactType>();
      baseSuperType.add(baseType);

      try {
         typeCache.addArtifactSuperType(baseType, baseSuperType);
         Assert.assertTrue(
               "This line should not be execute, an inheritance cycle should have been detected and an exception should have been thrown",
               false);
      } catch (OseeInvalidInheritanceException ex) {
         Assert.assertNotNull(ex);
      }
   }

   @org.junit.Test
   public void testArtifactInheritance() throws OseeCoreException {
      TestOseeTypesUtil.checkInheritance(typeCache, "000", "000");// inherits from returns true if comparing against itself
      TestOseeTypesUtil.checkInheritance(typeCache, "111", "000");
      TestOseeTypesUtil.checkInheritance(typeCache, "222", "000");
      TestOseeTypesUtil.checkInheritance(typeCache, "333", "000", "222");
      TestOseeTypesUtil.checkInheritance(typeCache, "444", "000", "222", "333");
      TestOseeTypesUtil.checkInheritance(typeCache, "555", "000", "444", "333", "222");
      TestOseeTypesUtil.checkInheritance(typeCache, "666", "000", "333", "222");
   }

   @org.junit.Test
   public void testNullArtifactInheritanceCheck() throws OseeCoreException {
      ArtifactTypeCache cache = typeCache.getArtifactTypeCache();
      ArtifactType baseType = cache.getTypeByName("BaseArtifactType");
      // Check for null inheritance
      Assert.assertFalse(baseType.inheritsFrom((ArtifactType) null));
   }

   @org.junit.Test
   public void testArtifactInheritanceByName() throws OseeCoreException {
      Assert.assertTrue(typeCache.getArtifactTypeCache().getTypeByGuid("666").inheritsFrom("ArtifactType3")); // check inherits from using artifact name
   }

   @org.junit.Test
   public void testAddArtifactSuperTypeMethod() throws OseeCoreException {
      ArtifactType artifactType = factory.createArtifactType("myGUID", false, "TestMethodCreated", typeCache);
      typeCache.getArtifactTypeCache().cacheType(artifactType);

      ArtifactType baseType = typeCache.getArtifactTypeCache().getTypeByName("BaseArtifactType");
      Assert.assertFalse(artifactType.inheritsFrom(baseType));
      Assert.assertEquals(0, artifactType.getSuperArtifactTypes().size());

      artifactType.addSuperType(new HashSet<ArtifactType>(Arrays.asList(baseType)));
      Assert.assertEquals(1, artifactType.getSuperArtifactTypes().size());
      Assert.assertTrue(artifactType.inheritsFrom(baseType));
   }

   @org.junit.Test
   public void testInheritedAttributeTypes() throws OseeCoreException {
      TestOseeTypesUtil.checkAttributes(typeCache, "000", branch1, "AAA");
      TestOseeTypesUtil.checkAttributes(typeCache, "111", branch1, "AAA", "BBB");
      TestOseeTypesUtil.checkAttributes(typeCache, "222", branch1, "AAA", "CCC");
      TestOseeTypesUtil.checkAttributes(typeCache, "333", branch1, "AAA", "DDD", "CCC");
      TestOseeTypesUtil.checkAttributes(typeCache, "444", branch1, "AAA", "FFF", "CCC", "DDD");
      TestOseeTypesUtil.checkAttributes(typeCache, "555", branch1, "AAA", "GGG", "FFF", "CCC", "DDD");
      TestOseeTypesUtil.checkAttributes(typeCache, "666", branch1, "AAA", "HHH", "DDD", "CCC");

      TestOseeTypesUtil.checkAttributes(typeCache, "000", branch2, "AAA");
      TestOseeTypesUtil.checkAttributes(typeCache, "111", branch2, "AAA", "BBB");
      TestOseeTypesUtil.checkAttributes(typeCache, "222", branch2, "AAA", "CCC");
      TestOseeTypesUtil.checkAttributes(typeCache, "333", branch2, "AAA", "DDD", "CCC", "EEE"); // EEE only visible on branch2
      TestOseeTypesUtil.checkAttributes(typeCache, "444", branch2, "AAA", "FFF", "CCC", "DDD", "EEE");
      TestOseeTypesUtil.checkAttributes(typeCache, "555", branch2, "AAA", "GGG", "FFF", "CCC", "DDD", "EEE");
      TestOseeTypesUtil.checkAttributes(typeCache, "666", branch2, "AAA", "HHH", "DDD", "CCC", "EEE");
   }

   public void testRelationTypeSides() throws OseeCoreException {
      TestOseeTypesUtil.checkInheritance(typeCache, "1A", RelationSide.SIDE_A, 1, "111");
      TestOseeTypesUtil.checkInheritance(typeCache, "1A", RelationSide.SIDE_B, 1, "444", "555");

      TestOseeTypesUtil.checkInheritance(typeCache, "2B", RelationSide.SIDE_A, 1, "555");
      TestOseeTypesUtil.checkInheritance(typeCache, "2B", RelationSide.SIDE_B, Integer.MAX_VALUE, "000", "111", "222",
            "333", "444", "555", "666");

      TestOseeTypesUtil.checkInheritance(typeCache, "3C", RelationSide.SIDE_A, Integer.MAX_VALUE, "222", "333", "444",
            "555", "666");
      TestOseeTypesUtil.checkInheritance(typeCache, "3C", RelationSide.SIDE_B, 1, "333", "444", "555", "666");

      TestOseeTypesUtil.checkInheritance(typeCache, "4D", RelationSide.SIDE_A, Integer.MAX_VALUE, "666");
      TestOseeTypesUtil.checkInheritance(typeCache, "4D", RelationSide.SIDE_B, Integer.MAX_VALUE, "666");
   }

   private static Branch createBranchHelper(String guid, String name, int id, Branch parentBranch, BranchType branchType) {
      return new Branch(guid, name, id, parentBranch, 0, false, -1, new Timestamp(new Date().getTime()), "", -1,
            branchType, BranchState.CREATED);
   }

   private static class TestData extends TestOseeTypeDataAccessor {
      private final List<ArtifactType> artifactTypes;
      private final List<AttributeType> attributeTypes;
      private final List<RelationType> relationTypes;
      private final List<OseeEnumType> oseeEnumTypes;
      private final Branch branch1;
      private final Branch branch2;

      public TestData(List<ArtifactType> artifactTypes, List<AttributeType> attributeTypes, List<RelationType> relationTypes, List<OseeEnumType> oseeEnumTypes, Branch branch1, Branch branch2) {
         this.artifactTypes = artifactTypes;
         this.attributeTypes = attributeTypes;
         this.relationTypes = relationTypes;
         this.oseeEnumTypes = oseeEnumTypes;
         this.branch1 = branch1;
         this.branch2 = branch2;
      }

      @Override
      public void loadAllArtifactTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.loadAllArtifactTypes(cache, factory);
         artifactTypes.add(factory.createArtifactType("000", true, "BaseArtifactType", cache));
         artifactTypes.add(factory.createArtifactType("111", true, "ArtifactType1", cache));
         artifactTypes.add(factory.createArtifactType("222", false, "ArtifactType2", cache));
         artifactTypes.add(factory.createArtifactType("333", true, "ArtifactType3", cache));
         artifactTypes.add(factory.createArtifactType("444", false, "ArtifactType4", cache));
         artifactTypes.add(factory.createArtifactType("555", true, "ArtifactType5", cache));
         artifactTypes.add(factory.createArtifactType("666", false, "ArtifactType6", cache));
         int typeId = 100;
         for (ArtifactType type : artifactTypes) {
            type.setTypeId(typeId++);
            cache.getArtifactTypeCache().cacheType(type);
         }
         setUpArtifactTypeInheritance(cache);
      }

      private void setUpArtifactTypeInheritance(OseeTypeCache typeCache) throws OseeCoreException {
         ArtifactTypeCache cache = typeCache.getArtifactTypeCache();
         ArtifactType baseType = cache.getTypeByName("BaseArtifactType");

         Set<ArtifactType> baseSuperType = new HashSet<ArtifactType>();
         baseSuperType.add(baseType);
         // 0<-1
         typeCache.addArtifactSuperType(cache.getTypeByGuid("111"), baseSuperType);
         // 0<-2
         typeCache.addArtifactSuperType(cache.getTypeByGuid("222"), baseSuperType);
         // 2<-3
         typeCache.addArtifactSuperType(cache.getTypeByGuid("333"), Arrays.asList(cache.getTypeByGuid("222")));
         // 2,3<-4 
         typeCache.addArtifactSuperType(cache.getTypeByGuid("444"), Arrays.asList(cache.getTypeByGuid("222"),
               cache.getTypeByGuid("333"), baseType));
         // 4<-5 
         typeCache.addArtifactSuperType(cache.getTypeByGuid("555"), Arrays.asList(cache.getTypeByGuid("444"), baseType));
         // 3<-6 
         typeCache.addArtifactSuperType(cache.getTypeByGuid("666"), Arrays.asList(cache.getTypeByGuid("333"), baseType));
      }

      private AttributeType createAttributeTypeHelper(IOseeTypeFactory factory, String guid, String name) throws OseeCoreException {
         return factory.createAttributeType(guid, name, "DummyBase", "DummyProvider", StringAttribute.class,
               DefaultAttributeDataProvider.class, "none", "none", null, 1, 1, "test data", null);
      }

      @Override
      public void loadAllAttributeTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.loadAllAttributeTypes(cache, factory);
         attributeTypes.add(createAttributeTypeHelper(factory, "AAA", "Attribute1"));
         attributeTypes.add(createAttributeTypeHelper(factory, "BBB", "Attribute2"));
         attributeTypes.add(createAttributeTypeHelper(factory, "CCC", "Attribute3"));
         attributeTypes.add(createAttributeTypeHelper(factory, "DDD", "Attribute4"));
         attributeTypes.add(createAttributeTypeHelper(factory, "EEE", "Attribute5"));
         attributeTypes.add(createAttributeTypeHelper(factory, "FFF", "Attribute6"));
         attributeTypes.add(createAttributeTypeHelper(factory, "GGG", "Attribute7"));
         attributeTypes.add(createAttributeTypeHelper(factory, "HHH", "Attribute8"));
         int typeId = 200;
         for (AttributeType type : attributeTypes) {
            type.setTypeId(typeId++);
            cache.getAttributeTypeCache().cacheType(type);
         }
      }

      @Override
      public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
         super.loadAllTypeValidity(cache, artifactTypeFactory);
         ArtifactTypeCache artifactCache = cache.getArtifactTypeCache();
         AttributeTypeCache attributeCache = cache.getAttributeTypeCache();

         cache.cacheTypeValidity(artifactCache.getTypeByGuid("000"), attributeCache.getTypeByGuid("AAA"), branch1);
         cache.cacheTypeValidity(artifactCache.getTypeByGuid("111"), attributeCache.getTypeByGuid("BBB"), branch1);
         cache.cacheTypeValidity(artifactCache.getTypeByGuid("222"), attributeCache.getTypeByGuid("CCC"), branch1);
         cache.cacheTypeValidity(artifactCache.getTypeByGuid("333"), attributeCache.getTypeByGuid("DDD"), branch1);
         cache.cacheTypeValidity(artifactCache.getTypeByGuid("333"), attributeCache.getTypeByGuid("EEE"), branch2);
         cache.cacheTypeValidity(artifactCache.getTypeByGuid("444"), attributeCache.getTypeByGuid("FFF"), branch1);
         cache.cacheTypeValidity(artifactCache.getTypeByGuid("555"), attributeCache.getTypeByGuid("GGG"), branch1);
         cache.cacheTypeValidity(artifactCache.getTypeByGuid("666"), attributeCache.getTypeByGuid("HHH"), branch1);
      }

      private RelationType createRelationHelper(OseeTypeCache cache, IOseeTypeFactory factory, String guid, String name, String aGUID, String bGUID, RelationTypeMultiplicity multiplicity) throws OseeCoreException {
         ArtifactType type1 = cache.getArtifactTypeCache().getTypeByGuid(aGUID);
         ArtifactType type2 = cache.getArtifactTypeCache().getTypeByGuid(bGUID);
         return factory.createRelationType(guid, name, name + "_A", name + "_B", type1, type2, multiplicity, true, "");
      }

      @Override
      public void loadAllRelationTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.loadAllRelationTypes(cache, factory);
         relationTypes.add(createRelationHelper(cache, factory, "1A", "REL_1", "111", "444",
               RelationTypeMultiplicity.ONE_TO_ONE));
         relationTypes.add(createRelationHelper(cache, factory, "2B", "REL_2", "555", "000",
               RelationTypeMultiplicity.ONE_TO_MANY));
         relationTypes.add(createRelationHelper(cache, factory, "3C", "REL_3", "222", "333",
               RelationTypeMultiplicity.MANY_TO_ONE));
         relationTypes.add(createRelationHelper(cache, factory, "4D", "REL_4", "666", "666",
               RelationTypeMultiplicity.MANY_TO_MANY));
         int typeId = 300;
         for (RelationType type : relationTypes) {
            type.setTypeId(typeId++);
            cache.getRelationTypeCache().cacheType(type);
         }
      }

      private OseeEnumType createEnumTypeHelper(OseeTypeCache cache, IOseeTypeFactory factory, String guid, String name, Object... entries) throws OseeCoreException {
         OseeEnumType type = factory.createEnumType(guid, name, cache);
         if (entries != null && entries.length > 0) {
            List<OseeEnumEntry> items = new ArrayList<OseeEnumEntry>();
            for (int index = 0; index < entries.length; index++) {
               String itemName = (String) entries[index];
               Integer ordinal = (Integer) entries[++index];
               items.add(factory.createEnumEntry(null, itemName, ordinal, cache));
            }
            type.setEntries(items);
         }
         return type;
      }

      @Override
      public void loadAllOseeEnumTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.loadAllOseeEnumTypes(cache, factory);
         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E1", "Enum1", "AAA", 1, "BBB", 2, "CCC", 3));
         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E2", "Enum2", "DDD", 4, "EEE", 5, "FFF", 6));
         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E3", "Enum3", "GGG", 7, "HHH", 8, "III", 9));
         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E4", "Enum4", "JJJ", 10, "KKK", 11, "LLL", 12));
         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E5", "Enum5", "MMM", 1, "NNN", 2, "OOO", 3));
         int typeId = 400;
         for (OseeEnumType type : oseeEnumTypes) {
            type.setTypeId(typeId++);
            cache.getEnumTypeCache().cacheType(type);
         }
      }
   }
}
