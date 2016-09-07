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

import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class OpenOfficeWriterRenderer extends FileSystemRenderer implements ITemplateRenderer {

   @Override
   public OpenOfficeWriterRenderer newInstance() {
      return new OpenOfficeWriterRenderer();
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "odt";
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact, Object... objects) {
      return NO_MATCH;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      throw new UnsupportedOperationException();
   }
}