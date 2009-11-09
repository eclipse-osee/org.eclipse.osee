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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class SessionManagementServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 3334123351267606890L;

   private static enum OperationType {
      CREATE,
      RELEASE,
      INVALID;

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

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      // Allow access to all
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         String[] protocols = MasterServletActivator.getInstance().getAuthenticationManager().getProtocols();
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(Arrays.deepToString(protocols));
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

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String operation = request.getParameter("operation");
      try {
         OperationType operationType = OperationType.fromString(operation);
         switch (operationType) {
            case CREATE:
               createSession(request, response);
               break;
            case RELEASE:
               releaseSession(request, response);
               break;
            default:
               break;
         }
      } catch (Exception ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format("Error processing session request [%s]",
               request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   private void createSession(HttpServletRequest request, HttpServletResponse response) throws OseeCoreException {
      try {
         ISessionManager manager = MasterServletActivator.getInstance().getSessionManager();
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         Lib.inputStreamToOutputStream(request.getInputStream(), outputStream);
         byte[] bytes = outputStream.toByteArray();
         // TODO Decrypt credential info

         OseeCredential credential = OseeCredential.fromXml(new ByteArrayInputStream(bytes));
         OseeSessionGrant oseeSessionGrant = manager.createSession(credential);

         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
         oseeSessionGrant.write(byteOutputStream);

         // TODO after encrypted these will need to change
         response.setContentType("application/xml");
         response.setCharacterEncoding("UTF-8");
         response.setContentLength(byteOutputStream.size());
         Lib.inputStreamToOutputStream(new ByteArrayInputStream(byteOutputStream.toByteArray()),
               response.getOutputStream());

      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         try {
            response.getOutputStream().flush();
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }

   private void releaseSession(HttpServletRequest request, HttpServletResponse response) throws OseeCoreException {
      try {
         ISessionManager manager = MasterServletActivator.getInstance().getSessionManager();
         String sessionId = request.getParameter("sessionId");
         manager.releaseSession(sessionId);
         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         response.setContentType("text/plain");
         response.getWriter().write(String.format("Session [%s] released.", sessionId));
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         try {
            response.getWriter().flush();
            response.getWriter().close();
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }
}
