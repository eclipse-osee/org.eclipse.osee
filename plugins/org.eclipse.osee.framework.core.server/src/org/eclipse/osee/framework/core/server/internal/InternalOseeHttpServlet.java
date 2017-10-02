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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class InternalOseeHttpServlet extends HttpServlet {
   private static final long serialVersionUID = -4965613535312739355L;
   private boolean areLogsAllowed;
   private ProcessingStateEnum processingState;
   private HttpServletRequest request;
   private final Log logger;

   public InternalOseeHttpServlet(Log logger) {
      this.logger = logger;
      this.areLogsAllowed = false;
      this.processingState = ProcessingStateEnum.IDLE;
      this.request = null;
   }

   protected Log getLogger() {
      return logger;
   }

   protected boolean areLogsAllowed() {
      return areLogsAllowed;
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

   protected abstract void checkAccessControl(HttpServletRequest request) ;

   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      long start = 0L;
      if (areLogsAllowed()) {
         start = System.currentTimeMillis();
      }
      try {
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
      } finally {
         if (areLogsAllowed()) {
            long elapsed = System.currentTimeMillis() - start;
            getLogger().info("[%s] [%s - %s] serviced in [%s] ms", request.getMethod(), request.getContextPath(),
               request.getQueryString(), elapsed);
         }
         this.processingState = ProcessingStateEnum.IDLE;
         this.request = null;
      }
   }
}
