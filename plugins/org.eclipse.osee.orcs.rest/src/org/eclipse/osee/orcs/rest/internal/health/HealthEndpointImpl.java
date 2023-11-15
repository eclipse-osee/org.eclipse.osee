/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.internal.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.health.HealthLinks;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthActiveMq;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthBalancers;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthDetails;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthJava;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthLog;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthStatus;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthTop;
import org.eclipse.osee.orcs.rest.internal.health.operations.RemoteHealthDetails;
import org.eclipse.osee.orcs.rest.internal.health.operations.RemoteHealthLog;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthExec;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthLinks;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthMain;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthOverviewDetails;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthTypes;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthUsage;

/**
 * @author Donald G. Dunne
 * @author Jaden W. Puckett
 */
@Path("")
@Swagger
public final class HealthEndpointImpl {
   private final IApplicationServerManager applicationServerManager;
   private final IAuthenticationManager authManager;
   private final ActivityLog activityLog;
   private final OrcsApi orcsApi;
   private final Map<String, JdbcService> jdbcServices;

   public HealthEndpointImpl(OrcsApi orcsApi, IApplicationServerManager applicationServerManager, Map<String, JdbcService> jdbcServices, IAuthenticationManager authManager, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.applicationServerManager = applicationServerManager;
      this.jdbcServices = jdbcServices;
      this.authManager = authManager;
      this.activityLog = activityLog;
   }

   @GET
   @Path("status")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthStatus getHealthStatus() {
      return new HealthStatus(getJdbcClient(), orcsApi);
   }

   @GET
   @Path("details")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthDetails getHealthDetails() {
      HealthDetails details = new HealthDetails(getJdbcClient(), applicationServerManager, authManager, activityLog);
      details.setHealthDetails();
      return details;
   }

   @GET
   @Path("details/remote")
   @Produces(MediaType.APPLICATION_JSON)
   public RemoteHealthDetails getRemoteHealthDetails(@QueryParam("remoteServerName") String remoteServerName) {
      RemoteHealthDetails details = new RemoteHealthDetails(remoteServerName, orcsApi);
      details.fetchRemoteHealthDetails();
      return details;
   }

   @GET
   @Path("log")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthLog getHealthLog(@QueryParam("appServerDir") String appServerDir,
      @QueryParam("serverUri") String serverUri) {
      HealthLog log = new HealthLog(appServerDir, serverUri);
      log.setHealthLog();
      return log;
   }

   @GET
   @Path("log/remote")
   @Produces(MediaType.APPLICATION_JSON)
   public RemoteHealthLog getRemoteHealthLog(@QueryParam("remoteServerName") String remoteServerName,
      @QueryParam("appServerDir") String appServerDir, @QueryParam("serverUri") String serverUri) {
      RemoteHealthLog log = new RemoteHealthLog(remoteServerName, appServerDir, serverUri, orcsApi);
      log.fetchRemoteHealthLog();
      return log;
   }

   @GET
   @Path("balancers")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthBalancers getHealthBalancers() {
      return new HealthBalancers(getJdbcClient());
   }

   @GET
   @Path("top")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthTop getHealthTop() {
      return new HealthTop();
   }

   @GET
   @Path("java")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthJava getHealthJava() {
      HealthJava javaInfo = new HealthJava(getJdbcClient());
      javaInfo.setJavaInfo();
      return javaInfo;
   }

   @GET
   @Path("http/headers")
   @Produces(MediaType.APPLICATION_JSON)
   public String getHealthHttpHeaders(@Context HttpHeaders headers) {
      MultivaluedMap<String, String> reqHeaders = headers.getRequestHeaders();
      Map<String, Object> jsonMap = new HashMap<>();
      reqHeaders.forEach((key, values) -> {
         jsonMap.put(key, values.size() == 1 ? values.get(0) : values);
      });
      try {
         ObjectMapper objectMapper = new ObjectMapper();
         return objectMapper.writeValueAsString(jsonMap);
      } catch (Exception e) {
         return e.getMessage();
      }
   }

   @GET
   @Path("activemq")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthActiveMq getHealthActiveMq() {
      HealthActiveMq activeMqInfo = new HealthActiveMq(getJdbcClient());
      activeMqInfo.setActiveMqInfo();
      return activeMqInfo;
   }

   @GET
   @Path("usage")
   @Produces(MediaType.TEXT_HTML)
   public String getUsage(@Context UriInfo uriInfo) {
      ServerHealthUsage ops = new ServerHealthUsage(uriInfo, orcsApi, getJdbcClient());
      return ops.getHtml();
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() {
      ServerHealthMain main = new ServerHealthMain(orcsApi, getJdbcClient());
      return main.getHtml();
   }

   @GET
   @Path("links")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthLinks getLinks() {
      ServerHealthLinks links = new ServerHealthLinks(orcsApi);
      return links.getLinks();
   }

   @Path("overview/details")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getServerHealthDetails() {
      ServerHealthOverviewDetails details = new ServerHealthOverviewDetails(getJdbcClient(), false);
      return details.getHtml();
   }

   @Path("overview/details/all")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getServerHealthDetailsAll() {
      ServerHealthOverviewDetails detailsAll = new ServerHealthOverviewDetails(getJdbcClient(), true);
      return detailsAll.getHtml();
   }

   @GET
   @Path("types")
   @Produces(MediaType.TEXT_HTML)
   public String getServerTypesHealth() {
      return (new ServerHealthTypes(getJdbcClient())).getHtml();
   }

   @Path("exec")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String exec(@Context UriInfo uriInfo) {
      ServerHealthExec exec = new ServerHealthExec(uriInfo);
      return exec.getHtml();
   }

   private JdbcClient getJdbcClient() {
      return jdbcServices.values().iterator().next().getClient();
   }

}
