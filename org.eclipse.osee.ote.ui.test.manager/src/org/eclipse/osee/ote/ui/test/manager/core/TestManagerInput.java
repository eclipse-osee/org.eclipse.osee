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
package org.eclipse.osee.ote.ui.test.manager.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestManagerInput implements IEditorInput {
   private HashMap<String, String> keyValue;

   public TestManagerInput() {
      keyValue = new HashMap<String, String>();
      loadFromFile();
   }

   public boolean equals(Object object) {
      if (object instanceof TestManagerInput) return true;
      return false;
   }

   public boolean exists() {
      return false;
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

   public String getDefaultXML() {
      return "<testManager>" + "<contact></contact>" + "<description>Test Manager</description>" + "</testManager>";

   }

   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   public String getName() {
      return "TestManager";
   }

   public IPersistableElement getPersistable() {
      return null;
   }

   public String getToolTipText() {
      return "OSEE TestManager";
   }

   public String getValue(String key) {
      return keyValue.get(key);
   }

   public void storeValue(String key, String value) throws ParserConfigurationException, TransformerException, IOException {
      keyValue.put(key, value);
      saveToFile();
   }

   private File getFile() {
      Location user = Platform.getUserLocation();
      String path = user.getURL().getPath();
      File file =
            new File(
                  path + File.separator + "org.eclipse.osee.ote.ui.test.manager" + File.separator + this.getClass().getName() + ".xml");
      file.getParentFile().mkdirs();
      return file;
   }

   private void loadFromFile() {
      keyValue.clear();
      Document document;
      try {
         document = Jaxp.readXmlDocument(getFile());
         NodeList viewList = document.getElementsByTagName("Pair");
         for (int index = 0; index < viewList.getLength(); index++) {
            Node node = viewList.item(index);
            if (node != null && node instanceof Element) {
               Element element = (Element) node;
               String key = Jaxp.getChildText(element, "Key");
               String value = Jaxp.getChildText(element, "Value");
               if (key != null && value != null && !key.equals("") && !value.equals("")) {
                  keyValue.put(key, value);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
   }

   private void saveToFile() throws ParserConfigurationException, TransformerException, IOException {
      Document document = Jaxp.newDocument();
      Element root = document.createElement("ValuePairs");
      document.appendChild(root);

      Iterator<String> it = keyValue.keySet().iterator();
      while (it.hasNext()) {
         String key = (String) it.next();
         String value = keyValue.get(key);
         if (key != null && value != null && !key.equals("") && !value.equals("")) {
            Element pair = document.createElement("Pair");

            Element keyElement = document.createElement("Key");
            keyElement.setTextContent(key);
            pair.appendChild(keyElement);

            Element valueElement = document.createElement("Value");
            valueElement.setTextContent(value);
            pair.appendChild(valueElement);
            root.appendChild(pair);
         }
      }

      Jaxp.writeXmlDocument(document, getFile(), Jaxp.getPrettyFormat(document));
   }
}
