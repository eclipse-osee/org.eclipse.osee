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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;

/**
 * @author Roberto E. Escobar
 */
public interface ISessionManager {

   public OseeSessionGrant createSession(OseeCredential credential);

   public void releaseSession(String sessionId);

   public ISession getSessionById(String sessionId);

   public Collection<ISession> getSessionByClientAddress(String clientAddress);

   public Collection<ISession> getSessionsByUserId(String userId);

   public Collection<ISession> getAllSessions();

   public void releaseSessionImmediate(String... sessionId);

}
