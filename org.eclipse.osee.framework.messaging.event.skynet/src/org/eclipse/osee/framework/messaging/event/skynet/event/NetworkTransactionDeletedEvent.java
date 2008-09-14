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

   private static final long serialVersionUID = 1L;
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
   public NetworkTransactionDeletedEvent(int author, int[] transactionIds) {
      super(0, 0, author);
      this.transactionIds = transactionIds;
   }

}
