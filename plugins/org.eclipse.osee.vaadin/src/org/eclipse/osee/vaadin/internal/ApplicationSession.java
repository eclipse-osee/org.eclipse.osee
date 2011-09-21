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
package org.eclipse.osee.vaadin.internal;

import javax.servlet.http.HttpSession;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

/**
 * @author Roberto E. Escobar
 */
public final class ApplicationSession implements Comparable<ApplicationSession> {

   private final Application application;
   private final HttpSession httpSession;

   public ApplicationSession(Application application, HttpSession session) {
      this.application = application;
      this.httpSession = session;
   }

   public Application getApplication() {
      return application;
   }

   public HttpSession getHttpSession() {
      return httpSession;
   }

   public void dispose() {
      Application application = getApplication();
      if (application != null) {
         application.close();
      }
      httpSession.removeAttribute(ApplicationServlet.class.getName());
      httpSession.removeAttribute(WebApplicationContext.class.getName());
   }

   @Override
   public int compareTo(ApplicationSession o) {
      return o.getHttpSession().getId().compareTo(this.getHttpSession().getId());
   }

}