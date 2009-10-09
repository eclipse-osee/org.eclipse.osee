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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.AICheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class SubscribeByActionableItem extends XNavigateItemAction {

   public SubscribeByActionableItem(XNavigateItem parent) {
      super(parent, "Subscribe by Actionable Item", FrameworkImage.EMAIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      final AICheckTreeDialog diag =
            new AICheckTreeDialog(getName(),
                  "Select Actionable Items\n\nEmail will be sent for every Action created against these AIs.",
                  Active.Active);
      try {
         List<ActionableItemArtifact> objs =
               Collections.castAll(UserManager.getUser().getRelatedArtifactsOfType(AtsRelation.SubscribedUser_Artifact,
                     ActionableItemArtifact.class));
         diag.setInitialAias(objs);
         if (diag.open() != 0) return;
         UserManager.getUser().setRelationsOfTypeUseCurrentOrder(AtsRelation.SubscribedUser_Artifact, diag.getChecked(),
               ActionableItemArtifact.class);
         UserManager.getUser().persist();
         AWorkbench.popup(getName(), "Subscriptions updated.");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
