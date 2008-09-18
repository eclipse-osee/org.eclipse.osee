/*
 * Created on Sep 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

/**
 * Event that represents a collection of ArtifactModifiedEvent and RelationModifiedEvent events that are collected and
 * persisted within a single SkynetTransaction.
 * 
 * @author Donald G. Dunne
 */
public interface IFrameworkTransactionEventListener extends IEventListner {

   /**
    * Notification of all artifact and relation modifications collected as part of a single skynet transaction.
    * 
    * @param source
    * @param transData collection of all changes within transaction
    */
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData);

}
