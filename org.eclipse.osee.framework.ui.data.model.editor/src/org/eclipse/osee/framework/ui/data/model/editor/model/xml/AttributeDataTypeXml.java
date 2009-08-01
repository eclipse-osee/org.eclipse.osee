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

import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataTypeXml extends BaseXmlDataType<AttributeDataType> {
   private final String ATTRIBUTE_TAG = "attribute";
   private final String MULTIPLICITY = "multiplicity";
   private final String MIN_OCCURRENCE = "min";
   private final String MAX_OCCURRENCE = "max";
   private final String ATTRIBUTE_BASE = "attributeBase";
   private final String ATTRIBUTE_PROVIDER = "attributeProvider";
   private final String TAGGER = "tagger";
   private final String EXTENSION = "extension";
   private final String DEFAULT_VALUE = "defaultValue";
   private final String ENUM_TYPE_ID = "enumTypeId";
   private final String TOOL_TIP = "toolTip";

   @Override
   public String getElementName() {
      return ATTRIBUTE_TAG;
   }

   @Override
   protected void writeBody(XMLStreamWriter writer, AttributeDataType dataType) throws XMLStreamException {
      writer.writeStartElement(MULTIPLICITY);
      writer.writeAttribute(MIN_OCCURRENCE, String.valueOf(dataType.getMinOccurrence()));
      writer.writeAttribute(MAX_OCCURRENCE, String.valueOf(dataType.getMaxOccurrence()));
      writer.writeEndElement();
      writeTextElement(writer, ATTRIBUTE_BASE, dataType.getBaseAttributeClass());
      writeTextElement(writer, ATTRIBUTE_PROVIDER, dataType.getProviderAttributeClass());
      writeTextElement(writer, TAGGER, dataType.getTaggerId());
      writeTextElement(writer, EXTENSION, dataType.getFileTypeExtension());
      writeCDataElement(writer, DEFAULT_VALUE, dataType.getDefaultValue());
      writeCDataElement(writer, ENUM_TYPE_ID, String.valueOf(dataType.getEnumTypeId()));
      writeTextElement(writer, TOOL_TIP, dataType.getToolTipText());
   }

   @Override
   protected void populateFromTextElement(AttributeDataType dataType, String tag, String text) throws XMLStreamException {
      super.populateFromTextElement(dataType, tag, text);
      if (ATTRIBUTE_BASE.equals(tag)) dataType.setBaseAttributeClass(text);
      if (ATTRIBUTE_PROVIDER.equals(tag)) dataType.setProviderAttributeClass(text);
      if (TAGGER.equals(tag)) dataType.setTaggerId(text);
      if (EXTENSION.equals(tag)) dataType.setFileTypeExtension(text);
      if (TOOL_TIP.equals(tag)) dataType.setToolTipText(text);
   }

   @Override
   protected void populateFromCDataElement(AttributeDataType dataType, String tag, String text) throws XMLStreamException {
      super.populateFromCDataElement(dataType, tag, text);
      if (DEFAULT_VALUE.equals(tag)) dataType.setDefaultValue(text);
      if (ENUM_TYPE_ID.equals(tag)) dataType.setEnumTypeId(Integer.parseInt(text));
   }

   @Override
   protected void populateFromAttributes(AttributeDataType dataType, String tag, Map<String, String> attributes) throws XMLStreamException {
      super.populateFromAttributes(dataType, tag, attributes);
      if (MULTIPLICITY.equals(tag)) {
         String value = attributes.get(MIN_OCCURRENCE);
         if (value != null) {
            dataType.setMinOccurrence(Integer.parseInt(value));
         }
         value = attributes.get(MAX_OCCURRENCE);
         if (value != null) {
            dataType.setMaxOccurrence(Integer.parseInt(value));
         }
      }
   }

   @Override
   protected AttributeDataType newDataTypeInstance() {
      return new AttributeDataType();
   }
}
