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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class OpenOfficeWriterRenderer extends FileRenderer implements ITemplateRenderer {

   /**
    * @param rendererId
    */
   public OpenOfficeWriterRenderer() {
      super();
   }

   @Override
   public OpenOfficeWriterRenderer newInstance() throws OseeCoreException {
      return new OpenOfficeWriterRenderer();
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "odt";
   }

   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      return NO_MATCH;
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }
}