/*******************************************************************************
 * Copyright (c) 2015 Boeing.
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.http.jetty.JettyServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

/**
 * Test Case for {@link JaxRsClient}
 * 
 * @author Roberto E. Escobar
 */
public class JaxRsClientToServerTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Rule
   public TestName testMethod = new TestName();

   private final AtomicInteger counter = new AtomicInteger();
   private JettyServer httpServer;
   private String httpAddress;

   @Before
   public void setup() throws IOException {
      String testName = testMethod.getMethodName();
      File workingDir = folder.newFolder(testName);

      httpServer = JettyServer.newBuilder() //
      .useRandomHttpPort(true) //
      .workingDirectory(workingDir.getAbsolutePath()) //
      .build() //
      .addServlet("/test", new PostCountingServlet(counter));

      httpServer.start();

      int httpPort = httpServer.getConfig().getHttpPort();
      httpAddress = String.format("http://localhost:%s/test", httpPort);
   }

   @After
   public void tearDown() {
      if (httpServer != null) {
         httpServer.stop();
      }
   }

   @Test
   public void testClient() {
      JaxRsWebTarget target = JaxRsClient.newClient().target(httpAddress);

      String actual = target.request().get(String.class);
      assertEquals("0", actual);

      actual = target.request().post(null, String.class);
      assertEquals("1", actual);

      actual = target.request().get(String.class);
      assertEquals("1", actual);
   }

   @Test
   public void testPoxyClient() {
      IntegerEndpoint proxy = JaxRsClient.newClient().targetProxy(httpAddress, IntegerEndpoint.class);

      int actual = proxy.get();
      assertEquals(0, actual);

      actual = proxy.incrementAndGet();
      assertEquals(1, actual);

      actual = proxy.get();
      assertEquals(1, actual);
   }

   @Test
   public void testClientWithReader() {
      JaxRsWebTarget target = JaxRsClient.newClient().target(httpAddress).register(MyObjectReader.class);

      MyObject actual = target.request().get(MyObject.class);
      assertEquals(0, actual.getValue());

      actual = target.request().post(null, MyObject.class);
      assertEquals(1, actual.getValue());

      actual = target.request().get(MyObject.class);
      assertEquals(1, actual.getValue());

      ComplexObjectEndpoint proxy = target.newProxy(ComplexObjectEndpoint.class);

      actual = proxy.get();
      assertEquals(1, actual.getValue());

      actual = proxy.incrementAndGet();
      assertEquals(2, actual.getValue());

      actual = proxy.get();
      assertEquals(2, actual.getValue());

      actual = proxy.incrementAndGet();
      assertEquals(3, actual.getValue());
   }

   public static interface IntegerEndpoint {

      @GET
      Integer get();

      @POST
      Integer incrementAndGet();

   }

   public static interface ComplexObjectEndpoint {

      @GET
      MyObject get();

      @POST
      MyObject incrementAndGet();

   }

   public static final class MyObject {

      private int value;

      public int getValue() {
         return value;
      }

      public void setValue(int value) {
         this.value = value;
      }

   }

   @Provider
   public static final class MyObjectReader implements MessageBodyReader<MyObject> {

      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
         return true;
      }

      @Override
      public MyObject readFrom(Class<MyObject> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
         MyObject object = new MyObject();
         String value = Lib.inputStreamToString(entityStream);
         object.setValue(Integer.parseInt(value));
         return object;
      }

   }

   private static final class PostCountingServlet extends HttpServlet {

      private static final long serialVersionUID = -6112225579822541495L;

      private static final String MEDIA_TYPE__TEXT_PLAIN = "text/plain";

      private final AtomicInteger counter;

      public PostCountingServlet(AtomicInteger counter) {
         super();
         this.counter = counter;
      }

      @Override
      protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
         try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MEDIA_TYPE__TEXT_PLAIN);
            response.getWriter().write(String.valueOf(counter.get()));
         } finally {
            response.flushBuffer();
         }
      }

      @Override
      protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
         try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MEDIA_TYPE__TEXT_PLAIN);
            response.getWriter().write(String.valueOf(counter.incrementAndGet()));
         } finally {
            response.flushBuffer();
         }
      }

   }
}
