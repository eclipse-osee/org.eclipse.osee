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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.server.SessionData;
import org.eclipse.osee.framework.core.server.SessionData.SessionState;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class SessionClientLoopbackServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 6402608843937837246L;

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      // Open to All;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         int remotePort = getSessionPort(request);
         response.setContentType("text/plain");
         String url = null;
         if (remotePort > -1) {
            // Session found - redirect to client.
            url = String.format("http://%s:%s/%s", request.getRemoteAddr(), remotePort, getLoopbackPostfix(request));
         } else {
            // No session found - redirect to web browser request handler.
            url =
                  String.format("http://%s:%s/%s?%s", getNormalizedAddress(request.getLocalAddr()),
                        request.getLocalPort(), OseeServerContext.ARTIFACT_CONTEXT, request.getQueryString());
         }
         response.sendRedirect(url);
      } catch (Exception ex) {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(
               "Error processing request [%s]", request.getQueryString()));
      } finally {
         response.flushBuffer();
      }
   }

   private String getRemoteHostUrl(OseeSession session) {
      String clientAddress = session.getClientAddress();
      try {
         if (Arrays.equals(InetAddress.getByName(clientAddress).getAddress(), InetAddress.getLocalHost().getAddress())) {
            clientAddress = "localhost";
         }
      } catch (UnknownHostException ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, ex);
      }
      return String.format("http://%s:%s/", clientAddress, session.getPort());
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
      boolean canConnect = false;
      HttpURLConnection connection = null;
      try {
         URL url = new URL(urlString);
         connection = (HttpURLConnection) url.openConnection();
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

   private boolean isSessionValid(SessionData session) {
      boolean result = false;
      if (session.getSessionState() != SessionState.DELETED) {
         try {
            String urlString = getRemoteHostUrl(session.getSession());
            result = canConnect(urlString);
         } catch (Exception ex) {
         }
      }
      return result;
   }

   private int getSessionPort(HttpServletRequest request) throws UnknownHostException {
      String remoteAddress = getNormalizedAddress(request.getRemoteAddr());
      String sessionId = request.getParameter("sessionId");

      ISessionManager sessionManager = MasterServletActivator.getInstance().getSessionManager();
      SessionData sessionData = null;
      if (Strings.isValid(sessionId)) {
         sessionData = sessionManager.getSessionById(sessionId);
         if (sessionData != null) {
            if (!sessionData.getSession().getClientAddress().equals(remoteAddress) || !isSessionValid(sessionData)) {
               sessionData = null;
            }
         }
      }

      if (sessionData == null) {
         List<SessionData> sessions = sessionManager.getSessionByClientAddress(remoteAddress);
         if (sessions.size() > 0) {
            for (int index = 0; index < sessions.size(); index++) {
               SessionData session = sessions.get(index);
               if (sessionData == null) {
                  if (isSessionValid(session)) {
                     sessionData = session;
                  }
               } else {
                  if (sessionData.getSession().getLastInteractionDate().getTime() < session.getSession().getLastInteractionDate().getTime()) {
                     if (isSessionValid(session)) {
                        sessionData = session;
                     }
                  }
               }
            }
         }
      }
      return sessionData != null ? sessionData.getSession().getPort() : -1;
   }
}
