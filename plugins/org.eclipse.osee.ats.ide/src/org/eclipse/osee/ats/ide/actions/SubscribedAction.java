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

package org.eclipse.osee.ats.ide.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.SubscribeManagerUI;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class SubscribedAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private boolean prompt = true;

   public SubscribedAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super();
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      updateEnablement();
   }

   public void updateEnablement() {
      String title = "Subscribed";
      try {
         setEnabled(getSelectedSubscribableArts().size() > 0);
         if (getSelectedSubscribableArts().size() == 1) {
            title = AtsApiService.get().getWorkItemService().getSubscribeService().amISubscribed(
               getSelectedSubscribableArts().iterator().next()) ? "Remove Subscribed" : "Add as Subscribed";
         } else {
            title = "Toggle Subscribed";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
      setText(title);
      setToolTipText(title);
   }

   public Collection<AbstractWorkflowArtifact> getSelectedSubscribableArts() {
      List<AbstractWorkflowArtifact> favoritableArts = new ArrayList<>();
      for (Artifact art : selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            favoritableArts.add((AbstractWorkflowArtifact) art);
         }
      }
      return favoritableArts;
   }

   @Override
   public void runWithException() {
      new SubscribeManagerUI(getSelectedSubscribableArts()).toggleSubscribe(prompt);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.SUBSCRIBED);
   }

   public void setPrompt(boolean prompt) {
      this.prompt = prompt;
   }

}
