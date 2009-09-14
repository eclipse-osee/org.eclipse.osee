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
   private static Branch branch1;
   private static Branch branch2;
   private static OseeTypeCache typeCache;
   private static IOseeTypeFactory factory;

   private static ArtifactCache artCache;
   private static AttributeCache attrCache;
   private static RelationCache relCache;
   private static EnumTypeCache enumCache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      artifactTypes = new ArrayList<ArtifactType>();
      attributeTypes = new ArrayList<AttributeType>();
      relationTypes = new ArrayList<RelationType>();
      oseeEnumTypes = new ArrayList<OseeEnumType>();
      factory = new OseeTypeFactory();
      branch1 = createBranchHelper("ROOT", "Root Branch", 999, null, BranchType.SYSTEM_ROOT);
      branch2 = createBranchHelper("TEST", "Test Branch", 998, branch1, BranchType.BASELINE);

      artCache = new ArtifactCache(artifactTypes, branch1, branch2);
      attrCache = new AttributeCache(attributeTypes);
      relCache = new RelationCache(relationTypes);
      enumCache = new EnumTypeCache(oseeEnumTypes);

      typeCache = new OseeTypeCache(factory, artCache, attrCache, relCache, enumCache);
      typeCache.getArtifactTypeCache().getAllTypes();
      Assert.assertTrue(artCache.wasLoaded());
      Assert.assertTrue(attrCache.wasLoaded());
      Assert.assertTrue(relCache.wasLoaded());
      Assert.assertTrue(enumCache.wasLoaded());
   }

   @org.junit.Test
   public void testAllArtifactTypes() throws OseeCoreException {
      List<ArtifactType> actualTypes = new ArrayList<ArtifactType>(typeCache.getArtifactTypeCache().getAllTypes());
      java.util.Collections.sort(actualTypes);
      java.util.Collections.sort(artifactTypes);
      Assert.assertEquals(artifactTypes.size(), actualTypes.size());
      for (int index = 0; index < artifactTypes.size(); index++) {
         Assert.assertNotNull(actualTypes.get(index));
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
         Assert.assertNotNull(actual);
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheArtifactTypesById() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeCache().getTypeById(expected.getTypeId());
         Assert.assertNotNull(actual);
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheArtifactTypesByName() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeCache().getTypeByName(expected.getName());
         Assert.assertNotNull(actual);
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
         Assert.assertNotNull(actual);
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheAttributeTypesById() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeCache().getTypeById(expected.getTypeId());
         Assert.assertNotNull(actual);
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheAttributeTypesByName() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeCache().getTypeByName(expected.getName());
         Assert.assertNotNull(actual);
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
         Assert.assertNotNull(actual);
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheRelationTypesById() throws OseeCoreException {
      for (RelationType expected : relationTypes) {
         RelationType actual = typeCache.getRelationTypeCache().getTypeById(expected.getTypeId());
         Assert.assertNotNull(actual);
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheRelationTypesByName() throws OseeCoreException {
      for (RelationType expected : relationTypes) {
         RelationType actual = typeCache.getRelationTypeCache().getTypeByName(expected.getName());
         Assert.assertNotNull(actual);
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
         OseeTypesUtil.checkEnumType(oseeEnumTypes.get(index), actualTypes.get(index));
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
         Assert.assertNotNull(actual);
         OseeTypesUtil.checkEnumType(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheOseeEnumTypesById() throws OseeCoreException {
      for (OseeEnumType expected : oseeEnumTypes) {
         OseeEnumType actual = typeCache.getEnumTypeCache().getTypeById(expected.getTypeId());
         Assert.assertNotNull(actual);
         OseeTypesUtil.checkEnumType(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheOseeEnumTypesByName() throws OseeCoreException {
      for (OseeEnumType expected : oseeEnumTypes) {
         OseeEnumType actual = typeCache.getEnumTypeCache().getTypeByName(expected.getName());
         Assert.assertNotNull(actual);
         OseeTypesUtil.checkEnumType(expected, actual);
      }
   }

   @org.junit.Test
   public void testArtifactInheritanceCycleDetect() throws OseeCoreException {
      ArtifactType baseType = typeCache.getArtifactTypeCache().getTypeByName("BaseArtifactType");
      Assert.assertNotNull(baseType);

      Set<ArtifactType> baseSuperType = new HashSet<ArtifactType>();
      baseSuperType.add(baseType);

      try {
         typeCache.getArtifactTypeCache().setArtifactSuperType(baseType, baseSuperType);
         Assert.assertTrue(
               "This line should not be execute, an inheritance cycle should have been detected and an exception should have been thrown",
               false);
      } catch (OseeInvalidInheritanceException ex) {
         Assert.assertNotNull(ex);
      }
   }

   @org.junit.Test
   public void testArtifactInheritance() throws OseeCoreException {
      OseeTypesUtil.checkInheritance(typeCache, "000", "000");// inherits from returns true if comparing against itself
      OseeTypesUtil.checkInheritance(typeCache, "111", "000");
      OseeTypesUtil.checkInheritance(typeCache, "222", "000");
      OseeTypesUtil.checkInheritance(typeCache, "333", "000", "222");
      OseeTypesUtil.checkInheritance(typeCache, "444", "000", "222", "333");
      OseeTypesUtil.checkInheritance(typeCache, "555", "000", "444", "333", "222");
      OseeTypesUtil.checkInheritance(typeCache, "666", "000", "333", "222");
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
      ArtifactType artifactType =
            factory.createArtifactType(typeCache.getArtifactTypeCache(), "myGUID", false, "TestMethodCreated");
      typeCache.getArtifactTypeCache().cacheType(artifactType);

      ArtifactType baseType = typeCache.getArtifactTypeCache().getTypeByName("BaseArtifactType");
      Assert.assertFalse(artifactType.inheritsFrom(baseType));
      Assert.assertEquals(0, artifactType.getSuperArtifactTypes().size());

      artifactType.setSuperType(new HashSet<ArtifactType>(Arrays.asList(baseType)));
      Assert.assertEquals(1, artifactType.getSuperArtifactTypes().size());
      Assert.assertTrue(artifactType.inheritsFrom(baseType));
   }

   @org.junit.Test
   public void testInheritedAttributeTypes() throws OseeCoreException {
      OseeTypesUtil.checkAttributes(typeCache, "000", branch1, "AAA");
      OseeTypesUtil.checkAttributes(typeCache, "111", branch1, "AAA", "BBB");
      OseeTypesUtil.checkAttributes(typeCache, "222", branch1, "AAA", "CCC");
      OseeTypesUtil.checkAttributes(typeCache, "333", branch1, "AAA", "DDD", "CCC");
      OseeTypesUtil.checkAttributes(typeCache, "444", branch1, "AAA", "FFF", "CCC", "DDD");
      OseeTypesUtil.checkAttributes(typeCache, "555", branch1, "AAA", "GGG", "FFF", "CCC", "DDD");
      OseeTypesUtil.checkAttributes(typeCache, "666", branch1, "AAA", "HHH", "DDD", "CCC");

      OseeTypesUtil.checkAttributes(typeCache, "000", branch2, "AAA");
      OseeTypesUtil.checkAttributes(typeCache, "111", branch2, "AAA", "BBB");
      OseeTypesUtil.checkAttributes(typeCache, "222", branch2, "AAA", "CCC");
      OseeTypesUtil.checkAttributes(typeCache, "333", branch2, "AAA", "DDD", "CCC", "EEE"); // EEE only visible on branch2
      OseeTypesUtil.checkAttributes(typeCache, "444", branch2, "AAA", "FFF", "CCC", "DDD", "EEE");
      OseeTypesUtil.checkAttributes(typeCache, "555", branch2, "AAA", "GGG", "FFF", "CCC", "DDD", "EEE");
      OseeTypesUtil.checkAttributes(typeCache, "666", branch2, "AAA", "HHH", "DDD", "CCC", "EEE");
   }

   public void testRelationTypeSides() throws OseeCoreException {
      OseeTypesUtil.checkInheritance(typeCache, "1A", RelationSide.SIDE_A, 1, "111");
      OseeTypesUtil.checkInheritance(typeCache, "1A", RelationSide.SIDE_B, 1, "444", "555");

      OseeTypesUtil.checkInheritance(typeCache, "2B", RelationSide.SIDE_A, 1, "555");
      OseeTypesUtil.checkInheritance(typeCache, "2B", RelationSide.SIDE_B, Integer.MAX_VALUE, "000", "111", "222",
            "333", "444", "555", "666");

      OseeTypesUtil.checkInheritance(typeCache, "3C", RelationSide.SIDE_A, Integer.MAX_VALUE, "222", "333", "444",
            "555", "666");
      OseeTypesUtil.checkInheritance(typeCache, "3C", RelationSide.SIDE_B, 1, "333", "444", "555", "666");

      OseeTypesUtil.checkInheritance(typeCache, "4D", RelationSide.SIDE_A, Integer.MAX_VALUE, "666");
      OseeTypesUtil.checkInheritance(typeCache, "4D", RelationSide.SIDE_B, Integer.MAX_VALUE, "666");
   }

   private static Branch createBranchHelper(String guid, String name, int id, Branch parentBranch, BranchType branchType) {
      return new Branch(guid, name, id, parentBranch, 0, false, -1, new Timestamp(new Date().getTime()), "", -1,
            branchType, BranchState.CREATED);
   }

   private final static class EnumTypeCache extends OseeTypeDataAccessor<OseeEnumType> {
      private final List<OseeEnumType> oseeEnumTypes;

      public EnumTypeCache(List<OseeEnumType> oseeEnumTypes) {
         super();
         this.oseeEnumTypes = oseeEnumTypes;
      }

      @Override
      public void load(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E1", "Enum1", "AAA", 1, "BBB", 2, "CCC", 3));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E2", "Enum2", "DDD", 4, "EEE", 5, "FFF", 6));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E3", "Enum3", "GGG", 7, "HHH", 8, "III", 9));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E4", "Enum4", "JJJ", 10, "KKK", 11, "LLL", 12));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E5", "Enum5", "MMM", 1, "NNN", 2, "OOO", 3));
         int typeId = 400;
         for (OseeEnumType type : oseeEnumTypes) {
            type.setTypeId(typeId++);
            cache.getEnumTypeCache().cacheType(type);
         }
      }
   }

   private final static class RelationCache extends OseeTypeDataAccessor<RelationType> {
      private final List<RelationType> relationTypes;

      public RelationCache(List<RelationType> relationTypes) {
         super();
         this.relationTypes = relationTypes;
      }

      private RelationType createRelationHelper(OseeTypeCache cache, IOseeTypeFactory factory, String guid, String name, String aGUID, String bGUID, RelationTypeMultiplicity multiplicity) throws OseeCoreException {
         ArtifactType type1 = cache.getArtifactTypeCache().getTypeByGuid(aGUID);
         ArtifactType type2 = cache.getArtifactTypeCache().getTypeByGuid(bGUID);
         return factory.createRelationType(guid, name, name + "_A", name + "_B", type1, type2, multiplicity, true, "");
      }

      @Override
      public void load(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
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
   }
   private final static class AttributeCache extends OseeTypeDataAccessor<AttributeType> {

      private final List<AttributeType> attributeTypes;

      public AttributeCache(List<AttributeType> attributeTypes) {
         super();
         this.attributeTypes = attributeTypes;
      }

      private AttributeType createAttributeTypeHelper(IOseeTypeFactory factory, String guid, String name) throws OseeCoreException {
         return factory.createAttributeType(guid, name, "DummyBase", "DummyProvider", StringAttribute.class,
               DefaultAttributeDataProvider.class, "none", "none", null, 1, 1, "test data", null);
      }

      @Override
      public void load(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
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
   }
   private final static class ArtifactCache extends OseeTypeDataAccessor<ArtifactType> {

      private final List<ArtifactType> artifactTypes;
      private final Branch branch1;
      private final Branch branch2;

      public ArtifactCache(List<ArtifactType> artifactTypes, Branch branch1, Branch branch2) {
         super();
         this.artifactTypes = artifactTypes;
         this.branch1 = branch1;
         this.branch2 = branch2;
      }

      @Override
      public void load(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
         ArtifactTypeCache typeCache = cache.getArtifactTypeCache();
         artifactTypes.add(factory.createArtifactType(typeCache, "000", true, "BaseArtifactType"));
         artifactTypes.add(factory.createArtifactType(typeCache, "111", true, "ArtifactType1"));
         artifactTypes.add(factory.createArtifactType(typeCache, "222", false, "ArtifactType2"));
         artifactTypes.add(factory.createArtifactType(typeCache, "333", true, "ArtifactType3"));
         artifactTypes.add(factory.createArtifactType(typeCache, "444", false, "ArtifactType4"));
         artifactTypes.add(factory.createArtifactType(typeCache, "555", true, "ArtifactType5"));
         artifactTypes.add(factory.createArtifactType(typeCache, "666", false, "ArtifactType6"));
         int typeId = 100;
         for (ArtifactType type : artifactTypes) {
            type.setTypeId(typeId++);
            typeCache.cacheType(type);
         }
         setUpArtifactTypeInheritance(typeCache);
         setUpTypeValidity(cache);
      }

      private void setUpArtifactTypeInheritance(ArtifactTypeCache cache) throws OseeCoreException {
         ArtifactType baseType = cache.getTypeByName("BaseArtifactType");

         Set<ArtifactType> baseSuperType = new HashSet<ArtifactType>();
         baseSuperType.add(baseType);
         // 0<-1
         cache.setArtifactSuperType(cache.getTypeByGuid("111"), baseSuperType);
         // 0<-2
         cache.setArtifactSuperType(cache.getTypeByGuid("222"), baseSuperType);
         // 2<-3
         cache.setArtifactSuperType(cache.getTypeByGuid("333"), Arrays.asList(cache.getTypeByGuid("222")));
         // 2,3<-4 
         cache.setArtifactSuperType(cache.getTypeByGuid("444"), Arrays.asList(cache.getTypeByGuid("222"),
               cache.getTypeByGuid("333"), baseType));
         // 4<-5 
         cache.setArtifactSuperType(cache.getTypeByGuid("555"), Arrays.asList(cache.getTypeByGuid("444"), baseType));
         // 3<-6 
         cache.setArtifactSuperType(cache.getTypeByGuid("666"), Arrays.asList(cache.getTypeByGuid("333"), baseType));
      }

      private void setUpTypeValidity(OseeTypeCache cache) throws OseeCoreException {
         ArtifactTypeCache artCache = cache.getArtifactTypeCache();
         AttributeTypeCache attributeCache = cache.getAttributeTypeCache();

         artCache.cacheTypeValidity(artCache.getTypeByGuid("000"), attributeCache.getTypeByGuid("AAA"), branch1);
         artCache.cacheTypeValidity(artCache.getTypeByGuid("111"), attributeCache.getTypeByGuid("BBB"), branch1);
         artCache.cacheTypeValidity(artCache.getTypeByGuid("222"), attributeCache.getTypeByGuid("CCC"), branch1);
         artCache.cacheTypeValidity(artCache.getTypeByGuid("333"), attributeCache.getTypeByGuid("DDD"), branch1);
         artCache.cacheTypeValidity(artCache.getTypeByGuid("333"), attributeCache.getTypeByGuid("EEE"), branch2);
         artCache.cacheTypeValidity(artCache.getTypeByGuid("444"), attributeCache.getTypeByGuid("FFF"), branch1);
         artCache.cacheTypeValidity(artCache.getTypeByGuid("555"), attributeCache.getTypeByGuid("GGG"), branch1);
         artCache.cacheTypeValidity(artCache.getTypeByGuid("666"), attributeCache.getTypeByGuid("HHH"), branch1);
      }

   }
}
