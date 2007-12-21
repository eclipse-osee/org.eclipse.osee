/*
 * Created on Dec 19, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import org.eclipse.osee.framework.skynet.core.event.LocalBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;

/**
 * @author Donald G. Dunne
 */
public class AtsEventHandler implements IEventReceiver {

   public static AtsEventHandler atsEventHandler = new AtsEventHandler();

   public static AtsEventHandler getInstance() {
      return atsEventHandler;
   }

   private AtsEventHandler() {
      SkynetEventManager.getInstance().register(LocalBranchEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteBranchEvent.class, this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(Event event) {
      if (event instanceof LocalBranchEvent) {
         BranchManager.clearCachedWorkingBranch(((LocalBranchEvent) event).getBranchId());
      } else if (event instanceof RemoteBranchEvent) {
         BranchManager.clearCachedWorkingBranch(((RemoteBranchEvent) event).getBranchId());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return false;
   }
}
