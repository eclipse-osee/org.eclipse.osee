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
package org.eclipse.osee.framework.server.ide.internal;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.ide.api.client.ClientEndpoint;
import org.eclipse.osee.framework.server.ide.api.client.model.ClientDetails;
import org.eclipse.osee.framework.server.ide.api.client.model.ClientInfo;
import org.eclipse.osee.framework.server.ide.api.client.model.Sessions;
import org.eclipse.osee.framework.server.ide.api.model.IdeVersion;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class ClientEndpointImpl implements ClientEndpoint {

   @Context
   private UriInfo uriInfo;

   private final JdbcService jdbcService;
   private final OrcsApi orcsApi;
   private static final String NEWEST_SESSIONS_BY_USER =
      "select client_address, user_id, created_on, client_port, client_version, session_id from osee_session where user_id = ? order by created_on desc";

   public ClientEndpointImpl(JdbcService jdbcService, OrcsApi orcsApi) {
      this.jdbcService = jdbcService;
      this.orcsApi = orcsApi;
   }

   @Override
   @GET
   @Path("client")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getAll() {
      Sessions activeSessions = new Sessions();
      activeSessions.get().addAll(getActiveSessions().keySet());
      return Response.ok(activeSessions).build();
   }

   @Override
   @GET
   @Path("client/details")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getAllDetails() {
      ClientDetails details = new ClientDetails();
      Map<IdeClientSession, ClientInfo> activeSessions = getActiveSessions();
      for (Entry<IdeClientSession, ClientInfo> entry : activeSessions.entrySet()) {
         IdeClientSession session = entry.getKey();
         details.getSessions().add(session);
         ClientInfo info = entry.getValue();
         increment(details.releaseCount, info.getVersion());
         addUserId(details.releaseToUserId, session.getClientVersion(), session.getUserId());
         if (info.getInstallation().contains("osee-installs")) {
            details.networkReleaseUserIds.add(session.getUserId());
         }
      }
      return Response.ok(details).build();
   }

   /**
    * @param idOrName as userId or name; underscores can be used instead of spaces if calling from browser;
    * @return all client sessions matching idOrName; multiple users sessions can be returned depending
    */
   @Override
   @GET
   @Path("client/{idOrName}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getClientsForUser(@PathParam("idOrName") String idOrName) {
      System.out.println(String.format("getClientsForUser [%s]", idOrName));
      Sessions sessions = new Sessions();
      Map<String, Boolean> portToAlive = new HashMap<>();
      List<String> resolvedUserIds = getUserIds(idOrName);
      if (resolvedUserIds.isEmpty()) {
         throw new OseeArgumentException("User with id or name of [%s] not found", idOrName);
      }

      Consumer<JdbcStatement> consumer = stmt -> {
         IdeClientSession session = createSession(stmt, uriInfo);
         String key = session.getClientAddress() + session.getClientPort();
         Boolean alive = portToAlive.get(key);
         if (alive == null) {
            alive = alive(session);
            portToAlive.put(key, alive);
            if (alive) {
               sessions.add(session);
            }
         }
      };

      for (String userId : resolvedUserIds) {
         jdbcService.getClient().runQueryWithLimit(consumer, 10, NEWEST_SESSIONS_BY_USER, userId);
      }
      return Response.ok(sessions).build();
   }

   @Override
   @GET
   @Path("client/{userId}/session/{sessionId}")
   @Produces({MediaType.TEXT_PLAIN})
   public Response getClientInfo(@PathParam("userId") String userId, @PathParam("sessionId") String sessionId) {
      if (!GUID.isValid(sessionId)) {
         return Response.ok(String.format("Session [%s] is invalid", sessionId)).build();
      }
      IdeClientSession session = getClientSession(sessionId);
      String infoStr = getInfoStr(session, true);
      return Response.ok(infoStr).build();
   }

   @Override
   @GET
   @Path("versions")
   @Produces({MediaType.APPLICATION_JSON})
   public IdeVersion getSupportedVersions() {
      IdeVersion versions = new IdeVersion();
      versions.addVersion(OseeCodeVersion.getVersion());
      return versions;
   }

   private List<String> getUserIds(String userIdOrName) {
      List<String> results = new LinkedList<>();
      if (Strings.isNumeric(userIdOrName)) {
         results.add(userIdOrName);
      } else {
         for (ArtifactReadable userArt : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            CoreArtifactTypes.User).and(CoreAttributeTypes.Name, userIdOrName,
               QueryOption.CONTAINS_MATCH_OPTIONS).getResults()) {
            results.add(userArt.getSoleAttributeValue(CoreAttributeTypes.UserId, null));
         }
      }
      return results;
   }

   private static IdeClientSession createSession(JdbcStatement stmt, UriInfo uriInfo) {
      IdeClientSession session = new IdeClientSession(stmt.getString("CLIENT_ADDRESS"), stmt.getString("CLIENT_PORT"),
         stmt.getString("USER_ID"), stmt.getString("CLIENT_VERSION"), stmt.getString("SESSION_ID"),
         DateUtil.get(stmt.getDate("CREATED_ON"), DateUtil.MMDDYYHHMM));
      URI location =
         UriBuilder.fromPath(uriInfo.getBaseUri().toASCIIString()).path("client").path(stmt.getString("USER_ID")).path(
            "session").path(stmt.getString("SESSION_ID")).build();
      session.setSessionLog(location.toString());
      return session;
   }

   private Map<IdeClientSession, ClientInfo> getActiveSessions() {
      Map<IdeClientSession, ClientInfo> sessionToInfo = new HashMap<>(200);
      Set<String> pinged = new HashSet<>(200);

      Consumer<JdbcStatement> consumer = stmt -> {
         IdeClientSession session = createSession(stmt, uriInfo);
         String key = session.getClientAddress() + session.getClientPort();
         // don't ping same host:port twice
         if (!pinged.contains(key)) {
            ClientInfo info = getClientInfo(session.getSessionId());
            // only deal with active sessions
            if (info != null) {
               sessionToInfo.put(session, info);
            }
            pinged.add(key);
         }
      };

      jdbcService.getClient().runQuery(consumer,
         "select * from osee_session where created_on > CURRENT_DATE - INTERVAL '7' DAY order by created_on desc");
      return sessionToInfo;
   }

   private void addUserId(Map<String, Collection<String>> releaseToUserId, String release, String userId) {
      Collection<String> userIds = releaseToUserId.get(release);
      if (userIds == null) {
         userIds = new HashSet<>(100);
      }
      userIds.add(userId);
      releaseToUserId.put(release, userIds);
   }

   private void increment(Map<String, Integer> releaseCount, String version) {
      Integer count = releaseCount.get(version);
      if (count == null) {
         count = 1;
      } else {
         count++;
      }
      releaseCount.put(version, count);
   }

   private ClientInfo getClientInfo(String sessionId) {
      IdeClientSession session = getClientSession(sessionId);
      String infoStr = getInfoStr(session, false);
      if (Strings.isValid(infoStr)) {
         return new ClientInfo(infoStr);
      }
      return null;
   }

   private IdeClientSession getClientSession(String sessionId) {
      return jdbcService.getClient().fetch((IdeClientSession) null, stmt -> createSession(stmt, uriInfo),
         "select * from osee_session where session_id = ?", sessionId);
   }

   private boolean alive(IdeClientSession session)  {
      boolean alive = isHostAlive(session);
      if (!alive) {
         return false;
      }

      alive = false;
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         URL url = new URL(
            String.format("http://%s:%s/osee/request?cmd=pingId", session.getClientAddress(), session.getClientPort()));
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 1000);
         if (result.wasSuccessful()) {
            alive = true;
         }
      } catch (Exception ex) {
         // do nothing
      }
      return alive;
   }

   private boolean isHostAlive(IdeClientSession session) {
      boolean reachable = false;
      try {
         String osName = System.getProperty("os.name");
         String option = osName.toLowerCase().contains("windows") ? "-n" : "-c";
         Process p1 =
            java.lang.Runtime.getRuntime().exec(String.format("ping %s 1 %s", option, session.getClientAddress()));
         int returnVal = p1.waitFor();
         reachable = (returnVal == 0);
      } catch (Exception ex1) {
         // do nothing
      }
      return reachable;
   }

   private String getInfoStr(IdeClientSession session, boolean withLog)  {
      try {
         boolean alive = isHostAlive(session);
         if (!alive) {
            return "Host Not Alive";
         }

         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         URL url = new URL(String.format("http://%s:%s/osee/request?cmd=" + (withLog ? "log" : "info"),
            session.getClientAddress(), session.getClientPort()));
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 1000);
         if (result.wasSuccessful()) {
            return outputStream.toString(result.getEncoding());
         }
      } catch (Exception ex) {
         // do nothing
      }
      return "";
   }

}
