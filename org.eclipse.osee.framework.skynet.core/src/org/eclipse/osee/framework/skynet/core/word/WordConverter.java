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
package org.eclipse.osee.framework.skynet.core.word;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpImageProcessor;

/**
 * @author Roberto E. Escobar
 */
public class WordConverter {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(WordConverter.class);
   private static WordConverter instance = null;

   private IWordMLConversionHandler wordMLConversionHandler;

   private WordConverter() {
      this.wordMLConversionHandler = null;
   }

   public static WordConverter getInstance() {
      if (instance == null) {
         instance = new WordConverter();
      }
      return instance;
   }

   private IWordMLConversionHandler getWordMLConversionHandler() throws Throwable {
      if (wordMLConversionHandler == null) {
         IWordMLConversionHandler toReturn = null;
         ExtensionDefinedObjects<IWordMLConversionHandler> extensionDefinedObjects =
               new ExtensionDefinedObjects<IWordMLConversionHandler>(
                     "org.eclipse.osee.framework.skynet.core.WordMLConversionHandler", "WordMLConversionHandler",
                     "ClassName");

         try {
            List<IWordMLConversionHandler> objects = extensionDefinedObjects.getObjects();
            System.out.println(objects);
            for (IWordMLConversionHandler wordMLConversionHandler : objects) {
               if (wordMLConversionHandler.isValid() != false) {
                  toReturn = wordMLConversionHandler;
                  break;
               }
            }
         } catch (Exception ex) {
            logger.log(Level.INFO, "Using default WordML Handler - Minimal Functionality Supported");
         }
         wordMLConversionHandler = (toReturn != null) ? toReturn : new DefaultWordMLConversionHandler();
      }
      return wordMLConversionHandler;
   }

   public boolean isDefaultConverter() {
      IWordMLConversionHandler currentConverter = null;
      try {
         currentConverter = getWordMLConversionHandler();
      } catch (Throwable ex) {
         // Do Nothing
      }
      return currentConverter != null && currentConverter instanceof DefaultWordMLConversionHandler;
   }

   public String toHtml(InputStream xml) {
      String html = null;
      try {
         HttpImageProcessor imageProcessor = HttpImageProcessor.getInstance();
         IWordMLConversionHandler handler = getWordMLConversionHandler();
         handler.setHttpImageRequestServer(imageProcessor.getImageProcessingMarker());
         handler.setImageDirectory(imageProcessor.getImageDirectory());
         html = handler.wordMLToHtml(xml);
      } catch (java.lang.StackOverflowError error) {
         logger.log(Level.SEVERE, error.getLocalizedMessage(), error);
         html = "Stack overflow error caused by recursion in the xslt transform";
      } catch (Throwable ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         html = ex.getLocalizedMessage();
      }
      return html;
   }

   private class DefaultWordMLConversionHandler implements IWordMLConversionHandler {

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.util.IWordMLConversionHandler#setHttpImageRequestServer(java.lang.String)
       */
      public void setHttpImageRequestServer(String httpImageServerRequest) {
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.util.IWordMLConversionHandler#setImageDirectory(java.io.File)
       */
      public void setImageDirectory(File directoryPath) {
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.util.IWordMLConversionHandler#wordMLToHtml(java.io.InputStream)
       */
      public String wordMLToHtml(InputStream inputStream) throws Exception {
         StringBuilder builder = new StringBuilder();
         builder.append("<HTML></BODY>");
         builder.append("<span style=\"color:#FF0000;font-size:medium;font-style:bold;\">");
         builder.append("Warning: ");
         builder.append("</span>");
         builder.append("<span style=\"font-color:#000000;font-size:medium;font-style:bold;\">");
         builder.append("Word content preview generation is not supported on this platform.");
         builder.append("</span>");
         builder.append("<div style=\"align:left; padding:8px; border-width:1px; border-top-style: solid;\">");
         String rawData = Lib.inputStreamToString(inputStream);
         builder.append(WordUtil.textOnly(rawData));
         builder.append("</BODY></HTML>");
         return builder.toString();
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.util.IWordMLConversionHandler#isValid()
       */
      public boolean isValid() {
         return true;
      }
   }
}