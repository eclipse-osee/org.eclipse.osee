/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.osee.jaxrs.client.JaxRsClient.JaxRsClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link JaxRsClient}
 * 
 * @author Roberto E. Escobar
 */
public class JaxRsClientTest {

   private static final String URI_STRING = "hello";
   private static final String URI = "http://hello.com";

   //@formatter:off
   @Mock private JaxRsClientFactory factory;
   @Mock private JaxRsClientConfig config;
   @Mock private JaxRsWebTarget target;
   //@formatter:on

   private JaxRsClient client;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      client = new JaxRsClient(factory, config);
   }

   @Test
   public void testTargetEmpty() {
      when(factory.newTarget(config, null)).thenReturn(target);

      JaxRsWebTarget actual = client.target();
      assertEquals(target, actual);
      verify(factory).newTarget(config, null);
   }

   @Test
   public void testTargetString() {
      when(factory.newTarget(config, URI_STRING)).thenReturn(target);

      JaxRsWebTarget actual = client.target(URI_STRING);
      assertEquals(target, actual);
      verify(factory).newTarget(config, URI_STRING);
   }

   @Test
   public void testTargetURI() throws URISyntaxException {
      URI expectedUri = new URI(URI);

      when(factory.newTarget(config, URI)).thenReturn(target);

      JaxRsWebTarget actual = client.target(expectedUri);
      assertEquals(target, actual);
      verify(factory).newTarget(config, URI);
   }

   @Test
   public void testTargetProxyString() {
      String instance = URI_STRING;
      Class<?> clazz = String.class;

      when(factory.newProxy(config, URI_STRING, clazz)).thenAnswer(answer(instance));

      Object actual = client.targetProxy(URI_STRING, clazz);
      assertEquals(instance, actual);
      verify(factory).newProxy(config, URI_STRING, clazz);
   }

   @Test
   public void testTargetProxyURI() throws URISyntaxException {
      URI expectedUri = new URI(URI);
      String instance = URI_STRING;
      Class<?> clazz = String.class;

      when(factory.newProxy(config, URI, clazz)).thenAnswer(answer(instance));

      Object actual = client.targetProxy(expectedUri, clazz);
      assertEquals(instance, actual);
      verify(factory).newProxy(config, URI, clazz);
   }

   private static <T> Answer<T> answer(final T object) {
      return new Answer<T>() {

         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return object;
         }
      };
   }
}
