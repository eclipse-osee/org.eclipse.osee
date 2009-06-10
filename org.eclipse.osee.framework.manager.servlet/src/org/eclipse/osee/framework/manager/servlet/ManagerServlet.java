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
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class ManagerServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 3334123351267606890L;

   private static enum OperationType {
      USERID, INVALID;

      public static OperationType fromString(String value) {
         OperationType toReturn = OperationType.INVALID;
         for (OperationType operType : OperationType.values()) {
            if (operType.name().equalsIgnoreCase(value)) {
               toReturn = operType;
               break;
            }
         }
         return toReturn;
      }
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
      String operation = request.getParameter("operation");
      try {
         OperationType operationType = OperationType.fromString(operation);
         switch (operationType) {
            case USERID:
               displayUser(request, response);
               break;
            default:
               displayOverview(request, response);
               break;
         }
      } catch (Exception ex) {
         OseeLog.log(InternalManagerServletActivator.class, Level.SEVERE, String.format(
               "Error processing request for protocols [%s]", request.toString()), ex);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
      //      try {
      //         response.setStatus(HttpServletResponse.SC_OK);
      //         response.setContentType("text/plain");
      //         response.setCharacterEncoding("UTF-8");
      //         response.getWriter().write("Hello Don");
      //      } catch (Exception ex) {
      //         OseeLog.log(InternalManagerServletActivator.class, Level.SEVERE, String.format(
      //               "Error processing request for protocols [%s]", request.toString()), ex);
      //         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      //         response.setContentType("text/plain");
      //         response.getWriter().write(Lib.exceptionToString(ex));
      //      } finally {
      //         response.getWriter().flush();
      //         response.getWriter().close();
      //      }
   }

   private void displayOverview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      displayResults("Overview Requested\nHello Don", request, response);
   }

   private void displayUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      displayResults("User Requested\nDonald G. Dunne", request, response);
   }

   private void displayResults(String results, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(results);
      } catch (Exception ex) {
         OseeLog.log(InternalManagerServletActivator.class, Level.SEVERE, String.format(
               "Error processing request for protocols [%s]", request.toString()), ex);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String operation = request.getParameter("operation");
      try {
         OperationType operationType = OperationType.fromString(operation);
         switch (operationType) {
            case USERID:
               break;
            default:
               break;
         }
      } catch (Exception ex) {
         OseeLog.log(InternalManagerServletActivator.class, Level.SEVERE, String.format(
               "Error processing session request [%s]", request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

}
