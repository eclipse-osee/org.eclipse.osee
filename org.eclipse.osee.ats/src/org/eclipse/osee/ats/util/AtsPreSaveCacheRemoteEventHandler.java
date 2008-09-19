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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * This class handles updating ATS state machine artifacts based on remote events that change the assignees. Without
 * this, the client will think it changed the assignees if the artifact is saved after the remote modified event.
 * 
 * @author Donald G. Dunne
 */
public class AtsPreSaveCacheRemoteEventHandler implements IFrameworkTransactionEventListener {

   private static AtsPreSaveCacheRemoteEventHandler instance = new AtsPreSaveCacheRemoteEventHandler();

   public static AtsPreSaveCacheRemoteEventHandler getInstance() {
      return instance;
   }

   private AtsPreSaveCacheRemoteEventHandler() {
      OSEELog.logInfo(AtsPlugin.class, "Starting ATS Pre-Save Remote Event Handler", false);
      OseeEventManager.addListener(this);
   }

   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) {
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      for (Artifact artifact : transData.cacheChangedArtifacts) {
         if (artifact instanceof StateMachineArtifact) {
            ((StateMachineArtifact) artifact).initalizePreSaveCache();
         }
      }
   }

}
