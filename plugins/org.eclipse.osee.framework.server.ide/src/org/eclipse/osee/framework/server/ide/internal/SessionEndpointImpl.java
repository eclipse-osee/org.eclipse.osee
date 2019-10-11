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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.server.ide.api.SessionEndpoint;

/**
 * @author Donald G. Dunne
 */
public class SessionEndpointImpl implements SessionEndpoint {

   @Context
   private UriInfo uriInfo;

   private final IAuthenticationManager authenticationManager;
   private final ISessionManager sessionManager;

   private final ActivityLog activityLog;

   public SessionEndpointImpl(IAuthenticationManager authenticationManager, ISessionManager sessionManager, ActivityLog activityLog) {
      this.authenticationManager = authenticationManager;
      this.sessionManager = sessionManager;
      this.activityLog = activityLog;
   }

   @Override
   @PUT
   @Path("session")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public Response createIdeClientSession(OseeCredential credential) {
      System.err.println(getClass().getSimpleName() + " - " + credential.toString());

      OseeSessionGrant sessionGrant = sessionManager.createSession(credential);
      if (sessionGrant != null) {
         logSessionCreation(credential, sessionGrant);
      } else {
         // session could not be created due to version or invalid user
         Response.serverError().status(Status.FORBIDDEN).entity("Unable to create session");
      }
      return Response.ok().entity(sessionGrant).build();
   }

   @Override
   @DELETE
   @Path("session/{sessionId}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response releaseIdeClientSession(@PathParam("sessionId") String sessionId) {
      logSessionReleased(sessionId);
      sessionManager.releaseSession(sessionId);
      return Response.ok().build();
   }

   @Override
   @GET
   @Path("session/protocols")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getIdeClientProtocols() {
      List<String> protocols = new LinkedList<>();
      for (String protocol : authenticationManager.getProtocols()) {
         protocols.add(protocol);
      }
      return Response.ok(protocols).build();
   }

   private void logSessionCreation(OseeCredential credential, OseeSessionGrant oseeSessionGrant) {
      try {
         activityLog.createEntry(CoreActivityTypes.IDE, ActivityLog.COMPLETE_STATUS,
            String.format("IDE Client Session Created " //
               + "{" //
               + "\"version\":\"%s\", " //
               + "\"clientAddress\":\"%s\", " //
               + "\"clientMachineName\":\"%s\", " //
               + "\"port\":\"%s\", " //
               + "\"userName\":\"%s\", " //
               + "\"userId\":\"%s\", " //
               + "\"sessionId\":\"%s\"" //
               + "}", //
               credential.getClientVersion(), credential.getClientAddress(), credential.getClientAddress(),
               credential.getClientPort(), credential.getUserName(), oseeSessionGrant.getUserToken().getUserId(),
               oseeSessionGrant.getSessionId()));
      } catch (Exception ex) {
         // do nothing
      }
   }

   private void logSessionReleased(String sessionId) {
      try {
         ISession session = sessionManager.getSessionById(sessionId);
         String duration = getDuration(session);
         String userId = session != null ? session.getUserId() : "unknown";
         activityLog.createEntry(CoreActivityTypes.IDE, ActivityLog.COMPLETE_STATUS,
            String.format("IDE Client Session Released " //
               + "{" //
               + "\"sessionId\":\"%s\", " //
               + "\"duration\":\"%s\", " //
               + "\"userId\":\"%s\"" //
               + "}", //
               sessionId, duration, userId));
      } catch (Exception ex) {
         // do nothing
      }
   }

   /**
    * @return duration in H:M:S
    */
   private String getDuration(ISession session) {
      String duration = "0";
      if (session != null) {
         duration = String.valueOf(new Date().getTime() - session.getCreationDate().getTime());
      }
      return duration;
   }

}
