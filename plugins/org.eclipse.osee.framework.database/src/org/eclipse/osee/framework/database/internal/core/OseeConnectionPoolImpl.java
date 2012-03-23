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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;

public class OseeConnectionPoolImpl {
   private static final int MAX_CONNECTIONS_PER_CLIENT = OseeProperties.getOseeDbConnectionCount();
   private final List<OseeConnectionImpl> connections = new LinkedList<OseeConnectionImpl>();
   private final Semaphore connectionsSemaphore = new Semaphore(MAX_CONNECTIONS_PER_CLIENT, true);
   private final String dbUrl;
   private final Properties properties;
   private final IConnectionFactory connectionFactory;

   public OseeConnectionPoolImpl(IConnectionFactory connectionFactory, String dbUrl, Properties properties) {
      this.connectionFactory = connectionFactory;
      this.dbUrl = dbUrl;
      this.properties = properties;
   }

   void removeConnection(OseeConnection conn) {
      synchronized (connections) {
         connections.remove(conn);
      }
   }

   public OseeConnectionImpl getConnection() throws OseeCoreException {
      //System.out.println("acquiring.  num available: " + connectionsSemaphore.availablePermits());
      connectionsSemaphore.acquireUninterruptibly();

      OseeConnectionImpl toReturn = null;

      synchronized (connections) {
         for (OseeConnectionImpl connection : connections) {
            if (connection.lease()) {
               toReturn = connection;
               break;
            }
         }

         if (toReturn == null) {
            OseeConnectionImpl connection;
            try {
               connection = getOseeConnection();
            } catch (OseeCoreException ex) {
               connectionsSemaphore.release();
               throw ex;
            }
            connections.add(connection);
            OseeLog.logf(Activator.class, Level.INFO, "DbConnection: [%s] - [%d]", dbUrl, connections.size());
            toReturn = connection;
         }
      }

      return toReturn;
   }

   private OseeConnectionImpl getOseeConnection() throws OseeCoreException {
      try {
         Connection connection = connectionFactory.getConnection(properties, dbUrl);
         connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
         return new OseeConnectionImpl(connection, this);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   void returnConnection(OseeConnectionImpl connection) {
      try {
         if (connection.isClosed()) {
            removeConnection(connection);
         } else {
            connection.expireLease();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         removeConnection(connection);
      }
      //System.out.println("releasing.  num available: " + connectionsSemaphore.availablePermits());
      connectionsSemaphore.release();
   }

   void releaseUneededConnections() throws OseeCoreException {
      List<OseeConnectionImpl> copy;
      synchronized (connections) {
         copy = new LinkedList<OseeConnectionImpl>(connections);
      }
      for (OseeConnectionImpl connection : copy) {
         if (connection.isStale()) {
            connection.destroy();
         }
      }
   }
}