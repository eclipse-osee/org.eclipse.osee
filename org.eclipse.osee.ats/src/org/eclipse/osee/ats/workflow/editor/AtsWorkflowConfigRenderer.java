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

import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkflowConfigRenderer extends DefaultArtifactRenderer {
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getImage()
    */
   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return AtsPlugin.getInstance().getImage("workflowConfig.gif");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getId()
    */
   @Override
   public String getId() {
      return "org.eclipse.osee.ats.editor.AtsWorkflowConfigRenderer";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#getName()
    */
   @Override
   public String getName() {
      return "ATS Workflow Config Editor";
   }

   /**
    * @param rendererId
    */
   public AtsWorkflowConfigRenderer(String rendererId) {
      super(rendererId);
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
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
   * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
   */
   @Override
   public AtsWorkflowConfigRenderer newInstance() throws OseeCoreException {
      return new AtsWorkflowConfigRenderer(getId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#isValidFor(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#preview(java.util.List)
    */
   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#minimumRanking()
    */
   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }
}
