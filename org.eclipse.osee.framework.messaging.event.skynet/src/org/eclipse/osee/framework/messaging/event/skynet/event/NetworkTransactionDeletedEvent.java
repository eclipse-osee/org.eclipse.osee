/*
 * Created on Sep 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.skynet.event;

/**
 * @author Donald G. Dunne
 */
public class NetworkTransactionDeletedEvent extends SkynetEventBase {

   private static final long serialVersionUID = -2467438797592036593L;
   private final int[] transactionIds;

   /**
    * @return the transactionIds
    */
   public int[] getTransactionIds() {
      return transactionIds;
   }

   /**
    * @param branchId
    * @param transactionId
    * @param author
    */
   public NetworkTransactionDeletedEvent(NetworkSender networkSender, int[] transactionIds) {
      super(networkSender);
      this.transactionIds = transactionIds;
   }

}
