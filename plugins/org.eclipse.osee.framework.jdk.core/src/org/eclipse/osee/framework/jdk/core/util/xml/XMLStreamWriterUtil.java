/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Ryan D. Brooks
 */
public class XMLStreamWriterUtil {

   public static void writeElement(XMLStreamWriter writer, String elementName, String characterData) throws XMLStreamException {
      writer.writeStartElement(elementName);
      writer.writeCharacters(characterData);
      writer.writeEndElement();
   }

}
