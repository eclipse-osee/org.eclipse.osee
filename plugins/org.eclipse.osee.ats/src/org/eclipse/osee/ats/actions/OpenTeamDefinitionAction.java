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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsEditors;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenTeamDefinitionAction extends AbstractAtsAction {

   private final TeamWorkFlowArtifact teamArt;

   public OpenTeamDefinitionAction(TeamWorkFlowArtifact teamArt) {
      super();
      this.teamArt = teamArt;
      setText("Open Team Definition");
      setToolTipText(getText());
   }

   @Override
   public void runWithException() {
      if (teamArt.getTeamDefinition() != null) {
         AtsEditors.openATSAction(AtsClientService.get().getQueryService().getArtifact(teamArt.getTeamDefinition()),
            AtsOpenOption.OpenOneOrPopupSelect);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TEAM_DEFINITION);
   }

}
