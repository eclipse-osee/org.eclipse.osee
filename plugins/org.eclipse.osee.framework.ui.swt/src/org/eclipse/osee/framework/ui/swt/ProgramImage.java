/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.internal.Activator;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class ProgramImage implements KeyedImage {
   private final String extension;

   public ProgramImage(String extension) {
      this.extension = extension;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      Program program = ProgramFinder.findProgram(extension);
      if (program == null || program.getImageData() == null) {
         return null;
      }
      return ImageDescriptor.createFromImageData(program.getImageData());
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + ".program." + extension;
   }
}