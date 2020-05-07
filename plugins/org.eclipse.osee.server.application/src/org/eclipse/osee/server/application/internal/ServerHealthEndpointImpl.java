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

package org.eclipse.osee.server.application.internal;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.health.HealthLink;
import org.eclipse.osee.orcs.health.HealthLinks;
import org.eclipse.osee.orcs.health.ServerStatus;
import org.eclipse.osee.server.application.internal.operations.BuildServerStatusOperation;
import org.eclipse.osee.server.application.internal.operations.ServerBalancerStatusTable;
import org.eclipse.osee.server.application.internal.operations.ServerProcesses;
import org.eclipse.osee.server.application.internal.operations.ServerStatusActiveMq;
import org.eclipse.osee.server.application.internal.operations.ServerStatusOverviewTable;
import org.eclipse.osee.server.application.internal.operations.ServerStatusTable;
import org.eclipse.osee.server.application.internal.operations.ServerStatusTop;
import org.eclipse.osee.server.application.internal.operations.UsageOperations;

/**
 * @author Donald G. Dunne
 */
@Path("/health")
public final class ServerHealthEndpointImpl {
   private final IApplicationServerManager applicationServerManager;
   private final JdbcClient jdbcClient;
   private final IAuthenticationManager authManager;
   private final ActivityLog activityLog;
   private final OrcsApi orcsApi;

   public ServerHealthEndpointImpl(OrcsApi orcsApi, IApplicationServerManager applicationServerManager, Map<String, JdbcService> jdbcServices, IAuthenticationManager authManager, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.applicationServerManager = applicationServerManager;
      this.jdbcClient = jdbcServices.values().iterator().next().getClient();
      this.authManager = authManager;
      this.activityLog = activityLog;
   }

   @GET
   @Path("usage")
   @Produces(MediaType.TEXT_HTML)
   public String getUsage(@Context UriInfo uriInfo) {
      MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
      String months = "1";
      Set<Entry<String, List<String>>> entrySet = queryParameters.entrySet();
      if (!entrySet.isEmpty()) {
         for (Entry<String, List<String>> entry : entrySet) {
            if (entry.getKey().toLowerCase().equals("months")) {
               months = entry.getValue().iterator().next();
            }
         }
      }
      if (!Strings.isNumeric(months)) {
         return AHTML.simplePage("Invalid Months = " + months);
      }
      UsageOperations ops = new UsageOperations(orcsApi, jdbcClient);
      return ops.getUsageHtml(months);
   }

   @GET
   @Path("links")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthLinks getLinks() {
      ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.GlobalPreferences).getArtifact();
      String json = "";
      List<String> values = artifact.getAttributeValues(CoreAttributeTypes.GeneralStringData);
      for (String value : values) {
         if (value.startsWith(OseeProperties.OSEE_HEALTH_STATUS_LINKS)) {
            json = value.replace(OseeProperties.OSEE_HEALTH_STATUS_LINKS + "=", "");
            break;
         }
      }
      if (Strings.isInValid(json)) {
         return new HealthLinks();
      }
      HealthLinks links = JsonUtil.readValue(json, HealthLinks.class);
      return links;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() {
      String mainHtml = OseeInf.getResourceContents("web/status/main.html", ServerHealthEndpointImpl.class);
      StringBuffer sb = new StringBuffer();
      HealthLinks links = getLinks();
      for (HealthLink link : links.getLinks()) {
         sb.append(String.format("<li><a target=\"_blank\" title=\"%s\" href=\"%s\">%s</a></li>\n", link.getName(),
            link.getUrl(), link.getName()));
      }
      mainHtml = mainHtml.replace("PUT_LI_HERE", sb.toString());
      return mainHtml;
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
      return (new ServerStatusActiveMq(applicationServerManager, jdbcClient)).getHtml();
   }

   @GET
   @Path("server/overview")
   @Produces(MediaType.TEXT_HTML)
   public String getMainStatus() {
      return (new ServerStatusOverviewTable(jdbcClient)).getHtml();
   }

   @GET
   @Path("server/balancer")
   @Produces(MediaType.TEXT_HTML)
   public String getBalancerStatus() {
      return (new ServerBalancerStatusTable(jdbcClient)).getHtml();
   }

   @Path("top")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getTop() throws Exception {
      return (new ServerStatusTop()).get();
   }

   @Path("status")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ServerStatus serverStatus() {
      return new BuildServerStatusOperation(applicationServerManager, authManager, activityLog).get();
   }

   @Path("processes")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String serverProcesses() {
      return new ServerProcesses().get();
   }

   @Path("status/all")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String serverStatusAsll() {
      return (new ServerStatusTable(jdbcClient, false)).getHtml();
   }

   @Path("status/all/detail")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String serverStatusAsllDetails() {
      return (new ServerStatusTable(jdbcClient, true)).getHtml();
   }

}
