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

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ryan D. Brooks
 */
public class TemplateRenderer extends FileSystemRenderer implements ITemplateRenderer {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#getAssociatedProgram(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#renderToFileSystem(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.resources.IFolder, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, Artifact artifact, Branch branch, String option, PresentationType presentationType) throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#renderToFileSystem(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.resources.IFolder, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public IFile renderToFileSystem(IProgressMonitor monitor, IFolder baseFolder, List<Artifact> artifacts, String option, PresentationType presentationType) throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer#renderInComposite(org.eclipse.swt.widgets.Composite, org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void renderInComposite(Composite composite, BlamVariableMap variableMap, boolean readOnly, IProgressMonitor monitor) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer#renderToFolder(org.eclipse.core.resources.IFolder, org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void renderToFolder(IFolder folder, BlamVariableMap variableMap, boolean readOnly, IProgressMonitor monitor) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#isValidFor(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      return NO_MATCH;
   }
}
