/*
 * Created on Feb 18, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;


/**
 * @author b1528444
 *
 */
public interface ConnectionListener {
   void connected(ConnectionNode node);
   void notConnected(ConnectionNode node);
}
