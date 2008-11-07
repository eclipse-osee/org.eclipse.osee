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

package org.eclipse.osee.framework.db.connection.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbConnectionFactory implements IDbConnectionFactory {

   private List<IConnection> connectionProviders;
   private Object myWait;

   public DbConnectionFactory() {
      connectionProviders = new CopyOnWriteArrayList<IConnection>();
      myWait = new Object();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionFactory#get(java.lang.String)
    */
   @Override
   public IConnection get(String driver) throws OseeCoreException {
      IConnection selectedDriver = getInternal(driver);
      if (selectedDriver == null) {
         long endTime = System.currentTimeMillis() + (1000 * 20);
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
         throw new OseeStateException(
               String.format("Unable to find matching driver provider for [%s].", driver, driver));
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#bind(java.lang.Object)
    */
   @Override
   public void bind(IConnection obj) {
      connectionProviders.add((IConnection) obj);
      synchronized (myWait) {
         myWait.notifyAll();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#unbind(java.lang.Object)
    */
   @Override
   public void unbind(IConnection obj) {
      connectionProviders.remove((IConnection) obj);
   }

}
