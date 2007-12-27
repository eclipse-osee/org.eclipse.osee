/*
 * Created on Dec 26, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.LocalNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * This class handles new branches and setting access control based on ATS workflows and their assignees
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchAccessHandler implements IEventReceiver {

   private static AtsBranchAccessHandler atsBranchAccessHandler = new AtsBranchAccessHandler();

   public static AtsBranchAccessHandler getInstance() {
      return atsBranchAccessHandler;
   }

   private AtsBranchAccessHandler() {
      OSEELog.logInfo(AtsPlugin.class, "Starting ATS Branch Access Handler", false);
      SkynetEventManager.getInstance().register(LocalNewBranchEvent.class, this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(Event event) {
      try {
         if (event instanceof LocalNewBranchEvent) {
            Branch branch =
                  BranchPersistenceManager.getInstance().getBranch(((LocalNewBranchEvent) event).getBranchId());
            Artifact artifact = branch.getAssociatedArtifact();
            if (artifact instanceof StateMachineArtifact) {
               ((StateMachineArtifact) artifact).getSmaMgr().getBranchMgr().updateBranchAccessControl();
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   public void dispose() {
      SkynetEventManager.getInstance().unRegisterAll(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

}
