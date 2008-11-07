/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.db.connection.IApplicationDatabaseInfoProvider;
import org.eclipse.osee.framework.db.connection.IApplicationDatabaseManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationDatabaseManager implements IApplicationDatabaseManager {

   private List<IApplicationDatabaseInfoProvider> applicationDatabaseInfoProvider;
   private Object myWait;

   public ApplicationDatabaseManager() {
      this.applicationDatabaseInfoProvider = new CopyOnWriteArrayList<IApplicationDatabaseInfoProvider>();
      myWait = new Object();
   }

   public IApplicationDatabaseInfoProvider getInternal() throws OseeDataStoreException {
      IApplicationDatabaseInfoProvider toReturn = null;
      for (IApplicationDatabaseInfoProvider provider : applicationDatabaseInfoProvider) {
         if (toReturn == null || (toReturn.getPriority() < provider.getPriority())) {
            toReturn = provider;
         }
      }
      return toReturn;
   }

   public IApplicationDatabaseInfoProvider getProvider() throws OseeCoreException {
      IApplicationDatabaseInfoProvider selectedDriver = getInternal();
      if (selectedDriver == null) {
         long endTime = System.currentTimeMillis() + (1000 * 20);
         long timeLeft = 1000 * 20;
         while (timeLeft > 0 && selectedDriver == null) {
            synchronized (myWait) {
               try {
                  myWait.wait(timeLeft);
               } catch (InterruptedException ex) {
               }
               selectedDriver = getInternal();
            }
            timeLeft = endTime - System.currentTimeMillis();
         }
      }
      if (selectedDriver == null) {
         throw new OseeStateException("Unable to find an application database provider");
      }
      return selectedDriver;
   }

   public void removeDatabaseProvider(IApplicationDatabaseInfoProvider provider) {
      System.out.println("Removing: " + provider);
      applicationDatabaseInfoProvider.remove(provider);
   }

   public void addDatabaseProvider(IApplicationDatabaseInfoProvider provider) {
      System.out.println("Adding: " + provider);
      applicationDatabaseInfoProvider.add(provider);
      synchronized (myWait) {
         myWait.notifyAll();
      }
   }
}
