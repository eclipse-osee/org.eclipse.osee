/*
 * Created on Jun 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.io;

/**
 * @author Ken J. Aguilar
 *
 */
public interface IMessageIoDriver {
   void start();
   void stop();
   boolean isStarted();
}
