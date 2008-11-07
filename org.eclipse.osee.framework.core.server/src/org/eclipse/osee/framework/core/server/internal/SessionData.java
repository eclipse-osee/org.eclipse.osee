/*
 * Created on Nov 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.server.internal;

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
