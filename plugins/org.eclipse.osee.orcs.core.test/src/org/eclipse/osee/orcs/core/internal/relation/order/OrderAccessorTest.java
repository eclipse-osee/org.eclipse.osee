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
package org.eclipse.osee.orcs.core.internal.relation.order;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link OrderAccessorImpl}
 * 
 * @author Roberto E. Escobar
 */
public class OrderAccessorTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private OrderStore storage;
   @Mock private OrderParser parser;
   
   @Mock private HasOrderData orderData;
   // @formatter:on

   private OrderAccessor accessor;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      accessor = new OrderAccessorImpl(parser, storage);
   }

   @Test
   public void testLoad()  {
      String storedData = "data";
      when(storage.getOrderData()).thenReturn(storedData);

      accessor.load(orderData);

      verify(storage).getOrderData();
      verify(parser).loadFromXml(orderData, storedData);
   }

   @Test
   public void testStoreNotAccessible()  {
      String storedData = "data";

      when(storage.isAccessible()).thenReturn(false);
      when(orderData.isEmpty()).thenReturn(true);
      when(parser.toXml(orderData)).thenReturn(storedData);

      accessor.store(orderData, OrderChange.Forced);

      verify(storage).isAccessible();
      verify(parser, times(0)).toXml(orderData);
      verify(storage, times(0)).storeOrderData(OrderChange.Forced, storedData);
   }

   @Test
   public void testStoreEmptyData()  {
      String storedData = "data";

      when(storage.isAccessible()).thenReturn(true);
      when(orderData.isEmpty()).thenReturn(true);
      when(parser.toXml(orderData)).thenReturn(storedData);

      accessor.store(orderData, OrderChange.Forced);

      verify(storage).isAccessible();
      verify(parser, times(0)).toXml(orderData);
      verify(storage).storeOrderData(OrderChange.Forced, "");
   }

   @Test
   public void testStore()  {
      String storedData = "data";

      when(storage.isAccessible()).thenReturn(true);
      when(orderData.isEmpty()).thenReturn(false);
      when(parser.toXml(orderData)).thenReturn(storedData);

      accessor.store(orderData, OrderChange.Forced);

      verify(storage).isAccessible();
      verify(parser).toXml(orderData);
      verify(storage).storeOrderData(OrderChange.Forced, storedData);
   }

}
