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
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osgi.service.datalocation.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Andrew M. Finkbeiner
 */
public class ServiceLaunchDataPersist {

   private static ServiceLaunchDataPersist instance = null;
   private static final String ROOT_ELEMENT = "ServiceLaunchData";
   private static final String HOST_ELEMENT = "Host";
   private static final String LAST_SERVICE_ELEMENT = "LastSelectedService";

   private List<String> hosts;
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
            if (value != null && !value.equals("")) {
               hosts.add(value);
            }
         }
         NodeList lastService = document.getElementsByTagName(LAST_SERVICE_ELEMENT);
         if (lastService.getLength() == 1) {
            Node node = lastService.item(0);
            String value = node.getTextContent();
            if (value != null && !value.equals("")) {
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
      if (addhost != null && !addhost.equals("") && !hosts.contains(addhost)) {
         hosts.add(addhost);
      }
      saveFile();
   }

   private void write(Element root, File fileString) {
      OutputFormat outputFormat;
      OutputStreamWriter out = null;
      try {
         OutputStream bout = new BufferedOutputStream(new FileOutputStream(fileString));
         out = new OutputStreamWriter(bout);

         outputFormat = new OutputFormat("XML", "UTF-8", true);
         XMLSerializer xmlSerializer = new XMLSerializer(out, outputFormat);
         xmlSerializer.serialize(root);
         out.flush();
      } catch (FileNotFoundException ex) {
         OseeLog.log(ControlPlugin.class, Level.SEVERE, "File error [" + fileString + "] ", ex);
      } catch (IOException ex) {
         OseeLog.log(ControlPlugin.class, Level.SEVERE, "Error writing to File [" + fileString + "] ", ex);
      } finally {
         try {
            out.close();
         } catch (IOException ex) {
            OseeLog.log(ControlPlugin.class, Level.SEVERE, "Error closing stream [" + fileString + "] ", ex);
         }
      }
   }

   public String getLastServiceLaunched() {
      return lastServiceLaunched;
   }

   public void saveLastServiceLaunched(String lastServiceLaunched) {
      this.lastServiceLaunched = lastServiceLaunched;
      saveFile();
   }

   private void saveFile() {
      if (hosts.size() > 0 || (lastServiceLaunched != null && !lastServiceLaunched.equals(""))) {

         Document xmlDoc;
         try {
            xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = xmlDoc.createElement(ROOT_ELEMENT);
            xmlDoc.appendChild(rootElement);

            Element hostElement = null;
            for (String host : hosts) {
               hostElement = xmlDoc.createElement(HOST_ELEMENT);
               hostElement.setTextContent(host);
               rootElement.appendChild(hostElement);
            }

            if (lastServiceLaunched != null && !lastServiceLaunched.equals("")) {
               Element lastServiceElement = xmlDoc.createElement(LAST_SERVICE_ELEMENT);
               lastServiceElement.setTextContent(this.lastServiceLaunched.trim());
               rootElement.appendChild(lastServiceElement);
            }
            write(rootElement, getFile());
         } catch (ParserConfigurationException ex) {
            OseeLog.log(ControlPlugin.class, Level.SEVERE, "Error saving File [" + getFile() + "] ", ex);
         }

      }
   }
}
