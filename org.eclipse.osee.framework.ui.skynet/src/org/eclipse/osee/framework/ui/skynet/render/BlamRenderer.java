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
package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.BlamWorkflow;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;

/**
 * @author Ryan D. Brooks
 */
public class BlamRenderer extends Renderer {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getApplicabilityRating(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact instanceof BlamWorkflow) {
         return ARTIFACT_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#edit(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void edit(Artifact artifact, String option, IProgressMonitor monitor) throws Exception {
      WorkflowEditor.editArtifact(artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#preview(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void preview(Artifact artifact, String option, IProgressMonitor monitor) throws Exception {
      WorkflowEditor.editArtifact(artifact);
   }
}
