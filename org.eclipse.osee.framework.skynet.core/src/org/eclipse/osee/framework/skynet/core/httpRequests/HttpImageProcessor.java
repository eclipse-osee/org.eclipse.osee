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
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;

/**
 * @author Roberto E. Escobar
 */
public class HttpImageProcessor {
   private static HttpImageProcessor instance = null;

   private ExtensionDefinedObjects<IHtmlImageHandler> extensionDefinedObjects;
   private File imageDirectory;

   private HttpImageProcessor() {
      this.imageDirectory = OseeData.getFile(".images");
      this.extensionDefinedObjects =
            new ExtensionDefinedObjects<IHtmlImageHandler>("org.eclipse.osee.framework.skynet.core.HtmlImageHandler",
                  "HtmlImageHandler", "ClassName");
   }

   public static HttpImageProcessor getInstance() {
      if (instance == null) {
         instance = new HttpImageProcessor();
      }
      return instance;
   }

   public File getImageDirectory() {
      return imageDirectory;
   }

   public String getImageProcessingMarker() {
      return "httpImage://";
   }

   public void processRequest(String imagePath, OutputStream outputStream) throws Exception {
      InputStream is = null;
      try {
         is = getInputStream(imagePath);
         if (is != null) {
            IHtmlImageHandler imageProcessor = getImageProcessor(is);
            imageProcessor.convert(is, outputStream);
         }
      } finally {
         if (is != null) {
            try {
               outputStream.flush();
               is.close();
            } catch (IOException ex) {
               OseeLog.log(SkynetActivator.class, Level.WARNING, ex);
            }
         }
      }
   }

   private InputStream getInputStream(String imagePath) throws IOException {
      InputStream toReturn = null;
      if (false != Strings.isValid(imagePath)) {
         File toGet = new File(imageDirectory.getAbsolutePath() + File.separator + imagePath);
         if (toGet != null && toGet.exists() && toGet.canRead()) {
            toReturn = new BufferedInputStream(new FileInputStream(toGet));
         }
      }
      return toReturn;
   }

   private IHtmlImageHandler getImageProcessor(InputStream inputStream) {
      IHtmlImageHandler toReturn = DefaultImageHandler.getInstance();
      try {
         List<IHtmlImageHandler> imageSupport = extensionDefinedObjects.getObjects();
         for (IHtmlImageHandler htmlImageSupport : imageSupport) {
            if (false != htmlImageSupport.isValid(inputStream)) {
               toReturn = htmlImageSupport;
               break;
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return toReturn;
   }
}
