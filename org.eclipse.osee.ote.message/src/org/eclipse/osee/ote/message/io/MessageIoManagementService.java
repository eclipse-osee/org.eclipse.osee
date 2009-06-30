/*
 * Created on Jun 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.io;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author b1529404
 *
 */
public class MessageIoManagementService implements IMessageIoManagementService{

   private final HashSet<IMessageIoDriver> drivers = new HashSet<IMessageIoDriver>();

   private final Lock lock = new ReentrantLock();
   private boolean ioStarted = false;
   
   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.io.IMessageIoManagementService#install(org.eclipse.osee.ote.message.io.IMessageIoDriver)
    */
   @Override
   public void install(IMessageIoDriver ioDriver) {
      lock.lock();
      if (!drivers.add(ioDriver)) {
         // driver was already installed
         lock.unlock();
         return;
      }
      if (ioStarted) {
         // make sure we release the lock before entering unknown code
         lock.unlock();
         ioDriver.start();
      } else {
         lock.unlock();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.io.IMessageIoManagementService#startIO()
    */
   @Override
   public void startIO() {
      lock.lock();
      ioStarted = true;

      Set<IMessageIoDriver> copiedDrivers = new HashSet<IMessageIoDriver>(drivers);
      lock.unlock();
      for (IMessageIoDriver driver : copiedDrivers) {
         driver.start();
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.io.IMessageIoManagementService#stopIO()
    */
   @Override
   public void stopIO() {
      lock.lock();
      ioStarted = false;
      Set<IMessageIoDriver> copiedDrivers = new HashSet<IMessageIoDriver>(drivers);
      lock.unlock();
      for (IMessageIoDriver driver : copiedDrivers) {
         driver.stop();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.io.IMessageIoManagementService#uninstall(org.eclipse.osee.ote.message.io.IMessageIoDriver)
    */
   @Override
   public void uninstall(IMessageIoDriver ioDriver) {
      lock.lock();
      boolean changed = drivers.remove(ioDriver);
      lock.unlock();
      if (changed && ioDriver.isStarted()) {
         ioDriver.stop();
      }
   }

}
