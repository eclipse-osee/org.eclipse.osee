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
package org.eclipse.osee.ote.ui.test.manager.configuration;

import java.io.File;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigFileHandler {

   /**
    * Writes the XML in sourceDoc to the test manager config file specified. If the destination file
    * already exists, and contains the XML specified for the sourceDoc's root node, it will be
    * replaced, otherwise it will be appended to the file. If the file does not exist, a new file
    * will be created only containing the sourceDoc underneath a root TestManagerConfig node.
    * 
    * @param sourceDoc
    * @param fileString
    * @throws Exception
    */
   public static void writeFile(Document sourceDoc, String fileString) throws Exception {
      Document doc;
      File file = new File(fileString);
      if (file.exists()) {
         doc = writeSectionToFile(sourceDoc, file);
      }
      else {
         doc = Jaxp.newDocument();
         Element root = doc.createElement("TestManagerConfig");
         root.appendChild(doc.importNode(sourceDoc.getDocumentElement(), true));
         doc.appendChild(root);
      }
      Jaxp.writeXmlDocument(doc, file, Jaxp.getPrettyFormat(doc));
   }

   private static Document writeSectionToFile(Document docToAdd, File file) throws Exception {
      Element nodeToAdd = docToAdd.getDocumentElement();
      String configType = nodeToAdd.getNodeName();
      Document doc = Jaxp.readXmlDocument(file);
      Element root = doc.getDocumentElement();
      Element configNode = Jaxp.getChild(root, configType);
      if (configNode != null) {
         root.removeChild(configNode);
         root.appendChild(doc.importNode(nodeToAdd, true));
      }
      else {
         root.appendChild(doc.importNode(nodeToAdd, true));
      }
      return doc;
   }
}
