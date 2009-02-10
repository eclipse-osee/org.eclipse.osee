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

import org.eclipse.osee.framework.core.data.OseeSession;

/**
 * @author Roberto E. Escobar
 */
public class SessionData {

   public static enum SessionState {
      CREATED, UPDATED, DELETED, CURRENT;
   }

   private SessionState sessionState;
   private OseeSession session;

   public SessionData(SessionState sessionState, OseeSession session) {
      super();
      this.sessionState = sessionState;
      this.session = session;
   }

   public SessionState getSessionState() {
      return sessionState;
   }

   public String getSessionId() {
      return this.session.getSessionId();
   }

   public void setSessionState(SessionState sessionState) {
      this.sessionState = sessionState;
   }

   public OseeSession getSession() {
      return session;
   }
}
