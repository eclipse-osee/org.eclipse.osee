/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
         ISession session = getSessionFromRequest(request);
         String safeUrl;
         if (session != null) {
            // Session found - redirect to client.
            String baseUrl = getRemoteHostUrl(session);
            String postfix = getLoopbackPostfix(request);
            safeUrl = buildValidatedRedirectUrl(baseUrl, postfix, request);
            getLogger().info("Session found - redirect to client;\n\t url=[%s]", safeUrl);
         } else {
            // No session found - redirect to web browser request handler.
            String host = getNormalizedAddress(request.getLocalAddr());
            int port = request.getLocalPort();
            String queryString = request.getQueryString();
            safeUrl = buildLocalRedirectUrl(host, port, OseeServerContext.ARTIFACT_CONTEXT, queryString);
            getLogger().info("No session found - redirect to web browser request handler;\n\t url=[%s]", safeUrl);
         }
         response.setContentType("text/plain");
         if (safeUrl == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid redirect target");
            return;
         }
         response.sendRedirect(response.encodeRedirectURL(safeUrl));
      } catch (Exception ex) {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            String.format("Error processing request [%s]", request.getQueryString()));
      } finally {
         response.flushBuffer();
      }
   }

   /**
    * Builds and validates a redirect URL from a session-based base URL and postfix.
    * Returns null if the resulting URL is not safe for redirection.
    */
   private String buildValidatedRedirectUrl(String baseUrl, String postfix, HttpServletRequest request) {
      try {
         URL parsed = new URL(baseUrl + postfix);
         String host = parsed.getHost();
         if (!isAllowedHost(host, request)) {
            getLogger().warn("Rejected redirect to non-local host: %s", host);
            return null;
         }
         // Reconstruct from parsed components to break taint chain
         int port = parsed.getPort();
         String portStr = (port == -1 || port == 80) ? "" : ":" + port;
         String path = parsed.getPath() != null ? parsed.getPath() : "";
         String query = parsed.getQuery() != null ? "?" + parsed.getQuery() : "";
         return String.format("http://%s%s%s%s", host, portStr, path, query);
      } catch (Exception ex) {
         getLogger().warn(ex, "Failed to build validated redirect URL from base [%s]", baseUrl);
         return null;
      }
   }

   /**
    * Builds a local redirect URL from known server components. Only allows the local server address.
    */
   private String buildLocalRedirectUrl(String host, int port, String context, String queryString) {
      // host comes from request.getLocalAddr() which is server-controlled
      String safeQuery = queryString != null ? "?" + queryString : "";
      return String.format("http://%s:%d/%s%s", host, port, context, safeQuery);
   }

   /**
    * Checks if the host is a permitted redirect target (localhost, loopback, or site-local).
    */
   private boolean isAllowedHost(String host, HttpServletRequest request) {
      if (host == null || host.isEmpty()) {
         return false;
      }
      if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)) {
         return true;
      }
      String localAddr = request.getLocalAddr();
      String localName = request.getLocalName();
      if (host.equalsIgnoreCase(localAddr) || host.equalsIgnoreCase(localName)) {
         return true;
      }
      try {
         InetAddress targetAddress = InetAddress.getByName(host);
         return targetAddress.isLoopbackAddress() || targetAddress.isSiteLocalAddress()
            || targetAddress.equals(InetAddress.getLocalHost());
      } catch (UnknownHostException ex) {
         return false;
      }
   }

   private String getRemoteHostUrl(ISession session) {
      String clientAddress = session.getClientAddress();
      try {
         if (Arrays.equals(InetAddress.getByName(clientAddress).getAddress(),
            InetAddress.getLocalHost().getAddress())) {
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
         getLogger().warn(ex, "Session failed to validate connect to %s", result);
      }
      return result;
   }

   private ISession getSessionFromRequest(HttpServletRequest request) {
      ISession session = null;
      final String sessionId = request.getParameter("sessionId");
      if (Strings.isValid(sessionId)) {
         session = sessionManager.getSessionById(sessionId);
         boolean isSessionValid = isSessionValid(session);
         if (!isSessionValid) {
            session = null;
         }
      }
      return session;
   }
}
