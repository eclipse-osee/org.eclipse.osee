/*
 * Created on Sep 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.eventx;

import org.eclipse.osee.framework.ui.plugin.event.Sender.Source;

/**
 * Event that represents a collection of artifact and relation change events that are collected and persisted within a
 * single SkynetTransaction.
 * 
 * @author Donald G. Dunne
 */
public interface IFrameworkTransactionEventListener extends IXEventListener {

   /**
    * Notification of all artifact and relation modifications collected as part of a single skynet transaction.
    * 
    * @param source
    * @param transData collection of all changes within transaction
    */
   public void handleFrameworkTransactionEvent(Source source, FrameworkTransactionData transData);

}
