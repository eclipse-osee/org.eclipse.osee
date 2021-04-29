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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.operation.ModifyActionableItemsBlam;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ModifyActionableItemAction extends Action {

   private final TeamWorkFlowArtifact team;

   public ModifyActionableItemAction(TeamWorkFlowArtifact team) {
      super("Modify Actionable Item(s)");
      this.team = team;
   }

   @Override
   public void run() {
      ModifyActionableItemsBlam blamOperation = new ModifyActionableItemsBlam();
      blamOperation.setDefaultTeamWorkflow(team);
      BlamEditor.edit(blamOperation);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.ACTIONABLE_ITEM);
   }

}
