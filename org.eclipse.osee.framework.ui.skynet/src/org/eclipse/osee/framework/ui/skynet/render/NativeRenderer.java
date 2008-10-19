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

import java.io.InputStream;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.swt.program.Program;

/**
 * Renders native content.
 * 
 * @author Ryan D. Brooks
 */
public class NativeRenderer extends FileRenderer {

   /**
    * @param rendererId
    */
   public NativeRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public NativeRenderer newInstance() throws OseeCoreException {
      return new NativeRenderer(getId());
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact.isOfType("Native")) {
         return ARTIFACT_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedExtension()
    */
   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return artifact.getSoleAttributeValue(NativeArtifact.EXTENSION, "");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedProgram()
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      String extension = getAssociatedExtension(artifact);
      Program program = Program.findProgram(extension);
      if (program == null) {
         throw new OseeArgumentException(
               "No program associated with the extension " + extension + " found on your local machine.");
      }
      return program;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      return artifact.getSoleAttributeValue(NativeArtifact.CONTENT_NAME);
   }
}