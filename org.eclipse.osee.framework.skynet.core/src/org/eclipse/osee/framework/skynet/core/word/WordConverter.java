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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpImageProcessor;

/**
 * @author Roberto E. Escobar
 */
public class WordConverter {
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

   private IWordMLConversionHandler getWordMLConversionHandler() {
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
            OseeLog.log(SkynetActivator.class, Level.INFO,
                  "Using default WordML Handler - Minimal Functionality Supported");
         }
         wordMLConversionHandler = (toReturn != null) ? toReturn : new DefaultWordMLConversionHandler();
      }
      return wordMLConversionHandler;
   }

   public boolean isDefaultConverter() {
      return instance.getWordMLConversionHandler() instanceof DefaultWordMLConversionHandler;
   }

   public static String toHtml(InputStream xmlInputStream) {
      String html = null;
      try {
         HttpImageProcessor imageProcessor = HttpImageProcessor.getInstance();
         IWordMLConversionHandler handler = instance.getWordMLConversionHandler();
         handler.setHttpImageRequestServer(imageProcessor.getImageProcessingMarker());
         handler.setImageDirectory(imageProcessor.getImageDirectory());
         html = handler.wordMLToHtml(xmlInputStream);
      } catch (java.lang.StackOverflowError er) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, er);
         html = "Stack overflow error caused by recursion in the xslt transform";
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         html = ex.getLocalizedMessage();
      } finally {
         try {
            xmlInputStream.close();
         } catch (IOException ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         }
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
      public String wordMLToHtml(InputStream inputStream) {
         StringBuilder builder = new StringBuilder();
         builder.append("<HTML></BODY>");
         builder.append("<span style=\"color:#FF0000;font-size:medium;font-style:bold;\">");
         builder.append("Warning: ");
         builder.append("</span>");
         builder.append("<span style=\"font-color:#000000;font-size:medium;font-style:bold;\">");
         builder.append("Word content preview generation is not supported on this platform.");
         builder.append("</span>");
         builder.append("<div style=\"align:left; padding:8px; border-width:1px; border-top-style: solid;\">");
         String rawData;
         try {
            rawData = Lib.inputStreamToString(inputStream);
         } catch (IOException ex) {
            OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            rawData = ex.getLocalizedMessage();
         }
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