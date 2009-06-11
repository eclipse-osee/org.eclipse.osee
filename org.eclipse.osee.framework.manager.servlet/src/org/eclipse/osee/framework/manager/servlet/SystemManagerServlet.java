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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.server.SessionData;
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

   private static enum Command {
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
         Command command = Command.overview;
         if (request.getParameter("cmd") != null) {
            command = Command.valueOf(request.getParameter("cmd"));
         }
         switch (command) {
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
            InternalSystemManagerServletActivator.getSessionManager().releaseSession(info.sessionId);
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

   private String getSessions(HttpServletRequest request) throws OseeCoreException {
      return getSessionResults(request, InternalSystemManagerServletActivator.getSessionManager().getSessions(),
            "Sessions");
   }

   private String getSessionsByUserId(HttpServletRequest request, String userId) throws OseeCoreException {
      return getSessionResults(request, InternalSystemManagerServletActivator.getSessionManager().getSessionsByUserId(
            userId), "Sessions for [" + userId + "]");
   }
   private static SimpleDateFormat dateFormat = (new SimpleDateFormat("yyyy/MM/dd hh:mm a"));

   private String getSessionResults(HttpServletRequest request, Collection<SessionData> sessions, String title) throws OseeCoreException {
      StringBuffer sb = new StringBuffer(1000);
      sb.append(AHTML.heading(3, title));
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Created", "Alive", "User", "Version", "Machine",
            "Info", "Log", "Last Interaction", "IP", "Port", "Delete"}));
      ArrayList<String> items = new ArrayList<String>();
      for (SessionData sessionData : sessions) {
         OseeSession oseeSession = sessionData.getSession();
         String clientIp = oseeSession.getClientAddress();
         String clientPort = oseeSession.getPort() + "";
         String alive = "";
         try {
            alive = String.valueOf(InternalSystemManagerServletActivator.getSessionManager().isAlive(oseeSession));
         } catch (Exception ex) {
            OseeLog.log(this.getClass(), Level.SEVERE, ex);
         }
         items.add(AHTML.addRowMultiColumnTable(new String[] {
               dateFormat.format(oseeSession.getCreation()),
               alive,
               oseeSession.getUserId(),
               oseeSession.getVersion(),
               oseeSession.getClientMachineName(),
               "<a href=\"http://" + clientIp + ":" + clientPort + "/osee/request?cmd=info\">info</a>",
               "<a href=\"http://" + clientIp + ":" + clientPort + "/osee/request?cmd=log\">log</a>",
               dateFormat.format(oseeSession.getLastInteractionDate()),
               clientIp,
               clientPort,
               "<a href=\"http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/osee/manager?cmd=delete&sessionId=" + oseeSession.getSessionId() + "\">delete session</a>"}));
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
