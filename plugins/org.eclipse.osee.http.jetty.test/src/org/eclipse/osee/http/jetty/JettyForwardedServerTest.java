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

import static org.eclipse.jetty.http.HttpHeaders.X_FORWARDED_HOST;
import static org.eclipse.jetty.http.HttpHeaders.X_FORWARDED_PROTO;
import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

/**
 * Test case for {@link JettyServer}
 * 
 * @author Roberto E. Escobar
 */
public class JettyForwardedServerTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Rule
   public TestName testMethod = new TestName();

   private JettyServer httpServer;

   @After
   public void tearDown() {
      if (httpServer != null) {
         httpServer.stop();
      }
   }

   @Test
   public void testHttpForwardThroughHeader() throws IOException {
      File workingDir = folder.newFolder("jetty.dir." + testMethod.getMethodName());
      httpServer = JettyServer.newBuilder()//
      .useRandomHttpPort(true)//
      .workingDirectory(workingDir.getAbsolutePath())//
      .httpForwarded(true)//
      .build() //
      .addServlet("test", new ServerNameServlet());

      httpServer.start();

      int httpPort = httpServer.getConfig().getHttpPort();
      String address = String.format("http://localhost:%s/test", httpPort);

      JaxRsWebTarget target = JaxRsClient.newClient().target(address);

      // Request with no forwarding headers
      Data data = target.request(MediaType.APPLICATION_JSON_TYPE).get(Data.class);

      assertEquals("http", data.getScheme());
      assertEquals("localhost", data.getName());
      assertEquals(httpPort, data.getPort());

      String reverseProxyAddress = "myserver1:34";
      String reverseProxyProtocol = "https";

      // Request from forwarding server
      data = target.request(MediaType.APPLICATION_JSON_TYPE)//
      .header(X_FORWARDED_HOST, reverseProxyAddress)//
      .header(X_FORWARDED_PROTO, reverseProxyProtocol)//
      .get(Data.class);

      assertEquals(reverseProxyProtocol, data.getScheme());
      assertEquals("myserver1", data.getName());
      assertEquals(34, data.getPort());

      reverseProxyAddress = "myserver2";
      reverseProxyProtocol = "http";

      // Request from another forwarding server
      data = target.request(MediaType.APPLICATION_JSON_TYPE)//
      .header(X_FORWARDED_HOST, reverseProxyAddress)//
      .header(X_FORWARDED_PROTO, reverseProxyProtocol)//
      .get(Data.class);

      assertEquals(reverseProxyProtocol, data.getScheme());
      assertEquals("myserver2", data.getName());
      assertEquals(80, data.getPort());
   }

   @Test
   public void testHttpStaticServerName() throws IOException {
      String serverName = "static_server_name:45";

      File workingDir = folder.newFolder("jetty.dir." + testMethod.getMethodName());
      httpServer = JettyServer.newBuilder()//
      .useRandomHttpPort(true)//
      .workingDirectory(workingDir.getAbsolutePath())//
      .serverName(serverName)//
      .build() //
      .addServlet("test", new ServerNameServlet());

      httpServer.start();

      int httpPort = httpServer.getConfig().getHttpPort();
      String address = String.format("http://localhost:%s/test", httpPort);

      JaxRsWebTarget target = JaxRsClient.newClient().target(address);

      // Request with no forwarding headers
      Data data = target.request(MediaType.APPLICATION_JSON_TYPE).get(Data.class);

      assertEquals("http", data.getScheme());
      assertEquals("static_server_name", data.getName());
      assertEquals(45, data.getPort());

      String reverseProxyAddress = "myserver1:34";
      String reverseProxyProtocol = "https";

      // Request from forwarding server -- host header should be ignored since we are using a static server name
      data = target.request(MediaType.APPLICATION_JSON_TYPE)//
      .header(X_FORWARDED_HOST, reverseProxyAddress)//
      .header(X_FORWARDED_PROTO, reverseProxyProtocol)//
      .get(Data.class);

      assertEquals("https", data.getScheme());
      assertEquals("static_server_name", data.getName());
      assertEquals(45, data.getPort());
   }

   public static class Data {
      private String scheme;
      private String name;
      private int port;

      public String getScheme() {
         return scheme;
      }

      public String getName() {
         return name;
      }

      public int getPort() {
         return port;
      }

      public void setScheme(String scheme) {
         this.scheme = scheme;
      }

      public void setName(String name) {
         this.name = name;
      }

      public void setPort(int port) {
         this.port = port;
      }

   }

   private static class ServerNameServlet extends HttpServlet {

      private static final long serialVersionUID = 1744534541625281931L;

      @Override
      protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
         try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON);
            PrintWriter writer = response.getWriter();
            writer.write("{\n");
            writer.write("\"scheme\":\"");
            writer.write(request.getScheme());
            writer.write("\",\n");
            writer.write("\"name\":\"");
            writer.write(request.getServerName());
            writer.write("\",\n");
            writer.write("\"port\":");
            writer.write(String.valueOf(request.getServerPort()));
            writer.write("\n");
            writer.write("}");
         } finally {
            response.flushBuffer();
         }
      }

   }
}
