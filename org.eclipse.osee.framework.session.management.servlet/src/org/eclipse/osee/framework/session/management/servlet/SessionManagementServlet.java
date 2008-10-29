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
package org.eclipse.osee.framework.session.management.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.session.management.ISessionManager;

/**
 * @author Roberto E. Escobar
 */
public class SessionManagementServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 3334123351267606890L;

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

      System.out.println("Session Management Called");

      ISessionManager manager = SessionManagementServletActivator.getInstance().getSessionManager();
      System.out.println("Manager: " + manager.getClass().getSimpleName());
   }
}
