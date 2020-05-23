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

package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.server.ISession;

/**
 * @author Roberto E. Escobar
 */
public final class DefaultSessionCollector implements ISessionCollector {
   private final Collection<ISession> sessions;
   private final SessionFactory factory;

   public DefaultSessionCollector(SessionFactory factory, Collection<ISession> sessions) {
      this.factory = factory;
      this.sessions = sessions;
   }

   @Override
   public void collect(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort) {
      Session session = factory.createLoadedSession(guid, userId, creationDate, clientVersion, clientMachineName,
         clientAddress, clientPort);
      sessions.add(session);
   }
}