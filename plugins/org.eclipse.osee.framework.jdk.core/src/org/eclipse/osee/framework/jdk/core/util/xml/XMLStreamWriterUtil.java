/*
 * Created on Nov 8, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.util.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterUtil {

   public static void writeElement(XMLStreamWriter writer, String elementName, String characterData) throws XMLStreamException {
      writer.writeStartElement(elementName);
      writer.writeCharacters(characterData);
      writer.writeEndElement();
   }

}
