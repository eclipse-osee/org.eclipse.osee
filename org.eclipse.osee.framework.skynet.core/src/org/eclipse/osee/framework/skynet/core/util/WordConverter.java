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
package org.eclipse.osee.framework.skynet.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlOutputTransform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
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

   private IWordMLConversionHandler getWordMLConversionHandler() throws FileNotFoundException, TransformerConfigurationException, IOException, TransformerFactoryConfigurationError {
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
      } catch (Exception ex) {
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
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         html = ex.getLocalizedMessage();
      }
      return html;
   }

   private class DefaultWordMLConversionHandler implements IWordMLConversionHandler {

      private final Transformer transformer;

      private DefaultWordMLConversionHandler() throws FileNotFoundException, IOException, TransformerConfigurationException, TransformerFactoryConfigurationError {
         FileInputStream xsl =
               new FileInputStream(SkynetActivator.getInstance().getPluginFile("support/xslt/word2html.xsl"));
         transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsl));
         if (xsl != null) {
            xsl.close();
         }
      }

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
         return XmlOutputTransform.xmlToHtmlString(inputStream, transformer);
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