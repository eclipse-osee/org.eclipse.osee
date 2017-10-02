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
package org.eclipse.osee.framework.skynet.core.relation.order;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.SupportingInfo_SupportedBy;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.MANY_TO_MANY;
import static org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity.ONE_TO_MANY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderParserTest {

   private static String oneEntry =
      "<OrderList>\n<Order relType=\"Default Hierarchical\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\" list=\"AAABDEJ_mIQBf8VXVtGqvA,AAABDEJ_nMkBf8VXVXptpg,AAABDEJ_oQ8Bf8VXLX7U_g\"/>\n</OrderList>";
   private static String oneDiffEntry =
      "<OrderList>\n<Order relType=\"Default Hierarchical\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\" list=\"AAABDEJ_mIQBf8VXVtGqvA\"/>\n</OrderList>";

   private static String twoEntries =
      "<OrderList>\n<Order relType=\"Default Hierarchical\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\" list=\"AAABDEJ_mIQBf8VXVtGqvA,AAABDEJ_oQ8Bf8VXLX7U_g\"/>\n" + "<Order relType=\"Supporting Info\" side=\"SIDE_A\" orderType=\"AAT0xogoMjMBhARkBZQA\" list=\"AAABDEJ_mIQXf8VXVtGqvA,AAABDEJ_oQVBf8VXLX7U_g\"/>\n</OrderList>";
   private static String oneEntryEmptyList =
      "<OrderList>\n<Order relType=\"Default Hierarchical\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\"/>\n</OrderList>";

   private static String emptyType =
      "<OrderList>\n<Order side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\"/>\n</OrderList>";
   private static String emptySide =
      "<OrderList>\n<Order relType=\"Default Hierarchical\" orderType=\"AAT0xogoMjMBhARkBZQA\"/>\n</OrderList>";
   private static String emptyOrderType =
      "<OrderList>\n<Order relType=\"Default Hierarchical\" side=\"SIDE_B\" />\n</OrderList>";

   @BeforeClass
   public static void setup() {
      RelationType dh = new RelationType(DEFAULT_HIERARCHY.getId(), DEFAULT_HIERARCHY.getName(), "parent", "child",
         Artifact, Artifact, ONE_TO_MANY, LEXICOGRAPHICAL_ASC);
      RelationType supportingInfo =
         new RelationType(SupportingInfo_SupportedBy.getId(), SupportingInfo_SupportedBy.getName(), "is supported by",
            "supporting info", Artifact, Artifact, MANY_TO_MANY, UNORDERED);
      RelationTypeManager.getCache().cache(dh);
      RelationTypeManager.getCache().cache(supportingInfo);
   }

   @Test
   public void testExceptions() {
      RelationOrderParser parser = new RelationOrderParser();
      try {
         parser.loadFromXml(null, "");
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
      try {
         parser.toXml(null);
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }
   }

   @Test
   public void testWithData1Parser()  {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new RelationOrderData(null, null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, oneEntry);

      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(1, data.size());

      List<Object[]> expectedData = new ArrayList<>();
      addData(expectedData, DEFAULT_HIERARCHY, RelationSide.SIDE_B, USER_DEFINED, "AAABDEJ_mIQBf8VXVtGqvA",
         "AAABDEJ_nMkBf8VXVXptpg", "AAABDEJ_oQ8Bf8VXLX7U_g");

      checkData(data, expectedData);
      Assert.assertEquals(oneEntry, parser.toXml(data));

      // Load Second time - check that data was cleared before loading
      parser.loadFromXml(data, oneEntry);
      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(1, data.size());
      Assert.assertEquals(oneEntry, parser.toXml(data));

      parser.loadFromXml(data, oneDiffEntry);
      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(1, data.size());
      Assert.assertEquals(oneDiffEntry, parser.toXml(data));
   }

   @Test
   public void testWithData2Parser()  {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new RelationOrderData(null, null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, twoEntries);
      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(2, data.size());

      List<Object[]> expectedData = new ArrayList<>();
      addData(expectedData, DEFAULT_HIERARCHY, RelationSide.SIDE_B, USER_DEFINED, "AAABDEJ_mIQBf8VXVtGqvA",
         "AAABDEJ_oQ8Bf8VXLX7U_g");
      addData(expectedData, SupportingInfo_SupportedBy, RelationSide.SIDE_A, USER_DEFINED, "AAABDEJ_mIQXf8VXVtGqvA",
         "AAABDEJ_oQVBf8VXLX7U_g");

      checkData(data, expectedData);
      Assert.assertEquals(twoEntries, parser.toXml(data));
   }

   @Test
   public void testOneEntryEmptyList()  {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new RelationOrderData(null, null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, oneEntryEmptyList);
      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(1, data.size());

      List<Object[]> expectedData = new ArrayList<>();
      addData(expectedData, DEFAULT_HIERARCHY, RelationSide.SIDE_B, USER_DEFINED);
      checkData(data, expectedData);
      Assert.assertEquals(oneEntryEmptyList, parser.toXml(data));
   }

   @Test
   public void testNullDataParser()  {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new RelationOrderData(null, null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, "");
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, "<OrderList></OrderList>");
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, emptyType);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, emptySide);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, emptyOrderType);
      checkEmptyEntries(data, parser);
   }

   @Test
   public void testBadDataParser()  {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new RelationOrderData(null, null);
      checkEmptyEntries(data, parser);

      try {
         parser.loadFromXml(data, "<OrderList");
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertNotNull(ex);
      }
      checkEmptyEntries(data, parser);
   }

   private void checkEmptyEntries(RelationOrderData data, RelationOrderParser parser)  {
      Assert.assertFalse(data.hasEntries());
      Assert.assertEquals("<OrderList>\n</OrderList>", parser.toXml(data));
   }

   private void addData(List<Object[]> expectedData, IRelationType relationType, RelationSide side, RelationSorter relationOrderIdGuid, String... guids) {
      expectedData.add(new Object[] {relationType, side, relationOrderIdGuid, Arrays.asList(guids)});
   }

   private void checkData(RelationOrderData orderData, List<Object[]> expectedValues) {
      int index = 0;
      Assert.assertEquals(expectedValues.size(), orderData.size());
      for (Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>> entry : orderData.getOrderedEntrySet()) {
         Object[] actual = new Object[] {
            entry.getKey().getFirst(),
            entry.getKey().getSecond(),
            entry.getValue().getFirst(),
            entry.getValue().getSecond()};
         Object[] expected = expectedValues.get(index++);
         Assert.assertEquals(expected.length, actual.length);
         for (int index2 = 0; index2 < expected.length; index2++) {
            Assert.assertEquals(expected[index2], actual[index2]);
         }
      }
   }
}