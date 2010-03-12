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
package org.eclipse.osee.framework.core.server.internal;

import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public abstract class InternalOseeHttpServlet extends HttpServlet {
   private static final long serialVersionUID = -4965613535312739355L;
   private boolean areRequestsAllowed;
   private boolean areLogsAllowed;
   private ProcessingStateEnum processingState;
   private HttpServletRequest request;

   public InternalOseeHttpServlet() {
      this.areRequestsAllowed = true;
      this.areLogsAllowed = false;
      this.processingState = ProcessingStateEnum.IDLE;
      this.request = null;
   }

   protected boolean areRequestsAllowed() {
      return areRequestsAllowed;
   }

   protected boolean areLogsAllowed() {
      return areLogsAllowed;
   }

   void setRequestsAllowed(boolean value) {
      areRequestsAllowed = value;
   }

   void setLogsAllowed(boolean value) {
      areLogsAllowed = value;
   }

   ProcessingStateEnum getState() {
      return processingState;
   }

   String getCurrentRequest() {
      return request != null ? String.format("[%s] [%s - %s]", request.getMethod(), request.getContextPath(),
            request.getQueryString()) : "";
   }

   protected abstract void checkAccessControl(HttpServletRequest request) throws OseeCoreException;

   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      long start = 0L;
      if (areLogsAllowed()) {
         start = System.currentTimeMillis();
      }
      try {
         if (areRequestsAllowed()) {
            this.processingState = ProcessingStateEnum.BUSY;
            this.request = request;
            try {
               checkAccessControl(request);
               super.service(request, response);
            } catch (OseeCoreException ex) {
               response.setStatus(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
               response.getWriter().write(Lib.exceptionToString(ex));
               throw new ServletException(ex);
            }
         } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("All requests are currently blocked.");
         }
      } finally {
         if (areLogsAllowed()) {
            long elapsed = System.currentTimeMillis() - start;
            OseeLog.log(this.getClass(), Level.INFO, String.format("[%s] [%s - %s] serviced in [%s] ms",
                  request.getMethod(), request.getContextPath(), request.getQueryString(), elapsed));
         }
         this.processingState = ProcessingStateEnum.IDLE;
         this.request = null;
      }
   }
}
