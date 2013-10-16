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
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

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
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            String.format("Error processing request [%s]", request.getQueryString()));
      } finally {
         response.flushBuffer();
      }
   }

   private String getRemoteHostUrl(ISession session) {
      String clientAddress = session.getClientAddress();
      try {
         if (Arrays.equals(InetAddress.getByName(clientAddress).getAddress(), InetAddress.getLocalHost().getAddress())) {
            clientAddress = "localhost";
         }
      } catch (UnknownHostException ex) {
         getLogger().error(ex, "Error resolving host for clientAddress [%s]", clientAddress);
      }
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

   private boolean isSessionValid(ISession session) {
      boolean result = false;
      try {
         String urlString = getRemoteHostUrl(session);
         result = canConnect(urlString);
      } catch (Exception ex) {
         // Do nothing
      }
      return result;
   }

   private int getSessionPort(HttpServletRequest request) throws UnknownHostException, OseeCoreException {
      String remoteAddress = getNormalizedAddress(request.getRemoteAddr());
      final String sessionId = request.getParameter("sessionId");

      Iterable<? extends ISession> filteredByAddress = getSessionsByClientAddress(remoteAddress);

      ISession sessionData = null;
      if (Strings.isValid(sessionId)) {
         Optional<? extends ISession> data = Iterables.tryFind(filteredByAddress, new Predicate<ISession>() {
            @Override
            public boolean apply(ISession session) {
               return sessionId.equals(session.getGuid());
            }
         });
         if (data.isPresent()) {
            if (isSessionValid(data.get())) {
               sessionData = data.get();
            }
         }
      }
      if (sessionData == null) {
         List<? extends ISession> sortedByLastInteractionDate = sortByLastInteractionDate(filteredByAddress);
         for (ISession session : sortedByLastInteractionDate) {
            if (isSessionValid(session)) {
               sessionData = session;
               break;
            }
         }
      }
      return sessionData != null ? sessionData.getClientPort() : -1;
   }

   private Iterable<? extends ISession> getSessionsByClientAddress(final String remoteAddress) throws OseeCoreException {
      Collection<? extends ISession> allSessions = sessionManager.getAllSessions(true);
      return Iterables.filter(allSessions, new Predicate<ISession>() {
         @Override
         public boolean apply(ISession session) {
            return remoteAddress.equals(session.getClientAddress());
         }
      });
   }

   private List<? extends ISession> sortByLastInteractionDate(Iterable<? extends ISession> sessions) {
      Ordering<ISession> ordered = Ordering.from(new Comparator<ISession>() {
         @Override
         public int compare(ISession arg1, ISession arg2) {
            return Long.valueOf(arg1.getLastInteractionDate().getTime()).compareTo(
               Long.valueOf(arg2.getLastInteractionDate().getTime()));
         }
      });
      return ordered.reverse().sortedCopy(sessions);
   }
}
