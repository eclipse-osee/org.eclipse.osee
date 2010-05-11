/*
 * Created on Feb 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface ConnectionNodeFailoverSupport extends ConnectionNode {
   boolean isConnected();
   public void start() throws OseeCoreException;
}
