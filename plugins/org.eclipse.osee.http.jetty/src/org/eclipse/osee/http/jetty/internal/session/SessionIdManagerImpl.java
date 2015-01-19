/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.internal.session;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.http.jetty.JettyLogger;

/**
 * Based on org.eclipse.jetty.server.session.JDBCSessionIdManager
 * 
 * @author Roberto E. Escobar
 */
public class SessionIdManagerImpl extends AbstractSessionIdManager {

   public interface SessionIdStorage {

      void insertSessionId(String id);

      void deleteSessionId(String id);

      boolean sessionIdExists(String id);

      List<String> cleanExpiredSessions();

      List<String> getBoundedExpiredSessions(long lastScavengeTime, long scavengeIntervalMs);

      void deleteExpiredSessions(long expiresInMax);
   }

   protected final HashSet<String> sessionIds = new HashSet<String>();

   private final JettyLogger logger;
   private final Server server;
   private final SessionIdStorage storage;

   protected long scavengeIntervalMs = 1000L * 60L * 10L; //10mins

   protected Timer scavengeTimer;
   protected TimerTask scavengeTimerTask;
   protected long lastScavengeTime;

   public SessionIdManagerImpl(JettyLogger logger, SessionIdStorage storage, Server server) {
      super();
      this.logger = logger;
      this.storage = storage;
      this.server = server;
   }

   public SessionIdManagerImpl(JettyLogger logger, SessionIdStorage storage, Server server, Random random) {
      super(random);
      this.logger = logger;
      this.storage = storage;
      this.server = server;
   }

   public void setScavengeInterval(long sec) {
      if (sec <= 0) {
         sec = 60;
      }

      long old_period = scavengeIntervalMs;
      long period = sec * 1000L;

      scavengeIntervalMs = period;

      //add a bit of variability into the scavenge time so that not all
      //nodes with the same scavenge time sync up
      long tenPercent = scavengeIntervalMs / 10;
      if ((System.currentTimeMillis() % 2) == 0) {
         scavengeIntervalMs += tenPercent;
      }

      logger.debug("Scavenging every [%s ms]", scavengeIntervalMs);
      if (scavengeTimer != null && (period != old_period || scavengeTimerTask == null)) {
         synchronized (this) {
            if (scavengeTimerTask != null) {
               scavengeTimerTask.cancel();
            }
            scavengeTimerTask = new TimerTask() {
               @Override
               public void run() {
                  scavenge();
               }
            };
            scavengeTimer.schedule(scavengeTimerTask, scavengeIntervalMs, scavengeIntervalMs);
         }
      }
   }

   public long getScavengeInterval() {
      return scavengeIntervalMs / 1000;
   }

   @Override
   public void addSession(HttpSession session) {
      if (session != null) {
         synchronized (sessionIds) {
            String id = ((HttpSessionImpl) session).getClusterId();
            try {
               storage.insertSessionId(id);
               sessionIds.add(id);
            } catch (Exception ex) {
               logger.warn(ex, "Problem storing session id - [%s]", id);
            }
         }
      }
   }

   @Override
   public void removeSession(HttpSession session) {
      if (session != null) {
         removeSession(((JDBCSessionManager.Session) session).getClusterId());
      }
   }

   public void removeSession(String id) {
      if (id != null) {
         synchronized (sessionIds) {
            logger.debug("Removing session id - [%s]", id);
            try {
               sessionIds.remove(id);
               storage.deleteSessionId(id);
            } catch (Exception ex) {
               logger.warn(ex, "Problem removing session id  - [%s]", id);
            }
         }
      }
   }

   @Override
   public String getClusterId(String nodeId) {
      int dotIndex = nodeId.lastIndexOf('.');
      return (dotIndex > 0) ? nodeId.substring(0, dotIndex) : nodeId;
   }

   @Override
   public String getNodeId(String clusterId, HttpServletRequest request) {
      return _workerName != null ? clusterId + '.' + _workerName : clusterId;
   }

