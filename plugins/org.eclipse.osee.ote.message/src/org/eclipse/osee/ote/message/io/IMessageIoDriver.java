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
public interface IMessageIoDriver {
   void start();
   void stop();
   boolean isStarted();
}
