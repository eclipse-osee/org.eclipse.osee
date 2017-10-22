/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.x.server.application.internal;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientConfig;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.x.server.application.internal.model.ServerStatus;
import org.eclipse.osee.x.server.application.internal.model.StatusKey;
import org.eclipse.osee.x.server.application.internal.operations.BuildServerStatusOperation;

/**
 * @author Donald G. Dunne
 */
@Path("/health")
public final class ServerHealthEndpointImpl {
   private final IApplicationServerManager applicationServerManager;
   private final Map<String, JdbcService> jdbcServices;
   private final IAuthenticationManager authManager;
   private final ActivityLog activityLog;
   private ObjectMapper mapper;
   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   public static final String OSEE_HEALTH_SERVERS_KEY = "osee.health.servers";

   public ServerHealthEndpointImpl(IApplicationServerManager applicationServerManager, Map<String, JdbcService> jdbcServices, IAuthenticationManager authManager, ActivityLog activityLog) {
      this.applicationServerManager = applicationServerManager;
      this.jdbcServices = jdbcServices;
      this.authManager = authManager;
      this.activityLog = activityLog;
   }

   @Path("top")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getTop() throws Exception {
      StringBuilder sb = new StringBuilder();
      if (Lib.isWindows()) {
         sb.append("Top is not available for windows");
      } else {
         ProcessBuilder pb = new ProcessBuilder("top", "-l", "1");
         pb.redirectError();
         Process p = pb.start();
         InputStream is = p.getInputStream();
         int value = -1;
         while ((value = is.read()) != -1) {
            sb.append(((char) value));
         }
         int exitCode = p.waitFor();
         sb.append("Top exited with " + exitCode);
      }
      return sb.toString();
   }

   @Path("status")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ServerStatus serverStatus() {
      return new BuildServerStatusOperation(applicationServerManager, authManager, activityLog).get();
   }

   @Path("status/all")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String serverStatusAsll() {
      return serverStatusAsll(false);
   }

   @Path("status/all/detail")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String serverStatusAsllDetails() {
      return serverStatusAsll(true);
   }

   private String serverStatusAsll(boolean details) {

      // Retrieve servers from osee.json
      final JdbcClientConfig config = jdbcServices.values().iterator().next().getClient().getConfig();
      Object serverObj = config.getDbProps().get("application.servers");
      if (serverObj == null || !(serverObj instanceof String)) {
         throw new IllegalStateException("No application.servers configured in osee.json file");
      }
      String serversStr = ((String) serverObj).replaceAll("[\\[\\]]", "");
      serversStr = serversStr.replaceAll(" ", "");
      Set<String> servers = new HashSet<>();
      for (String server : serversStr.split(",")) {
         servers.add(server);
      }

      // Retrieve servers from OseeInfo
      serversStr = getValue(jdbcServices.values().iterator().next().getClient(), OSEE_HEALTH_SERVERS_KEY);
      serversStr = serversStr.replaceAll(" ", "");
      for (String server : serversStr.split(",")) {
         servers.add(server);
      }

      if (servers.size() == 0) {
         throw new IllegalStateException("No application.servers configured in osee.json file");
      }

      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(95, 2));
      List<String> headers = new LinkedList<>();
      headers.add("Name");
      headers.add("Alive");
      for (StatusKey key : StatusKey.values()) {
         if (details || !key.isDetails()) {
            headers.add(key.name());
         }
      }
      sb.append(AHTML.addHeaderRowMultiColumnTable(headers));
      for (String server : (servers)) {
         addServer(sb, server, details);
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private ObjectMapper getMapper() {
      if (mapper == null) {
         mapper = new ObjectMapper();
      }
      return mapper;
   }

   private void addServer(StringBuilder sb, String server, boolean details) {
      List<String> values = new LinkedList<>();
      String statusUrl = "http://" + server + "/server/health/status";
      values.add(AHTML.getHyperlink(statusUrl, server));
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         URL url = new URL(String.format("http://%s%s", server, "/server/health/status"));
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 5000);
         if (result.wasSuccessful()) {
            values.add("Ok");
         } else {
            values.add("Not successful: " + result.getResult());
            return;
         }

         String json = outputStream.toString(result.getEncoding());
         ServerStatus stat = getMapper().readValue(json, ServerStatus.class);
         for (StatusKey key : StatusKey.values()) {
            if (details || !key.isDetails()) {
               String value = stat.get(key);
               if (value == null) {
                  value = "";
               }
               values.add(value);
            }
         }
      } catch (Exception ex) {
         values.add("Exception: " + ex.getMessage());
      }
      sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
   }

   private String getValue(JdbcClient jdbcClient, String key) {
      String toReturn = jdbcClient.fetch("", GET_VALUE_SQL, key);
      return toReturn;
   }

}