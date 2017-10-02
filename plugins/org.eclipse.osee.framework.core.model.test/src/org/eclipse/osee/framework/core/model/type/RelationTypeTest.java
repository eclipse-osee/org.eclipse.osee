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

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeTest {
   @Ignore
   @Test
   public void test() {

   }
   //   @org.junit.Test
   //   public void testRelationTypeSides()  {
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "1A", RelationSide.SIDE_A, 1, "111");
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "1A", RelationSide.SIDE_B, 1, "444", "555");
   //
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "2B", RelationSide.SIDE_A, 1, "555");
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "2B", RelationSide.SIDE_B, Integer.MAX_VALUE, "000",
   //            "111", "222", "333", "444", "555", "666");
   //
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "3C", RelationSide.SIDE_A, Integer.MAX_VALUE, "222",
   //            "333", "444", "555", "666");
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "3C", RelationSide.SIDE_B, 1, "333", "444", "555",
   //            "666");
   //
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "4D", RelationSide.SIDE_A, Integer.MAX_VALUE, "666");
   //      OseeTypesUtil.checkRelationTypeInheritance(cache, artCache, "4D", RelationSide.SIDE_B, Integer.MAX_VALUE, "666");
   //   }
   //
   //   @Override
   //   public void testDirty()  {
   //   }
   //
   //   private final static class ArtifactDataAccessor extends MockOseeDataAccessor<ArtifactType> {
   //      private final List<ArtifactType> artifactTypes;
   //
   //      public ArtifactDataAccessor(List<ArtifactType> artifactTypes) {
   //         super();
   //         this.artifactTypes = artifactTypes;
   //      }
   //
   //      @Override
   //      public void load(AbstractOseeCache<ArtifactType> cache)  {
   //         super.load(cache);
   //         artifactTypes.add(factory.createArtifactType(cache, "000", true, "BaseArtifactType"));
   //         artifactTypes.add(factory.createArtifactType(cache, "111", true, "ArtifactType1"));
   //         artifactTypes.add(factory.createArtifactType(cache, "222", false, "ArtifactType2"));
   //         artifactTypes.add(factory.createArtifactType(cache, "333", true, "ArtifactType3"));
   //         artifactTypes.add(factory.createArtifactType(cache, "444", false, "ArtifactType4"));
   //         artifactTypes.add(factory.createArtifactType(cache, "555", true, "ArtifactType5"));
   //         artifactTypes.add(factory.createArtifactType(cache, "666", false, "ArtifactType6"));
   //         int typeId = 100;
   //         for (ArtifactType type : artifactTypes) {
   //            type.setId(typeId++);
   //            cache.cache(type);
   //         }
   //         ArtifactTypeCache artCache = (ArtifactTypeCache) cache;
   //         setUpArtifactTypeInheritance(artCache);
   //      }
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
   //         // 2,3<-4
   //         cache.cacheArtifactSuperType(cache.getByGuid("444"), Arrays.asList(cache.getByGuid("222"),
   //               cache.getByGuid("333"), baseType));
   //         // 4<-5
   //         cache.cacheArtifactSuperType(cache.getByGuid("555"), Arrays.asList(cache.getByGuid("444"), baseType));
   //         // 3<-6
   //         cache.cacheArtifactSuperType(cache.getByGuid("666"), Arrays.asList(cache.getByGuid("333"), baseType));
   //      }
   //   }
   //
   //   private final static class RelationDataAccessor extends MockOseeDataAccessor<RelationType> {
   //      private final List<RelationType> relationTypes;
   //      private final AbstractOseeCache<ArtifactType> artCache;
   //
   //      public RelationDataAccessor(AbstractOseeCache<ArtifactType> artCache, List<RelationType> relationTypes) {
   //         super();
   //         this.artCache = artCache;
   //         this.relationTypes = relationTypes;
   //      }
   //
   //      @Override
   //      public void load(AbstractOseeCache<RelationType> cache)  {
   //         artCache.ensurePopulated();
   //         super.load(cache);
   //         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "1A", "REL_1", "111", "444",
   //               RelationTypeMultiplicity.ONE_TO_ONE));
   //         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "2B", "REL_2", "555", "000",
   //               RelationTypeMultiplicity.ONE_TO_MANY));
   //         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "3C", "REL_3", "222", "333",
   //               RelationTypeMultiplicity.MANY_TO_ONE));
   //         relationTypes.add(OseeTypesUtil.createRelationType(cache, artCache, factory, "4D", "REL_4", "666", "666",
   //               RelationTypeMultiplicity.MANY_TO_MANY));
   //         int typeId = 300;
   //         for (RelationType type : relationTypes) {
   //            type.setId(typeId++);
   //            cache.cache(type);
   //         }
   //      }
   //   }
}
