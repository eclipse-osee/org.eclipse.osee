/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;


/**
 * @author Donald G. Dunne
 */
public interface ITransactionsDeletedEventListener extends IEventListner {
   public void handleTransactionsDeletedEvent(Sender sender, int[] transactionIds);

}
