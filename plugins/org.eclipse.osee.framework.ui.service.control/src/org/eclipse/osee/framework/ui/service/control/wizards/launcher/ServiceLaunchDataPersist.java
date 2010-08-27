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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osgi.service.datalocation.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrew M. Finkbeiner
 */
public class ServiceLaunchDataPersist {

   private static ServiceLaunchDataPersist instance = null;
   private static final String ROOT_ELEMENT = "ServiceLaunchData";
   private static final String HOST_ELEMENT = "Host";
   private static final String LAST_SERVICE_ELEMENT = "LastSelectedService";

   private final List<String> hosts;
   private String lastServiceLaunched;

   private ServiceLaunchDataPersist() {
      hosts = new ArrayList<String>();
      lastServiceLaunched = "";
      parseFile(read());
   }

   public static ServiceLaunchDataPersist getInstance() {
      if (instance == null) {
         instance = new ServiceLaunchDataPersist();
      }
      return instance;
   }

   private File getFile() {
      Location user = Platform.getUserLocation();
      String path = user.getURL().getPath();
      File file = new File(path + File.separator + this.getClass().getCanonicalName() + ".xml");
      file.getParentFile().mkdirs();
      return file;
   }

   public List<String> getHosts() {
      return hosts;
   }

   private void parseFile(Document document) {
      hosts.clear();
      lastServiceLaunched = "";
      if (document != null) {
         NodeList viewList = document.getElementsByTagName(HOST_ELEMENT);
         for (int i = 0; i < viewList.getLength(); i++) {
            Node node = viewList.item(i);
            String value = node.getTextContent();
            if (Strings.isValid(value)) {
               hosts.add(value);
            }
         }
         NodeList lastService = document.getElementsByTagName(LAST_SERVICE_ELEMENT);
         if (lastService.getLength() == 1) {
            Node node = lastService.item(0);
            String value = node.getTextContent();
            if (Strings.isValid(value)) {
               lastServiceLaunched = value;
            }
         }
      }
   }

   private Document read() {
      Document document = null;
      File file = getFile();
      try {
         if (file.exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);
         }
      } catch (Exception ex) {
         OseeLog.log(ControlPlugin.class, Level.SEVERE, "Error reading File [" + file.getAbsolutePath() + "] ", ex);
      }
      return document;
   }

   public void saveHostName(String addhost) {
      if (Strings.isValid(addhost) && !hosts.contains(addhost)) {
         hosts.add(addhost);
      }
      saveFile();
   }

   public String getLastServiceLaunched() {
      return lastServiceLaunched;
   }

   public void saveLastServiceLaunched(String lastServiceLaunched) {
      this.lastServiceLaunched = lastServiceLaunched;
      saveFile();
   }

   private void saveFile() {
      if (hosts.size() > 0 || Strings.isValid(lastServiceLaunched)) {
         File fileString = getFile();
         OutputStream outputStream = null;
         try {
            outputStream = new BufferedOutputStream(new FileOutputStream(fileString));

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream);

            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement(ROOT_ELEMENT);

            for (String host : hosts) {
               writer.writeStartElement(HOST_ELEMENT);
               writer.writeCharacters(host);
               writer.writeEndElement();
            }

            if (Strings.isValid(lastServiceLaunched)) {
               writer.writeStartElement(LAST_SERVICE_ELEMENT);
               writer.writeCharacters(this.lastServiceLaunched.trim());
               writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();

         } catch (FileNotFoundException ex) {
            OseeLog.log(ControlPlugin.class, Level.SEVERE, "File error [" + fileString + "] ", ex);
         } catch (XMLStreamException ex) {
            OseeLog.log(ControlPlugin.class, Level.SEVERE, "Error writing to File [" + fileString + "] ", ex);
         } finally {
            Lib.close(outputStream);
         }
      }
   }
}
