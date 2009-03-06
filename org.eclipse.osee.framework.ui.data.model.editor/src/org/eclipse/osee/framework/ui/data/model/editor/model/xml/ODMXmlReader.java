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

import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;

/**
 * @author Roberto E. Escobar
 */
public class ODMXmlReader {

   private final ODMXmlFactory xmlDataTypeFactory;
   private final XMLStreamReader reader;

   public ODMXmlReader(InputStream inputStream) throws OseeCoreException {
      try {
         XMLInputFactory factory = XMLInputFactory.newInstance();
         reader = factory.createXMLStreamReader(inputStream, "UTF-8");
         xmlDataTypeFactory = new ODMXmlFactory();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public void execute(IProgressMonitor monitor) throws XMLStreamException {
      while (reader.hasNext()) {
         int eventType = reader.getEventType();
         switch (eventType) {
            case XMLStreamConstants.START_ELEMENT:
               String element = reader.getLocalName();
               BaseXmlDataType<DataType> handler = xmlDataTypeFactory.getXmlDataType(element);
               if (handler != null) {
                  //                  handler.fromXml(reader, collector);
               }
               break;
            default:
               break;
         }
         reader.next();
      }
   }

   public void close() throws XMLStreamException {
      if (reader != null) {
         reader.close();
      }
   }
}
