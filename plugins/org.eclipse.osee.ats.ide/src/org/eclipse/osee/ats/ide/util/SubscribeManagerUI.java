/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class SubscribeManagerUI {

   private final Collection<AbstractWorkflowArtifact> awas;

   public SubscribeManagerUI(AbstractWorkflowArtifact sma) {
      this(Arrays.asList(sma));
   }

   public SubscribeManagerUI(Collection<AbstractWorkflowArtifact> awas) {
      super();
      this.awas = awas;
   }

   public void toggleSubscribe(boolean prompt) {
      try {
         if (AtsApiService.get().getWorkItemService().getSubscribeService().amISubscribed(awas.iterator().next())) {
            boolean result = true;
            if (prompt) {
               result = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Un-Subscribe",
                  "You are currently subscribed to receive emails when this artifact transitions." + "\n\nAre You sure you wish to Un-Subscribe?");
            }
            if (result) {
               AtsApiService.get().getWorkItemService().getSubscribeService().toggleSubscribe(
                  Collections.castAll(awas));
            }
         } else {
            boolean result = true;
            if (prompt) {
               result = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Subscribe", "Are you sure you wish to subscribe to receive emails when this artifact transitions?");
            }
            if (result) {
               AtsApiService.get().getWorkItemService().getSubscribeService().toggleSubscribe(
                  Collections.castAll(awas));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
