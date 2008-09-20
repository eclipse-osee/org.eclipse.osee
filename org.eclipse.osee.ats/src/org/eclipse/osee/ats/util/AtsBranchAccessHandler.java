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
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * This class handles new branches and setting access control based on ATS workflows and their assignees
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchAccessHandler implements IBranchEventListener {

   private static AtsBranchAccessHandler atsBranchAccessHandler = new AtsBranchAccessHandler();

   public static AtsBranchAccessHandler getInstance() {
      return atsBranchAccessHandler;
   }

   private AtsBranchAccessHandler() {
      OSEELog.logInfo(AtsPlugin.class, "Starting ATS Branch Access Handler", false);
      OseeEventManager.addListener(this);
   }

   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      try {
         if (branchModType == BranchEventType.Added) {
            Branch branch = BranchPersistenceManager.getBranch(branchId);
            Artifact artifact = branch.getAssociatedArtifact();
            if (artifact instanceof StateMachineArtifact) {
               ((StateMachineArtifact) artifact).getSmaMgr().getBranchMgr().updateBranchAccessControl();
            }
         }
         // TODO Need to remove branch access control if branch deleted, archived, etc
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(AtsPlugin.class, Level.INFO, ex);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }
}
