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

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class SubscribeByTeamDefinition extends XNavigateItemAction {

   public SubscribeByTeamDefinition(XNavigateItem parent) {
      super(parent, "Subscribe by Team Definition", FrameworkImage.EMAIL);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      AWorkbench.popup("Not implemented yet");
      //      final TeamDefinitionCheckTreeDialog diag =
      //            new TeamDefinitionCheckTreeDialog(getName(),
      //                  "Select Team Definition\n\nEmail will be sent for every action written against these Teams.",
      //                  Active.Active);
      //      try {
      //         List<Object> objs =
      //               Collections.castAll(UserManager.getUser().getRelatedArtifactsOfType(AtsRelation.SubscribedUser_Artifact,
      //                     TeamDefinitionArtifact.class));
      //         diag.setInitialSelections(objs);
      //         if (diag.open() != 0) return;
      //         UserManager.getUser().setRelationsOfType(AtsRelation.SubscribedUser_Artifact, diag.getChecked(),
      //               TeamDefinitionArtifact.class);
      //         UserManager.getUser().persistAttributesAndRelations();
      //         AWorkbench.popup(getName(), "Subscriptions updated.");
      //      } catch (Exception ex) {
      //         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      //      }
   }
}
