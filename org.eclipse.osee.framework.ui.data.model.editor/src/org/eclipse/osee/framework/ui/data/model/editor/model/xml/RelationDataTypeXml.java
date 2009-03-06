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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;

/**
 * @author Roberto E. Escobar
 */
public class RelationDataTypeXml extends BaseXmlDataType<RelationDataType> {
   private final String RELATION_TAG = "relation";

   private final String ORDER = "order";
   private final String SIDE_A_NAME = "sideAName";
   private final String SIDE_B_NAME = "sideBName";

   private final String SHORT_NAME = "shortName";
   private final String A_TO_B_PHRASE = "aToBPhrase";
   private final String B_TO_A_PHRASE = "bToAPhrase";

   public RelationDataTypeXml() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.utility.ODMXmlWriter.DataTypeWriter#getElementName()
    */
   @Override
   public String getElementName() {
      return RELATION_TAG;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.utility.ODMXmlWriter.DataTypeWriter#writeBody(org.eclipse.osee.framework.ui.data.model.editor.model.DataType)
    */
   @Override
   protected void writeBody(XMLStreamWriter writer, RelationDataType dataType) throws XMLStreamException {
      writeTextElement(writer, ORDER, String.valueOf(dataType.getOrdered()));
      writeTextElement(writer, SIDE_A_NAME, dataType.getSideAName());
      writeTextElement(writer, SIDE_B_NAME, dataType.getSideBName());
      writeTextElement(writer, SHORT_NAME, dataType.getShortName());
      writeTextElement(writer, A_TO_B_PHRASE, dataType.getAToBPhrase());
      writeTextElement(writer, B_TO_A_PHRASE, dataType.getBToAPhrase());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.model.xml.BaseXmlDataType#populateFromTextElement(org.eclipse.osee.framework.ui.data.model.editor.model.DataType, java.lang.String, java.lang.String)
    */
   @Override
   protected void populateFromTextElement(RelationDataType dataType, String tag, String text) throws XMLStreamException {
      super.populateFromTextElement(dataType, tag, text);
      if (ORDER.equals(tag)) dataType.setOrdered(Boolean.parseBoolean(text));
      if (SIDE_A_NAME.equals(tag)) dataType.setSideAName(text);
      if (SIDE_B_NAME.equals(tag)) dataType.setSideBName(text);
      if (SHORT_NAME.equals(tag)) dataType.setShortName(text);
      if (A_TO_B_PHRASE.equals(tag)) dataType.setAToBPhrase(text);
      if (B_TO_A_PHRASE.equals(tag)) dataType.setBToAPhrase(text);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.model.xml.BaseXmlDataType#newDataTypeInstance()
    */
   @Override
   protected RelationDataType newDataTypeInstance() {
      return new RelationDataType();
   }
}
