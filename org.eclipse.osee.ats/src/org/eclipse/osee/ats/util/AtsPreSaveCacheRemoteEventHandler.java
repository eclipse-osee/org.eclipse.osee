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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * This class handles updating ATS state machine artifacts based on remote events that change the assignees. Without
 * this, the client will think it changed the assignees if the artifact is saved after the remote modified event.
 * 
 * @author Donald G. Dunne
 */
public class AtsPreSaveCacheRemoteEventHandler implements IEventReceiver {

   private static AtsPreSaveCacheRemoteEventHandler instance = new AtsPreSaveCacheRemoteEventHandler();

   public static AtsPreSaveCacheRemoteEventHandler getInstance() {
      return instance;
   }

   private AtsPreSaveCacheRemoteEventHandler() {
      OSEELog.logInfo(AtsPlugin.class, "Starting ATS Pre-Save Remote Event Handler", false);
      SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(Event event) {
      try {
         if (event instanceof RemoteTransactionEvent) {
            for (Event localEvent : ((RemoteTransactionEvent) event).getLocalEvents()) {
               if (localEvent instanceof ArtifactModifiedEvent) {
                  Artifact artifact = ((ArtifactModifiedEvent) localEvent).getArtifact();
                  if (artifact instanceof StateMachineArtifact) {
                     ((StateMachineArtifact) artifact).initalizePreSaveCache();
                  }
               }
            }
         }
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
