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

import java.util.Arrays;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact sma;

   public OpenInAtsWorldAction(AbstractWorkflowArtifact sma) {
      super();
      this.sma = sma;
      setText("Open in ATS World Editor");
   }

   @Override
   public void runWithException() throws OseeCoreException {
      if (sma.isTeamWorkflow()) {
         ActionArtifact actionArt = ((TeamWorkFlowArtifact) sma).getParentActionArtifact();
         if (actionArt != null) {
            WorldEditor.open(new WorldEditorSimpleProvider("Action " + actionArt.getAtsId(),
               Arrays.asList(actionArt)));
         } else {
            WorldEditor.open(new WorldEditorSimpleProvider(sma.getArtifactTypeName() + " " + sma.getAtsId(),
               Arrays.asList(sma)));
            throw new OseeStateException("No Parent Action; Opening Team Workflow");
         }
         return;
      } else {
         WorldEditor.open(new WorldEditorSimpleProvider(sma.getArtifactTypeName() + ": " + sma.getAtsId(),
            Arrays.asList(sma)));
         return;
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.GLOBE);
   }

}
