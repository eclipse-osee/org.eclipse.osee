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
package org.eclipse.osee.framework.server.ide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SessionClientLoopbackServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = 6402608843937837246L;

   private final ISessionManager sessionManager;

   public SessionClientLoopbackServlet(Log logger, ISessionManager sessionManager) {
      super(logger);
      this.sessionManager = sessionManager;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         getLogger().info(
            "Entered doGet;\n\t PathInfo=[%s],\n\t RequestURI=[%s],\n\t RequestURL=[%s],\n\t isDeleted=[%s],\n\t context=[%s],\n\t guid=[%s],\n\t branchUuid=[%s],\n\t cmd=[%s],\n\t id=[%s],\n\t sessionId=[%s]",
            request.getPathInfo(), request.getRequestURI(), request.getRequestURL(), request.getParameter("isDeleted"),
            request.getParameter("context"), request.getParameter("guid"), request.getParameter("branchUuid"),
            request.getParameter("cmd"), request.getParameter("id"), request.getParameter("sessionId"));
         ISession session = getSessionFromRequest(request);
         getLogger().info(
            "Returned from getSessionFromRequest to doGet;\n\t Class=[%s],\n\t clientAddress=[%s],\n\t clientMachineName=[%s],\n\t clientPort=[%s],\n\t clientVersion=[%s],\n\t creationDate=[%s],\n\t guid=[%s],\n\t userId=[%s]",
            session.getClass(), session.getClientAddress(), session.getClientMachineName(), session.getClientPort(),
            session.getClientVersion(), session.getCreationDate(), session.getGuid(), session.getUserId());
         String url = null;
         if (session != null) {
            // Session found - redirect to client.
            url = String.format("%s%s", getRemoteHostUrl(session), getLoopbackPostfix(request));
            getLogger().info("Session found - redirect to client;\n\t url=[%s]", url);
         } else {
            // No session found - redirect to web browser request handler.
            url = String.format("http://%s:%s/%s?%s", getNormalizedAddress(request.getLocalAddr()),
               request.getLocalPort(), OseeServerContext.ARTIFACT_CONTEXT, request.getQueryString());
            getLogger().info("No session found - redirect to web browser request handler;\n\t url=[%s]", url);
         }
         response.setContentType("text/plain");
         response.sendRedirect(url);
      } catch (Exception ex) {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            String.format("Error processing request [%s]", request.getQueryString()));
      } finally {
         response.flushBuffer();
      }
   }

   private String getRemoteHostUrl(ISession session) {
      getLogger().info("Entered getRemoteHostUrl;");
      String clientAddress = session.getClientAddress();
      getLogger().info("Returned from getClientAddress to getRemoteHostUrl;\n\t clientAddress=[%s]", clientAddress);
      try {
         getLogger().info("\n\t InetAddress=[%s]", InetAddress.getByName(clientAddress).getAddress());
         if (Arrays.equals(InetAddress.getByName(clientAddress).getAddress(),
            InetAddress.getLocalHost().getAddress())) {
            clientAddress = "localhost";
         }
      } catch (UnknownHostException ex) {
         getLogger().error(ex, "Error resolving host for clientAddress [%s]", clientAddress);
      }
      getLogger().info("remoteHostUrl=[%s]", String.format("http://%s:%s/", clientAddress, session.getClientPort()));
      return String.format("http://%s:%s/", clientAddress, session.getClientPort());
   }

   private String getLoopbackPostfix(HttpServletRequest request) throws UnsupportedEncodingException {
      StringBuffer buffer = new StringBuffer();
      String context = "";
      Enumeration<?> enumeration = request.getParameterNames();
      int count = 0;
      while (enumeration.hasMoreElements()) {
         Object object = enumeration.nextElement();
         if (object instanceof String) {
            String key = (String) object;
            if (!key.equals("sessionId")) {
               String value = request.getParameter(key);
               if (Strings.isValid(value)) {
                  if (key.equals("context")) {
                     context = value;
                     if (context.startsWith("/")) {
                        context = context.substring(1, context.length());
                     }
                  } else {
                     if (count != 0) {
                        buffer.append("&");
                     }
                     buffer.append(key);
                     buffer.append("=");
                     buffer.append(URLEncoder.encode(value, "UTF-8"));
                     count++;
                  }
               }
            }
         }
      }
      if (buffer.length() > 0) {
         buffer.insert(0, '?');
      }
      return String.format("%s%s", context, buffer.toString());
   }

   private boolean canConnect(String urlString) throws IOException {
      getLogger().info("Entered canConnect;");
      boolean canConnect = false;
      HttpURLConnection connection = null;
      try {
         URL url = new URL(urlString);
         getLogger().info("\turl=[%s]", url);
         connection = (HttpURLConnection) url.openConnection();
         getLogger().info("\tconnection=[%s]", connection);
         connection.connect();
         canConnect = true;
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
      return canConnect;
   }

   private String getNormalizedAddress(String remoteAddress) throws UnknownHostException {
      if (remoteAddress.equals("127.0.0.1") || remoteAddress.equals("localhost")) {
         remoteAddress = InetAddress.getLocalHost().getHostAddress();
      }
      return remoteAddress;
   }

   private boolean isSessionValid(ISession session) {
      boolean result = false;
      try {
         String urlString = getRemoteHostUrl(session);
         getLogger().info("Entered isSessionValid;\n\t urlString=[%s]", urlString);
         result = canConnect(urlString);
         getLogger().info("\tcanConnect=[%s]", result);
      } catch (Exception ex) {
         getLogger().warn(ex, "Session failed to validate canConnect=[%s]", result);
      }
      return result;
   }

   private ISession getSessionFromRequest(HttpServletRequest request) {
      getLogger().info("Entered getSessionFromRequest;");
      ISession session = null;
      final String sessionId = request.getParameter("sessionId");
      getLogger().info("\tsessionId=[%s], sessionIsValidString=[%s], ", sessionId, Strings.isValid(sessionId));
      if (Strings.isValid(sessionId)) {
         session = sessionManager.getSessionById(sessionId);
         boolean isSessionValid = isSessionValid(session);
         getLogger().info(
            "Returned from getSessionById to getSessionFromRequest;\n\t clientAddress=[%s],\n\t clientMachineName=[%s],\n\t clientPort=[%s],\n\t clientVersion=[%s],\n\t creationDate=[%s],\n\t guid=[%s],\n\t userId=[%s]",
            isSessionValid, session.getClientAddress(), session.getClientMachineName(), session.getClientPort(),
            session.getClientVersion(), session.getCreationDate(), session.getGuid(), session.getUserId());
         if (!isSessionValid) {
            session = null;
         }
      }
      return session;
   }
}
