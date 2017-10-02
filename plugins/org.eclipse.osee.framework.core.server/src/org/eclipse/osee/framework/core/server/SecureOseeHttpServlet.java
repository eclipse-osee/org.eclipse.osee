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
package org.eclipse.osee.framework.core.server;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SecureOseeHttpServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -4034231476048459552L;
   private final ISessionManager sessionManager;

   public SecureOseeHttpServlet(Log logger, ISessionManager sessionManager) {
      super(logger);
      this.sessionManager = sessionManager;
   }

   protected ISessionManager getSessionManager() {
      return sessionManager;
   }

   @Override
   protected void checkAccessControl(HttpServletRequest request) {
      String sessionId = getSessionId(request);
      ISession session = sessionManager.getSessionById(sessionId);
      Conditions.checkNotNull(session, "session");
   }

   public boolean isInitializing(HttpServletRequest request) {
      String sessionId = getSessionId(request);
      ISession session = sessionManager.getSessionById(sessionId);
      String userId = session.getUserId();
      return SystemUser.BootStrap.getUserId().equals(userId);
   }

   protected String getSessionId(HttpServletRequest request) {
      return request.getParameter("sessionId");
   }

}
