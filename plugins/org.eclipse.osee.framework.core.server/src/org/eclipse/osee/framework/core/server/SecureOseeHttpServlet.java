/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.server;

import javax.servlet.http.HttpServletRequest;
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

   protected String getSessionId(HttpServletRequest request) {
      return request.getParameter("sessionId");
   }

}
