/*
 * Created on Feb 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import org.eclipse.osee.framework.messaging.services.RegisteredServiceReference;

/**
 * @author Andrew M. Finkbeiner
 *
 */
class ServiceReferenceImp implements RegisteredServiceReference {

   UpdateStatus updateStatus;

   ServiceReferenceImp(UpdateStatus updateStatus){
      this.updateStatus = updateStatus;
   }
   
   @Override
   public void update() {
      updateStatus.run();
   }

}
