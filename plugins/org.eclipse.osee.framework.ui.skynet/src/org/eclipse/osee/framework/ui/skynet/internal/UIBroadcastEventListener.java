/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.internal;

import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IBroadcastEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

public class UIBroadcastEventListener implements IBroadcastEventListener {

   @Override
   public void handleBroadcastEvent(Sender sender, final BroadcastEvent broadcastEvent) {
      // Determine whether this is a shutdown event
      // Prevent shutting down users without a valid message
      if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Force_Shutdown) {
         if (broadcastEvent.getMessage() == null || broadcastEvent.getMessage().length() == 0) {
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               boolean isShutdownRequest = false;
               try {
                  isShutdownRequest = broadcastEvent.getUsers().contains(UserManager.getUser());
               } catch (OseeCoreException ex) {
                  // do nothing
               }
               if (isShutdownRequest) {
                  MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Shutdown Requested", broadcastEvent.getMessage());
                  // Shutdown the bench when this event is received
                  PlatformUI.getWorkbench().close();
               }
            }
         });
      } else if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Message) {
         if (broadcastEvent.getMessage() == null || broadcastEvent.getMessage().length() == 0) {
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Remote Message", broadcastEvent.getMessage());
            }
         });
      } else if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Ping) {
         // Another client ping'd this client for session information; Pong back with
         // original client's session id so it can be identified as the correct pong
         try {
            OseeEventManager.kickBroadcastEvent(this, new BroadcastEvent(BroadcastEventType.Pong, null,
               sender.getOseeSession().toString()));
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Pong) {
         // Got pong from another client; If message == this client's sessionId, then it's
         // the response from this client's ping
         try {
            if (broadcastEvent.getMessage() != null && broadcastEvent.getMessage().equals(
               ClientSessionManager.getSession().toString())) {
               OseeLog.log(Activator.class, Level.INFO, "Pong: " + sender.toString());
            }
         } catch (OseeAuthenticationRequiredException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Pong: " + sender.toString(), ex);
         }
      }
   }

}
