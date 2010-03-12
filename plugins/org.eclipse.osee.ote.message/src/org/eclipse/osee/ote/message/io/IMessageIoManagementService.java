/*
 * Created on Jun 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.io;

/**
 * @author b1529404
 *
 */
public interface IMessageIoManagementService {

   void install(IMessageIoDriver ioDriver);
   void uninstall(IMessageIoDriver ioDriver);
   void startIO();
   void stopIO();
}
