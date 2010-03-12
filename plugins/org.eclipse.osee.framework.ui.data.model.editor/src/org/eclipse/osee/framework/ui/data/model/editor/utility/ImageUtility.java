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
package org.eclipse.osee.framework.ui.data.model.editor.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * @author Roberto E. Escobar
 */
public class ImageUtility {

   private final static byte[] JPEG_HEADER = new byte[] {(byte) 0xff, (byte) 0xd8};
   private final static byte[] PNG_HEADER =
         new byte[] {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A,
               (byte) 0x0A};
   private final static byte[] GIF_HEADER = new byte[] {(byte) 0x47, (byte) 0x49, (byte) 0x46};
   private final static byte[] TIFF_HEADER = new byte[] {(byte) 0x49, (byte) 0x49, (byte) 0x2A};
   private final static byte[] BMP_HEADER = new byte[] {(byte) 0x42, (byte) 0x4D};
   private final static byte[] ICO_HEADER = new byte[] {(byte) 0x00};

   private ImageUtility() {
   }

   public static byte[] imageToBytes(Image image) {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      ImageLoader imageLoader = new ImageLoader();
      imageLoader.data = new ImageData[] {image.getImageData()};
      imageLoader.save(buffer, SWT.IMAGE_JPEG);
      return buffer.toByteArray();
   }

   public static Image bytesToImage(byte[] rawData) {
      ImageLoader imageLoader = new ImageLoader();
      ImageData[] images = imageLoader.load(new ByteArrayInputStream(rawData));
      if (images != null && images.length > 0) {
         return ImageDescriptor.createFromImageData(images[0]).createImage();
      }
      return null;
   }

   public static byte[] imageToBase64(Image image) {
      return Base64Converter.encode(imageToBytes(image));
   }

   public static Image base64ToImage(byte[] rawData) {
      return bytesToImage(Base64Converter.decode(rawData));
   }

   private static boolean doesHeaderMatch(byte[] header, byte[] data) {
      if (data != null && header != null && data.length > header.length) {
         for (int index = 0; index < header.length; index++) {
            if (header[index] != data[index]) {
               return false;
            }
         }
      } else {
         return false;
      }
      return true;
   }

   public static int getImageType(byte[] data) {
      if (doesHeaderMatch(ICO_HEADER, data)) return SWT.IMAGE_ICO;
      if (doesHeaderMatch(JPEG_HEADER, data)) return SWT.IMAGE_JPEG;
      if (doesHeaderMatch(BMP_HEADER, data)) {
         if (data.length > 14 && data[14] == 0x40) {
            if (data.length > 30 && data[30] == 0x00) {
               return SWT.IMAGE_BMP;
            } else {
               return SWT.IMAGE_BMP_RLE;
            }
         } else {
            return SWT.IMAGE_OS2_BMP;
         }
      }
      if (doesHeaderMatch(GIF_HEADER, data)) return SWT.IMAGE_GIF;
      if (doesHeaderMatch(TIFF_HEADER, data)) return SWT.IMAGE_TIFF;
      if (doesHeaderMatch(PNG_HEADER, data)) return SWT.IMAGE_PNG;
      return SWT.IMAGE_UNDEFINED;
   }
}
