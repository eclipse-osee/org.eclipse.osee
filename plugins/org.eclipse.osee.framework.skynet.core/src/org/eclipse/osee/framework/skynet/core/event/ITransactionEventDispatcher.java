/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;

/**
 * @author Donald G. Dunne
 */
public interface ITransactionEventDispatcher extends IEventDispatcher {

   public void kickLocalEvents(final Sender sender, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents, FrameworkTransactionData transData);

   public void generateNetworkSkynetEvents(Sender sender, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents, List<ISkynetEvent> resultEvents) throws OseeCoreException;

}
