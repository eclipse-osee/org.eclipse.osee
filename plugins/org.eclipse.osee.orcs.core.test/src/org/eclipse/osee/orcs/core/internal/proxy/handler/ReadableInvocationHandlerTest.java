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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.junit.Assert;
import org.eclipse.osee.orcs.core.internal.proxy.HasProxiedObject;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyUtil;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ReadableInvocationHandler}
 * 
 * @author Roberto E. Escobar
 */
public class ReadableInvocationHandlerTest {

   //@formatter:off
   @Mock private ArtifactReadable proxiedObject;
   //@formatter:on

   private ReadableInvocationHandler<ArtifactReadable> handler;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      handler = new ReadableInvocationHandler<ArtifactReadable>(proxiedObject);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetProxiedObject() {
      ArtifactReadable proxy = ProxyUtil.create(ArtifactReadable.class, handler);

      Assert.assertNotNull(proxy);
      Assert.assertTrue(Proxy.isProxyClass(proxy.getClass()));
      Assert.assertTrue(proxy instanceof HasProxiedObject);
      HasProxiedObject<ArtifactReadable> proxied = (HasProxiedObject<ArtifactReadable>) proxy;
      Assert.assertEquals(proxiedObject, proxied.getProxiedObject());
   }

   @Test
   public void testMethodInvocation() throws Exception {
      InvocationHandler spy = Mockito.spy(handler);
      ArtifactReadable proxy = ProxyUtil.create(ArtifactReadable.class, spy);

      for (Method method : proxy.getClass().getMethods()) {
         ProxyTestHelper.checkNoneStaticMethodForwarding(method, proxy, proxiedObject, spy);
      }
   }
}
