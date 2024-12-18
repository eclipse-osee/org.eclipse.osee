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
import org.eclipse.osee.framework.core.data.MicrosoftOfficeApplicationEnum;
import org.eclipse.osee.framework.ui.swt.internal.Activator;
import org.eclipse.swt.program.Program;

/**
 * Represents an image descriptor based on a file extension and the "mso-application" processing instruction extracted
 * from XML content. Interacts with {@link MsoApplicationExtractor} to extract the associated Microsoft Office
 * application and {@link ProgramFinder} to retrieve the image.
 *
 * @see MsoApplicationExtractor
 * @see ProgramFinder
 * @see ImageDescriptor
 * @see KeyedImage
 * @author Ryan D. Brooks
 * @author Jaden W. Puckett
 */
public class ProgramImage implements KeyedImage {

   private final String extension;
   private MicrosoftOfficeApplicationEnum msoApplication = MicrosoftOfficeApplicationEnum.SENTINEL;

   /**
    * Constructs a {@code ProgramImage} with the specified file extension.
    *
    * @param extension the file extension; must not be {@code null} or empty.
    */
   public ProgramImage(String extension) {
      this.extension = extension;
   }

   public ProgramImage(String extension, MicrosoftOfficeApplicationEnum msoApplication) {
      this(extension);
      this.msoApplication = msoApplication;
   }

   /**
    * Creates an {@link ImageDescriptor} based on the program and extracted metadata.
    *
    * @return an {@link ImageDescriptor} or {@code null} if no image data is available.
    */
   @Override
   public ImageDescriptor createImageDescriptor() {
      Program program = ProgramFinder.findProgram(extension, msoApplication);
      if (program == null || program.getImageData() == null) {
         return null;
      }
      return ImageDescriptor.createFromImageData(program.getImageData());
   }

   /**
    * Generates a unique image key based on the file extension and "mso-application" value.
    *
    * @return a unique image key in the format {@code PLUGIN_ID.program.extension.msoApplication}.
    */
   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + ".program." + extension + "." + msoApplication;
   }
}
