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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * ImageDescriptor concrete class that can be fed data from any storage medium, such as a database, and can then produce
 * images without having to write the image back to disk.
 * 
 * @author Robert A. Fisher
 */
public class InputStreamImageDescriptor extends ImageDescriptor implements Serializable {
   private static final long serialVersionUID = -1671707512486351173L;
   private final byte[] data;
   private ImageData imageData;

   public InputStreamImageDescriptor(InputStream input) {
      this.data = input == null ? null : Streams.getByteArray(input);
   }

   public InputStreamImageDescriptor(Image image) {
      ImageLoader loader = new ImageLoader();
      loader.data = new ImageData[] {image.getImageData()};
      ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
      loader.save(imageStream, SWT.IMAGE_GIF);

      this.data = imageStream.toByteArray();
   }

   /**
    * @param data The data to be used for producing the image. The byte[] should match the format that would be acquired
    *           from reading an image file.
    */
   public InputStreamImageDescriptor(byte[] data) {
      this.data = data;
   }

   @Override
   public ImageData getImageData() {
      try {
         if (data != null && data.length > 0) {
            imageData = new ImageData(new ByteArrayInputStream(data));
         }
      } catch (SWTException ex) {
         OseeLog.log(OseePluginUiActivator.class, Level.WARNING, ex);
      }
      return imageData;
   }

   public byte[] getData() {
      return data;
   }
}
