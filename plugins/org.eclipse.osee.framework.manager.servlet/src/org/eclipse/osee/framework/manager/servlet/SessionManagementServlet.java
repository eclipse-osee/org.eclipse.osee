/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.activity.api.Activity;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SessionManagementServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = 3334123351267606890L;

   private static enum OperationType {
      CREATE,
      RELEASE,
      INVALID;

      public static OperationType fromString(String value) {
         OperationType toReturn = OperationType.INVALID;
         for (OperationType operType : OperationType.values()) {
            if (operType.name().equalsIgnoreCase(value)) {
               toReturn = operType;
               break;
            }
         }
         return toReturn;
      }
   }

   private final IAuthenticationManager authenticationManager;
   private final ISessionManager sessionManager;
   private final ActivityLog activityLog;

   public SessionManagementServlet(Log logger, ISessionManager sessionManager, IAuthenticationManager authenticationManager, ActivityLog activityLog) {
      super(logger);
      this.sessionManager = sessionManager;
      this.authenticationManager = authenticationManager;
      this.activityLog = activityLog;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         String[] protocols = authenticationManager.getProtocols();
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(Arrays.deepToString(protocols));
      } catch (Exception ex) {
         getLogger().error(ex, "Error processing request for protocols [%s]", request.toString());
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String operation = request.getParameter("operation");
      try {
         OperationType operationType = OperationType.fromString(operation);
         switch (operationType) {
            case CREATE:
               createSession(request, response);
               break;
            case RELEASE:
               releaseSession(request, response);
               break;
            default:
               break;
         }
      } catch (Exception ex) {
         getLogger().error(ex, "Error processing session request [%s]", request.toString());
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.getWriter().write(Lib.exceptionToString(ex));
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   private void createSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      Lib.inputStreamToOutputStream(request.getInputStream(), outputStream);
      byte[] bytes = outputStream.toByteArray();

      // TODO Decrypt credential info
      OseeCredential credential = OseeCredential.fromXml(new ByteArrayInputStream(bytes));
      OseeSessionGrant oseeSessionGrant = sessionManager.createSession(credential);

      if (oseeSessionGrant != null) {
         logSessionCreation(credential, oseeSessionGrant);
         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
         oseeSessionGrant.write(byteOutputStream);

         // TODO after encrypted these will need to change
         response.setContentType("application/xml");
         response.setCharacterEncoding("UTF-8");
         response.setContentLength(byteOutputStream.size());
         Lib.inputStreamToOutputStream(new ByteArrayInputStream(byteOutputStream.toByteArray()),
            response.getOutputStream());
      } else {
         // session could not be created due to version or invalid user
         response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to create session");
      }
   }

   private void logSessionCreation(OseeCredential credential, OseeSessionGrant oseeSessionGrant) {
      try {
         activityLog.createEntry(Activity.IDE.getTypeId(), ActivityLog.COMPLETE_STATUS, String.format(
            "IDE Client Session Created " // 
               + "{" // 
               + "\"version\":\"%s\", " // 
               + "\"clientAddress\":\"%s\", " // 
               + "\"clientMachineName\":\"%s\", " // 
               + "\"port\":\"%s\", " // 
               + "\"userName\":\"%s\", " // 
               + "\"userId\":\"%s\", " // 
               + "\"sessionId\":\"%s\"" //
               + "}", //
            credential.getVersion(), credential.getClientAddress(), credential.getClientMachineName(),
            credential.getPort(), credential.getUserName(), oseeSessionGrant.getUserToken().getUserId(),
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
         activityLog.createEntry(Activity.IDE.getTypeId(), ActivityLog.COMPLETE_STATUS, String.format(
            "IDE Client Session Released " // 
               + "{" // 
               + "\"sessionId\":\"%s\"" // 
               + "\"duration\":\"%s\"" //
               + "\"userId\":\"%s\", " //  
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

   private void releaseSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
      String sessionId = request.getParameter("sessionId");
      logSessionReleased(sessionId);
      sessionManager.releaseSession(sessionId);
      response.setStatus(HttpServletResponse.SC_ACCEPTED);
      response.setContentType("text/plain");
      response.getWriter().write(String.format("Session [%s] released.", sessionId));
   }
}
