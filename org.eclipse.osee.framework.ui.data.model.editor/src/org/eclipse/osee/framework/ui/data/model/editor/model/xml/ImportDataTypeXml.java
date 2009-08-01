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
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;

/**
 * @author Roberto E. Escobar
 */
public class ImportDataTypeXml extends BaseXmlDataType<DataType> {

   @Override
   public String getElementName() {
      return "import";
   }

   @Override
   protected void writeBody(XMLStreamWriter writer, DataType dataType) throws XMLStreamException {
      writer.writeAttribute("type", dataType.getClass().getSimpleName());
   }

   @Override
   protected DataType newDataTypeInstance() {
      return null;
   }
}
