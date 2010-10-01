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
package org.eclipse.osee.framework.skynet.core.test.relation;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.relation.RelationCache;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.test.mocks.DataFactory;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link RelationCache}
 * 
 * @author Roberto E. Escobar
 */
public class RelationCacheTest {

   private static Branch branch1;
   private static Branch branch2;

   private static IArtifact artfact1;
   private static IArtifact artfact2;

   private static List<RelationLink> sourceLinksRelType1;
   private static List<RelationLink> sourceLinksRelType2;
   private static List<RelationLink> sourceLinksRelType1AndType2;

   private static RelationType relType1;
   private static RelationType relType2;

   @BeforeClass
   public static void setup() throws OseeCoreException {
      branch1 = MockDataFactory.createBranch(100);
      branch1.setId(100);

      branch2 = MockDataFactory.createBranch(200);
      branch2.setId(200);

      artfact1 = createArtifact(111, branch1);
      artfact2 = createArtifact(222, branch2);

      relType1 = DataFactory.createRelationType(51);
      relType2 = DataFactory.createRelationType(52);

      sourceLinksRelType1 = DataFactory.createLinks(4, branch1, relType1);
      sourceLinksRelType2 = DataFactory.createLinks(4, branch1, relType2);

      sourceLinksRelType1AndType2 = new ArrayList<RelationLink>();
      sourceLinksRelType1AndType2.addAll(sourceLinksRelType1);
      sourceLinksRelType1AndType2.addAll(sourceLinksRelType2);

      DataFactory.setEveryOtherToDeleted(sourceLinksRelType1);
      DataFactory.setEveryOtherToDeleted(sourceLinksRelType2);

      checkAssumptions();
   }

   @AfterClass
   public static void tearDown() {
      branch1 = null;
      branch2 = null;

      artfact1 = null;
      artfact2 = null;

      sourceLinksRelType1 = null;
      sourceLinksRelType2 = null;
      sourceLinksRelType1AndType2 = null;

      relType1 = null;
      relType2 = null;
   }

   @Test
   public void testAddRemoveFromCache() {
      RelationCache relCache = new RelationCache();
      fillCache(relCache);

      List<RelationLink> art1Rels = relCache.getAll(artfact1);
      int totalArt1 = sourceLinksRelType1.size() + sourceLinksRelType2.size();
      Assert.assertEquals(totalArt1, art1Rels.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1AndType2, art1Rels));

      int totalArt2 = sourceLinksRelType1.size();
      List<RelationLink> actualArt2 = relCache.getAll(artfact2);
      Assert.assertEquals(totalArt2, actualArt2.size());

      relCache.deCache(artfact1);

      Assert.assertEquals(0, relCache.getAll(artfact1).size());

      //Decache again check no problem
      relCache.deCache(artfact1);
      Assert.assertEquals(0, relCache.getAll(artfact1).size());

