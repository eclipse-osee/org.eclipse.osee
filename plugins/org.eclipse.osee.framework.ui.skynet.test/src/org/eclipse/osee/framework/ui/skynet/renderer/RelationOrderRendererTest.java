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
package org.eclipse.osee.framework.ui.skynet.renderer;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.ui.skynet.render.ArtifactGuidToWordML;
import org.eclipse.osee.framework.ui.skynet.render.RelationOrderRenderer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderRendererTest {

   private static RelationOrderRenderer renderer;
   private static RelationType relType1;
   private static RelationType relType2;
   private static RelationType relType3;

   @BeforeClass
   public static void prepareTest() throws Exception {
      MockArtifactGuidResolver resolver = new MockArtifactGuidResolver(null);

      AbstractOseeCache<RelationType> typeCache = new RelationTypeCache();
      relType1 = createRelationType(1, typeCache, "Relation 1", Artifact, Artifact);
      relType2 = createRelationType(2, typeCache, "Relation 2", Artifact, Artifact);
      relType3 = createRelationType(3, typeCache, "Relation 3", Artifact, Artifact);
      renderer = new RelationOrderRenderer(typeCache, resolver);
   }

   @Test
   public void testRenderingAllValid()  {
      RelationOrderData orderData = new RelationOrderData(null, null);
      List<Object[]> expectedData = new ArrayList<>();

      addData(orderData, expectedData, relType1, "Relation 1_A", SIDE_A, LEXICOGRAPHICAL_ASC, "1", "2", "3");
      addData(orderData, expectedData, relType2, "Relation 2_B", SIDE_B, UNORDERED, "4", "5", "6");
      addData(orderData, expectedData, relType3, "Relation 3_B", SIDE_B, USER_DEFINED, "7", "8", "9");

      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   @Test
   public void testRenderingEmptyGuids()  {
      RelationOrderData orderData = new RelationOrderData(null, null);
      List<Object[]> expectedData = new ArrayList<>();
      addData(orderData, expectedData, relType1, "Relation 1_A", RelationSide.SIDE_A, USER_DEFINED);
      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   @Test
   public void testEmptyData() {
      RelationOrderData orderData = new RelationOrderData(null, null);
      List<Object[]> expectedData = new ArrayList<>();
      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   private void addData(RelationOrderData orderData, List<Object[]> expectedData, RelationTypeToken relationType, String relationSideName, RelationSide side, RelationSorter expectedSorterId, String... guids)  {
      List<String> guidList = Arrays.asList(guids);
      orderData.addOrderList(relationType, side, expectedSorterId, guidList);
      expectedData.add(new Object[] {
         relationType.getName(),
         relationSideName,
         side.name().toLowerCase(),
         expectedSorterId,
         guidList});
   }

   private String getExpected(List<Object[]> data) {
      StringBuilder builder = new StringBuilder();
      builder.append(
         "<wx:sub-section><w:tbl><w:tblPr><w:tblW w:w=\"8200\" w:type=\"dxa\"/><w:jc w:val=\"center\"/></w:tblPr>");
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

   private void checkRelationOrderRenderer(String expected, RelationOrderData orderData) {
      StringBuilder builder = new StringBuilder();
      WordMLProducer producer = new WordMLProducer(builder);
      renderer.toWordML(producer, null, orderData);
      Assert.assertEquals(expected, builder.toString());
   }

   private final static RelationType createRelationType(long id, AbstractOseeCache<RelationType> cache, String name, IArtifactType artifactType1, IArtifactType artifactType2)  {
      RelationType type = new RelationType(id, name, name + "_A", name + "_B", artifactType1, artifactType2,
         RelationTypeMultiplicity.MANY_TO_MANY, null);
      cache.cache(type);
      return type;
   }

   private static final class MockArtifactGuidResolver extends ArtifactGuidToWordML {

      public MockArtifactGuidResolver(OseeLinkBuilder linkBuilder) {
         super(linkBuilder);
      }

      @Override
      public List<String> resolveAsOseeLinks(BranchId branch, List<String> artifactGuids) {
         return artifactGuids;
      }
   }
}