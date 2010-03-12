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
package org.eclipse.osee.framework.ui.data.model.editor.model.xml;

import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseXmlDataType<T extends DataType> {

   public BaseXmlDataType() {
   }

   protected void writeCDataElement(XMLStreamWriter writer, String tag, String value) throws XMLStreamException {
      if (value != null) {
         value = value.trim();
         if (Strings.isValid(value)) {
            writer.writeStartElement(tag);
            writer.writeCData(value);
            writer.writeEndElement();
         }
      }
   }

   protected void writeTextElement(XMLStreamWriter writer, String tag, String value) throws XMLStreamException {
      if (value != null) {
         value = value.trim();
         if (Strings.isValid(value)) {
            writer.writeStartElement(tag);
            writer.writeCharacters(value);
            writer.writeEndElement();
         }
      }
   }

   protected void writeIdAttributes(XMLStreamWriter writer, DataType dataType) throws XMLStreamException {
      writer.writeAttribute("name", dataType.getName());
   }

   public final void write(XMLStreamWriter writer, T dataType) throws XMLStreamException {
      writer.writeStartElement(getElementName());
      writeIdAttributes(writer, dataType);
      writeBody(writer, dataType);
      writer.writeEndElement();
   }

   protected abstract T newDataTypeInstance();

   protected void populateFromAttributes(T dataType, String tag, Map<String, String> attributes) throws XMLStreamException {
      if (tag.equals(getElementName())) {
         dataType.setName(attributes.get("name"));
      }
   }

   protected void populateFromTextElement(T dataType, String tag, String text) throws XMLStreamException {

   }

   protected void populateFromCDataElement(T dataType, String tag, String text) throws XMLStreamException {

   }

   private Map<String, String> getAttributes(XMLStreamReader reader) throws XMLStreamException {
      int count = reader.getAttributeCount();
      Map<String, String> attributes = new HashMap<String, String>();
      for (int index = 0; index < count; index++) {
         attributes.put(reader.getAttributeLocalName(index), reader.getAttributeValue(index));
      }
      return attributes;
   }

   public final void fromXml(XMLStreamReader reader, IDataTypeCollector<T> collector) throws XMLStreamException {
      T object = null;
      String currentElement = null;
      boolean isDone = false;
      while (reader.hasNext() && !isDone) {
         int eventType = reader.getEventType();
         switch (eventType) {
            case XMLStreamConstants.START_ELEMENT:
               currentElement = reader.getLocalName();
               if (currentElement.equals(getElementName())) {
                  object = newDataTypeInstance();
               }
               populateFromAttributes(object, currentElement, getAttributes(reader));
               break;
            case XMLStreamConstants.END_ELEMENT:
               if (currentElement.equals(getElementName())) {
                  isDone = true;
                  collector.collect(object);
               }
               currentElement = null;
               break;
            case XMLStreamConstants.CDATA:
               populateFromCDataElement(object, currentElement, reader.getText());
               break;
            case XMLStreamConstants.CHARACTERS:
               populateFromTextElement(object, currentElement, reader.getText());
               break;
            default:
               break;
         }
         reader.next();
      }
   }

   public abstract String getElementName();

   protected abstract void writeBody(XMLStreamWriter writer, T dataType) throws XMLStreamException;
}
