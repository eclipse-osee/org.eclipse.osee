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

import java.sql.SQLException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ISubscribableArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class Subscribe {

   private final StateMachineArtifact sma;

   public Subscribe(StateMachineArtifact sma) {
      super();
      this.sma = sma;
   }

   public void toggleSubscribe() {
      try {
         if (((ISubscribableArtifact) sma).amISubscribed()) {
            boolean result =
                  MessageDialog.openQuestion(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Un-Subscribe",
                        "You are currently subscribed to receive emails when this artifact transitions." + "\n\nAre You sure you wish to Un-Subscribe?");
            if (result) {
               ((ISubscribableArtifact) sma).removeSubscribed(SkynetAuthentication.getInstance().getAuthenticatedUser());
            }
         } else {
            boolean result =
                  MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Subscribe",
                        "Are you sure you wish to subscribe to receive emails when this artifact transitions?");
            if (result) {
               ((ISubscribableArtifact) sma).addSubscribed(SkynetAuthentication.getInstance().getAuthenticatedUser());
            }

         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

}
