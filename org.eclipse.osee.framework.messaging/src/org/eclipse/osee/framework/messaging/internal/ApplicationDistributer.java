/*
 * Created on May 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.Message;

/**
 * @author b1528444
 *
 */
public interface ApplicationDistributer {

   /**
    * @param message
    */
   void distribute(Message message);

}
