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

import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationOrderAccessor;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.test.types.MockIArtifact;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTestDataAccessor;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTypesUtil;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.RelationTypeCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class RelationTypeSideSorterTest {

   private static RelationType relationType1;
   private static RelationType relationType2;

   private static RelationOrderData orderData1;
   private static RelationOrderData orderData2;

   private static RelationSorterProvider provider;

   private static ArtifactType artType1;
   private static ArtifactType artType2;
   private static ArtifactType artType3;
   private static ArtifactType artType4;

   private static IArtifact artifact1;
   private static IArtifact artifact2;

   private static IRelationOrderAccessor accessor;

   @BeforeClass
   public static void beforeTestClass() throws OseeCoreException {
      provider = new RelationSorterProvider();
      accessor = new TestRelationOrderAccessor();
      OseeTypeFactory factory = new OseeTypeFactory();
      RelationTypeCache typeCache = new RelationTypeCache(factory, new OseeTestDataAccessor<RelationType>());
      ArtifactTypeCache artCache = new ArtifactTypeCache(factory, new OseeTestDataAccessor<ArtifactType>());

      artType1 = factory.createArtifactType(artCache, GUID.create(), false, "Artifact Type 1");
      artType2 = factory.createArtifactType(artCache, GUID.create(), false, "Artifact Type 2");
      artType3 = factory.createArtifactType(artCache, GUID.create(), false, "Artifact Type 3");
      artType4 = factory.createArtifactType(artCache, GUID.create(), false, "Artifact Type 4");
      artCache.cache(artType1, artType2, artType3, artType4);

      relationType1 =
            OseeTypesUtil.createRelationType(typeCache, artCache, factory, GUID.create(), "RelationType1",
                  artType1.getGuid(), artType2.getGuid(), RelationTypeMultiplicity.MANY_TO_MANY);
      relationType2 =
            OseeTypesUtil.createRelationType(typeCache, artCache, factory, GUID.create(), "RelationType2",
                  artType3.getGuid(), artType4.getGuid(), RelationTypeMultiplicity.ONE_TO_MANY);

      IArtifact artifact1 = new MockIArtifact(100, "Mock Artifact 1", GUID.create(), null, artType1);
      IArtifact artifact2 = new MockIArtifact(100, "Mock Artifact 1", GUID.create(), null, artType2);

      orderData1 = new RelationOrderData(accessor, artifact1);
      orderData2 = new RelationOrderData(accessor, artifact2);
   }

   @AfterClass
   public static void afterTestClass() {
      relationType1 = null;
      relationType2 = null;

      orderData1 = null;
      orderData2 = null;

      provider = null;

      artType1 = null;
      artType2 = null;
      artType3 = null;
      artType4 = null;

      artifact1 = null;
      artifact2 = null;

      accessor = null;
   }

   @Test
   public void testConstruction() throws OseeCoreException {
      RelationTypeSideSorter relationSorter =
            new RelationTypeSideSorter(relationType1, RelationSide.SIDE_A, provider, orderData1);
      checkSorterData(relationType1, RelationSide.SIDE_A, relationSorter);

      relationSorter = new RelationTypeSideSorter(relationType1, RelationSide.SIDE_B, provider, orderData1);
      checkSorterData(relationType1, RelationSide.SIDE_B, relationSorter);
   }

   @Test
   public void testOrderId() throws OseeCoreException {
      //      RelationTypeSideSorter sorter =
      //            new RelationTypeSideSorter(relationType1, RelationSide.SIDE_A, provider, orderData1);
      //      IRelationOrderId actual = sorter.getOrderId();

      //      Assert.assertEquals("", actual.getGuid());
      //      Assert.assertEquals("", actual.prettyName());
   }

   @Test
   public void testOrderGuid() throws OseeCoreException {
      //      RelationTypeSideSorter sorter = new RelationTypeSideSorter(provider, relationType1, RelationSide.SIDE_A, orderData1);
      //      sorter.getOrderGuid();

   }

   @Test
   public void testOrderName() throws OseeCoreException {
      //      RelationSorter sorter = null;
      //      sorter.getOrderGuid();
   }

   @Test
   public void testSortedRelatives() throws OseeCoreException {
      //      RelationSorter sorter = null;
      //      List<IArtifact> sorted = sorter.getSortedRelatives(relatives);

   }

   @Test
   public void testEquals() throws OseeCoreException {
      //      RelationSorter a = new RelationSorter(RelationTypeManager.getType(6), RelationSide.SIDE_A);
      //      RelationSorter b = new RelationSorter(RelationTypeManager.getType(7), RelationSide.SIDE_B);
      //      assertFalse(a.equals(b));
      //      assertTrue(a.equals(a));
   }

   @Test
   public void testSetOrder() throws OseeCoreException {
      //      RelationSorter sorter;
      //      sorter.setOrder(orderId, relatives);
   }

   @Test
   public void testSort() throws OseeCoreException {

   }

   @Test
   public void testToString() throws OseeCoreException {
      RelationTypeSideSorter sorter =
            new RelationTypeSideSorter(relationType1, RelationSide.SIDE_A, provider, orderData1);
      String artGuid = sorter.getIArtifact().getGuid();
      Assert.assertEquals(
            "Relation Sorter {relationType=[RelationType1_A]<-[RelationType1]->[RelationType1_B], relationSide=[SIDE_A,RelationType1_A], artifact=[" + artGuid + "], sorterId=null}",
            sorter.toString());
   }

   private void checkSorterData(RelationType expectedRelationType, RelationSide expectedSide, RelationTypeSideSorter actual) throws OseeCoreException {
      Assert.assertEquals(expectedRelationType, actual.getRelationType());
      Assert.assertEquals(expectedSide, actual.getSide());
      Assert.assertEquals(orderData1.getIArtifact(), actual.getIArtifact());

      String expectedSideName;
      if (expectedSide == RelationSide.SIDE_A) {
         expectedSideName = expectedRelationType.getSideAName();
      } else {
         expectedSideName = expectedRelationType.getSideBName();
      }
      Assert.assertEquals(expectedSideName, actual.getSideName());

      Assert.assertTrue(actual.getIArtifact() instanceof MockIArtifact);
      MockIArtifact mockArtifact = (MockIArtifact) actual.getIArtifact();
      mockArtifact.clear();

      // Test Get Full Artifact is Called from IArtifact
      actual.getArtifact();
      Assert.assertTrue(mockArtifact.wasGetFullArtifactCalled());
   }

   private static final class TestRelationOrderAccessor implements IRelationOrderAccessor {

      public TestRelationOrderAccessor() {

      }

      @Override
      public void load(IArtifact artifact, RelationOrderData orderData) throws OseeCoreException {
         List<String> guids = Collections.emptyList();
         orderData.addOrderList(relationType1, RelationSide.SIDE_A, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC, guids);
         orderData.addOrderList(relationType2, RelationSide.SIDE_A, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC, guids);
      }

      @Override
      public void store(IArtifact artifact, RelationOrderData orderData) throws OseeCoreException {

      }
   }
}
