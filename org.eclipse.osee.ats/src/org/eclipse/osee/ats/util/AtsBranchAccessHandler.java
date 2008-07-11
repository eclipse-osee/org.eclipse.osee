/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.LocalNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
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
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(AtsPlugin.class, Level.INFO, ex);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
