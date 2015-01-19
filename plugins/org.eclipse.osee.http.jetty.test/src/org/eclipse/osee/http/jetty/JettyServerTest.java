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
package org.eclipse.osee.http.jetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.http.jetty.internal.session.InMemoryJettySessionManagerFactory;
import org.eclipse.osee.http.jetty.util.CookieStoringHttpClientFilter;
import org.eclipse.osee.http.jetty.util.PostCountingServlet;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link JettyServer} {@link InMemoryJettySessionManagerFactory}
 * 
 * @author Roberto E. Escobar
 */
public class JettyServerTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Rule
   public TestName testMethod = new TestName();

   private JettyServer httpServer;
   private int httpPort;

   private final CookieStoringHttpClientFilter filter = new CookieStoringHttpClientFilter();

   @Before
   public void setUp() throws IOException {
      MockitoAnnotations.initMocks(this);

      String testName = testMethod.getMethodName();

      File workingDir = folder.newFolder("jetty.dir." + testName);

      httpServer = JettyServer.newBuilder() //
      .useRandomHttpPort(true) //
      .workingDirectory(workingDir.getAbsolutePath()) //
      .build() //
      .addServlet("/test", new PostCountingServlet());

      httpServer.start();

      httpPort = httpServer.getConfig().getHttpPort();
   }

   @After
   public void tearDown() {
      if (httpServer != null) {
         httpServer.stop();
      }
   }

   @Test
   public void testHttpJdbcSessions() {
      JaxRsWebTarget target1 = newTarget(filter, httpPort);

      assertEquals(1, incrementCount(target1));
      assertEquals(1, getCount(target1));

      assertEquals(2, incrementCount(target1));
      assertEquals(3, incrementCount(target1));

      assertEquals(3, getCount(target1));
   }

   private int incrementCount(JaxRsWebTarget target) {
      Response response = target.request(MediaType.TEXT_PLAIN_TYPE).post(null);
      Cookie session = response.getCookies().get("JSESSIONID");
      assertNotNull(session);
      return response.readEntity(Integer.class);
   }

   private int getCount(JaxRsWebTarget target) {
      Response response = target.request(MediaType.TEXT_PLAIN_TYPE).get();
      Cookie session = response.getCookies().get("JSESSIONID");
      assertNotNull(session);
      return response.readEntity(Integer.class);
   }

   private JaxRsWebTarget newTarget(CookieStoringHttpClientFilter filter, int port) {
      String address = String.format("http://localhost:%s/test", port);
      JaxRsWebTarget target = JaxRsClient.newClient().target(address);
      return target.register(filter);
   }

}
