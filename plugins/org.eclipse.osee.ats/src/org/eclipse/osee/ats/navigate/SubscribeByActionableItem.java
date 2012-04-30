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

package org.eclipse.osee.ats.navigate;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class SubscribeByActionableItem extends XNavigateItemAction {

   public SubscribeByActionableItem(XNavigateItem parent) {
      super(parent, "Subscribe by Actionable Item", FrameworkImage.EMAIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      final AICheckTreeDialog diag =
         new AICheckTreeDialog(getName(),
            "Select Actionable Items\n\nEmail will be sent for every Action created against these AIs.", Active.Active);
      try {
         List<ActionableItemArtifact> objs =
            Collections.castAll(AtsUsersClient.getOseeUser().getRelatedArtifactsOfType(
               AtsRelationTypes.SubscribedUser_Artifact, ActionableItemArtifact.class));
         diag.setInitialAias(objs);
         if (diag.open() != 0) {
            return;
         }
         AtsUsersClient.getOseeUser().setRelationsOfTypeUseCurrentOrder(AtsRelationTypes.SubscribedUser_Artifact,
            diag.getChecked(), ActionableItemArtifact.class);
         AtsUsersClient.getOseeUser().persist(getClass().getSimpleName());
         AWorkbench.popup(getName(), "Subscriptions updated.");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
