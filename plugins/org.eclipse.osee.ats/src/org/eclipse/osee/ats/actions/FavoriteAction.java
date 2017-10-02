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
package org.eclipse.osee.ats.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class FavoriteAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private boolean prompt;

   public FavoriteAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      this.prompt = true;
      updateEnablement();
   }

   public void updateEnablement() {
      String title = "Favorite";
      try {
         Collection<AbstractWorkflowArtifact> workflows = getSelectedFavoritableArts();
         setEnabled(!workflows.isEmpty());
         if (workflows.size() == 1) {
            title = FavoritesManager.amIFavorite(workflows.iterator().next()) ? "Remove Favorite" : "Add as Favorite";
         } else {
            title = "Toggle Favorites";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
      setText(title);
      setToolTipText(title);
   }

   public Collection<AbstractWorkflowArtifact> getSelectedFavoritableArts()  {
      List<AbstractWorkflowArtifact> favoritableArts = new ArrayList<>();
      for (Artifact art : selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            favoritableArts.add((AbstractWorkflowArtifact) art);
         }
      }
      return favoritableArts;
   }

   @Override
   public void runWithException()  {
      new FavoritesManager(getSelectedFavoritableArts()).toggleFavorite(prompt);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.FAVORITE);
   }

   public void setPrompt(boolean prompt) {
      this.prompt = prompt;
   }
}
