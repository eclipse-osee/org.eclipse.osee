/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.io.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class SaxTransformer extends AbstractSaxHandler {
   protected XMLStreamWriter writer;

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
      try {
         writer.writeCharacters(getContents());
         writer.writeEndElement();
      } catch (XMLStreamException ex) {
         throw new SAXException(ex);
      }
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception {
      writer.writeStartElement(localName);
      for (int i = 0; i < attributes.getLength(); i++) {
         writer.writeAttribute(attributes.getLocalName(i), attributes.getValue(i));
      }
   }

   public XMLStreamWriter getWriter() {
      return writer;
   }

   public void setWriter(XMLStreamWriter writer) throws XMLStreamException {
      this.writer = writer;
      writer.writeStartDocument("1.0");
   }

   public void finish() throws XMLStreamException {
      writer.writeEndDocument();
      writer.flush();
      writer.close();
   }
}
