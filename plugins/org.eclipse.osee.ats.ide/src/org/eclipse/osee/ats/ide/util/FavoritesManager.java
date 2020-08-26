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
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class FavoritesManager {

   private final Collection<AbstractWorkflowArtifact> awas;

   public FavoritesManager(AbstractWorkflowArtifact sma) {
      this(Arrays.asList(sma));
   }

   public FavoritesManager(Collection<AbstractWorkflowArtifact> awas) {
      super();
      this.awas = awas;
   }

   public void toggleFavorite(boolean prompt) {
      try {
         if (amIFavorite(awas.iterator().next())) {
            boolean result = true;
            if (prompt) {
               result = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Remove Favorite", "Are You sure you wish to remove this as Favorite?");
            }
            if (result) {
               SkynetTransaction transaction =
                  TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), "Toggle Favorites");
               for (AbstractWorkflowArtifact awa : awas) {
                  removeFavorite(awa, AtsApiService.get().getUserService().getCurrentUser(), transaction);
               }
               transaction.execute();
            }
         } else {
            boolean result = true;
            if (prompt) {
               result = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Favorite", "Are you sure you wish add this as a Favorite?");
            }
            if (result) {
               SkynetTransaction transaction =
                  TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), "Toggle Favorites");
               for (AbstractWorkflowArtifact awa : awas) {
                  addFavorite(awa, AtsApiService.get().getUserService().getCurrentUser(), transaction);
               }
               transaction.execute();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static boolean amIFavorite(AbstractWorkflowArtifact workflow) {
      try {
         return isFavorite(workflow, AtsApiService.get().getUserService().getCurrentUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   public static void addFavorite(AbstractWorkflowArtifact workflow, AtsUser user, SkynetTransaction transaction) {
      if (!workflow.getRelatedArtifactsUnSorted(AtsRelationTypes.FavoriteUser_User).contains(user.getStoreObject())) {
         workflow.addRelation(AtsRelationTypes.FavoriteUser_User,
            (Artifact) AtsApiService.get().getQueryService().getArtifact(user.getArtifactId()));
         workflow.persist(transaction);
      }
   }

   public static void removeFavorite(AbstractWorkflowArtifact workflow, AtsUser user, SkynetTransaction transaction) {
      workflow.deleteRelation(AtsRelationTypes.FavoriteUser_User,
         (Artifact) AtsApiService.get().getQueryService().getArtifact(user.getArtifactId()));
      workflow.persist(transaction);
   }

   public static boolean isFavorite(AbstractWorkflowArtifact workflow, AtsUser user) {
      return workflow.getRelatedArtifactsUnSorted(AtsRelationTypes.FavoriteUser_User).contains(user.getStoreObject());
   }

}
