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

import java.io.Serializable;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.osee.http.jetty.JettyLogger;
import org.eclipse.osee.http.jetty.internal.session.SessionManagerImpl.SessionStorage;

/**
 * @author Roberto E. Escobar
 */
public class HttpSessionImpl extends AbstractSession implements Serializable {

   private static final long serialVersionUID = -7879368993881029829L;

   private final JettyLogger logger;
   private final SessionManagerImpl manager;
   private boolean dirty = false;
   private long cookieSet;
   private long expirationTime;
   private long lastSaved;
   private String lastNode;
   private String virtualHost;
   private String rowId;
   private String canonicalContext;

   private final SessionStorage sessionStorage;

   public HttpSessionImpl(JettyLogger logger, SessionManagerImpl manager, SessionStorage sessionStorage, HttpServletRequest request) {
      super(manager, request);
      this.lastNode = manager.getSessionIdManager().getWorkerName();

      this.logger = logger;
      this.manager = manager;
      this.sessionStorage = sessionStorage;
   }

   public HttpSessionImpl(JettyLogger logger, SessionManagerImpl manager, SessionStorage sessionStorage, String sessionId, String rowId, long created, long accessed) {
      super(manager, created, accessed, sessionId);
      this.rowId = rowId;

      this.logger = logger;
      this.manager = manager;
      this.sessionStorage = sessionStorage;
   }

   public synchronized String getRowId() {
      return rowId;
   }

   public synchronized void setRowId(String rowId) {
      this.rowId = rowId;
   }

   public synchronized void setVirtualHost(String vhost) {
      this.virtualHost = vhost;
   }

   public synchronized String getVirtualHost() {
      return virtualHost;
   }

   public synchronized long getLastSaved() {
      return lastSaved;
   }

   public synchronized void setLastSaved(long time) {
      this.lastSaved = time;
   }

   public synchronized void setExpiryTime(long time) {
      this.expirationTime = time;
   }

   public synchronized long getExpiryTime() {
      return expirationTime;
   }

   public synchronized void setCanonicalContext(String str) {
      this.canonicalContext = str;
   }

   public synchronized String getCanonicalContext() {
      return canonicalContext;
   }

   public void setCookieSet(long ms) {
      this.cookieSet = ms;
   }

   public synchronized long getCookieSet() {
      return cookieSet;
   }

   public synchronized void setLastNode(String node) {
      this.lastNode = node;
   }

   public synchronized String getLastNode() {
      return lastNode;
   }

   @Override
   public void setAttribute(String name, Object value) {
      super.setAttribute(name, value);
      dirty = true;
   }

   @Override
   public void removeAttribute(String name) {
      super.removeAttribute(name);
      dirty = true;
   }

   @Override
   public Map<String, Object> getAttributeMap() {
      return super.getAttributeMap();
   }

   public void setAttributeMap(Map<String, Object> map) {
      super.addAttributes(map);
   }

   @Override
   protected void cookieSet() {
      cookieSet = getAccessed();
   }

   @Override
   protected boolean access(long time) {
      synchronized (this) {
         if (super.access(time)) {
            int maxInterval = getMaxInactiveInterval();
            expirationTime = (maxInterval <= 0 ? 0 : (time + maxInterval * 1000L));
            return true;
         }
         return false;
      }
   }

   @Override
   protected void complete() {
      synchronized (this) {
         super.complete();
         try {
            if (isValid()) {
               String nodeId = manager.getSessionIdManager().getWorkerName();
               if (dirty) {
                  //The session attributes have changed, write to the db, ensuring
                  //http passive/active listeners called
                  willPassivate();
                  sessionStorage.updateSession(nodeId, this);
                  logger.debug("Updated session - [%s]", this);
                  didActivate();
               } else if ((getAccessed() - lastSaved) >= (manager.getSaveInterval() * 1000L)) {
                  sessionStorage.updateSessionAccessTime(nodeId, this);
                  logger.debug("Updated access time session - [%s]", this);
               }
            }
         } catch (Exception ex) {
            logger.warn(ex, "Problem persisting changed session data id - [%s]", getId());
         } finally {
            dirty = false;
         }
      }
   }

   @Override
   protected void timeout() throws IllegalStateException {
      logger.debug("Timing out session id - [%s]" + getClusterId());
      super.timeout();
   }

   @Override
   public String toString() {
      return "Session rowId=" + rowId + ", id=" + getId() + ", lastNode=" + lastNode + ", created=" + getCreationTime() + ", accessed=" + getAccessed() + ", lastAccessed=" + getLastAccessedTime() + ", cookieSet=" + cookieSet + ", lastSaved=" + lastSaved + ", expiry=" + expirationTime;
   }

}