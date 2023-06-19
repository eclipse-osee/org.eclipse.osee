/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util.io.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;

/**
 * @author Ryan D. Brooks
 */
public class SaxTransformer extends AbstractSaxHandler {
   protected XMLStreamWriter writer;

   @Override
   public void endElementFound(String uri, String localName, String qName) throws XMLStreamException {
      writer.writeCharacters(getContents());
      writer.writeEndElement();
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes)
      throws XMLStreamException {
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
      if (writer != null) {
         writer.writeEndDocument();
         writer.close();
      }
   }
}