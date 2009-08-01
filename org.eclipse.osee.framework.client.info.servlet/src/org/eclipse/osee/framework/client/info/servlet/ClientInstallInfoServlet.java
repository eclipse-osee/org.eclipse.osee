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
package org.eclipse.osee.framework.client.info.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Sends a page with links to OSEE client install locations.
 * <p>
 * Client Install information are entries in the OSEE Info Table:
 * <ul>
 * <li><b>osee_key:</b> uniquely identifies this install record <br/><b>NOTE:</b> must with prefixed with
 * "<b><i>osee.install.</i></b>"</li>
 * <li><b>osee_value:</b> contains client install information
 * 
 * <pre>
 *    Data for this field is formatted as follows:
 *    &lt;client&gt;
 *       &lt;install os=&quot;Windows&quot; isActive=&quot;true&quot; /&gt;
 *       &lt;comment&gt; This is a shared installation &lt;/comment&gt;
 *       &lt;location&gt;\\server\\OSEE\\Shared\\osee.exe&lt;/location&gt;
 *    &lt;client&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * <code>
 * </p>
 * 
 * @author Roberto E. Escobar
 */
public class ClientInstallInfoServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -4089363221030046759L;

   private static final String QUERY = "Select OSEE_KEY, OSEE_VALUE FROM osee_info where OSEE_KEY LIKE ?";

   private enum CommandType {
      exec_path;
   }

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      // Allow access to all
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         String cmd = request.getParameter("cmd");
         String key = request.getParameter("key");
         boolean isCloseAllowed = Boolean.valueOf(request.getParameter("close"));
         boolean isPromptAllowed = Boolean.valueOf(request.getParameter("prompt"));
         if (Strings.isValid(cmd)) {
            CommandType cmdType = CommandType.valueOf(cmd);
            switch (cmdType) {
               case exec_path:
                  if (!Strings.isValid(key)) {
                     key = "osee.install.%";
                  }
                  if (key.startsWith("osee.install.")) {
                     List<ClientInstallInfo> infos = getInfoEntry(key);
                     response.setStatus(HttpServletResponse.SC_OK);
                     response.setContentType("text/html");
                     if (infos.size() == 0) {
                        response.getWriter().write("<html><body>No installations found</body></html>");
                     } else {
                        String html = InstallLinkPageGenerator.generate(infos, isCloseAllowed, isPromptAllowed);
                        response.getWriter().print(html);
                     }
                  } else {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.setContentType("text/plain");
                     response.getWriter().write("key parameter was invalid. must start with: osee.install.");
                  }
                  break;
               default:
                  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  break;
            }
         } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().write(
                  String.format("cmd parameter was invalid. use any of the following: %s",
                        Arrays.deepToString(CommandType.values())));
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Failed to process client install info request [%s]",
               request.toString()), ex);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }

   private List<ClientInstallInfo> getInfoEntry(String key) throws OseeCoreException {
      List<ClientInstallInfo> infos = new ArrayList<ClientInstallInfo>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(QUERY, key);
         while (chStmt.next()) {
            String name = chStmt.getString("osee_key");
            String data = chStmt.getString("osee_value");
            infos.add(ClientInstallInfo.createFromXml(name, data));
         }
      } finally {
         chStmt.close();
      }
      return infos;
   }
}
