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

import java.io.InputStream;
import java.sql.Connection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
final class InputStreamTagProcessor extends InputToTagQueueTx {
   private final InputStream inputStream;

   InputStreamTagProcessor(ISearchEngineTagger tagger, ITagListener listener, InputStream inputStream, boolean isCacheAll, int cacheLimit) {
      super(tagger, listener, isCacheAll, cacheLimit);
      this.inputStream = inputStream;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.internal.ConvertToTagQueueTx#doWork(java.sql.Connection)
    */
   @Override
   protected void convertInput(Connection connection) throws Exception {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new AttributeXmlParser(connection));
      xmlReader.parse(new InputSource(inputStream));
   }

   private final class AttributeXmlParser extends AbstractSaxHandler {
      private final Connection connection;

      AttributeXmlParser(Connection connection) {
         this.connection = connection;
      }

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
               try {
                  addEntry(connection, Long.parseLong(gammaId));
               } catch (Exception ex) {
                  throw new RuntimeException("Error Processing Attribute Xml - ", ex);
               }
            }
         }
      }
   }
}
