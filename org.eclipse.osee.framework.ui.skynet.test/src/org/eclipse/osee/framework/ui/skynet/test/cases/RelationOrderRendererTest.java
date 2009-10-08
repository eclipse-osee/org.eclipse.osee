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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTestDataAccessor;
import org.eclipse.osee.framework.skynet.core.test.types.OseeTypesUtil;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.RelationTypeCache;
import org.eclipse.osee.framework.ui.skynet.render.ArtifactGuidToWordML;
import org.eclipse.osee.framework.ui.skynet.render.RelationOrderRenderer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderRendererTest {

   private static RelationOrderRenderer renderer;
   private static RelationSorterProvider sorterProvider;

   @BeforeClass
   public static void prepareTest() throws OseeCoreException {
      MockArtifactGuidResolver resolver = new MockArtifactGuidResolver(null);

      IOseeTypeFactory factory = new OseeTypeFactory();
      RelationTypeCache typeCache = new RelationTypeCache(factory, new OseeTestDataAccessor<RelationType>());
      addRelationTypeData(typeCache, factory);
      sorterProvider = new RelationSorterProvider();
      renderer = new RelationOrderRenderer(typeCache, resolver, sorterProvider);
   }

   @AfterClass
   public static void cleanupTest() {
      sorterProvider = null;
      renderer = null;
   }

   @Test
   public void testRenderingAllValid() throws OseeCoreException {
      RelationOrderData orderData = new MockRelationOrderData();
      List<Object[]> expectedData = new ArrayList<Object[]>();

      addData(orderData, expectedData, "Relation 1", "Relation 1_A", RelationSide.SIDE_A,
            RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC.getGuid(), "1", "2", "3");

      addData(orderData, expectedData, "Relation 2", "Relation 2_B", RelationSide.SIDE_B, //
            RelationOrderBaseTypes.UNORDERED.getGuid(), "4", "5", "6");

      addData(orderData, expectedData, "Relation 3", "Relation 3_B", RelationSide.OPPOSITE, //
            RelationOrderBaseTypes.USER_DEFINED.getGuid(), "7", "8", "9");

      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   @Test
   public void testRenderingOrderTypeNotFoud() throws OseeCoreException {
      RelationOrderData orderData = new MockRelationOrderData();
      List<Object[]> expectedData = new ArrayList<Object[]>();
      addData(orderData, expectedData, "Relation 1", "Relation 1_A", RelationSide.SIDE_A, GUID.create(), "0", "1", "2");
      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   @Test
   public void testRenderingEmptyGuids() throws OseeCoreException {
      RelationOrderData orderData = new MockRelationOrderData();
      List<Object[]> expectedData = new ArrayList<Object[]>();
      addData(orderData, expectedData, "Relation 1", "Relation 1_A", RelationSide.SIDE_A,
            RelationOrderBaseTypes.USER_DEFINED.getGuid());
      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   @Test
   public void testEmptyData() throws OseeCoreException {
      RelationOrderData orderData = new MockRelationOrderData();
      List<Object[]> expectedData = new ArrayList<Object[]>();
      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   private void addData(RelationOrderData orderData, List<Object[]> expectedData, String relationType, String relationSideName, RelationSide side, String relationOrderIdGuid, String... guids) {
      List<String> artGuids = new ArrayList<String>();
      if (guids != null && guids.length > 0) {
         artGuids.addAll(Arrays.asList(guids));
      }
      orderData.addOrderList(relationType, side.name(), relationOrderIdGuid, artGuids);

      String expectedOrderId = relationOrderIdGuid;
      try {
         IRelationSorter sorter = sorterProvider.getRelationOrder(relationOrderIdGuid);
         expectedOrderId = sorter.getSorterId().prettyName();
      } catch (Exception ex) {
         // Do Nothing
      }
      expectedData.add(new Object[] {relationType, relationSideName, side.name().toLowerCase(), expectedOrderId,
            artGuids});
   }

   private String getExpected(List<Object[]> data) {
      StringBuilder builder = new StringBuilder();
      builder.append("<wx:sub-section><w:tbl><w:tblPr><w:tblW w:w=\"8200\" w:type=\"dxa\"/><w:jc w:val=\"center\"/></w:tblPr>");
      if (data.isEmpty()) {
         builder.append("<w:tr>");
         builder.append("<w:tc>");
         builder.append(getCellData("None"));
         builder.append("</w:tc>");
         builder.append("</w:tr>");
      } else {
         builder.append("<w:tr>");
         builder.append("<w:tc>");
         builder.append(getCellData("Relation Type"));
         builder.append("</w:tc>");
         builder.append("<w:tc>");
         builder.append(getCellData("Side Name"));
         builder.append("</w:tc>");
         builder.append("<w:tc>");
         builder.append(getCellData("Side"));
         builder.append("</w:tc>");
         builder.append("<w:tc>");
         builder.append(getCellData("Order Type"));
         builder.append("</w:tc>");
         builder.append("<w:tc>");
         builder.append(getCellData("Related Artifacts"));
         builder.append("</w:tc>");
         builder.append("</w:tr>");
         for (Object[] dataArray : data) {
            builder.append("<w:tr>");
            for (int index = 0; index < dataArray.length; index++) {
               builder.append("<w:tc>");
               builder.append(getCellData(dataArray[index]));
               builder.append("</w:tc>");
            }
            builder.append("</w:tr>");
         }
      }
      builder.append("</w:tbl></wx:sub-section>");
      return builder.toString();
   }

   private String getCellData(Object object) {
      if (object instanceof Collection<?>) {
         Collection<?> values = (Collection<?>) object;
         if (!values.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Object data : values) {
               builder.append(getCellData(data));
            }
            return builder.toString();
         } else {
            return getCellData("None");
         }
      } else {
         return "<w:p><w:r><w:t>" + object + "</w:t></w:r></w:p>";
      }
   }

   private void checkRelationOrderRenderer(String expected, RelationOrderData orderData) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      WordMLProducer producer = new WordMLProducer(builder);
      renderer.toWordML(producer, null, orderData);
      Assert.assertEquals(expected, builder.toString());
   }

   private final static void addRelationTypeData(AbstractOseeCache<RelationType> cache, IOseeTypeFactory factory) throws OseeCoreException {
      ArtifactTypeCache artCache = new ArtifactTypeCache(factory, new OseeTestDataAccessor<ArtifactType>());
      ArtifactType artifactType1 = createArtifactType(artCache, factory, "Artifact 2");
      ArtifactType artifactType2 = createArtifactType(artCache, factory, "Artifact 1");

      createRelationType(cache, artCache, factory, "Relation 1", artifactType1, artifactType2);
      createRelationType(cache, artCache, factory, "Relation 2", artifactType1, artifactType2);
      createRelationType(cache, artCache, factory, "Relation 3", artifactType1, artifactType2);
   }

   private final static ArtifactType createArtifactType(AbstractOseeCache<ArtifactType> artCache, IOseeTypeFactory factory, String name) throws OseeCoreException {
      ArtifactType artifactType = factory.createArtifactType(artCache, GUID.create(), false, name);
      artCache.cache(artifactType);
      return artifactType;
   }

   private final static void createRelationType(AbstractOseeCache<RelationType> cache, AbstractOseeCache<ArtifactType> artCache, IOseeTypeFactory factory, String name, ArtifactType artifactType1, ArtifactType artifactType2) throws OseeCoreException {
      RelationType type =
            OseeTypesUtil.createRelationType(cache, artCache, factory, GUID.create(), name, artifactType1.getGuid(),
                  artifactType2.getGuid(), RelationTypeMultiplicity.MANY_TO_MANY);
      cache.cache(type);
   }

   private final static class MockRelationOrderData extends RelationOrderData {
      public MockRelationOrderData() {
         super(null, null);
      }
   }

   private static final class MockArtifactGuidResolver extends ArtifactGuidToWordML {

      public MockArtifactGuidResolver(OseeLinkBuilder linkBuilder) {
         super(linkBuilder);
      }

      @Override
      public List<String> resolveAsOseeLinks(Branch branch, List<String> artifactGuids) throws OseeCoreException {
         List<String> values = new ArrayList<String>();
         for (String guid : artifactGuids) {
            values.add(guid);
         }
         return values;
      }

   }
}
