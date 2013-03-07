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
package org.eclipse.osee.framework.database.internal.core;

import java.sql.Connection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class OseeConnectionImpl extends BaseOseeConnection {
   final static private long timeout = 60000;
   private final OseeConnectionPoolImpl pool;
   private volatile boolean inuse;
   private volatile long lastUsedTime;

   public OseeConnectionImpl(Connection conn, OseeConnectionPoolImpl pool) {
      super(conn);
      this.pool = pool;
      this.inuse = true;
      this.lastUsedTime = 0;
   }

   @Override
   public void close() throws OseeCoreException {
      if (isClosed()) {
         destroy();
      } else {
         pool.returnConnection(this);
      }
   }

   @Override
   public boolean isStale() {
      return !inUse() && getLastUse() + timeout < System.currentTimeMillis();
   }

   synchronized boolean lease() {
      if (inuse) {
         return false;
      } else {
         inuse = true;
         return true;
      }
   }

   @Override
   protected void destroy() throws OseeCoreException {
      pool.removeConnection(this);
      super.destroy();
   }

   boolean inUse() {
      return inuse;
   }

   long getLastUse() {
      return lastUsedTime;
   }

   synchronized void expireLease() {
      inuse = false;
      lastUsedTime = System.currentTimeMillis();
   }
}