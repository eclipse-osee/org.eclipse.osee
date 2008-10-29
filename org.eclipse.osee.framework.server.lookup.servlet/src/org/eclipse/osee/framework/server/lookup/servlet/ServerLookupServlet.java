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
package org.eclipse.osee.framework.server.lookup.servlet;

import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ServerLookupServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -7055381632202456561L;

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         String version = request.getParameter("version");
         boolean wasBadRequest = false;

         OseeServerInfo info = null;
         if (Strings.isValid(version)) {
            info = CoreServerActivator.getApplicationServerLookup().searchBy(version);
         } else {
            wasBadRequest = true;
         }

         if (info == null) {
            response.setStatus(wasBadRequest ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_NO_CONTENT);
            response.getWriter().write(
                  String.format("Unable to locate application server matching - [%s]", request.toString()));
         } else {
            response.setStatus(HttpServletResponse.SC_OK);
            info.write(response.getOutputStream());
         }
      } catch (Exception ex) {
         OseeLog.log(ServerLookupActivator.class, Level.SEVERE, String.format(
               "Failed to process application server lookup request [%s]", request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }
}
