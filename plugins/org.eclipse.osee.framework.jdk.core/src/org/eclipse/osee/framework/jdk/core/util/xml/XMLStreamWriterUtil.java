/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Ryan D. Brooks
 */
public class XMLStreamWriterUtil {

   public static void writeElement(XMLStreamWriter writer, String elementName, String characterData)
      throws XMLStreamException {
      writer.writeStartElement(elementName);
      writer.writeCharacters(characterData);
      writer.writeEndElement();
   }

}
