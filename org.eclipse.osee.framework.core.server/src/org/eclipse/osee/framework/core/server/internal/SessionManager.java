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

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.exception.OseeInvalidSessionException;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.core.server.SessionData;
import org.eclipse.osee.framework.core.server.SessionData.SessionState;
import org.eclipse.osee.framework.db.connection.DatabaseInfoManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class SessionManager implements ISessionManager {

   private static final long DATASTORE_UPDATE = 1000 * 5;

   private final Map<String, SessionData> sessionCache;
   private final Timer updateTimer;

   public SessionManager() {
      this.sessionCache = Collections.synchronizedMap(new HashMap<String, SessionData>());
      this.updateTimer = new Timer("Persist Session Data Timer");
      updateTimer.scheduleAtFixedRate(new UpdateDataStore(), DATASTORE_UPDATE, DATASTORE_UPDATE);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#getSessionByClientAddress(java.lang.String)
    */
   @Override
   public List<SessionData> getSessionByClientAddress(String clientAddress) {
      List<SessionData> toReturn = new ArrayList<SessionData>();
      synchronized (sessionCache) {
         for (SessionData sessionData : sessionCache.values()) {
            if (sessionData.getSession().getClientAddress().equals(clientAddress)) {
               toReturn.add(sessionData);
            }
         }
      }
      return toReturn;
   }

   public boolean isAlive(OseeSession oseeSession) throws OseeCoreException {
      boolean wasAlive = false;
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         URL url =
               new URL(String.format("http://%s:%s/osee/request?cmd=pingId", oseeSession.getClientAddress(),
                     oseeSession.getPort()));
         AcquireResult result = HttpProcessor.acquire(url, outputStream);
         if (result.wasSuccessful()) {
            String sessionId = outputStream.toString(result.getEncoding());
            if (Strings.isValid(sessionId)) {
               wasAlive = sessionId.contains(oseeSession.getSessionId());
            }
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      return wasAlive;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#getSessions(java.lang.String, boolean includeNonServerManagedSessions)
    */
   @Override
   public List<SessionData> getAllSessions(boolean includeNonServerManagedSessions) throws OseeDataStoreException {
      List<SessionData> toReturn = null;
      if (includeNonServerManagedSessions) {
         toReturn = SessionDataStore.getAllSessions();
      } else {
         synchronized (sessionCache) {
            toReturn = new ArrayList<SessionData>(sessionCache.values());
         }
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#getSessionsByUserId(java.lang.String, boolean)
    */
   @Override
   public List<SessionData> getSessionsByUserId(String userId, boolean includeNonServerManagedSessions) throws OseeCoreException {
      List<SessionData> toReturn = null;
      if (includeNonServerManagedSessions) {
         toReturn = SessionDataStore.getAllSessions();
      } else {
         toReturn = new ArrayList<SessionData>();
         synchronized (sessionCache) {
            for (SessionData sessionData : sessionCache.values()) {
               if (sessionData.getSession().getUserId().equals(userId)) {
                  toReturn.add(sessionData);
               }
            }
         }
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#getSessionById(java.lang.String)
    */
   @Override
   public SessionData getSessionById(String sessionId) {
      return sessionCache.get(sessionId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.ISessionManager#createSession(org.eclipse.osee.framework.core.data.OseeCredential)
    */
   @Override
   public OseeSessionGrant createSession(OseeCredential credential) throws OseeCoreException {
      OseeSessionGrant sessionGrant = null;

      IAuthenticationManager authenticationManager = CoreServerActivator.getAuthenticationManager();
      boolean isAuthenticated = authenticationManager.authenticate(credential);

      if (isAuthenticated) {
         SessionState sessionState = SessionState.CREATED;
         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();

         IOseeUserInfo oseeUserInfo = authenticationManager.asOseeUser(credential);

         OseeSession session =
               new OseeSession(GUID.generateGuidStr(), oseeUserInfo.getUserID(), timestamp,
                     credential.getClientMachineName(), credential.getClientAddress(), credential.getPort(),
                     credential.getVersion(), timestamp, sessionState.name().toLowerCase());

         SessionData sessionData = new SessionData(sessionState, session);
         sessionCache.put(sessionData.getSessionId(), sessionData);
         sessionGrant = new OseeSessionGrant(sessionData.getSessionId());
         sessionGrant.setCreationRequired(oseeUserInfo.isCreationRequired());
         sessionGrant.setOseeUserInfo(oseeUserInfo);
         sessionGrant.setDatabaseInfo(DatabaseInfoManager.getDefault());
         sessionGrant.setSqlProperties(OseeSql.getSqlProperties());
         sessionGrant.setDataStorePath(OseeServerProperties.getOseeApplicationServerData());
      }
      return sessionGrant;
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

   public void releaseSessionImmediate(String... sessionIds) throws OseeCoreException {
      if (sessionIds != null && sessionIds.length > 0) {
         SessionDataStore.deleteSession(sessionIds);
         synchronized (sessionCache) {
            for (String session : sessionIds) {
               sessionCache.remove(session);
            }
         }
      }
   }

   private final class UpdateDataStore extends TimerTask {

      private boolean firstTimeThrough = true;

      @Override
      public void run() {
         if (firstTimeThrough) {
            firstTimeThrough = false;
            if (SessionDataStore.isSessionTableAvailable()) {
               recoverSessions();
            }
         }

         List<String> deleteIds = new ArrayList<String>();
         List<OseeSession> createData = new ArrayList<OseeSession>();
         List<OseeSession> updateData = new ArrayList<OseeSession>();
         synchronized (sessionCache) {
            for (SessionData sessionData : sessionCache.values()) {
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
      }

      private void recoverSessions() {
         try {
            String serverId = CoreServerActivator.getApplicationServerManager().getId();
            SessionDataStore.loadSessions(serverId, sessionCache);
         } catch (OseeDataStoreException ex) {
            OseeLog.log(CoreServerActivator.class, Level.WARNING, "Error loading sessions.", ex);
         }
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
               String serverId = CoreServerActivator.getApplicationServerManager().getId();
               OseeSession[] sessionsArray = sessionsList.toArray(new OseeSession[sessionsList.size()]);
               SessionState stateToSet = isCreate ? SessionState.CREATED : SessionState.UPDATED;
               if (isCreate) {
                  SessionDataStore.createSessions(serverId, sessionsArray);
               } else {
                  SessionDataStore.updateSessions(serverId, sessionsArray);
               }
               for (OseeSession session : sessionsArray) {
                  SessionData sessionData = sessionCache.get(session.getSessionId());
                  if (sessionData.getSessionState() == stateToSet) {
                     sessionData.setSessionState(SessionState.CURRENT);
                  }
               }
            }
         } catch (OseeDataStoreException ex) {
            // Do Nothing
         }
      }

      private void deleteItems(List<String> sessionIds) {
         try {
            if (!sessionIds.isEmpty()) {
               SessionDataStore.deleteSession(sessionIds.toArray(new String[sessionIds.size()]));
               for (String ids : sessionIds) {
                  sessionCache.remove(ids);
               }
            }
         } catch (OseeDataStoreException ex) {
            // Do Nothing
         }
      }
   }

}
