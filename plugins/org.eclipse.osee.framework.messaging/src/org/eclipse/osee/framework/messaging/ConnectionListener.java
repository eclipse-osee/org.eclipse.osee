/*
 * Created on Feb 18, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;


/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface ConnectionListener {
   void connected(ConnectionNode node);
   void notConnected(ConnectionNode node);
}
