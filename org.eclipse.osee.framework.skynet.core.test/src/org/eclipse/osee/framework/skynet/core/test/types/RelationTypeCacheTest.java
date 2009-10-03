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
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.RelationTypeCache;
import org.junit.BeforeClass;

/**
 * Low-level OseeTypeCache Test - This test does not require database access
 * 
 * @author Roberto E. Escobar
 */
public class RelationTypeCacheTest extends AbstractOseeCacheTest<RelationType> {

   private static List<RelationType> data;
   private static IOseeTypeFactory factory;
   private static RelationTypeCache cache;
   private static ArtifactTypeCache artCache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      factory = new OseeTypeFactory();
      data = new ArrayList<RelationType>();
      List<ArtifactType> artData = new ArrayList<ArtifactType>();

      ArtifactDataAccessor artAccessor = new ArtifactDataAccessor(artData);
      artCache = new ArtifactTypeCache(factory, artAccessor);

      RelationDataAccessor relationAccessor = new RelationDataAccessor(artCache, data);
      cache = new RelationTypeCache(factory, relationAccessor);

      cache.ensurePopulated();
      Assert.assertTrue(artAccessor.wasLoaded());
      Assert.assertTrue(relationAccessor.wasLoaded());
   }

   public RelationTypeCacheTest() {
      super(data, cache);
   }

   @org.junit.Test
   public void testRelationTypeSides() throws OseeCoreException {
      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "1A", RelationSide.SIDE_A, 1, "111");
      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "1A", RelationSide.SIDE_B, 1, "444", "555");

      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "2B", RelationSide.SIDE_A, 1, "555");
      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "2B", RelationSide.SIDE_B, Integer.MAX_VALUE, "000",
            "111", "222", "333", "444", "555", "666");

      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "3C", RelationSide.SIDE_A, Integer.MAX_VALUE, "222",
            "333", "444", "555", "666");
      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "3C", RelationSide.SIDE_B, 1, "333", "444", "555",
            "666");

      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "4D", RelationSide.SIDE_A, Integer.MAX_VALUE, "666");
      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "4D", RelationSide.SIDE_B, Integer.MAX_VALUE, "666");
   }

   @Override
   public void testDirty() throws OseeCoreException {
   }

   private final static class ArtifactDataAccessor extends OseeTestDataAccessor<ArtifactType> {
      private final List<ArtifactType> artifactTypes;

      public ArtifactDataAccessor(List<ArtifactType> artifactTypes) {
         super();
         this.artifactTypes = artifactTypes;
      }

      @Override
      public void load(AbstractOseeCache<ArtifactType> cache, IOseeTypeFactory factory) throws OseeCoreException {
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
         // 2,3<-4 
         cache.cacheArtifactSuperType(cache.getByGuid("444"), Arrays.asList(cache.getByGuid("222"),
               cache.getByGuid("333"), baseType));
         // 4<-5 
         cache.cacheArtifactSuperType(cache.getByGuid("555"), Arrays.asList(cache.getByGuid("444"), baseType));
         // 3<-6 
         cache.cacheArtifactSuperType(cache.getByGuid("666"), Arrays.asList(cache.getByGuid("333"), baseType));
      }
   }

   private final static class RelationDataAccessor extends OseeTestDataAccessor<RelationType> {
      private final List<RelationType> relationTypes;
      private final AbstractOseeCache<ArtifactType> artCache;

      public RelationDataAccessor(AbstractOseeCache<ArtifactType> artCache, List<RelationType> relationTypes) {
         super();
         this.artCache = artCache;
         this.relationTypes = relationTypes;
      }

      @Override
      public void load(AbstractOseeCache<RelationType> cache, IOseeTypeFactory factory) throws OseeCoreException {
         artCache.ensurePopulated();
         super.load(cache, factory);
         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "1A", "REL_1", "111", "444",
               RelationTypeMultiplicity.ONE_TO_ONE));
         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "2B", "REL_2", "555", "000",
               RelationTypeMultiplicity.ONE_TO_MANY));
         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "3C", "REL_3", "222", "333",
               RelationTypeMultiplicity.MANY_TO_ONE));
         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "4D", "REL_4", "666", "666",
               RelationTypeMultiplicity.MANY_TO_MANY));
         int typeId = 300;
         for (RelationType type : relationTypes) {
            type.setId(typeId++);
            cache.cache(type);
         }
      }
   }
}
