/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.server.ide.internal;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
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
   public OseeSessionGrant createIdeClientSession(OseeCredential credential) {
      OseeSessionGrant sessionGrant = sessionManager.createSession(credential);
      if (sessionGrant != null) {
         logSessionCreation(credential, sessionGrant);
      }
      return sessionGrant;
   }

   @Override
   public void releaseIdeClientSession(String sessionId) {
      logSessionReleased(sessionId);
      sessionManager.releaseSession(sessionId);
   }

   @Override
   public List<String> getIdeClientProtocols() {
      List<String> protocols = new LinkedList<>();
      for (String protocol : authenticationManager.getProtocols()) {
         protocols.add(protocol);
      }
      return protocols;
   }

   private void logSessionCreation(OseeCredential credential, OseeSessionGrant oseeSessionGrant) {
      try {
         activityLog.createEntry(oseeSessionGrant.getUserToken(), CoreActivityTypes.IDE, ActivityLog.COMPLETE_STATUS,
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
         OseeLog.logf(getClass(), Level.SEVERE, "Exception in logSessionCreated [%s]", Lib.exceptionToString(ex));
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
         OseeLog.logf(getClass(), Level.SEVERE, "Exception in logSessionReleased [%s]", Lib.exceptionToString(ex));
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