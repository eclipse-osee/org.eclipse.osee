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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class SubscribeByActionableItem extends XNavigateItemAction {

   public SubscribeByActionableItem() {
      super("Subscribe by Actionable Item", FrameworkImage.EMAIL, XNavigateItem.EMAIL_NOTIFICATIONS);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      final AICheckTreeDialog diag = new AICheckTreeDialog(getName(),
         "Select Actionable Items\n\nEmail will be sent for every Action created against these AIs.", Active.Active);
      try {
         List<IAtsActionableItem> objs = new ArrayList<>();
         for (ArtifactToken artifact : AtsApiService.get().getRelationResolver().getRelated(
            (IAtsObject) AtsApiService.get().getUserService().getCurrentUser(),
            AtsRelationTypes.SubscribedUser_Artifact)) {
            if (artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
               objs.add(AtsApiService.get().getActionableItemService().getActionableItemById(artifact));
            }
         }
         diag.setInitialAias(objs);
         if (diag.open() != 0) {
            return;
         }
         Collection<IAtsActionableItem> selected = diag.getChecked();
         Collection<Artifact> arts =
            Collections.castAll(AtsApiService.get().getQueryService().getArtifactsFromObjects(selected));

         User user = UserManager.getUserByArtId(AtsApiService.get().getUserService().getCurrentUser());
         SubscribeUtility.setSubcriptionsAndPersist(user, AtsRelationTypes.SubscribedUser_Artifact, arts,
            AtsArtifactTypes.ActionableItem, getClass().getSimpleName());
         AWorkbench.popup(getName(), "Subscriptions updated.");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
