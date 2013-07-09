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
package org.eclipse.osee.orcs.core.internal.proxy.handler;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.AttributeProxyFactory;
import org.eclipse.osee.orcs.core.internal.proxy.HasProxiedObject;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyUtil;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyWriteable;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeWriteable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link ArtifactWriteableInvocationHandler}
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactWriteableInvocationHandlerTest {

   //@formatter:off
   @Mock private Artifact proxiedObject;
   @Mock private Artifact copy;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private AttributeProxyFactory attributeProxyFactory;
   //@formatter:on

   private ArtifactWriteableInvocationHandler handler;

   @Before
   public void setup() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      handler = new ArtifactWriteableInvocationHandler(artifactFactory, attributeProxyFactory, proxiedObject);

      when(artifactFactory.clone(proxiedObject)).thenReturn(copy);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetProxiedObject() {
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, handler);

      Assert.assertNotNull(proxy);
      Assert.assertTrue(Proxy.isProxyClass(proxy.getClass()));
      Assert.assertTrue(proxy instanceof HasProxiedObject);
      ProxyWriteable<ArtifactReadable> proxied = (ProxyWriteable<ArtifactReadable>) proxy;
      Assert.assertEquals(proxiedObject, proxied.getProxiedObject());
      Assert.assertEquals(proxiedObject, proxied.getOriginalObject());
   }

   @Test
   public void testMethodInvocation() throws Exception {
      InvocationHandler spy = Mockito.spy(handler);
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, spy);

      Set<String> readMethods = new HashSet<String>();
      for (Method method : ArtifactReadable.class.getMethods()) {
         readMethods.add(method.getName());
      }
      for (Method method : Object.class.getMethods()) {
         readMethods.add(method.getName());
      }

      boolean copied = false;

      for (Method method : proxy.getClass().getMethods()) {
         Artifact object;
         if (!copied && readMethods.contains(method.getName())) {
            object = proxiedObject;
         } else {
            object = copy;
            copied = true;
         }
         ProxyTestHelper.checkNoneStaticMethodForwarding(method, proxy, object, spy);
      }
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCopyOnWrite() throws OseeCoreException {
      ArtifactWriteableInvocationHandler spy = Mockito.spy(handler);
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, spy);

      ProxyWriteable<Artifact> proxied = (ProxyWriteable<Artifact>) proxy;
      Assert.assertEquals(proxiedObject, proxied.getProxiedObject());
      Assert.assertEquals(proxiedObject, proxied.getOriginalObject());

      Assert.assertTrue(spy.isCopyRequired());
      proxy.createAttribute(null);

      Assert.assertFalse(spy.isCopyRequired());
      Assert.assertTrue(spy.isWriteAllowed());

      verify(spy).createCopyForWrite(proxiedObject);

      Assert.assertEquals(copy, proxied.getProxiedObject());
      Assert.assertEquals(proxiedObject, proxied.getOriginalObject());

      reset(spy);

      proxy.createAttribute(CoreAttributeTypes.Name);
      Assert.assertTrue(spy.isWriteAllowed());
      verify(spy, times(0)).createCopyForWrite(proxiedObject);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testGetAttributes() throws OseeCoreException {
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, handler);

      AttributeReadable<Object> r1 = Mockito.mock(AttributeReadable.class);
      AttributeReadable<Object> r2 = Mockito.mock(AttributeReadable.class);

      Attribute<Object> attr1 = Mockito.mock(Attribute.class);
      Attribute<Object> attr2 = Mockito.mock(Attribute.class);

      List<Attribute<Object>> list = new ArrayList<Attribute<Object>>();
      list.add(attr1);
      list.add(attr2);

      when(proxiedObject.getAttributes()).thenAnswer(new AttributesAnswer(list));
      when(attributeProxyFactory.createReadable(attr1)).thenReturn(r1);
      when(attributeProxyFactory.createReadable(attr2)).thenReturn(r2);

      List<AttributeReadable<Object>> proxiedList = proxy.getAttributes();
      Assert.assertEquals(list.size(), proxiedList.size());

      Iterator<AttributeReadable<Object>> iterator = proxiedList.iterator();
      Assert.assertEquals(r1, iterator.next());
      Assert.assertEquals(r2, iterator.next());
   }

   @SuppressWarnings({"unchecked"})
   @Test
   public void testGetAttributesByType() throws OseeCoreException {
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, handler);

      AttributeReadable<String> r1 = Mockito.mock(AttributeReadable.class);
      Attribute<String> attr1 = Mockito.mock(Attribute.class);

      List<Attribute<String>> list = new ArrayList<Attribute<String>>();
      list.add(attr1);

      when(proxiedObject.getAttributes(CoreAttributeTypes.Name)).thenAnswer(new StringAttributesAnswer(list));
      when(attributeProxyFactory.createReadable(attr1)).thenReturn(r1);

      List<AttributeReadable<String>> proxiedList = proxy.getAttributes(CoreAttributeTypes.Name);
      Assert.assertEquals(list.size(), proxiedList.size());

      Iterator<AttributeReadable<String>> iterator = proxiedList.iterator();
      Assert.assertEquals(r1, iterator.next());
   }

   @SuppressWarnings({"unchecked"})
   @Test
   public void testGetWriteableAttributes() throws OseeCoreException {
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, handler);

      AttributeWriteable<Object> r1 = Mockito.mock(AttributeWriteable.class);
      AttributeWriteable<Object> r2 = Mockito.mock(AttributeWriteable.class);

      Attribute<Object> attr1 = Mockito.mock(Attribute.class);
      Attribute<Object> attr2 = Mockito.mock(Attribute.class);

      List<Attribute<Object>> list = new ArrayList<Attribute<Object>>();
      list.add(attr1);
      list.add(attr2);

      when(copy.getWriteableAttributes()).thenAnswer(new AttributesAnswer(list));
      when(attributeProxyFactory.createWriteable(attr1)).thenReturn(r1);
      when(attributeProxyFactory.createWriteable(attr2)).thenReturn(r2);

      List<AttributeWriteable<Object>> proxiedList = proxy.getWriteableAttributes();
      Assert.assertEquals(list.size(), proxiedList.size());

      Iterator<AttributeWriteable<Object>> iterator = proxiedList.iterator();
      Assert.assertEquals(r1, iterator.next());
      Assert.assertEquals(r2, iterator.next());
   }

   @SuppressWarnings({"unchecked"})
   @Test
   public void testGetWriteableAttributesByType() throws OseeCoreException {
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, handler);

      AttributeWriteable<String> r1 = Mockito.mock(AttributeWriteable.class);
      Attribute<String> attr1 = Mockito.mock(Attribute.class);

      List<Attribute<String>> list = new ArrayList<Attribute<String>>();
      list.add(attr1);

      when(copy.getWriteableAttributes(CoreAttributeTypes.Name)).thenAnswer(new StringAttributesAnswer(list));
      when(attributeProxyFactory.createWriteable(attr1)).thenReturn(r1);

      List<AttributeWriteable<String>> proxiedList = proxy.getWriteableAttributes(CoreAttributeTypes.Name);
      Assert.assertEquals(list.size(), proxiedList.size());

      Iterator<AttributeWriteable<String>> iterator = proxiedList.iterator();
      Assert.assertEquals(r1, iterator.next());
   }

   private static final class AttributesAnswer implements Answer<List<Attribute<Object>>> {

      private final List<Attribute<Object>> list;

      public AttributesAnswer(List<Attribute<Object>> list) {
         this.list = list;
      }

      @Override
      public List<Attribute<Object>> answer(InvocationOnMock invocation) throws Throwable {
         return list;
      }

   }

   private static final class StringAttributesAnswer implements Answer<List<Attribute<String>>> {

      private final List<Attribute<String>> list;

      public StringAttributesAnswer(List<Attribute<String>> list) {
         this.list = list;
      }

      @Override
      public List<Attribute<String>> answer(InvocationOnMock invocation) throws Throwable {
         return list;
      }

   }

}
