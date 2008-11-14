/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.db.connection.IApplicationDatabaseInfoProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

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

   public IApplicationDatabaseInfoProvider getProvider() throws OseeDataStoreException {
      IApplicationDatabaseInfoProvider provider = getInternal();
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
