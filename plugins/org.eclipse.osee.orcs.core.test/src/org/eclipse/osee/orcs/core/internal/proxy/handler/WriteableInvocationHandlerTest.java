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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.junit.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.proxy.HasProxiedObject;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyUtil;
import org.eclipse.osee.orcs.core.internal.proxy.ProxyWriteable;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link WriteableInvocationHandler}
 * 
 * @author Roberto E. Escobar
 */
public class WriteableInvocationHandlerTest {

   @Mock
   private ArtifactWriteable proxiedObject;

   private WriteableInvocationHandler<ArtifactWriteable> handler;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      handler = new WriteableInvocationHandler<ArtifactWriteable>(proxiedObject);
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

   @SuppressWarnings("unchecked")
   @Test
   public void testCopyOnWrite() throws OseeCoreException {
      WriteableInvocationHandler<ArtifactWriteable> spy = Mockito.spy(handler);
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, spy);

      ProxyWriteable<ArtifactReadable> proxied = (ProxyWriteable<ArtifactReadable>) proxy;
      Assert.assertEquals(proxiedObject, proxied.getProxiedObject());
      Assert.assertEquals(proxiedObject, proxied.getOriginalObject());

      Assert.assertTrue(spy.isCopyRequired());
      proxy.createAttribute(null);

      Assert.assertFalse(spy.isCopyRequired());
      Assert.assertTrue(spy.isWriteAllowed());
      verify(spy).createCopyForWrite(proxiedObject);

      reset(spy);

      proxy.createAttribute(null);
      Assert.assertTrue(spy.isWriteAllowed());
      verify(spy, times(0)).createCopyForWrite(proxiedObject);
   }

   @Test
   public void testMethodInvocation() throws Exception {
      InvocationHandler spy = Mockito.spy(handler);
      ArtifactWriteable proxy = ProxyUtil.create(ArtifactWriteable.class, spy);

      for (Method method : proxy.getClass().getMethods()) {
         ProxyTestHelper.checkNoneStaticMethodForwarding(method, proxy, proxiedObject, spy);
      }
   }
}
