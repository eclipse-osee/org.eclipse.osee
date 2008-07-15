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
package org.eclipse.osee.framework.search.engine.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineTagger implements ISearchEngineTagger {

   private ExecutorService executor;

   public SearchEngineTagger() {
      this.executor = Executors.newSingleThreadExecutor();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchTagger#tagAttribute(java.lang.String, int, long)
    */
   @Override
   public void tagAttribute(long gammaId) {
      this.executor.submit(new TaggerRunnable(gammaId));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchTagger#tagFromXmlStream(java.io.InputStream)
    */
   @Override
   public void tagFromXmlStream(InputStream inputStream) {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         xmlReader.setContentHandler(new AttributeXmlParser());
         xmlReader.parse(new InputSource(inputStream));
      } catch (SAXException ex) {
         ex.printStackTrace();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private final class AttributeXmlParser extends AbstractSaxHandler {

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String, java.lang.String, java.lang.String)
       */
      @Override
      public void endElementFound(String uri, String localName, String name) throws SAXException {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
       */
      @Override
      public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
         if (name.equalsIgnoreCase("entry")) {
            String gammaId = attributes.getValue("gammaId");
            if (Strings.isValid(gammaId)) {
               tagAttribute(Long.parseLong(gammaId));
            }
         }
      }
   }
}
