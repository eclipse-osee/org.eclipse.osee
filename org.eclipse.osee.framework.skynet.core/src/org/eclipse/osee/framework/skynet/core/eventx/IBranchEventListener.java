/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.eventx;

import org.eclipse.osee.framework.skynet.core.artifact.BranchModType;
import org.eclipse.osee.framework.ui.plugin.event.Sender;

/**
 * @author Donald G. Dunne
 */
public interface IBranchEventListener extends IXEventListener {
   public void handleBranchEvent(Sender sender, BranchModType branchModType, int branchId);

   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender);

}
