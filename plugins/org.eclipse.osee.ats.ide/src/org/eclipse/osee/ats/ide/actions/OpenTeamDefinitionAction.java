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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
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
         AtsEditors.openATSAction(AtsApiService.get().getQueryService().getArtifact(teamArt.getTeamDefinition()),
            AtsOpenOption.OpenOneOrPopupSelect);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TEAM_DEFINITION);
   }

}
