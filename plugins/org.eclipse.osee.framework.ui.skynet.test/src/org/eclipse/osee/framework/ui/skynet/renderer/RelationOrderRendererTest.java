/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.renderer;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.CodeRequirement;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
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
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
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

   @BeforeClass
   public static void prepareTest() throws Exception {
      MockArtifactGuidResolver resolver = new MockArtifactGuidResolver(null);
      renderer = new RelationOrderRenderer(resolver);
   }

   @Test
   public void testRenderingAllValid() {
      RelationOrderData orderData = new RelationOrderData(null, null);
      List<Object[]> expectedData = new ArrayList<>();

      addData(orderData, expectedData, Allocation, SIDE_B, USER_DEFINED, "7", "8", "9");
      addData(orderData, expectedData, CodeRequirement, SIDE_B, UNORDERED, "4", "5", "6");
      addData(orderData, expectedData, DEFAULT_HIERARCHY, SIDE_A, LEXICOGRAPHICAL_ASC, "1", "2", "3");

      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   @Test
   public void testRenderingEmptyGuids() {
      RelationOrderData orderData = new RelationOrderData(null, null);
      List<Object[]> expectedData = new ArrayList<>();
      addData(orderData, expectedData, DEFAULT_HIERARCHY, RelationSide.SIDE_A, USER_DEFINED);
      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   @Test
   public void testEmptyData() {
      RelationOrderData orderData = new RelationOrderData(null, null);
      List<Object[]> expectedData = new ArrayList<>();
      checkRelationOrderRenderer(getExpected(expectedData), orderData);
   }

   private void addData(RelationOrderData orderData, List<Object[]> expectedData, RelationTypeToken relationType, RelationSide side, RelationSorter expectedSorterId, String... guids) {
      List<String> guidList = Arrays.asList(guids);
      orderData.addOrderList(relationType, side, expectedSorterId, guidList);
      expectedData.add(new Object[] {
         relationType.getName(),
         relationType.getSideName(side),
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
      var stringBuilder = new StringBuilder();
      var publishingAppender = FormatIndicator.WORD_ML.createPublishingAppender(stringBuilder);
      renderer.toWordML(publishingAppender, null, orderData);
      Assert.assertEquals(expected, stringBuilder.toString());
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