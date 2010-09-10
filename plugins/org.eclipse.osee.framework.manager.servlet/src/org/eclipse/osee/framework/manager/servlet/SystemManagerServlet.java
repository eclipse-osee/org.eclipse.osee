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
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.HttpSystemManagerCreationInfo;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class SystemManagerServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = 3334123351267606890L;

   private static enum Command {
      user,
      delete,
      alive,
      invalid,
      overview;
   }

   private final ISessionManager sessionManager;

   public SystemManagerServlet(ISessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Command command = Command.overview;
      String cmd = request.getParameter("cmd");
      try {
         StringBuilder appendable = new StringBuilder(1000);
         if (Strings.isValid(cmd)) {
            command = Command.valueOf(cmd);
         }
         try {
            switch (command) {
               case user:
                  writeUserInfo(appendable, request, response);
                  break;
               case delete:
                  deleteSession(appendable, request, response);
                  break;
               case alive:
                  writeSessionAlive(appendable, request, response);
                  break;
               default:
                  writeOverview(appendable, request, response);
                  break;
            }
         } catch (Exception ex) {
            appendable.append("Exception: ");
            appendable.append(Lib.exceptionToString(ex));
         }
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/html");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(appendable.toString() + AHTML.newline() + "As of: " + new Date());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE,
            String.format("Error processing request for protocols [%s]", request.toString()), ex);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   private void writeOverview(Appendable appendable, HttpServletRequest request, HttpServletResponse response) throws Exception {
      String requestAddress = request.getLocalAddr();
      String requestPort = String.valueOf(request.getLocalPort());

      appendable.append(AHTML.heading(2, "OSEE Dashboard"));
      appendable.append(getAnchor(AnchorType.MANAGER_HOME_ANCHOR, null, requestAddress, requestPort));
      appendable.append(AHTML.newline(2));
      try {
         writeSessionByUserIdEntry(appendable, requestAddress, requestPort);
      } catch (Exception ex) {
         appendable.append("Exception: ");
         appendable.append(Lib.exceptionToString(ex));
      }
      Collection<ISession> sessionData = sessionManager.getAllSessions(true);
      writeSessionTable(appendable, sessionData, "Sessions", requestAddress, requestPort);
   }

   private void writeSessionByUserIdEntry(Appendable appendable, String requestAddress, String requestPort) throws Exception {
      appendable.append("<form METHOD=GET ACTION=\"http://");
      appendable.append(requestAddress);
      appendable.append(":");
      appendable.append(requestPort);
      appendable.append("/osee/manager\">");
      appendable.append("By UserId: <input TYPE=\"text\" NAME=\"userId\" SIZE=\"10\" MAXLENGTH=\"10\">");
      appendable.append("<input TYPE=\"hidden\" NAME=\"cmd\" VALUE=\"user\">");
      appendable.append("<INPUT TYPE=SUBMIT></form>");
   }

   private void writeUserInfo(Appendable appendable, HttpServletRequest request, HttpServletResponse response) throws Exception {
      HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
      String requestAddress = request.getLocalAddr();
      String requestPort = String.valueOf(request.getLocalPort());
      String userId = info.getUserId();
      if (!Strings.isValid(userId)) {
         appendable.append("Invalid userId [" + userId + "]");
      } else {
         appendable.append(AHTML.heading(2, "OSEE System Manager"));
         appendable.append(getAnchor(AnchorType.MANAGER_HOME_ANCHOR, null, requestAddress, requestPort));
         appendable.append(AHTML.newline(1));
         Collection<ISession> sessionData = sessionManager.getSessionsByUserId(userId, true);
         writeSessionTable(appendable, sessionData, "Sessions for [" + userId + "]", requestAddress, requestPort);
      }
   }

   private void deleteSession(Appendable appendable, HttpServletRequest request, HttpServletResponse response) throws Exception {
      HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
      if (!Strings.isValid(info.getSessionId())) {
         appendable.append("Invalid userId");
      } else {
         sessionManager.releaseSessionImmediate(info.getSessionId());
         appendable.append("Deleted session");
      }
      appendable.append(" [");
      appendable.append(info.getSessionId());
      appendable.append("]");
   }

   private void writeSessionAlive(Appendable appendable, HttpServletRequest request, HttpServletResponse response) throws Exception {
      HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
      if (!Strings.isValid(info.getSessionId())) {
         appendable.append("Invalid userId [" + info.getSessionId() + "]");
      } else {
         try {
            ISession session = sessionManager.getSessionById(info.getSessionId());
            if (session == null) {
               appendable.append("Can't retrieve SessionData for [" + info.getSessionId() + "]");
               return;
            }
            String clientAddress = session.getClientAddress();
            String clientPort = String.valueOf(session.getClientPort());
            String url = String.format("http://%s:%s/osee/request?cmd=pingId", clientAddress, clientPort);
            String responseStr = HttpProcessor.acquireString(new URL(url));
            if (!Strings.isValid(responseStr)) {
               appendable.append("[" + info.getSessionId() + "] not responding to info request");
            } else if (!responseStr.contains(info.getSessionId())) {
               appendable.append("NOT ALIVE - [" + info.getSessionId() + "] (response, but mismatched sessionId)");
            } else {
               appendable.append("ALIVE - [" + info.getSessionId() + "]");
            }
         } catch (Exception ex) {
            OseeLog.log(this.getClass(), Level.SEVERE, ex);
            appendable.append("Exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private static enum AnchorType {
      INFO_ANCHOR,
      ALIVE_ANCHOR,
      LOG_ANCHOR,
      DELETE_ANCHOR,
      MANAGER_HOME_ANCHOR;
   }

   private String getAnchor(AnchorType anchorType, String sessionId, String address, String port) throws IOException {
      String anchor;
      switch (anchorType) {
         case INFO_ANCHOR:
            anchor = String.format("<a href=\"http://%s:%s/osee/request?cmd=info\">info</a>", address, port);
            break;
         case LOG_ANCHOR:
            anchor = String.format("<a href=\"http://%s:%s/osee/request?cmd=log\">log</a>", address, port);
            break;
         case ALIVE_ANCHOR:
            String eSessionId = URLEncoder.encode(sessionId, "UTF-8");
            anchor =
               String.format("<a href=\"http://%s:%s/%s?cmd=alive&sessionId=%s\">alive</a>", address, port,
                  OseeServerContext.MANAGER_CONTEXT, eSessionId);
            break;
         case DELETE_ANCHOR:
            String encodedSessionId = URLEncoder.encode(sessionId, "UTF-8");
            anchor =
               String.format("<a href=\"http://%s:%s/%s?cmd=delete&sessionId=%s\">delete</a>", address, port,
                  OseeServerContext.MANAGER_CONTEXT, encodedSessionId);
            break;
         case MANAGER_HOME_ANCHOR:
            anchor =
               String.format("<a href=\"http://%s:%s/%s\">Home</a>", address, port, OseeServerContext.MANAGER_CONTEXT);
            break;
         default:
            anchor = Strings.emptyString();
            break;
      }
      return anchor;
   }

   private void writeSessionTable(Appendable appendable, Collection<ISession> sessionDatas, String title, String requestAddress, String requestPort) throws Exception {
      appendable.append(AHTML.heading(3, title));
      appendable.append(AHTML.beginMultiColumnTable(100, 1));
      appendable.append(AHTML.addHeaderRowMultiColumnTable(new String[] {
         "Created",
         "Cmds",
         "User",
         "Version",
         "Machine",
         "Last Interaction",
         "IP",
         "Port",
         "Delete"}));

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
      List<String> items = new ArrayList<String>();
      for (ISession session : sessionDatas) {
         String sessionId = session.getGuid();
         String clientAddress = session.getClientAddress();
         String clientPort = String.valueOf(session.getClientPort());

         items.add(AHTML.addRowMultiColumnTable(new String[] {
            dateFormat.format(session.getCreationDate()),
            getCmdLinks(sessionId, clientAddress, clientPort, requestAddress, requestPort),
            session.getUserId(),
            session.getClientVersion(),
            session.getClientMachineName(),
            dateFormat.format(session.getLastInteractionDate()),
            clientAddress,
            clientPort,
            getAnchor(AnchorType.DELETE_ANCHOR, sessionId, requestAddress, requestPort)}));
      }
      String[] sortedItems = items.toArray(new String[items.size()]);
      Arrays.sort(sortedItems);
      for (int x = sortedItems.length - 1; x >= 0; x--) {
         appendable.append(sortedItems[x]);
      }
      appendable.append(AHTML.endMultiColumnTable());
   }

   private String getCmdLinks(String sessionId, String clientAddress, String clientPort, String requestAddress, String requestPort) throws Exception {
      String aliveAnchor = getAnchor(AnchorType.ALIVE_ANCHOR, sessionId, requestAddress, requestPort);
      String infoAnchor = getAnchor(AnchorType.INFO_ANCHOR, sessionId, clientAddress, clientPort);
      String logAnchor = getAnchor(AnchorType.LOG_ANCHOR, sessionId, clientAddress, clientPort);
      return String.format("%s - %s - %s", aliveAnchor, infoAnchor, logAnchor);
   }
}
