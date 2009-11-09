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
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.server.SessionData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.HttpSystemManagerCreationInfo;

/**
 * @author Donald G. Dunne
 */
public class SystemManagerServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 3334123351267606890L;

   private static enum Command {
      user,
      delete,
      alive,
      invalid,
      overview;
   }

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      // Allow access to all
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         Command command = Command.overview;
         String cmd = request.getParameter("cmd");
         if (Strings.isValid(cmd)) {
            command = Command.valueOf(cmd);
         }
         switch (command) {
            case user:
               displayUser(request, response);
               break;
            case delete:
               deleteSession(request, response);
               break;
            case alive:
               sessionAlive(request, response);
               break;
            default:
               displayOverview(request, response);
               break;
         }
      } catch (Exception ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format(
               "Error processing request for protocols [%s]", request.toString()), ex);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   private void displayOverview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String requestAddress = request.getLocalAddr();
      String requestPort = String.valueOf(request.getLocalPort());

      StringBuffer sb = new StringBuffer(1000);
      try {
         sb.append(AHTML.heading(2, "OSEE Dashboard"));
         sb.append(createAnchor(AnchorType.MANAGER_HOME_ANCHOR, null, requestAddress, requestPort));
         sb.append(AHTML.newline(2));
         sb.append(getSessionByUserIdEntry(request, response));
         sb.append(getSessions(requestAddress, requestPort));
      } catch (Exception ex) {
         sb.append("Exception: ");
         sb.append(Lib.exceptionToString(ex));
      }
      displayResults(sb.toString(), request, response);
   }

   private String getSessionByUserIdEntry(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuffer sb = new StringBuffer(1000);
      try {
         sb.append("<form METHOD=GET ACTION=\"http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/osee/manager\">");
         sb.append("By UserId: <input TYPE=\"text\" NAME=\"userId\" SIZE=\"10\" MAXLENGTH=\"10\">");
         sb.append("<input TYPE=\"hidden\" NAME=\"cmd\" VALUE=\"user\">");
         sb.append("<INPUT TYPE=SUBMIT></form>");
      } catch (Exception ex) {
         sb.append("Exception: ");
         sb.append(Lib.exceptionToString(ex));
      }
      return sb.toString();
   }

   private void displayUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuffer sb = new StringBuffer(1000);
      try {
         HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
         String requestAddress = request.getLocalAddr();
         String requestPort = String.valueOf(request.getLocalPort());
         String userId = info.getUserId();
         if (!Strings.isValid(userId)) {
            sb.append("Invalid userId [" + userId + "]");
         } else {
            sb.append(AHTML.heading(2, "OSEE System Manager"));
            sb.append(createAnchor(AnchorType.MANAGER_HOME_ANCHOR, null, requestAddress, requestPort));
            sb.append(AHTML.newline(1));
            sb.append(getSessionsByUserId(userId, requestAddress, requestPort));
         }
      } catch (Exception ex) {
         sb.append("Exception: ");
         sb.append(Lib.exceptionToString(ex));
      }
      displayResults(sb.toString(), request, response);
   }

   private void deleteSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuffer sb = new StringBuffer(1000);
      try {
         HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
         if (!Strings.isValid(info.getSessionId())) {
            sb.append("Invalid userId [" + info.getSessionId() + "]");
         } else {
            MasterServletActivator.getInstance().getSessionManager().releaseSessionImmediate(info.getSessionId());
            sb.append("Deleted session [" + info.getSessionId() + "]");
         }
      } catch (OseeCoreException ex) {
         sb.append("Exception: " + ex.getLocalizedMessage());
      }
      displayResults(sb.toString(), request, response);
   }

   private void sessionAlive(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuffer sb = new StringBuffer(1000);
      try {
         HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
         if (!Strings.isValid(info.getSessionId())) {
            sb.append("Invalid userId [" + info.getSessionId() + "]");
         } else {
            try {
               SessionData sessionData =
                     MasterServletActivator.getInstance().getSessionManager().getSessionById(info.getSessionId());
               if (sessionData == null) {
                  sb.append("Can't retrieve SessionData for [" + info.getSessionId() + "]");
                  return;
               }
               OseeSession session = sessionData.getSession();
               if (session == null) {
                  sb.append("Can't retrieve OseeSession for [" + info.getSessionId() + "]");
                  return;
               }
               String clientAddress = session.getClientAddress();
               String clientPort = String.valueOf(session.getPort());
               String url = String.format("http://%s:%s/osee/request?cmd=pingId", clientAddress, clientPort);
               String responseStr = HttpProcessor.acquireString(new URL(url));
               if (responseStr == null || responseStr.equals("")) {
                  sb.append("[" + info.getSessionId() + "] not responding to info request");
               } else if (!responseStr.contains(info.getSessionId())) {
                  sb.append("NOT ALIVE - [" + info.getSessionId() + "] (response, but mismatched sessionId)");
               } else {
                  sb.append("ALIVE - [" + info.getSessionId() + "]");
               }
            } catch (Exception ex) {
               OseeLog.log(this.getClass(), Level.SEVERE, ex);
               sb.append("Exception: " + ex.getLocalizedMessage());
            }
         }
      } catch (OseeCoreException ex) {
         sb.append("Exception: " + ex.getLocalizedMessage());
      }
      displayResults(sb.toString(), request, response);
   }

   private void displayResults(String results, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/html");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(results + AHTML.newline() + "As of: " + new Date());
      } catch (Exception ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format(
               "Error processing request for protocols [%s]", request.toString()), ex);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   private String getSessions(String requestAddress, String requestPort) throws Exception {
      Collection<SessionData> sessionData =
            MasterServletActivator.getInstance().getSessionManager().getAllSessions(true);
      return createSessionTable(sessionData, "Sessions", requestAddress, requestPort);
   }

   private String getSessionsByUserId(String userId, String requestAddress, String requestPort) throws Exception {
      Collection<SessionData> sessionData =
            MasterServletActivator.getInstance().getSessionManager().getSessionsByUserId(userId, true);
      return createSessionTable(sessionData, "Sessions for [" + userId + "]", requestAddress, requestPort);
   }

   private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");

   enum AnchorType {
      INFO_ANCHOR,
      ALIVE_ANCHOR,
      LOG_ANCHOR,
      DELETE_ANCHOR,
      MANAGER_HOME_ANCHOR;
   }

   private String createAnchor(AnchorType anchorType, String sessionId, String address, String port) throws UnsupportedEncodingException {
      String toReturn = Strings.emptyString();
      switch (anchorType) {
         case INFO_ANCHOR:
            toReturn = String.format("<a href=\"http://%s:%s/osee/request?cmd=info\">info</a>", address, port);
            break;
         case LOG_ANCHOR:
            toReturn = String.format("<a href=\"http://%s:%s/osee/request?cmd=log\">log</a>", address, port);
            break;
         case ALIVE_ANCHOR:
            String eSessionId = URLEncoder.encode(sessionId, "UTF-8");
            toReturn =
                  String.format("<a href=\"http://%s:%s/%s?cmd=alive&sessionId=%s\">alive</a>", address, port,
                        OseeServerContext.MANAGER_CONTEXT, eSessionId);
            break;
         case DELETE_ANCHOR:
            String encodedSessionId = URLEncoder.encode(sessionId, "UTF-8");
            toReturn =
                  String.format("<a href=\"http://%s:%s/%s?cmd=delete&sessionId=%s\">delete</a>", address, port,
                        OseeServerContext.MANAGER_CONTEXT, encodedSessionId);
            break;
         case MANAGER_HOME_ANCHOR:
            toReturn =
                  String.format("<a href=\"http://%s:%s/%s\">Home</a>", address, port,
                        OseeServerContext.MANAGER_CONTEXT);
            break;
         default:
            break;
      }
      return toReturn;
   }

   private String createSessionTable(Collection<SessionData> sessionDatas, String title, String requestAddress, String requestPort) throws Exception {
      StringBuffer sb = new StringBuffer(1000);
      sb.append(AHTML.heading(3, title));
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Created", "Cmds", "User", "Version", "Machine",
            "Last Interaction", "IP", "Port", "Delete"}));

      List<String> items = new ArrayList<String>();
      for (SessionData sessionData : sessionDatas) {
         OseeSession session = sessionData.getSession();
         String sessionId = session.getSessionId();
         String clientAddress = session.getClientAddress();
         String clientPort = String.valueOf(session.getPort());

         items.add(AHTML.addRowMultiColumnTable(new String[] {dateFormat.format(session.getCreation()),
               createCmds(sessionId, clientAddress, clientPort, requestAddress, requestPort), session.getUserId(),
               session.getVersion(), session.getClientMachineName(),
               dateFormat.format(session.getLastInteractionDate()), clientAddress, clientPort,
               createAnchor(AnchorType.DELETE_ANCHOR, sessionId, requestAddress, requestPort)}));
      }
      String[] sortedItems = items.toArray(new String[items.size()]);
      Arrays.sort(sortedItems);
      for (int x = sortedItems.length - 1; x >= 0; x--) {
         sb.append(sortedItems[x]);
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   private String createCmds(String sessionId, String clientAddress, String clientPort, String requestAddress, String requestPort) throws Exception {
      return createAnchor(AnchorType.ALIVE_ANCHOR, sessionId, requestAddress, requestPort) + " - " + createAnchor(
            AnchorType.INFO_ANCHOR, sessionId, clientAddress, clientPort) + " - " + createAnchor(AnchorType.LOG_ANCHOR,
            sessionId, clientAddress, clientPort);
   }
}
