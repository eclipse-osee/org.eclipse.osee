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
package org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer;

import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class IndexerXmlInputStreamParser {
   private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

   public static interface IndexItemIdCollector {
      void onItemId(long itemId) throws OseeCoreException;
   }

   public void parse(InputStream inputStream, IndexItemIdCollector collector) throws OseeCoreException {
      try {
         XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(inputStream, "UTF-8");
         while (streamReader.hasNext()) {
            process(streamReader, collector);
            streamReader.next();
         }
      } catch (XMLStreamException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
   }

   private void process(XMLStreamReader reader, IndexItemIdCollector collector) throws OseeCoreException {
      String localName;
      String uri;
      int eventType = reader.getEventType();
      switch (eventType) {
         case XMLStreamConstants.START_ELEMENT:
            localName = reader.getLocalName();
            uri = reader.getNamespaceURI();
            if (localName.equalsIgnoreCase("entry")) {
               String gammaId = reader.getAttributeValue(uri, "gammaId");
               if (Strings.isValid(gammaId)) {
                  long gammaLong = Long.parseLong(gammaId);
                  collector.onItemId(gammaLong);
               }
            }
            break;
         default:
            break;
      }
   }
}