   @Override
   public boolean idInUse(String id) {
      boolean result = false;
      if (id != null) {
         String clusterId = getClusterId(id);
         boolean inUse = false;
         synchronized (sessionIds) {
            inUse = sessionIds.contains(clusterId);
         }
         if (inUse) {
            result = true;
         } else {
            try {
               result = storage.sessionIdExists(clusterId);
            } catch (Exception ex) {
               logger.warn(ex, "Problem checking inUse for id - [%s]", clusterId);
               result = false;
            }
         }
      }
      return result;
   }

   @Override
   public void invalidateAll(String id) {
      removeSession(id);
      synchronized (sessionIds) {
         // tell all contexts that may have a session object with this id to get rid of them
         Handler[] contexts = server.getChildHandlersByClass(ContextHandler.class);
         for (int i = 0; contexts != null && i < contexts.length; i++) {
            SessionHandler sessionHandler = ((ContextHandler) contexts[i]).getChildHandlerByClass(SessionHandler.class);
            if (sessionHandler != null) {
               SessionManager manager = sessionHandler.getSessionManager();

               if (manager != null && manager instanceof SessionManagerImpl) {
                  ((SessionManagerImpl) manager).invalidateSession(id);
               }
            }
         }
      }
   }

   @Override
   public void doStart() throws Exception {
      try {
         List<String> expiredSessionIds = storage.cleanExpiredSessions();
         synchronized (sessionIds) {
            sessionIds.removeAll(expiredSessionIds); //in case they were in our local cache of session ids
         }
      } catch (Exception ex) {
         logger.debug("Error cleaning up expired sessions [%s]", Lib.exceptionToString(ex));
      }

      super.doStart();
      logger.debug("Scavenging interval = [%s secs]", getScavengeInterval());
      scavengeTimer = new Timer("JDBCSessionScavenger", true);
      setScavengeInterval(getScavengeInterval());
   }

   @Override
   public void doStop() throws Exception {
      synchronized (this) {
         if (scavengeTimerTask != null) {
            scavengeTimerTask.cancel();
         }
         if (scavengeTimer != null) {
            scavengeTimer.cancel();
         }
         scavengeTimer = null;
      }
      sessionIds.clear();
      super.doStop();
   }

   /**
    * Look for sessions in the database that have expired. We do this in the SessionIdManager and not the SessionManager
    * so that we only have 1 scavenger, otherwise if there are n SessionManagers there would be n scavengers, all
    * contending for the database. We look first for sessions that expired in the previous interval, then for sessions
    * that expired previously - these are old sessions that no node is managing any more and have become stuck in the
    * database.
    */
   private void scavenge() {
      try {
         logger.debug("Scavenge sweep started at [%s]", System.currentTimeMillis());
         if (lastScavengeTime > 0) {

            long lowerBound = (lastScavengeTime - scavengeIntervalMs);
            long upperBound = lastScavengeTime;

            List<String> expiredSessionIds = storage.getBoundedExpiredSessions(lowerBound, upperBound);

            //tell the SessionManagers to expire any sessions with a matching sessionId in memory
            Handler[] contexts = server.getChildHandlersByClass(ContextHandler.class);
            for (int i = 0; contexts != null && i < contexts.length; i++) {

               SessionHandler sessionHandler =
                  ((ContextHandler) contexts[i]).getChildHandlerByClass(SessionHandler.class);
               if (sessionHandler != null) {
                  SessionManager manager = sessionHandler.getSessionManager();
                  if (manager != null && manager instanceof JDBCSessionManager) {
                     ((SessionManagerImpl) manager).expire(expiredSessionIds);
                  }
               }
            }
            //find all sessions that have expired at least a couple of scanIntervals ago and just delete them
            upperBound = lastScavengeTime - (2 * scavengeIntervalMs);
            if (upperBound > 0) {
               storage.deleteExpiredSessions(upperBound);
            }
         }
      } catch (Exception ex) {
         if (isRunning()) {
            logger.debug("Problem selecting expired sessions - [%s]", Lib.exceptionToString(ex));
         }
      } finally {
         lastScavengeTime = System.currentTimeMillis();
         logger.debug("Scavenge sweep ended at [%s]", lastScavengeTime);
      }
   }
}