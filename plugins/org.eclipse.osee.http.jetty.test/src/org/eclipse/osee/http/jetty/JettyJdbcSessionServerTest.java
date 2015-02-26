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
import java.util.Arrays;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.http.jetty.internal.jdbc.JdbcHttpSessionMigrationResource;
import org.eclipse.osee.http.jetty.internal.session.JdbcJettySessionManagerFactory;
import org.eclipse.osee.http.jetty.util.CookieStoringHttpClientFilter;
import org.eclipse.osee.http.jetty.util.PostCountingServlet;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientBuilder;
import org.eclipse.osee.jdbc.JdbcMigrationOptions;
import org.eclipse.osee.jdbc.JdbcMigrationResource;
import org.eclipse.osee.jdbc.JdbcServer;
import org.eclipse.osee.jdbc.JdbcServerBuilder;
import org.eclipse.osee.jdbc.JdbcServerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link JdbcJettySessionManagerFactory}
 * 
 * @author Roberto E. Escobar
 */
public class JettyJdbcSessionServerTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Rule
   public TestName testMethod = new TestName();

   private JdbcServer jdbcServer;
   private JettyServer httpServer1;
   private JettyServer httpServer2;

   private int httpPort1;
   private int httpPort2;

   private final CookieStoringHttpClientFilter filter = new CookieStoringHttpClientFilter();

   @Before
   public void setUp() throws IOException {
      MockitoAnnotations.initMocks(this);

      String testName = testMethod.getMethodName();

      File newFile = folder.newFile("hsql.db." + testName);

      jdbcServer = JdbcServerBuilder.hsql(newFile.toURI().toASCIIString())//
      .useRandomPort(true) //
      .build();

      jdbcServer.start();

      JdbcServerConfig config = jdbcServer.getConfig();
      JdbcClient jdbcClient = JdbcClientBuilder.hsql(config.getDbName(), config.getDbPort()).build();
      JdbcMigrationResource sessionMigration = new JdbcHttpSessionMigrationResource();
      jdbcClient.migrate(new JdbcMigrationOptions(true, true), Arrays.asList(sessionMigration));

      String clusterName = "jetty.cluster." + testName;

      File workingDir1 = folder.newFolder("jetty.dir.1." + testName);
      httpServer1 = newServer(clusterName, "test", jdbcClient, workingDir1);
      httpServer1.start();

      File workingDir2 = folder.newFolder("jetty.dir.2." + testName);
      httpServer2 = newServer(clusterName, "test", jdbcClient, workingDir2);
      httpServer2.start();

      httpPort1 = httpServer1.getConfig().getHttpPort();
      httpPort2 = httpServer2.getConfig().getHttpPort();
   }

   @After
   public void tearDown() {
      if (httpServer2 != null) {
         httpServer2.stop();
      }

      if (httpServer1 != null) {
         httpServer1.stop();
      }

      if (jdbcServer != null) {
         jdbcServer.stop();
      }
   }

   @Test
   public void testHttpJdbcSessions() {
      JaxRsWebTarget target1 = newTarget(filter, httpPort1);
      JaxRsWebTarget target2 = newTarget(filter, httpPort2);

      assertEquals(1, incrementCount(target1));
      assertEquals(1, getCount(target1));

      assertEquals(2, incrementCount(target1));
      assertEquals(3, incrementCount(target1));

      assertEquals(3, getCount(target1));

      // Work with target 2
      assertEquals(3, getCount(target2));

      assertEquals(4, incrementCount(target2));
      assertEquals(5, incrementCount(target2));

      assertEquals(5, getCount(target2));

      // Go back to target 1
      assertEquals(5, getCount(target1));
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

   private JettyServer newServer(String clusterName, String context, JdbcClient jdbcClient, File workingDir) {
      return JettyServer.newBuilder()//
      .useRandomHttpPort(true)//
      .jdbcSessionManagerFactory(jdbcClient)//
      .workingDirectory(workingDir.getAbsolutePath())//
      .extraParam(JettyConstants.JETTY_JDBC_SESSION__CLUSTER_NAME, clusterName) //
      .extraParam(JettyConstants.JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS, 0) // always access database for latest session info
      .build() //
      .addServlet(context, new PostCountingServlet());
   }
}
