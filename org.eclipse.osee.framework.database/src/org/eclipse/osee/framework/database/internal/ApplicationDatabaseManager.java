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
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IApplicationDatabaseManager;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationDatabaseManager implements IApplicationDatabaseManager {

   private List<IDatabaseInfoProvider> applicationDatabaseInfoProvider;
   private Object myWait;

   public ApplicationDatabaseManager() {
      this.applicationDatabaseInfoProvider = new CopyOnWriteArrayList<IDatabaseInfoProvider>();
      myWait = new Object();
   }

   public IDatabaseInfoProvider getInternal() throws OseeDataStoreException {
      IDatabaseInfoProvider toReturn = null;
      for (IDatabaseInfoProvider provider : applicationDatabaseInfoProvider) {
         if (toReturn == null || (toReturn.getPriority() < provider.getPriority())) {
            toReturn = provider;
         }
      }
      return toReturn;
   }

   public IDatabaseInfoProvider getProvider() throws OseeDataStoreException {
      IDatabaseInfoProvider provider = getInternal();
      if (provider == null) {
         long endTime = System.currentTimeMillis() + (1000 * 20);
         long timeLeft = 1000 * 20;
         while (timeLeft > 0 && provider == null) {
            synchronized (myWait) {
               try {
                  myWait.wait(timeLeft);
               } catch (InterruptedException ex) {
               }
               provider = getInternal();
            }
            timeLeft = endTime - System.currentTimeMillis();
         }
      }
      if (provider == null) {
         throw new OseeDataStoreException("Unable to find an application database provider");
      }
      return provider;
   }

   public void removeDatabaseProvider(IDatabaseInfoProvider provider) {
      System.out.println("Removing: " + provider);
      applicationDatabaseInfoProvider.remove(provider);
   }

   public void addDatabaseProvider(IDatabaseInfoProvider provider) {
      System.out.println("Adding: " + provider);
      applicationDatabaseInfoProvider.add(provider);
      synchronized (myWait) {
         myWait.notifyAll();
      }
   }
}
