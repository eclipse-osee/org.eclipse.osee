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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;

/**
 * @author Ryan D. Brooks
 */
public class DefaultArtifactRenderer extends Renderer {

   /**
    * @param rendererId
    */
   public DefaultArtifactRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public DefaultArtifactRenderer newInstance() throws OseeCoreException {
      return new DefaultArtifactRenderer(getId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#edit(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void edit(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      ArtifactEditor.editArtifact(artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#preview(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void preview(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      ArtifactEditor.editArtifact(artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#supportsEdit()
    */
   @Override
   public boolean supportsEdit() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#isValidFor(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      return DEFAULT_MATCH;
   }
}