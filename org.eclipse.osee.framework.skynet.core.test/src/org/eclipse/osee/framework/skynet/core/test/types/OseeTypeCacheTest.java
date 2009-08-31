package org.eclipse.osee.framework.skynet.core.test.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DefaultAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache.ArtifactTypeCache;
import org.junit.BeforeClass;

public class OseeTypeCacheTest {
   private static List<ArtifactType> artifactTypes;
   private static List<AttributeType> attributeTypes;
   private static TestOseeTypeDataAccessor testAccessor;

   private static OseeTypeCache typeCache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      artifactTypes = new ArrayList<ArtifactType>();
      attributeTypes = new ArrayList<AttributeType>();
      testAccessor = new TestData(artifactTypes, attributeTypes);
      typeCache = new OseeTypeCache(testAccessor, new OseeTypeFactory());

      typeCache.getArtifactTypeData().getAllTypes();
      Assert.assertTrue(testAccessor.isLoadAllArtifactTypes());
      Assert.assertTrue(testAccessor.isLoadAllAttributeTypes());
      Assert.assertTrue(testAccessor.isLoadAllRelationTypes());
      Assert.assertTrue(testAccessor.isLoadAllTypeValidity());
      Assert.assertTrue(testAccessor.isLoadAllOseeEnumTypes());
   }

   @org.junit.Test
   public void testAllArtifactTypes() throws OseeCoreException {
      List<ArtifactType> actualTypes = new ArrayList<ArtifactType>(typeCache.getArtifactTypeData().getAllTypes());
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
         Assert.assertTrue(typeCache.getArtifactTypeData().existsByGuid(expected.getGuid()));
      }
      Assert.assertFalse(typeCache.getArtifactTypeData().existsByGuid("notExist"));
   }

   @org.junit.Test
   public void testCacheArtifactTypesByGuid() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeData().getTypeByGuid(expected.getGuid());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheArtifactTypesById() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeData().getTypeById(expected.getTypeId());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheArtifactTypesByName() throws OseeCoreException {
      for (ArtifactType expected : artifactTypes) {
         ArtifactType actual = typeCache.getArtifactTypeData().getTypeByName(expected.getName());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testAllAttributeTypes() throws OseeCoreException {
      List<AttributeType> actualTypes = new ArrayList<AttributeType>(typeCache.getAttributeTypeData().getAllTypes());
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
         Assert.assertTrue(typeCache.getAttributeTypeData().existsByGuid(expected.getGuid()));
      }
      Assert.assertFalse(typeCache.getAttributeTypeData().existsByGuid("notExist"));
   }

   @org.junit.Test
   public void testCacheAttributeTypesByGuid() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeData().getTypeByGuid(expected.getGuid());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheAttributeTypesById() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeData().getTypeById(expected.getTypeId());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheAttributeTypesByName() throws OseeCoreException {
      for (AttributeType expected : attributeTypes) {
         AttributeType actual = typeCache.getAttributeTypeData().getTypeByName(expected.getName());
         Assert.assertEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testArtifactInheritanceCycleDetect() throws OseeCoreException {
      ArtifactType baseType = typeCache.getArtifactTypeData().getTypeByName("BaseArtifactType");

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
   @SuppressWarnings("unchecked")
   public void testArtifactInheritance() throws OseeCoreException {
      ArtifactTypeCache cache = typeCache.getArtifactTypeData();
      List<ArtifactType> actualTypes = new ArrayList<ArtifactType>(typeCache.getArtifactTypeData().getAllTypes());

      ArtifactType baseType = cache.getTypeByName("BaseArtifactType");

      Set<ArtifactType> baseSuperType = new HashSet<ArtifactType>();
      baseSuperType.add(baseType);

      // 0<-1
      typeCache.addArtifactSuperType(cache.getTypeByGuid("111"), baseSuperType);
      // 0<-2
      typeCache.addArtifactSuperType(cache.getTypeByGuid("222"), baseSuperType);
      // 2<-3
      typeCache.addArtifactSuperType(cache.getTypeByGuid("333"),
            Collections.toSet(Arrays.asList(cache.getTypeByGuid("222"))));
      // 2,0<-4 
      typeCache.addArtifactSuperType(cache.getTypeByGuid("444"), Collections.toSet(Arrays.asList(
            cache.getTypeByGuid("222"), baseType)));
      // 4<-5 
      typeCache.addArtifactSuperType(cache.getTypeByGuid("444"), Collections.toSet(Arrays.asList(
            cache.getTypeByGuid("222"), baseType)));
      // 3<-6 
      typeCache.addArtifactSuperType(cache.getTypeByGuid("444"), Collections.toSet(Arrays.asList(
            cache.getTypeByGuid("222"), baseType)));

      // All inherit from base type
      for (ArtifactType artifactType : actualTypes) {
         Assert.assertTrue(
               String.format("[%s] does not inherit from [%s]", artifactType.getName(), baseType.getName()),
               artifactType.inheritsFrom(baseType));
      }
   }

   @org.junit.Test
   public void testInheritedAttributeTypes() throws OseeCoreException {

   }

   private static class TestData extends TestOseeTypeDataAccessor {
      private final List<ArtifactType> artifactTypes;
      private final List<AttributeType> attributeTypes;

      public TestData(List<ArtifactType> artifactTypes, List<AttributeType> attributeTypes) {
         this.artifactTypes = artifactTypes;
         this.attributeTypes = attributeTypes;
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
            cache.getArtifactTypeData().cacheType(type);
         }
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
         int typeId = 200;
         for (AttributeType type : attributeTypes) {
            type.setTypeId(typeId++);
            cache.getAttributeTypeData().cacheType(type);
         }
      }

      @Override
      public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
         super.loadAllTypeValidity(cache, artifactTypeFactory);
         //         cache.cacheTypeValidity(artifactType, attributeType, branch);
      }

      @Override
      public void storeValidity(CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>> validityData) throws OseeCoreException {
         super.storeValidity(validityData);
      }

   }
}
