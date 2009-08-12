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

package org.eclipse.osee.framework.database.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.IConnection;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbConnectionFactory implements IDbConnectionFactory {

   private final List<IConnection> connectionProviders;
   private final Object myWait;

   public DbConnectionFactory() {
      connectionProviders = new CopyOnWriteArrayList<IConnection>();
      myWait = new Object();
   }

   @Override
   public IConnection get(String driver) throws OseeCoreException {
      IConnection selectedDriver = getInternal(driver);
      if (selectedDriver == null) {
         long endTime = System.currentTimeMillis() + 1000 * 20;
         long timeLeft = 1000 * 20;
         while (timeLeft > 0 && selectedDriver == null) {
            synchronized (myWait) {
               try {
                  myWait.wait(timeLeft);
               } catch (InterruptedException ex) {
               }
               selectedDriver = getInternal(driver);
            }
            timeLeft = endTime - System.currentTimeMillis();
         }
      }
      if (selectedDriver == null) {
         throw new OseeStateException(String.format("Unable to find matching driver provider for [%s].", driver));
      }
      return selectedDriver;
   }

   private IConnection getInternal(String driver) {
      for (IConnection connection : connectionProviders) {
         if (connection.getDriver().equals(driver)) {
            return connection;
         }
      }
      return null;
   }

   @Override
   public void bind(IConnection connection) {
      connectionProviders.add(connection);
      synchronized (myWait) {
         myWait.notifyAll();
      }
   }

   @Override
   public void unbind(IConnection connection) {
      connectionProviders.remove(connection);
   }

}
