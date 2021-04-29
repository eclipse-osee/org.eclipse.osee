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

import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditActionableItemsAction extends AbstractAtsAction {

   private final TeamWorkFlowArtifact teamWf;

   public EditActionableItemsAction(TeamWorkFlowArtifact teamWf) {
      super("Add/Update Actionable Items/Workflows");
      this.teamWf = teamWf;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ACTIONABLE_ITEM));
   }

   @Override
   public void runWithException() {
      editActionableItems(teamWf);
   }

   public static void editActionableItems(TeamWorkFlowArtifact teamArt) {
      new ModifyActionableItemAction(teamArt).run();
   }

}
