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
package org.eclipse.osee.framework.core.server.internal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.data.SqlKey;
import org.eclipse.osee.framework.core.exception.OseeInvalidSessionException;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class SessionManager implements ISessionManager {

   private static final long DATASTORE_UPDATE = 1000 * 5;

   private static enum SessionState {
      CREATED, UPDATED, DELETED, CURRENT;
   }

   private final Map<String, SessionData> sessions;
   private final Timer updateTimer;

   public SessionManager() {
      this.sessions = Collections.synchronizedMap(new HashMap<String, SessionData>());

      this.updateTimer = new Timer("Persist Session Data Timer");
      updateTimer.scheduleAtFixedRate(new UpdateDataStore(), 2000, DATASTORE_UPDATE);
   }

   public SessionData getSessionById(String sessionId) {
      return sessions.get(sessionId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#createSession(java.lang.String, org.eclipse.osee.framework.core.data.OseeCredential)
    */
   @Override
   public OseeSessionGrant createSession(String authenticationProtocol, OseeCredential credential) throws OseeCoreException {
      boolean isAuthenticated = false;
      boolean wasDbInitUser = false;
      OseeSessionGrant toReturn = null;

      wasDbInitUser = credential.getUserId().equals(OseeServerContext.DB_INIT_SESSION_ID);

      if (!wasDbInitUser) {
         IAuthenticationManager authenticationManager = CoreServerActivator.getAuthenticationManager();
         isAuthenticated = authenticationManager.authenticate(authenticationProtocol, credential);
      } else {
         isAuthenticated = true;
      }

      if (isAuthenticated) {
         String sessionId = wasDbInitUser != true ? GUID.generateGuidStr() : credential.getUserId();

         SessionState sessionState = SessionState.CREATED;
         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();

         // TODO Find user id 

         OseeSession session =
               new OseeSession(sessionId, credential.getUserId(), timestamp, credential.getClientMachineName(),
                     credential.getClientAddress(), credential.getPort(), credential.getVersion(), timestamp,
                     sessionState.name().toLowerCase());

         SessionData sessionData = new SessionData(sessionState, session);
         sessions.put(sessionData.getSessionId(), sessionData);
         toReturn = internalCreateGrant(sessionData);
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#releaseSession(java.lang.String)
    */
   @Override
   public void releaseSession(String sessionId) {
      SessionData sessionData = getSessionById(sessionId);
      if (sessionData != null) {
         sessionData.setSessionState(SessionState.DELETED);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#updateSessionActivity(java.lang.String, java.lang.String)
    */
   @Override
   public void updateSessionActivity(String sessionId, String interactionName) throws OseeInvalidSessionException {
      SessionData sessionData = getSessionById(sessionId);
      if (sessionData != null) {
         if (sessionData.getSessionState() == SessionState.CURRENT) {
            sessionData.setSessionState(SessionState.UPDATED);
         }
         sessionData.getSession().setLastInteraction(Strings.isValid(interactionName) ? interactionName : "");
         sessionData.getSession().setLastInteractionDate(GlobalTime.GreenwichMeanTimestamp());
      } else {
         throw new OseeInvalidSessionException(String.format("Session was invalid: [%s]", sessionId));
      }
   }

   private OseeSessionGrant internalCreateGrant(SessionData sessionData) throws OseeDataStoreException {
      OseeSessionGrant sessionGrant = new OseeSessionGrant(sessionData.getSessionId());
      DbInformation dbInformation = OseeDbConnection.getDefaultDatabaseService();
      sessionGrant.setUserArtifactId(sessionData.getSession().getUserId());
      sessionGrant.setDbDriver(dbInformation.getConnectionData().getDBDriver());
      sessionGrant.setDbUrl(dbInformation.getConnectionUrl());
      sessionGrant.setDbConnectionName(dbInformation.getDatabaseDetails().getFieldValue(ConfigField.DatabaseName));
      sessionGrant.setDbConnectionProperties(dbInformation.getProperties());
      sessionGrant.setSqlProperties(SqlKey.getSqlProperties());
      return sessionGrant;
   }

   private final class UpdateDataStore extends TimerTask {
      @Override
      public void run() {
         List<String> deleteIds = new ArrayList<String>();
         List<OseeSession> createData = new ArrayList<OseeSession>();
         List<OseeSession> updateData = new ArrayList<OseeSession>();
         for (String sessionId : sessions.keySet()) {
            SessionData sessionData = sessions.get(sessionId);
            if (sessionData != null) {
               switch (sessionData.getSessionState()) {
                  case CREATED:
                     createData.add(sessionData.getSession());
                     break;
                  case DELETED:
                     deleteIds.add(sessionData.getSessionId());
                     break;
                  case UPDATED:
                     updateData.add(sessionData.getSession());
                     break;
                  default:
                     break;
               }
            }
         }
         createItems(createData);
         updateItems(updateData);
         deleteItems(deleteIds);

      }

      private void updateItems(List<OseeSession> sessionsList) {
         createUpdateHelper(sessionsList, false);
      }

      private void createItems(List<OseeSession> sessionsList) {
         createUpdateHelper(sessionsList, true);
      }

      private void createUpdateHelper(List<OseeSession> sessionsList, boolean isCreate) {
         try {
            if (!sessionsList.isEmpty()) {
               OseeSession[] sessionsArray = sessionsList.toArray(new OseeSession[sessionsList.size()]);
               SessionState stateToSet = isCreate ? SessionState.CREATED : SessionState.UPDATED;
               if (isCreate) {
                  SessionDataStore.createSessions(sessionsArray);
               } else {
                  SessionDataStore.updateSessions(sessionsArray);
               }
               for (OseeSession session : sessionsArray) {
                  SessionData sessionData = sessions.get(session.getSessionId());
                  if (sessionData.sessionState == stateToSet) {
                     sessionData.setSessionState(SessionState.CURRENT);
                  }
               }
            }
         } catch (OseeDataStoreException ex) {
            OseeLog.log(CoreServerActivator.class, Level.SEVERE, String.format("Error persisting %s sessions",
                  isCreate ? "new" : "modified"), ex);
         }
      }

      private void deleteItems(List<String> sessionIds) {
         try {
            if (!sessionIds.isEmpty()) {
               SessionDataStore.deleteSession(sessionIds.toArray(new String[sessionIds.size()]));
               for (String ids : sessionIds) {
                  sessions.remove(ids);
               }
            }
         } catch (OseeDataStoreException ex) {
            OseeLog.log(CoreServerActivator.class, Level.SEVERE, "Error deleting sessions", ex);
         }
      }
   }

   private final class SessionData {
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
}
