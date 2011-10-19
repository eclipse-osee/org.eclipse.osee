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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldAction extends Action {

   private final AbstractWorkflowArtifact sma;

   public OpenInAtsWorldAction(AbstractWorkflowArtifact sma) {
      super();
      this.sma = sma;
      setText("Open in ATS World Editor");
   }

   public void performOpen() {
      try {
         if (sma.isTeamWorkflow()) {
            Artifact actionArt = ((TeamWorkFlowArtifact) sma).getParentActionArtifact();
            if (actionArt != null) {
               WorldEditor.open(new WorldEditorSimpleProvider("Action " + actionArt.getHumanReadableId(),
                  Arrays.asList(actionArt)));
            } else {
               AWorkbench.popup("No Parent Action; Opening Team Workflow");
               WorldEditor.open(new WorldEditorSimpleProvider(
                  sma.getArtifactTypeName() + " " + sma.getHumanReadableId(), Arrays.asList(sma)));
               return;
            }
            return;
         } else {
            WorldEditor.open(new WorldEditorSimpleProvider(sma.getArtifactTypeName() + ": " + sma.getHumanReadableId(),
               Arrays.asList(sma)));
            return;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void run() {
      performOpen();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.GLOBE);
   }

}
