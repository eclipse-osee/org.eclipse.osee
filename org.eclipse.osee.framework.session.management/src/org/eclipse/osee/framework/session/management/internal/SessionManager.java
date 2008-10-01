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
package org.eclipse.osee.framework.session.management.internal;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.session.management.AuthenticationException;
import org.eclipse.osee.framework.session.management.IAuthenticationManager;
import org.eclipse.osee.framework.session.management.ICredential;
import org.eclipse.osee.framework.session.management.ISession;
import org.eclipse.osee.framework.session.management.ISessionManager;
import org.eclipse.osee.framework.session.management.SessionManagementActivator;

/**
 * @author Roberto E. Escobar
 */
public class SessionManager implements ISessionManager {

   private final Map<String, ISession> sessions;

   public SessionManager() {
      this.sessions = new HashMap<String, ISession>();
   }

   public ISession getSessionById(String sessionId) {
      return sessions.get(sessionId);
   }

   public ISession authenticate(ICredential credential) throws AuthenticationException {
      IAuthenticationManager authenticationManager = SessionManagementActivator.getAuthenticationManager();
      boolean isAuthenticated = authenticationManager.authenticate(credential);
      return isAuthenticated ? createSession(credential) : null;
   }

   private ISession createSession(ICredential credential) {
      ISession toReturn = null;
      // TODO Populate Session Here;

      sessions.put(toReturn.getSessionId(), toReturn);
      return toReturn;
   }
}
