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
package org.eclipse.osee.ats.workflow.editor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkflowConfigRenderer extends DefaultArtifactRenderer {

   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsworkflowconfigeditor.command";

   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return ImageManager.getImage(AtsImage.WORKFLOW_CONFIG);
   }

   @Override
   public String getName() {
      return "ATS Workflow Config Editor";
   }

   /**
    * @param rendererId
    */
   public AtsWorkflowConfigRenderer() {
      super();
   }

   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      try {
         for (Artifact artifact : artifacts) {
            if (artifact.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
               AtsWorkflowConfigEditor.editWorkflow(new WorkFlowDefinition(artifact));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public AtsWorkflowConfigRenderer newInstance() throws OseeCoreException {
      return new AtsWorkflowConfigRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);
      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add(COMMAND_ID);
      }
      return commandIds;
   }

}
