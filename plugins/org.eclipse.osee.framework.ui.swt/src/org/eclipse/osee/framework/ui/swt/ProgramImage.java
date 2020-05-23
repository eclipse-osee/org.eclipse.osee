/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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