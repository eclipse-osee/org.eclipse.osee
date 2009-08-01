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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public abstract class WordRenderer extends FileRenderer {

   // We need MS Word, so look for the program that is for .doc files
   private static final Program wordApp = Program.findProgram("doc");

   /**
    * @param rendererId
    */
   public WordRenderer() {
      super();
   }

   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return ImageManager.getProgramImage("doc");
   }

   public static ImageDescriptor getImageDescriptor() throws OseeArgumentException {
      return ImageManager.getProgramImageDescriptor("doc");
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }

   @Override
   public String getName() {
      return "MS Word Edit";
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return "xml";
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      if (wordApp == null) {
         throw new OseeStateException("No program associated with the extension .doc");
      }
      return wordApp;
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }
}