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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.operation.ModifyActionableItemsBlam;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
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
