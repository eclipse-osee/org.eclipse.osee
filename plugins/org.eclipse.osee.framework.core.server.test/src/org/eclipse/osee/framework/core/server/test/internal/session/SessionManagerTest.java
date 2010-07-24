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
package org.eclipse.osee.framework.core.server.test.internal.session;

import org.eclipse.osee.framework.core.model.test.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.internal.session.ISessionQuery;
import org.eclipse.osee.framework.core.server.internal.session.Session;
import org.eclipse.osee.framework.core.server.internal.session.SessionCache;
import org.eclipse.osee.framework.core.server.internal.session.SessionFactory;
import org.eclipse.osee.framework.core.server.internal.session.SessionManagerImpl;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link SessionManagerImpl}
 * 
 * @author Roberto E. Escobar
 */
public class SessionManagerTest {

   @Ignore
   @Test
   public void test() {
      SessionFactory factory = null;
      ISessionQuery query = null;
      IAuthenticationManager authenticator = null;
      MockOseeDataAccessor<Session> accessor = new MockOseeDataAccessor<Session>();
      SessionCache sessionCache = new SessionCache(accessor);
      new SessionManagerImpl("ABCD", factory, query, sessionCache, authenticator);

      //		OseeSessionGrant grant = sessionManager.createSession(OseeCredential credential); throws OseeCoreException;

      //		sessionManager.releaseSession(String sessionId) throws OseeCoreException;
      //		sessionManager.updateSessionActivity(String sessionId, String interactionName) throws OseeCoreException;

      //		ISession session = sessionManager.getSessionById(String sessionId) throws OseeCoreException;

      //		Collection<ISession> sessionManager.getSessionByClientAddress(String clientAddress) throws OseeCoreException;
      //		Collection<ISession> sessionManager.getSessionsByUserId(String userId, boolean includeNonServerManagedSessions) throws OseeCoreException;
      //		Collection<ISession> sessionManager.getAllSessions(boolean includeNonServerManagedSessions) throws OseeCoreException;

      //		sessionManager.releaseSessionImmediate(String... sessionId) throws OseeCoreException;
   }
}
