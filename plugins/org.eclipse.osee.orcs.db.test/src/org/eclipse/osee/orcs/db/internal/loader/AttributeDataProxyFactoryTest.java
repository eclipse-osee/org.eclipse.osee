/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.DataProxyFactory;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeDataProxyFactory}
 *
 * @author Roberto E. Escobar
 */
public class AttributeDataProxyFactoryTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock DataProxyFactoryProvider proxyProvider;
   @Mock AttributeTypes attributeTypeCache;
   @Mock IAttributeType attributeType;
   @Mock DataProxyFactory dataProxyFactory;
   @Mock DataProxy proxy;
   //@formatter:on

   private AttributeDataProxyFactory factory;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      factory = new AttributeDataProxyFactory(proxyProvider, null, attributeTypeCache);
   }

   @Test
   public void testCreateFromDataArrayNull() throws OseeCoreException {
      long typeUuid = 45L;

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("data cannot be null");
      factory.createProxy(typeUuid, (Object[]) null);
   }

   @Test
   public void testCreateFromDataArrayNotEnoughFields() throws OseeCoreException {
      long typeUuid = 45L;
      Object[] data = new Object[] {"hello"};

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Data must have at least [2] elements - size was [1]");
      factory.createProxy(typeUuid, data);
   }

   @Test
   public void testCreateFromDataArrayTypeNotFound() throws OseeCoreException {
      long typeUuid = 45L;
      Object[] data = new Object[] {"hello", "uri"};

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("AttributeType cannot be null - Unable to find attributeType for [45]");

      factory.createProxy(typeUuid, data);
   }

   @Test
   public void testCreateFromStringsNullDataProxyFactory() throws OseeCoreException {
      long typeUuid = 45L;
      String value = "hello";
      String uri = "theUri";

      Mockito.when(attributeTypeCache.getByUuid(45L)).thenReturn(attributeType);
      Mockito.when(attributeTypeCache.getAttributeProviderId(attributeType)).thenReturn("org.eclipse.proxyfactory");

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("DataProxyFactory cannot be null - Unable to find data proxy factory for [proxyfactory]");
      factory.createProxy(typeUuid, value, uri);
   }

   @Test
   public void testCreateFromStrings() throws OseeCoreException {
      long typeUuid = 45L;
      String value = "hello";
      String uri = "theUri";

      Mockito.when(attributeTypeCache.getByUuid(45L)).thenReturn(attributeType);
      Mockito.when(attributeTypeCache.getAttributeProviderId(attributeType)).thenReturn("org.eclipse.proxyfactory");
      Mockito.when(proxyProvider.getFactory("proxyfactory")).thenReturn(dataProxyFactory);
      Mockito.when(dataProxyFactory.createInstance("proxyfactory")).thenReturn(proxy);

      DataProxy theProxy = factory.createProxy(typeUuid, value, uri);

      Assert.assertEquals(proxy, theProxy);
      Mockito.verify(proxy).setData("hello", "theUri");
   }

   @Test
   public void testCreateFromStringsNullUri() throws OseeCoreException {
      long typeUuid = 45L;
      String value = "hello";
      String uri = null;

      Mockito.when(attributeTypeCache.getByUuid(45L)).thenReturn(attributeType);
      Mockito.when(attributeTypeCache.getAttributeProviderId(attributeType)).thenReturn("org.eclipse.proxyfactory");
      Mockito.when(proxyProvider.getFactory("proxyfactory")).thenReturn(dataProxyFactory);
      Mockito.when(dataProxyFactory.createInstance("proxyfactory")).thenReturn(proxy);

      DataProxy theProxy = factory.createProxy(typeUuid, value, uri);

      Assert.assertEquals(proxy, theProxy);
      Mockito.verify(proxy).setData("hello", null);
   }

   @Test
   public void testCreateFromStringsNullValue() throws OseeCoreException {
      long typeUuid = 45L;
      String value = null;
      String uri = "theUri";

      Mockito.when(attributeTypeCache.getByUuid(45L)).thenReturn(attributeType);
      Mockito.when(attributeTypeCache.getAttributeProviderId(attributeType)).thenReturn("org.eclipse.proxyfactory");
      Mockito.when(proxyProvider.getFactory("proxyfactory")).thenReturn(dataProxyFactory);
      Mockito.when(dataProxyFactory.createInstance("proxyfactory")).thenReturn(proxy);

      DataProxy theProxy = factory.createProxy(typeUuid, value, uri);

      Assert.assertEquals(proxy, theProxy);
      Mockito.verify(proxy).setData(null, "theUri");
   }

   @Test
   public void testCreateFromStringsBothNull() throws OseeCoreException {
      long typeUuid = 45L;
      String value = null;
      String uri = null;

      Mockito.when(attributeTypeCache.getByUuid(45L)).thenReturn(attributeType);
      Mockito.when(attributeTypeCache.getAttributeProviderId(attributeType)).thenReturn("org.eclipse.proxyfactory");
      Mockito.when(proxyProvider.getFactory("proxyfactory")).thenReturn(dataProxyFactory);
      Mockito.when(dataProxyFactory.createInstance("proxyfactory")).thenReturn(proxy);

      DataProxy theProxy = factory.createProxy(typeUuid, value, uri);

      Assert.assertEquals(proxy, theProxy);
      Mockito.verify(proxy).setData(null, null);
   }
}
