/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.health.operations.DuplicateRelationsOperation;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthActiveMq;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthBalancers;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthDbTablespace;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthDetails;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthJava;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthLog;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthSql;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthSqlTableSize;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthStatus;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthTop;
import org.eclipse.osee.orcs.rest.internal.health.operations.HealthUsage;
import org.eclipse.osee.orcs.rest.internal.health.operations.RemoteHealthDetails;
import org.eclipse.osee.orcs.rest.internal.health.operations.RemoteHealthJava;
import org.eclipse.osee.orcs.rest.internal.health.operations.RemoteHealthLog;
import org.eclipse.osee.orcs.rest.internal.health.operations.RemoteHealthTop;

/**
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

   private JdbcClient getJdbcClient() {
      return jdbcServices.values().iterator().next().getClient();
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
      HealthBalancers bal = new HealthBalancers(getJdbcClient(), orcsApi);
      bal.getBalancers();
      return new HealthBalancers(getJdbcClient(), orcsApi);
   }

   @GET
   @Path("top")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthTop getHealthTop() {
      HealthTop top = new HealthTop();
      top.setTop();
      return top;
   }

   @GET
   @Path("top/remote")
   @Produces(MediaType.APPLICATION_JSON)
   public RemoteHealthTop getRemoteHealthTop(@QueryParam("remoteServerName") String remoteServerName) {
      RemoteHealthTop top = new RemoteHealthTop(remoteServerName, orcsApi);
      top.fetchRemoteHealthTop();
      return top;
   }

   @GET
   @Path("java")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthJava getHealthJava() {
      HealthJava javaInfo = new HealthJava(getJdbcClient(), orcsApi);
      javaInfo.setJavaInfo();
      return javaInfo;
   }

   @GET
   @Path("java/remote")
   @Produces(MediaType.APPLICATION_JSON)
   public RemoteHealthJava getRemoteHealthJava(@QueryParam("remoteServerName") String remoteServerName) {
      RemoteHealthJava javaInfo = new RemoteHealthJava(remoteServerName, orcsApi);
      javaInfo.fetchRemoteHealthJava();
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
   @Produces(MediaType.APPLICATION_JSON)
   public HealthUsage getUsage() {
      HealthUsage usage = new HealthUsage(orcsApi, getJdbcClient());
      usage.calculateUsage();
      return usage;
   }

   @GET
   @Path("prometheus")
   @Produces(MediaType.TEXT_PLAIN)
   public String getPrometheusUrl(@QueryParam("url") String url) {
      if (url != null) {
         if (url.length() > 0) {
            OseeInfo.setValue(getJdbcClient(), "osee.health.prometheus.url", url);
         }
      }
      try {
         String updatedUrl = OseeInfo.getValue(getJdbcClient(), "osee.health.prometheus.url");
         return updatedUrl + "/graph?g0.expr=jvm_memory_bytes_used{area%3D%22heap%22}&g0.tab=0&g0.stacked=0&g0.show_exemplars=0&g0.range_input=1h";
      } catch (Exception e) {
         return "Error: key [osee.health.prometheus.url] is NOT SET in table [osee_info]";
      }
   }

   @GET
   @Path("osee.health.servers")
   @Produces(MediaType.TEXT_PLAIN)
   public String getServersTableEntry(@QueryParam("servers") String servers) {
      if (servers != null && servers.length() > 0) {
         OseeInfo.setValue(getJdbcClient(), "osee.health.servers", servers);
      }
      try {
         String updatedServers = OseeInfo.getValue(getJdbcClient(), "osee.health.servers");
         return "Value for key [osee.health.servers] in table [osee_info]: " + updatedServers;
      } catch (Exception e) {
         return "Error: key [osee.health.servers] is NOT SET in table [osee_info]";
      }
   }

   @GET
   @Path("osee.health.balancers")
   @Produces(MediaType.TEXT_PLAIN)
   public String getBalancersTableEntry(@QueryParam("balancers") String balancers) {
      if (balancers != null && balancers.length() > 0) {
         OseeInfo.setValue(getJdbcClient(), "osee.health.balancers", balancers);
      }
      try {
         String updatedBalancers = OseeInfo.getValue(getJdbcClient(), "osee.health.balancers");
         return "Value for key [osee.health.balancers] in table [osee_info]: " + updatedBalancers;
      } catch (Exception e) {
         return "Error: key [osee.health.servers] is NOT SET in table [osee_info]";
      }
   }

   @GET
   @Path("db/sql")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthSql getSqlHealth(@QueryParam("pageNum") long pageNum, @QueryParam("pageSize") long pageSize,
      @QueryParam("orderByName") String orderByName, @QueryParam("orderByDirection") String orderByDirection) {
      if (orderByName == null) {
         orderByName = "";
      }
      if (orderByDirection == null) {
         orderByDirection = "";
      }
      pageNum++;
      HealthSql sql = new HealthSql(orcsApi, pageNum, pageSize, orderByName, orderByDirection);
      sql.querySqlHealth();
      return sql;
   }

   @GET
   @Path("db/tablespace")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthDbTablespace getDbTablespaceStats(@QueryParam("orderByName") String orderByName,
      @QueryParam("orderByDirection") String orderByDirection) {
      if (orderByName == null) {
         orderByName = "";
      }
      if (orderByDirection == null) {
         orderByDirection = "";
      }
      HealthDbTablespace ts = new HealthDbTablespace(orcsApi, orderByName, orderByDirection);
      ts.queryDbTablespace();
      return ts;
   }

   @GET
   @Path("db/sql/size")
   @Produces(MediaType.APPLICATION_JSON)
   public HealthSqlTableSize getSqlHealthTableSize() {
      HealthSqlTableSize size = new HealthSqlTableSize(orcsApi);
      size.querySqlHealthTableSize();
      return size;
   }

   @Path("duprels/{branchId}/")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getDupRelsReport(@PathParam("branchId") BranchId branch,
      @QueryParam("fix") @DefaultValue("false") boolean fix) {
      DuplicateRelationsOperation ops = new DuplicateRelationsOperation(orcsApi);
      return ops.getReport(branch, fix);
   }

}
