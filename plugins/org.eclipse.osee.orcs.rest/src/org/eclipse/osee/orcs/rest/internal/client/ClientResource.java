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
package org.eclipse.osee.orcs.rest.internal.client;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.rest.internal.client.model.ClientDetails;
import org.eclipse.osee.orcs.rest.internal.client.model.ClientInfo;
import org.eclipse.osee.orcs.rest.internal.client.model.ClientSession;
import org.eclipse.osee.orcs.rest.internal.client.model.Sessions;

/**
 * @author Donald G. Dunne
 */
public class ClientResource {

   private final JdbcService jdbcService;
   private final OrcsApi orcsApi;

   public ClientResource(JdbcService jdbcService, OrcsApi orcsApi) {
      this.jdbcService = jdbcService;
      this.orcsApi = orcsApi;
   }

   @GET
   @Path("client")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getAll() {
      Sessions activeSessions = new Sessions();
      activeSessions.get().addAll(getActiveSessions().keySet());
      return Response.ok(activeSessions).build();
   }

   @GET
   @Path("client/details")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getAllDetails() {
      ClientDetails details = new ClientDetails();
      Map<ClientSession, ClientInfo> activeSessions = getActiveSessions();
      for (Entry<ClientSession, ClientInfo> entry : activeSessions.entrySet()) {
         ClientSession session = entry.getKey();
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

   @GET
   @Path("client/{userId}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getClientsForUser(@PathParam("userId") String userId) {
      Sessions sessions = new Sessions();
      Map<String, Boolean> portToAlive = new HashMap<>();
      String uId = getUserId(userId);
      if (!Strings.isValid(uId)) {
         throw new OseeArgumentException("User with id or name of [%s] not found", userId);
      }
      String queryStr = "select * from osee_session where user_id = '" + uId + "' order by created_on desc";
      int x = 0;
      for (IVariantData data : jdbcService.getClient().runQuery(queryStr)) {
         ClientSession session = new ClientSession(data);
         String key = session.getClientAddress() + session.getClientPort();
         Boolean alive = portToAlive.get(key);
         if (alive == null) {
            alive = alive(session);
            portToAlive.put(key, alive);
            if (alive) {
               sessions.add(session);
            }
         }
         // only check last 5 entries
         if (++x > 10) {
            break;
         }
      }
      return Response.ok(sessions).build();
   }

   private String getUserId(String userId) {
      if (Strings.isNumeric(userId)) {
         return userId;
      } else {
         ArtifactReadable userArt =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).andNameEquals(
               userId).getResults().getAtMostOneOrNull();
         if (userArt != null) {
            return userArt.getSoleAttributeValue(CoreAttributeTypes.UserId, null);
         }
      }
      return null;
   }

   @GET
   @Path("client/{userId}/session/{sessionId}")
   @Produces({MediaType.TEXT_PLAIN})
   public String getClientInfo(@PathParam("userId") String userId, @PathParam("sessionId") String sessionId) {
      ClientSession session = getClientSession(sessionId);
      String infoStr = getInfoStr(session, true);
      return infoStr;
   }

   private Map<ClientSession, ClientInfo> getActiveSessions() {
      Map<ClientSession, ClientInfo> sessionToInfo = new HashMap<>(200);
      String queryStr =
         "select * from osee_session where created_on > CURRENT_DATE - INTERVAL '7' DAY order by created_on desc ";
      Set<String> pinged = new HashSet<>(200);
      for (IVariantData data : jdbcService.getClient().runQuery(queryStr)) {
         ClientSession session = new ClientSession(data);
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
      }
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
      ClientSession session = getClientSession(sessionId);
      String infoStr = getInfoStr(session, false);
      if (Strings.isValid(infoStr)) {
         return new ClientInfo(infoStr);
      }
      return null;
   }

   private ClientSession getClientSession(String sessionId) {
      ClientSession session = null;
      String queryStr = "select * from osee_session where session_id = '" + sessionId + "'";
      List<IVariantData> sessions = jdbcService.getClient().runQuery(queryStr);
      if (!sessions.isEmpty()) {
         session = new ClientSession(sessions.iterator().next());
      }
      return session;
   }

   private boolean alive(ClientSession session) throws OseeCoreException {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         URL url = new URL(
            String.format("http://%s:%s/osee/request?cmd=pingId", session.getClientAddress(), session.getClientPort()));
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 1000);
         if (result.wasSuccessful()) {
            return true;
         }
      } catch (Exception ex) {
         // do nothing
      }
      return false;
   }

   private String getInfoStr(ClientSession session, boolean withLog) throws OseeCoreException {
      try {
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
