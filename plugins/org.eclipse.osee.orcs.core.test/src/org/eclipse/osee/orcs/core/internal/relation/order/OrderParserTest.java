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

package org.eclipse.osee.orcs.core.internal.relation.order;

import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link OrderParser}
 *
 * @author Roberto E. Escobar
 */
public class OrderParserTest {
   private static final RelationTypeSide relationType1 = CoreRelationTypes.DefaultHierarchical_Child;
   private static final RelationTypeSide relationType2 = CoreRelationTypes.Allocation_Requirement;

   private static final List<String> ORDER_LIST_1 =
      Arrays.asList("AAABDEJ_mIQBf8VXVtGqvA", "AAABDEJ_nMkBf8VXVXptpg", "AAABDEJ_oQ8Bf8VXLX7U_g");
   private static final List<String> ORDER_LIST_2 = Arrays.asList("AAABDEJ_mIQBf8VXVtGqvA");
   private static final List<String> ORDER_LIST_3 = Arrays.asList("AAABDEJ_mIQXf8VXVtGqvA", "AAABDEJ_oQVBf8VXLX7U_g");

   //@formatter:off
   private static final String ENTRY_PATTERN = "<Order relType=\"%s\" side=\"%s\" orderType=\"%s\" list=\"%s\"/>";
   private static final String ENTRY_NO_LIST_PATTERN = "<Order relType=\"%s\" side=\"%s\" orderType=\"%s\"/>";
   private static final String ONE_ENTRY_PATTERN = String.format("<OrderList>%s</OrderList>", ENTRY_PATTERN);
   private static final String ONE_ENTRY_NO_LIST_PATTERN = String.format("<OrderList>%s</OrderList>", ENTRY_NO_LIST_PATTERN);
   private static final String TWO_ENTRY_PATTERN = String.format("<OrderList>%s%s</OrderList>", ENTRY_PATTERN, ENTRY_PATTERN);

   private static final String DATA_1 = String.format(ONE_ENTRY_PATTERN, relationType1.getName(), RelationSide.SIDE_B, USER_DEFINED.getGuid(), Collections.toString(",", ORDER_LIST_1));
   private static final String DATA_2 = String.format(ONE_ENTRY_PATTERN, relationType1.getName(), RelationSide.SIDE_B, USER_DEFINED.getGuid(),  Collections.toString(",", ORDER_LIST_2));
   private static final String DATA_3 = String.format(TWO_ENTRY_PATTERN,
      relationType1.getName(), RelationSide.SIDE_B, USER_DEFINED.getGuid(), Collections.toString(",", ORDER_LIST_2),
      relationType2.getName(), RelationSide.SIDE_A, LEXICOGRAPHICAL_ASC.getGuid(), Collections.toString(",", ORDER_LIST_3));
   private static final String DATA_4 = String.format(ONE_ENTRY_NO_LIST_PATTERN, relationType2.getName(), RelationSide.SIDE_A, LEXICOGRAPHICAL_DESC.getGuid());

   private static final String EMPTY_TYPE = "<OrderList><Order side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\"></Order></OrderList>";
   private static final String EMPTY_SIDE = "<OrderList><Order relType=\"X\" orderType=\"AAT0xogoMjMBhARkBZQA\"></Order></OrderList>";
   private static final String EMPTY_ORDER_TYPE = "<OrderList><Order relType=\"X\" side=\"SIDE_B\"></Order></OrderList>";
   private static final String NO_ENTRIES = "<OrderList/>";
   //@formatter:on

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private OrcsTokenService tokenService;
   @Mock private HasOrderData hasOrderData;

   @Captor private ArgumentCaptor<RelationTypeSide> typeSideCaptor;
   @Captor private ArgumentCaptor<OrderData> orderDataCaptor;
   // @formatter:on

   private OrderParser parser;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      parser = new OrderParser(tokenService);

      final Collection<? extends RelationTypeToken> types = Arrays.asList(relationType1, relationType2);

