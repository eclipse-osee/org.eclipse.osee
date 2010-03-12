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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.OseeConnection;
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

   InputStreamTagProcessor(ISearchEngineTagger tagger, ITagListener listener, InputStream inputStream, boolean isCacheAll, int cacheLimit) throws OseeCoreException {
      super(tagger, listener, isCacheAll, cacheLimit);
      this.inputStream = inputStream;
   }

   @Override
   protected void convertInput(OseeConnection connection) throws Exception {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new AttributeXmlParser(connection));
      xmlReader.parse(new InputSource(inputStream));
   }

   private final class AttributeXmlParser extends AbstractSaxHandler {
      private final OseeConnection connection;

      AttributeXmlParser(OseeConnection connection) {
         this.connection = connection;
      }

      @Override
      public void endElementFound(String uri, String localName, String name) throws SAXException {
      }

      @Override
      public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException, NumberFormatException, OseeDataStoreException {
         if (name.equalsIgnoreCase("entry")) {
            String gammaId = attributes.getValue("gammaId");
            if (Strings.isValid(gammaId)) {
               addEntry(connection, Long.parseLong(gammaId));
            }
         }
      }
   }
}
