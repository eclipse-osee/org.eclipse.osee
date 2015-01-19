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

import static org.eclipse.osee.http.jetty.JettyException.newJettyException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.osee.http.jetty.JettyConstants;
import org.eclipse.osee.http.jetty.JettyLogger;

/**
 * Based on org.eclipse.jetty.server.session.JDBCSessionManager
 * 
 * <pre>
 * SessionManager that persists sessions to a database to enable clustering. 
 * 
 * Session data is persisted to the SessionStorage: 
 *    rowId (unique in cluster: webapp name/path + virtualhost + sessionId) 
 *    contextPath (of the context owning the session) 
 *    sessionId (unique in a context) 
 *    lastNode (name of node last handled session) 
 *    accessTime (time in milliseconds session was accessed) 
 *    lastAccessTime (previous time in milliseconds session was accessed)
 *    createTime (time in milliseconds session created) 
 *    cookieTime (time in milliseconds session cookie created)
 *    lastSavedTime (last time in milliseconds session access times were saved) 
 *    expiryTime (time in milliseconds that the session is due to expire) 
 *    attributes (attribute map) 
 * 
 * As an optimization, to prevent thrashing the database, we do not persist the accessTime and lastAccessTime 
 * every time the session is accessed. Rather, we write it out every so often.
 * 
 * The frequency is controlled by the saveIntervalSec field.
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public class SessionManagerImpl extends AbstractSessionManager {

   public interface SessionStorage {

      HttpSessionImpl loadSession(SessionManagerImpl sessionManager, String id, String contextPath, String virtualHosts);

      void insertSession(String nodeId, String sessionRowId, HttpSessionImpl session);

      void deleteSession(HttpSessionImpl data);

      void updateSession(String nodeId, HttpSessionImpl session);

      void updateSessionNode(String nodeId, HttpSessionImpl session);

      void updateSessionAccessTime(String nodeId, HttpSessionImpl session);

   }

   private ConcurrentHashMap<String, AbstractSession> sessionMap;

   private final JettyLogger logger;
   private final SessionStorage storage;

   private SessionIdManagerImpl sessionIdManager;
   protected volatile long saveIntervalSec = JettyConstants.DEFAULT_JETTY_JDBC_SESSION__SAVE_INTERVAL_SECS;

   public SessionManagerImpl(JettyLogger logger, SessionStorage storage) {
      super();
      this.logger = logger;
      this.storage = storage;
   }

   /**
    * Set the time in seconds which is the interval between saving the session access time to the database. This is an
    * optimization that prevents the database from being overloaded when a session is accessed very frequently. On
    * session exit, if the session attributes have NOT changed, the time at which we last saved the accessed time is
    * compared to the current accessed time. If the interval is at least saveIntervalSecs, then the access time will be
    * persisted to the database. If any session attribute does change, then the attributes and the accessed time are
    * persisted.
    */
   public void setSaveInterval(long sec) {
      saveIntervalSec = sec;
   }

   public long getSaveInterval() {
      return saveIntervalSec;
   }

   /**
    * A method that can be implemented in subclasses to support distributed caching of sessions. This method will be
    * called whenever the session is written to the database because the session data has changed. This could be used eg
    * with a JMS backplane to notify nodes that the session has changed and to delete the session from the node's cache,
    * and re-read it from the database.
    */
   public void cacheInvalidate(HttpSessionImpl session) {
      //  This would be used to send an event to other servers in the cluster to tell them to invalidate there in memory cache
   }

   /**
    * A session has been requested by its id on this node. Load the session by id AND context path from the database.
    * Multiple contexts may share the same session id (due to dispatching) but they CANNOT share the same contents.
    * Check if last node id is my node id, if so, then the session we have in memory cannot be stale. If another node
    * used the session last, then we need to refresh from the db. NOTE: this method will go to the database, so if you
    * only want to check for the existence of a Session in memory, use _sessions.get(id) instead.
    */
   @Override
   public HttpSessionImpl getSession(String idInCluster) {
      HttpSessionImpl session = null;
      HttpSessionImpl memSession = (HttpSessionImpl) sessionMap.get(idInCluster);

      synchronized (this) {
         // check if we need to reload the session -
         // as an optimization, don't reload on every access
         // to reduce the load on the database. This introduces a window of
         // possibility that the node may decide that the session is local to it,
         // when the session has actually been live on another node, and then
         // re-migrated to this node. This should be an extremely rare occurrence,
         // as load-balancers are generally well-behaved and consistently send
         // sessions to the same node, changing only if that node fails.
         long now = System.currentTimeMillis();
         long lastSaved = memSession == null ? 0 : memSession.getLastSaved();

         String nodeId = getSessionIdManager().getWorkerName();
         if (memSession == null) {
            logger.debug("getSession(%s) - not in session map - now[%s] lastSaved[%s] interval[%s]", idInCluster, now,
               lastSaved, saveIntervalSec * 1000L);
         } else {
            logger.debug(
               "getSession(%s) - in session map - now[%s] lastSaved[%s] interval[%s] lastNode[%s] thisNode[%s] difference[%s]",
               idInCluster, now, lastSaved, saveIntervalSec * 1000L, memSession.getLastNode(), nodeId, now - lastSaved);
         }

         try {
            if (memSession == null) {
               logger.debug("getSession(%s) - no session in session map. Reloading session data from db.", idInCluster);
               session = loadSession(idInCluster, canonicalize(_context.getContextPath()), getVirtualHost(_context));
            } else if ((now - lastSaved) >= (saveIntervalSec * 1000L)) {
               logger.debug("getSession(%s) - stale session. Reloading session data from db.", idInCluster);
               session = loadSession(idInCluster, canonicalize(_context.getContextPath()), getVirtualHost(_context));
            } else {
               logger.debug("getSession(%s) - session in session map", idInCluster);
               session = memSession;
            }
         } catch (Exception ex) {
            logger.warn(ex, "Unable to load session - idInCluster [%s]", idInCluster);
            return null;
         }

         if (session != null) {
            //If the session was last used on a different node, or session doesn't exist on this node
            if (!session.getLastNode().equals(nodeId) || memSession == null) {
               //if session doesn't expire, or has not already expired, update it and put it in this nodes' memory
               long expiresIn = session.getExpiryTime();
               if (expiresIn <= 0 || expiresIn > now) {
                  logger.debug("getSession(%s): lastNode[%s] thisNode[%s]", idInCluster, session.getLastNode(), nodeId);
                  session.setLastNode(nodeId);
                  sessionMap.put(idInCluster, session);

                  //update in storage: if unable to update, session will be scavenged later
                  try {
                     updateSessionNode(session);
                     session.didActivate();
                  } catch (Exception ex) {
                     logger.warn(ex, "Unable to update freshly loaded session - idInCluster[%s]", idInCluster);
                     return null;
                  }
               } else {
                  logger.debug("getSession(%s) - Session has expired", idInCluster);
                  session = null;
               }

            } else {
               logger.debug("getSession(%s) - Session not stale - session[%s]", idInCluster, session);
            }
         } else {
            logger.debug("getSession(%s) - No session in database matching id - idInCluster[%s]", idInCluster,
               idInCluster);
         }
         return session;
      }
   }

   @Override
   public int getSessions() {
      int size = 0;
      synchronized (this) {
         size = sessionMap.size();
      }
      return size;
   }

   @Override
   public void doStart() throws Exception {
      if (_sessionIdManager == null) {
         throw newJettyException("No session id manager defined");
      } else if (!(_sessionIdManager instanceof SessionIdManagerImpl)) {
         throw newJettyException("Session id manager must be of type SessionIdManagerImpl");
      }
      this.sessionIdManager = (SessionIdManagerImpl) _sessionIdManager;
      this.sessionMap = new ConcurrentHashMap<String, AbstractSession>();
      super.doStart();
   }

   @Override
   public void doStop() throws Exception {
      sessionMap.clear();
      sessionMap = null;
      super.doStop();
   }

   @Override
   protected void invalidateSessions() {
      //Do nothing - we don't want to remove and
      //invalidate all the sessions because this
      //method is called from doStop(), and just
      //because this context is stopping does not
      //mean that we should remove the session from
      //any other nodes
   }

   protected void invalidateSession(String idInCluster) {
      HttpSessionImpl session = null;
      synchronized (this) {
         session = (HttpSessionImpl) sessionMap.get(idInCluster);
      }
      if (session != null) {
         session.invalidate();
      }
   }

   /**
    * Delete an existing session, both from the in-memory map and the database.
    */
   @Override
   protected boolean removeSession(String idInCluster) {
      synchronized (this) {
         HttpSessionImpl session = (HttpSessionImpl) sessionMap.remove(idInCluster);
         try {
            if (session != null) {
               deleteSession(session);
            }
         } catch (Exception ex) {
            logger.warn(ex, "Problem deleting session id - [%s]", idInCluster);
         }
         return session != null;
      }
   }

   @Override
   protected void addSession(AbstractSession session) {
      if (session == null) {
         return;
      }
      synchronized (this) {
         sessionMap.put(session.getClusterId(), session);
      }
      //TODO or delay the store until exit out of session? If we crash before we store it
      //then session data will be lost.
      try {
         synchronized (session) {
            session.willPassivate();
            storeSession(((HttpSessionImpl) session));
            session.didActivate();
         }
      } catch (Exception ex) {
         logger.warn(ex, "Unable to store new session id [%s]", session.getId());
      }
   }

   @Override
   protected AbstractSession newSession(HttpServletRequest request) {
      HttpSessionImpl session = new HttpSessionImpl(logger, this, storage, request);

      int maxInactiveInterval = session.getMaxInactiveInterval();
      long expirationTime = maxInactiveInterval <= 0 ? 0 : (System.currentTimeMillis() + maxInactiveInterval * 1000L);

      session.setExpiryTime(expirationTime);
      session.setVirtualHost(getVirtualHost(_context));
      session.setCanonicalContext(canonicalize(_context.getContextPath()));

      return session;
   }

   @Override
   public void removeSession(AbstractSession session, boolean invalidate) {
      boolean removed = false;
      synchronized (this) {
         //take this session out of the map of sessions for this context
         if (getSession(session.getClusterId()) != null) {
            removed = true;
            removeSession(session.getClusterId());
         }
      }
      if (removed) {
         _sessionIdManager.removeSession(session);
         if (invalidate) {
            _sessionIdManager.invalidateAll(session.getClusterId());
         }

         if (invalidate && !_sessionListeners.isEmpty()) {
            HttpSessionEvent event = new HttpSessionEvent(session);
            for (HttpSessionListener l : _sessionListeners) {
               l.sessionDestroyed(event);
            }
         }
         if (!invalidate) {
            session.willPassivate();
         }
      }
   }

   /**
    * Expire any Sessions we have in memory matching the list of expired Session ids.
    */
   protected void expire(List<?> sessionIds) {
      //don't attempt to scavenge if we are shutting down
      if (isStopping() || isStopped()) {
         return;
      }

      //Remove any sessions we already have in memory that match the ids
      Thread thread = Thread.currentThread();
      ClassLoader old_loader = thread.getContextClassLoader();
      ListIterator<?> itor = sessionIds.listIterator();

      try {
         while (itor.hasNext()) {
            String sessionId = (String) itor.next();
            logger.debug("Expiring session id [%s]", sessionId);

            HttpSessionImpl session = (HttpSessionImpl) sessionMap.get(sessionId);
            if (session != null) {
               session.timeout();
               itor.remove();
            } else {
               logger.debug("Unrecognized session id - [%s]", sessionId);
            }
         }
      } catch (Throwable th) {
         logger.warn(th, "Problem expiring sessions");
      } finally {
         thread.setContextClassLoader(old_loader);
      }
   }

   private HttpSessionImpl loadSession(final String id, final String canonicalContextPath, final String vhost) throws Exception {
      final AtomicReference<HttpSessionImpl> reference = new AtomicReference<HttpSessionImpl>();
      final AtomicReference<Exception> exception = new AtomicReference<Exception>();
      Runnable load = new Runnable() {
         @Override
         public void run() {
            try {
               HttpSessionImpl session = storage.loadSession(SessionManagerImpl.this, id, canonicalContextPath, vhost);
               logger.debug("Loaded session [%s]", session);
               reference.set(session);
            } catch (Exception e) {
               exception.set(e);
            }
         }
      };

      if (_context == null) {
         load.run();
      } else {
         _context.getContextHandler().handle(load);
      }

      if (exception.get() != null) {
         //if the session could not be restored, take its id out of the pool of currently-in-use session ids
         sessionIdManager.removeSession(id);
         throw exception.get();
      }
      return reference.get();
   }

   private void storeSession(HttpSessionImpl session) throws Exception {
      if (session != null) {
         String nodeId = getSessionIdManager().getWorkerName();
         String sessionRowId = calculateRowId(session);
         storage.insertSession(nodeId, sessionRowId, session);
         logger.debug("Stored session [%s]", session);
      }
   }

   private void updateSessionNode(HttpSessionImpl session) throws Exception {
      if (session != null) {
         String nodeId = getSessionIdManager().getWorkerName();
         storage.updateSessionNode(nodeId, session);
         logger.debug("Updated last node for sessionId[%s] - lastNode[%s]", session.getId(), nodeId);
      }
   }

   private void deleteSession(HttpSessionImpl session) {
      if (session != null) {
         storage.deleteSession(session);
         logger.debug("Deleted Session [%s]", session);
      }
   }

   /**
    * Calculate a unique id for this session across the cluster. Unique id is composed of:
    * contextpath_virtualhost0_sessionid
    */
   private String calculateRowId(HttpSessionImpl data) {
      return String.format("%s_%s_%s", canonicalize(_context.getContextPath()), getVirtualHost(_context), data.getId());
   }

   /**
    * Get the first virtual host for the context. Used to help identify the exact session/contextPath.
    * 
    * @return 0.0.0.0 if no virtual host is defined
    */
   private String getVirtualHost(ContextHandler.Context context) {
      String vhost = "0.0.0.0";
      if (context != null) {
         String[] vhosts = context.getContextHandler().getVirtualHosts();
         if (vhosts != null && vhosts.length > 0) {
            if (vhosts[0] != null) {
               vhost = vhosts[0];
            }
         }
      }
      return vhost;
   }

   private String canonicalize(String path) {
      return path != null ? path.replace('/', '_').replace('.', '_').replace('\\', '_') : "";
   }

}
