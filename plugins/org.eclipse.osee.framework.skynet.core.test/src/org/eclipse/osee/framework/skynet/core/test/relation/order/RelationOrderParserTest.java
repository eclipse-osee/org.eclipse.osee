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
package org.eclipse.osee.framework.skynet.core.test.relation.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderParser;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
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
         "<OrderList>\n<Order relType=\"Default Hierarchical\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\" list=\"AAABDEJ_mIQBf8VXVtGqvA,AAABDEJ_oQ8Bf8VXLX7U_g\"/>\n" + "<Order relType=\"Some Type\" side=\"SIDE_A\" orderType=\"AAABDEJ_mIQBf8VXVtGqvA\" list=\"AAABDEJ_mIQXf8VXVtGqvA,AAABDEJ_oQVBf8VXLX7U_g\"/>\n</OrderList>";
   private static String oneEntryEmptyList =
         "<OrderList>\n<Order relType=\"X\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\"/>\n</OrderList>";

   private static String emptyType =
         "<OrderList>\n<Order side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\"/>\n</OrderList>";
   private static String emptySide =
         "<OrderList>\n<Order relType=\"X\" orderType=\"AAT0xogoMjMBhARkBZQA\"/>\n</OrderList>";
   private static String emptyOrderType = "<OrderList>\n<Order relType=\"X\" side=\"SIDE_B\" />\n</OrderList>";

   @Test
   public void testExceptions() throws OseeCoreException {
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
   public void testWithData1Parser() throws OseeCoreException {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new MockRelationOrderData(null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, oneEntry);

      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(1, data.size());

      List<Object[]> expectedData = new ArrayList<Object[]>();
      addData(expectedData, "Default Hierarchical", RelationSide.SIDE_B, "AAT0xogoMjMBhARkBZQA",
            "AAABDEJ_mIQBf8VXVtGqvA", "AAABDEJ_nMkBf8VXVXptpg", "AAABDEJ_oQ8Bf8VXLX7U_g");

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
   public void testWithData2Parser() throws OseeCoreException {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new MockRelationOrderData(null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, twoEntries);
      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(2, data.size());

      List<Object[]> expectedData = new ArrayList<Object[]>();
      addData(expectedData, "Default Hierarchical", RelationSide.SIDE_B, "AAT0xogoMjMBhARkBZQA",
            "AAABDEJ_mIQBf8VXVtGqvA", "AAABDEJ_oQ8Bf8VXLX7U_g");
      addData(expectedData, "Some Type", RelationSide.SIDE_A, "AAABDEJ_mIQBf8VXVtGqvA", "AAABDEJ_mIQXf8VXVtGqvA",
            "AAABDEJ_oQVBf8VXLX7U_g");

      checkData(data, expectedData);
      Assert.assertEquals(twoEntries, parser.toXml(data));
   }

   @Test
   public void testOneEntryEmptyList() throws OseeCoreException {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new MockRelationOrderData(null);
      checkEmptyEntries(data, parser);

      parser.loadFromXml(data, oneEntryEmptyList);
      Assert.assertTrue(data.hasEntries());
      Assert.assertEquals(1, data.size());

      List<Object[]> expectedData = new ArrayList<Object[]>();
      addData(expectedData, "X", RelationSide.SIDE_B, "AAT0xogoMjMBhARkBZQA");
      checkData(data, expectedData);
      Assert.assertEquals(oneEntryEmptyList, parser.toXml(data));
   }

   @Test
   public void testNullDataParser() throws OseeCoreException {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new MockRelationOrderData(null);
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
   public void testBadDataParser() throws OseeCoreException {
      RelationOrderParser parser = new RelationOrderParser();

      RelationOrderData data = new MockRelationOrderData(null);
      checkEmptyEntries(data, parser);

      try {
         parser.loadFromXml(data, "<OrderList");
         Assert.assertNull("This line should not be executed");
      } catch (Exception ex) {
         Assert.assertNotNull(ex);
      }
      checkEmptyEntries(data, parser);
   }

   private void checkEmptyEntries(RelationOrderData data, RelationOrderParser parser) throws OseeArgumentException {
      Assert.assertFalse(data.hasEntries());
      Assert.assertEquals("<OrderList>\n</OrderList>", parser.toXml(data));
   }

   private void addData(List<Object[]> expectedData, String relationType, RelationSide side, String relationOrderIdGuid, String... guids) {
      List<String> artGuids = new ArrayList<String>();
      if (guids != null && guids.length > 0) {
         artGuids.addAll(Arrays.asList(guids));
      }
      expectedData.add(new Object[] {relationType, side.name(), relationOrderIdGuid, artGuids});
   }

   private void checkData(RelationOrderData orderData, List<Object[]> expectedValues) {
      int index = 0;
      Assert.assertEquals(expectedValues.size(), orderData.size());
      for (Entry<Pair<String, String>, Pair<String, List<String>>> entry : orderData.getOrderedEntrySet()) {
         Object[] actual =
               new Object[] {entry.getKey().getFirst(), entry.getKey().getSecond(), entry.getValue().getFirst(),
                     entry.getValue().getSecond()};
         Object[] expected = expectedValues.get(index++);
         Assert.assertEquals(expected.length, actual.length);
         for (int index2 = 0; index2 < expected.length; index2++) {
            Assert.assertEquals(expected[index2], actual[index2]);
         }
      }
   }

   private final static class MockRelationOrderData extends RelationOrderData {
      public MockRelationOrderData(IArtifact artifact) {
         super(null, artifact);
      }
   }
}
