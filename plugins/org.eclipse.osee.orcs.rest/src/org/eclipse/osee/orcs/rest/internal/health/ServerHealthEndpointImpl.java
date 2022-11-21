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

import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.health.HealthLinks;
import org.eclipse.osee.orcs.health.ServerStatus;
import org.eclipse.osee.orcs.rest.internal.health.operations.BuildServerStatusOperation;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthActiveMq;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthBalancers;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthExec;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthLinks;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthLogs;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthMain;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthOverview;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthOverviewDetails;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthProcesses;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthTypes;
import org.eclipse.osee.orcs.rest.internal.health.operations.ServerHealthUsage;

/**
 * @author Donald G. Dunne
 */
@Path("/health")
public final class ServerHealthEndpointImpl {
   private final IApplicationServerManager applicationServerManager;
   private final IAuthenticationManager authManager;
   private final ActivityLog activityLog;
   private final OrcsApi orcsApi;
   private final Map<String, JdbcService> jdbcServices;

   public ServerHealthEndpointImpl(OrcsApi orcsApi, IApplicationServerManager applicationServerManager, Map<String, JdbcService> jdbcServices, IAuthenticationManager authManager, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.applicationServerManager = applicationServerManager;
      this.jdbcServices = jdbcServices;
      this.authManager = authManager;
      this.activityLog = activityLog;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() {
      ServerHealthMain main = new ServerHealthMain(orcsApi, getJdbcClient());
      return main.getHtml();
   }

   @GET
   @Path("usage")
   @Produces(MediaType.TEXT_HTML)
   public String getUsage(@Context UriInfo uriInfo) {
      ServerHealthUsage ops = new ServerHealthUsage(uriInfo, orcsApi, getJdbcClient());
      return ops.getHtml();
   }

   @GET
   @Path("links")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthLinks getLinks() {
      ServerHealthLinks links = new ServerHealthLinks(orcsApi);
      return links.getLinks();
   }

   @GET
   @Path("headers")
   @Produces(MediaType.TEXT_HTML)
   public String getAllHeaders(@Context HttpHeaders headers) {
      MultivaluedMap<String, String> rh = headers.getRequestHeaders();
      return rh.entrySet().stream().map(e -> e.getKey() + " = " + e.getValue()).collect(Collectors.joining("<br/>"));
   }

   @GET
   @Path("activemq")
   @Produces(MediaType.TEXT_HTML)
   public String getActiveMq() {
      ServerHealthActiveMq amq = new ServerHealthActiveMq(applicationServerManager, getJdbcClient());
      return amq.getHtml();
   }

   @GET
   @Path("overview")
   @Produces(MediaType.TEXT_HTML)
   public String getServerHealthOverview() {
      ServerHealthOverview overview = new ServerHealthOverview(getJdbcClient());
      return overview.getHtml();
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
   @Path("balancer")
   @Produces(MediaType.TEXT_HTML)
   public String getBalancerStatus() {
      return (new ServerHealthBalancers(getJdbcClient())).getHtml();
   }

   @GET
   @Path("logs")
   @Produces(MediaType.TEXT_HTML)
   public String getServerLogs() {
      return (new ServerHealthLogs(getJdbcClient())).getHtml();
   }

   @GET
   @Path("types")
   @Produces(MediaType.TEXT_HTML)
   public String getServerTypesHealth() {
      return (new ServerHealthTypes(getJdbcClient())).getHtml();
   }

   @Path("top")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getTop() throws Exception {
      Scanner s =
         new Scanner(Runtime.getRuntime().exec(new String[] {"bash", "-c", "top -n 1"}).getInputStream()).useDelimiter(
            "\\A");
      String results = s.hasNext() ? s.next() : "";
      s.close();
      return AHTML.simplePage(results);
      //      return (new ServerStatusTop()).get();
   }

   @Path("status")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ServerStatus serverStatus() {
      return new BuildServerStatusOperation(applicationServerManager, authManager, activityLog).get();
   }

   @Path("exec")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String exec(@Context UriInfo uriInfo) {
      ServerHealthExec exec = new ServerHealthExec(uriInfo);
      return exec.getHtml();
   }

   @Path("processes")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String serverProcesses() {
      ServerHealthProcesses proc = new ServerHealthProcesses(getJdbcClient());
      return proc.getHtml();
   }

   private JdbcClient getJdbcClient() {
      return jdbcServices.values().iterator().next().getClient();
   }

}
