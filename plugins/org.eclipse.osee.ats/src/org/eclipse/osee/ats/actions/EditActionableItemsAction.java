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

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public void runWithException()  {
      AtsUtil.editActionableItems(teamWf);
   }

}
