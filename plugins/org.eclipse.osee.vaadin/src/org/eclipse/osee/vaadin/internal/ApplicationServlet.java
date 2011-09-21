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

import java.security.Principal;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.eclipse.osee.vaadin.ApplicationFactory;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class ApplicationServlet extends AbstractApplicationServlet {

   private final Set<ApplicationSession> sessions;
   private final ApplicationFactory factory;

   public ApplicationServlet(Set<ApplicationSession> sessions, ApplicationFactory factory) {
      this.sessions = sessions;
      this.factory = factory;
   }

   @Override
   protected Application getNewApplication(HttpServletRequest request) throws ServletException {
      Application application = factory.createInstance();
      if (application == null) {
         throw new ServletException(String.format("Error creating vaading application using [%s]",
            factory.getClass().getName()));
      }
      setApplicationMetaData(request, application);
      HttpSession httpSession = request.getSession();
      final ApplicationSession session = new ApplicationSession(application, httpSession);
      sessions.add(session);
      httpSession.setAttribute(ApplicationSession.class.getName(), new HttpSessionListener() {

         @Override
         public void sessionDestroyed(HttpSessionEvent arg0) {
            session.dispose();
            sessions.remove(session);
         }

         @Override
         public void sessionCreated(HttpSessionEvent arg0) {
            // Do Nothing
         }
      });
      return application;
   }

   @SuppressWarnings("unused")
   private void setApplicationMetaData(HttpServletRequest request, Application application) throws ServletException {

      // TODO: Hook into user admin - or have this done through a filter
      //      application.setUser(user);
      //      application.setLogoutURL(logoutURL);
      //      application.setMainWindow(mainWindow);

      Principal principal = request.getUserPrincipal();
      if (principal == null) {
         principal = new Principal() {

            @Override
            public String getName() {
               return "Guest";
            }

            @Override
            public String toString() {
               return getName();
            }
         };
      }
      //      if (request.isUserInRole("Some Role")) {
      //         application.setUserRole("myRole");
      //      } else {
      //         throw new ServletException("Access Denied");
      //      }
      application.setUser(principal);
      //      application.setLogoutURL(request.getContextPath() + "logout.jsp");
   }

   @Override
   protected Class<? extends Application> getApplicationClass() {
      return factory.getApplicationClass();
   }

   @Override
   public void destroy() {
      super.destroy();
      for (ApplicationSession info : sessions) {
         info.dispose();
      }
      sessions.clear();
   }
}