      // Check Art 2 still there
      Assert.assertEquals(sourceLinksRelType1.size(), relCache.getAll(artfact2).size());
      relCache.deCache(artfact2);
      Assert.assertEquals(0, relCache.getAll(artfact2).size());
   }

   @Test
   public void testCanAddCacheSameRelTwice() {
      RelationCache relCache = new RelationCache();

      RelationLink link1 = sourceLinksRelType1.iterator().next();

      relCache.cache(artfact1, link1);
      relCache.cache(artfact1, link1);

      Assert.assertEquals(2, relCache.getAll(artfact1).size());
   }

   @Test
   public void testGetByType() {
      RelationCache relCache = new RelationCache();
      fillCache(relCache);

      int totalArt1 = sourceLinksRelType1.size() + sourceLinksRelType2.size();
      Assert.assertEquals(totalArt1, relCache.getAll(artfact1).size());

      int totalArt2 = sourceLinksRelType1.size();
      List<RelationLink> actualArt2 = relCache.getAll(artfact2);
      Assert.assertEquals(totalArt2, actualArt2.size());

      // Find RelationType1s for Artifact 1
      List<RelationLink> art1RelType1 = relCache.getAllByType(artfact1, relType1);
      Assert.assertEquals(sourceLinksRelType1.size(), art1RelType1.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1, art1RelType1));
      Assert.assertTrue(Compare.isDifferent(sourceLinksRelType2, art1RelType1));

      // Find RelationType2s for Artifact 1
      List<RelationLink> art1RelType2 = relCache.getAllByType(artfact1, relType2);
      Assert.assertEquals(sourceLinksRelType1.size(), art1RelType2.size());
      Assert.assertTrue(Compare.isDifferent(sourceLinksRelType1, art1RelType2));
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType2, art1RelType2));

      // Find nothing
      List<RelationLink> relType2ForArt2 = relCache.getAllByType(artfact2, relType2);
      Assert.assertNull(relType2ForArt2);

      // Find RelationType1s for Artifact 2
      List<RelationLink> art2RelType1 = relCache.getAllByType(artfact2, relType1);
      Assert.assertEquals(sourceLinksRelType1.size(), art2RelType1.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1, art2RelType1));
      Assert.assertTrue(Compare.isDifferent(sourceLinksRelType2, art2RelType1));
   }

   @Test
   public void testGetRelations() {
      RelationCache relCache = new RelationCache();
      fillCache(relCache);

      List<RelationLink> art1RelsAll = relCache.getRelations(artfact1, DeletionFlag.INCLUDE_DELETED);

      int totalArt1 = sourceLinksRelType1.size() + sourceLinksRelType2.size();
      Assert.assertEquals(totalArt1, art1RelsAll.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1AndType2, art1RelsAll));

      List<RelationLink> art1RelsNoDeleteds = relCache.getRelations(artfact1, DeletionFlag.EXCLUDE_DELETED);
      Assert.assertEquals(totalArt1 / 2, art1RelsNoDeleteds.size());

      for (RelationLink link : art1RelsNoDeleteds) {
         Assert.assertFalse(String.format("Was deleted [%s]", link), link.isDeleted());
      }
   }

   @Test
   public void testGetRelationByIdOnArtifact() {
      RelationCache relCache = new RelationCache();

      RelationLink link11 = DataFactory.createRelationLink(0, 11, 22, branch1, relType1);
      RelationLink link12 = DataFactory.createRelationLink(0, 33, 44, branch1, relType1);

      RelationLink link21 = DataFactory.createRelationLink(1, 55, 66, branch2, relType2);
      RelationLink link22 = DataFactory.createRelationLink(1, 77, 88, branch2, relType2);

      relCache.cache(artfact1, link11);
      relCache.cache(artfact1, link12);

      relCache.cache(artfact2, link21);
      relCache.cache(artfact2, link22);

      RelationLink actual = null;

      // Find Relation Link Id 1 - 
      actual = relCache.getByRelIdOnArtifact(1, artfact1.getArtId(), 0, artfact1.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(1, artfact1.getArtId(), 0, artfact2.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(1, artfact2.getArtId(), 0, artfact1.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(1, artfact2.getArtId(), 0, artfact2.getBranch());
      Assert.assertEquals(link21, actual);
      Assert.assertFalse(link22.equals(actual));

      actual = relCache.getByRelIdOnArtifact(1, 0, artfact2.getArtId(), artfact2.getBranch());
      Assert.assertEquals(link21, actual);
      Assert.assertFalse(link22.equals(actual));

      // Find Relation Link Id 0 - 
      actual = relCache.getByRelIdOnArtifact(0, 0, artfact2.getArtId(), artfact2.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(0, 0, artfact2.getArtId(), artfact1.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(0, 0, artfact1.getArtId(), artfact2.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(0, 0, artfact1.getArtId(), artfact1.getBranch());
      Assert.assertEquals(link11, actual);
      Assert.assertFalse(link12.equals(actual));

      actual = relCache.getByRelIdOnArtifact(0, artfact1.getArtId(), 0, artfact1.getBranch());
      Assert.assertEquals(link11, actual);
      Assert.assertFalse(link12.equals(actual));
   }

   @Test
   public void testGetLoadedRelation() {
      RelationCache relCache = new RelationCache();

      int art1Id = artfact1.getArtId();
      int art2Id = artfact2.getArtId();

      RelationLink link11 = DataFactory.createRelationLink(0, art1Id, 22, branch1, relType1);
      RelationLink link12 = DataFactory.createRelationLink(0, 33, 44, branch1, relType1);

      RelationLink link21 = DataFactory.createRelationLink(1, 55, 66, branch2, relType2);
      RelationLink link22 = DataFactory.createRelationLink(1, 77, art2Id, branch2, relType2);
      link22.delete(false);

      relCache.cache(artfact1, link11);
      relCache.cache(artfact1, link12);

      relCache.cache(artfact2, link21);
      relCache.cache(artfact2, link22);

      RelationLink actual = null;
      // Find relation link 11
      actual = relCache.getLoadedRelation(artfact1, art1Id, 44, relType1, DeletionFlag.INCLUDE_DELETED);
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(artfact1, art1Id, 22, relType1, DeletionFlag.INCLUDE_DELETED);
      Assert.assertEquals(link11, actual);

      // Find relation link 22
      actual = relCache.getLoadedRelation(artfact2, 77, art2Id, relType2, DeletionFlag.EXCLUDE_DELETED);
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(artfact2, 77, art2Id, relType2, DeletionFlag.INCLUDE_DELETED);
      Assert.assertEquals(link22, actual);
   }

   @Test
   public void testGetLoadedRelationNoId() {
      RelationCache relCache = new RelationCache();

      RelationLink link11 = DataFactory.createRelationLink(0, 11, 22, branch1, relType1);
      RelationLink link12 = DataFactory.createRelationLink(1, 11, 22, branch1, relType1);
      RelationLink link13 = DataFactory.createRelationLink(3, 22, 11, branch1, relType1);

      RelationLink link21 = DataFactory.createRelationLink(4, 551, 661, branch2, relType2);
      RelationLink link22 = DataFactory.createRelationLink(5, 551, 661, branch2, relType2);
      RelationLink link23 = DataFactory.createRelationLink(6, 661, 551, branch2, relType2);

      relCache.cache(artfact1, link11);
      relCache.cache(artfact1, link12);
      relCache.cache(artfact1, link13);

      relCache.cache(artfact2, link21);
      relCache.cache(artfact2, link22);
      relCache.cache(artfact2, link23);

      RelationLink actual = null;

      //  Must match branch
      actual = relCache.getLoadedRelation(relType1, artfact1.getArtId(), 22, artfact2.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(relType2, artfact1.getArtId(), 22, artfact1.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(relType1, artfact1.getArtId(), 22, artfact1.getBranch());
      Assert.assertEquals(link11, actual);
      Assert.assertFalse(link12.equals(actual));

      actual = relCache.getLoadedRelation(relType1, 22, artfact1.getArtId(), artfact1.getBranch());
      Assert.assertEquals(link13, actual);

      actual = relCache.getLoadedRelation(relType1, artfact1.getArtId(), 11, artfact1.getBranch());
      Assert.assertEquals(link13, actual);

      // Find  Must match branch
      actual = relCache.getLoadedRelation(relType2, artfact2.getArtId(), 661, artfact1.getBranch());
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(relType2, artfact2.getArtId(), 661, artfact2.getBranch());
      Assert.assertEquals(link21, actual);
      Assert.assertFalse(link22.equals(actual));

      actual = relCache.getLoadedRelation(relType2, 661, artfact2.getArtId(), artfact2.getBranch());
      Assert.assertEquals(link23, actual);

      actual = relCache.getLoadedRelation(relType2, artfact2.getArtId(), 551, artfact2.getBranch());
      Assert.assertEquals(link23, actual);
   }

   private static void checkAssumptions() {
      Assert.assertTrue(!artfact1.equals(artfact2));
      Assert.assertTrue(artfact1.getBranch().getId() != artfact2.getBranch().getId());
   }

   private static void fillCache(RelationCache relCache) {
      for (RelationLink link : sourceLinksRelType1) {
         relCache.cache(artfact1, link);
      }

      for (RelationLink link : sourceLinksRelType1) {
         relCache.cache(artfact2, link);
      }

      for (RelationLink link : sourceLinksRelType2) {
         relCache.cache(artfact1, link);
      }
   }

   private static IArtifact createArtifact(int id, Branch branch) {
      return DataFactory.createArtifact(id, "Art-" + id, GUID.create(), branch);
   }

}