      when(tokenService.getValidRelationTypes()).thenAnswer(new Answer<Collection<? extends RelationTypeToken>>() {

         @Override
         public Collection<? extends RelationTypeToken> answer(InvocationOnMock invocation) throws Throwable {
            return types;
         }

      });
   }

   @Test
   public void testLoadFromXmlNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("orderData cannot be null");
      parser.loadFromXml(null, "");
   }

   @Test
   public void testToXmlNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("orderData cannot be null");
      parser.toXml(null);
   }

   @Test
   public void testInvalidXml() {
      thrown.expect(OseeCoreException.class);
      parser.loadFromXml(hasOrderData, "<OrderList");
      verify(hasOrderData, never()).add(typeSideCaptor.capture(), orderDataCaptor.capture());
   }

   @Test
   public void testInvalidData() {
      parser.loadFromXml(hasOrderData, null);
      verify(hasOrderData, never()).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      reset(hasOrderData);
      parser.loadFromXml(hasOrderData, "");
      verify(hasOrderData, never()).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      reset(hasOrderData);
      parser.loadFromXml(hasOrderData, NO_ENTRIES);
      verify(hasOrderData, never()).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      reset(hasOrderData);
      parser.loadFromXml(hasOrderData, EMPTY_TYPE);
      verify(hasOrderData, never()).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      reset(hasOrderData);
      parser.loadFromXml(hasOrderData, EMPTY_SIDE);
      verify(hasOrderData, never()).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      reset(hasOrderData);
      parser.loadFromXml(hasOrderData, EMPTY_ORDER_TYPE);
      verify(hasOrderData, never()).add(typeSideCaptor.capture(), orderDataCaptor.capture());
   }

   @Test
   public void testWithData1() {
      parser.loadFromXml(hasOrderData, DATA_1);

      verify(hasOrderData, times(1)).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      verifyData(0, relationType1, USER_DEFINED, ORDER_LIST_1);
   }

   @Test
   public void testWithData2() {
      parser.loadFromXml(hasOrderData, DATA_2);

      verify(hasOrderData, times(1)).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      verifyData(0, relationType1, USER_DEFINED, ORDER_LIST_2);
   }

   @Test
   public void testWithData3() {
      parser.loadFromXml(hasOrderData, DATA_3);

      verify(hasOrderData, times(2)).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      verifyData(0, relationType1, USER_DEFINED, ORDER_LIST_2);
      verifyData(1, relationType2, LEXICOGRAPHICAL_ASC, ORDER_LIST_3);
   }

   @Test
   public void testWithData4EmptyList() {
      parser.loadFromXml(hasOrderData, DATA_4);

      verify(hasOrderData, times(1)).add(typeSideCaptor.capture(), orderDataCaptor.capture());

      verifyData(0, relationType2, LEXICOGRAPHICAL_DESC);
   }

   @Test
   public void testToXml() {
      //@formatter:off
      Map<RelationTypeSide, OrderData> data = new LinkedHashMap<>();
      add(relationType1, data, USER_DEFINED, ORDER_LIST_2);
      add(relationType2, data, LEXICOGRAPHICAL_ASC, ORDER_LIST_3);
      //@formatter:on

      when(hasOrderData.iterator()).thenReturn(data.entrySet().iterator());

      String actual = parser.toXml(hasOrderData);
      assertEquals(DATA_3, actual);
   }

   @Test
   public void testToXmlEmptyEntries() {
      Map<RelationTypeSide, OrderData> data = new LinkedHashMap<>();
      when(hasOrderData.iterator()).thenReturn(data.entrySet().iterator());
      when(hasOrderData.isEmpty()).thenReturn(true);

      String actual = parser.toXml(hasOrderData);
      assertEquals(NO_ENTRIES, actual);
   }

   @Test
   public void testToXmlEmptyList() {
      Map<RelationTypeSide, OrderData> data = new LinkedHashMap<>();
      add(relationType2, data, LEXICOGRAPHICAL_DESC);
      when(hasOrderData.iterator()).thenReturn(data.entrySet().iterator());

      String actual = parser.toXml(hasOrderData);

      assertEquals(DATA_4, actual);
   }

   private void add(RelationTypeSide typeSide, Map<RelationTypeSide, OrderData> data, RelationSorter sorter) {
      add(typeSide, data, sorter, java.util.Collections.<String> emptyList());
   }

   private void add(RelationTypeSide typeSide, Map<RelationTypeSide, OrderData> data, RelationSorter sorter,
      List<String> list) {
      OrderData orderData = new OrderData(sorter, list);
      data.put(typeSide, orderData);
   }

   private void verifyData(int index, RelationTypeSide relationType, RelationSorter sorter) {
      verifyData(index, relationType, sorter, java.util.Collections.<String> emptyList());
   }

   private void verifyData(int index, RelationTypeSide relationType, RelationSorter sorter, List<String> list) {
      RelationTypeSide actualTypeSide = typeSideCaptor.getAllValues().get(index);
      OrderData actualData = orderDataCaptor.getAllValues().get(index);

      assertEquals(relationType.getSide(), actualTypeSide.getSide());
      assertEquals(relationType.getId(), actualTypeSide.getId());
      assertEquals(sorter, actualData.getSorterId());

      List<String> actualIds = actualData.getOrderIds();
      assertEquals(list.size(), actualIds.size());

      int itemIndex = 0;
      for (String expectedId : list) {
         assertEquals(expectedId, actualIds.get(itemIndex++));
      }
   }
}