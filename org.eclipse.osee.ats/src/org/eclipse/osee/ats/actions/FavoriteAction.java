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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.IFavoriteableArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.FavoritesManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class FavoriteAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public FavoriteAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      updateName();
   }

   private void updateName() {
      String title = "Favorite";
      try {
         if (getSelectedFavoritableArts().size() == 1) {
            title =
                  getSelectedFavoritableArts().iterator().next().amIFavorite() ? "Remove Favorite" : "Add as Favorite";
         } else {
            title = "Toggle Favorites";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      setText(title);
      setToolTipText(title);
   }

   public Collection<IFavoriteableArtifact> getSelectedFavoritableArts() throws OseeCoreException {
      List<IFavoriteableArtifact> favoritableArts = new ArrayList<IFavoriteableArtifact>();
      for (Artifact art : selectedAtsArtifacts.getSelectedSMAArtifacts()) {
         if (art instanceof IFavoriteableArtifact) {
            favoritableArts.add((IFavoriteableArtifact) art);
         }
      }
      return favoritableArts;
   }

   @Override
   public void run() {
      try {
         for (IFavoriteableArtifact sma : getSelectedFavoritableArts()) {
            (new FavoritesManager((StateMachineArtifact) sma)).toggleFavorite();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.FAVORITE);
   }

   public void updateEnablement() {
      try {
         setEnabled(getSelectedFavoritableArts().size() > 0);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
      updateName();
   }
}
