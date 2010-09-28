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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class FavoritesManager {

   private final Collection<AbstractWorkflowArtifact> smas;

   public FavoritesManager(AbstractWorkflowArtifact sma) {
      this(Arrays.asList(sma));
   }

   public FavoritesManager(Collection<AbstractWorkflowArtifact> smas) {
      super();
      this.smas = smas;
   }

   public void toggleFavorite() {
      toggleFavorite(true);
   }

   public void toggleFavorite(boolean prompt) {
      try {
         if ((amIFavorite(smas.iterator().next()))) {
            boolean result = true;
            if (prompt) {
               result =
                  MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Remove Favorite", "Are You sure you wish to remove this as Favorite?");
            }
            if (result) {
               SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Toggle Favorites");
               for (AbstractWorkflowArtifact sma : smas) {
                  removeFavorite(sma, UserManager.getUser(), transaction);
               }
               transaction.execute();
            }
         } else {
            boolean result = true;
            if (prompt) {
               result =
                  MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Favorite", "Are you sure you wish add this as a Favorite?");
            }
            if (result) {
               SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Toggle Favorites");
               for (AbstractWorkflowArtifact sma : smas) {
                  addFavorite(sma, UserManager.getUser(), transaction);
               }
               transaction.execute();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static boolean amIFavorite(AbstractWorkflowArtifact workflow) {
      try {
         return isFavorite(workflow, UserManager.getUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   public static void addFavorite(AbstractWorkflowArtifact workflow, User user, SkynetTransaction transaction) throws OseeCoreException {
      if (!workflow.getRelatedArtifacts(AtsRelationTypes.FavoriteUser_User).contains(user)) {
         workflow.addRelation(AtsRelationTypes.FavoriteUser_User, user);
         workflow.persist(transaction);
      }
   }

   public static void removeFavorite(AbstractWorkflowArtifact workflow, User user, SkynetTransaction transaction) throws OseeCoreException {
      workflow.deleteRelation(AtsRelationTypes.FavoriteUser_User, user);
      workflow.persist(transaction);
   }

   public static boolean isFavorite(AbstractWorkflowArtifact workflow, User user) throws OseeCoreException {
      return workflow.getRelatedArtifacts(AtsRelationTypes.FavoriteUser_User).contains(user);
   }

   public static List<User> getFavorites(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : workflow.getRelatedArtifacts(AtsRelationTypes.FavoriteUser_User)) {
         arts.add((User) art);
      }
      return arts;
   }
}
