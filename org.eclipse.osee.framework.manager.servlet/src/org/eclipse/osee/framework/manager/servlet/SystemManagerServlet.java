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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class SystemManagerServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 3334123351267606890L;

   private static enum OperationType {
      user, delete, invalid, overview;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.OseeHttpServlet#checkAccessControl(javax.servlet.http.HttpServletRequest)
    */
   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      // Allow access to all
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         OperationType operationType = OperationType.overview;
         if (request.getParameter("operation") != null) operationType =
               OperationType.valueOf(request.getParameter("operation"));
         switch (operationType) {
            case user:
               displayUser(request, response);
               break;
            case delete:
               deleteSession(request, response);
               break;
            default:
               displayOverview(request, response);
               break;
         }
      } catch (Exception ex) {
         OseeLog.log(InternalSystemManagerServletActivator.class, Level.SEVERE, String.format(
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
      StringBuffer sb = new StringBuffer(1000);
      try {
         sb.append(AHTML.heading(2, "OSEE Dashboard"));
         sb.append(getHeader(request));
         sb.append(AHTML.newline() + getSessionByUserIdEntry(request, response));
         sb.append(getSessions(request));
      } catch (OseeCoreException ex) {
         sb.append("Exception: " + ex.getLocalizedMessage());
      }
      displayResults(sb.toString(), request, response);
   }

   private String getHeader(HttpServletRequest request) {
      return "<a href=\"http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/osee/manager\">Home</a><br>";
   }

   private String getSessionByUserIdEntry(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuffer sb = new StringBuffer(1000);
      try {
         sb.append("<form METHOD=GET ACTION=\"http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/osee/manager\">");
         sb.append("By UserId: <input TYPE=\"text\" NAME=\"userId\" SIZE=\"10\" MAXLENGTH=\"10\">");
         sb.append("<input TYPE=\"hidden\" NAME=\"operation\" VALUE=\"user\">");
         sb.append("<INPUT TYPE=SUBMIT></form>");
      } catch (Exception ex) {
         sb.append("Exception: " + ex.getLocalizedMessage());
      }
      return sb.toString();
   }

   private void displayUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuffer sb = new StringBuffer(1000);
      try {
         HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
         String userId = info.userId;
         if (!Strings.isValid(userId)) {
            sb.append("Invalid userId [" + userId + "]");
         } else {
            sb.append(AHTML.heading(2, "OSEE System Manager"));
            sb.append(getHeader(request));
            sb.append(getSessionsByUserId(request, userId));
         }
      } catch (OseeCoreException ex) {
         sb.append("Exception: " + ex.getLocalizedMessage());
      }
      displayResults(sb.toString(), request, response);
   }

   private void deleteSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuffer sb = new StringBuffer(1000);
      try {
         HttpSystemManagerCreationInfo info = new HttpSystemManagerCreationInfo(request);
         if (!Strings.isValid(info.sessionId)) {
            sb.append("Invalid userId [" + info.sessionId + "]");
         } else {
            deleteSessionBySessionId(request, info.sessionId);
            sb.append("Deleted session [" + info.sessionId + "]");
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
         OseeLog.log(InternalSystemManagerServletActivator.class, Level.SEVERE, String.format(
               "Error processing request for protocols [%s]", request.toString()), ex);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   private static final String SESSION_QUERY_ALL = "Select * from osee_session";
   private static final String SESSION_QUERY_USER = "Select * from osee_session where user_id = ?";
   private static final String SESSION_DELETE_QUERY = "Delete from osee_session where session_id = ?";

   private String getSessions(HttpServletRequest request) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SESSION_QUERY_ALL);
         return getSessionResults(request, chStmt, "Sessions");
      } finally {
         chStmt.close();
      }

   }

   private String getSessionsByUserId(HttpServletRequest request, String userId) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SESSION_QUERY_USER, userId);
         return getSessionResults(request, chStmt, "Sessions for [" + userId + "]");
      } finally {
         chStmt.close();
      }

   }

   private void deleteSessionBySessionId(HttpServletRequest request, String sessionId) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(SESSION_DELETE_QUERY, sessionId);
   }

   private static SimpleDateFormat dateFormat = (new SimpleDateFormat("yyyy/MM/dd hh:mm a"));

   private String getSessionResults(HttpServletRequest request, ConnectionHandlerStatement chStmt, String title) throws OseeCoreException {
      StringBuffer sb = new StringBuffer(1000);
      sb.append(AHTML.heading(3, title));
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Created", "User", "Version", "Machine", "Info",
            "Log", "Last Interaction", "IP", "Port", "Delete"}));
      ArrayList<String> items = new ArrayList<String>();
      while (chStmt.next()) {
         String clientIp = chStmt.getString("client_address");
         String clientPort = chStmt.getString("client_port");
         items.add(AHTML.addRowMultiColumnTable(new String[] {
               dateFormat.format(chStmt.getTimestamp("created_on")),
               chStmt.getString("user_id"),
               chStmt.getString("client_version"),
               chStmt.getString("client_machine_name"),
               "<a href=\"http://" + clientIp + ":" + clientPort + "/osee/request?cmd=info\">info</a>",
               "<a href=\"http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/osee/manager?cmd=log\">log</a>",
               dateFormat.format(chStmt.getTimestamp("last_interaction_date")),
               clientIp,
               clientPort,
               "<a href=\"http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/osee/manager?operation=delete&sessionId=" + chStmt.getString("session_id") + "\">delete</a>"}));
      }
      Arrays.sort(items.toArray(new String[items.size()]));
      Collections.reverse(items);
      for (String item : items) {
         sb.append(item);
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }
}
