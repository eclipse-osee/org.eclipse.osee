/*
 * Created on Dec 19, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Donald G. Dunne
 */
public class LocalBranchToArtifactCacheUpdateEvent extends Event {

   /**
    * @param sender
    * @param branchId
    */
   public LocalBranchToArtifactCacheUpdateEvent(Object sender) {
      super(sender);
   }

}
