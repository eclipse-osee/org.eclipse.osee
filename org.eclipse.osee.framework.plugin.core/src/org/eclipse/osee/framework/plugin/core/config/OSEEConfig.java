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

package org.eclipse.osee.framework.plugin.core.config;

import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.OSEE_CONFIG_FILE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrew M. Finkbeiner
 */
public class OSEEConfig {

   private String[] serviceLookups = new String[0];
   private String[] serviceGroups = null;
   private boolean disableRemoteEvents = false;
   private OseeRunMode runMode;
   private String[][] bookmarks;
   private String authenticationProvider;
   private String remoteHttpServer;

   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(OSEEConfig.class);

   private static OSEEConfig signleton = null;

   private OSEEConfig() {
      super();
      try {
         File file = getConfigFile();
         logger.log(Level.INFO, "Using config file: " + file.getAbsolutePath());
         Document document = null;

         document = Jaxp.readXmlDocument(file);

         Element rootElement = document.getDocumentElement();
         parseServiceLookup(rootElement);
         parseDisableRemoteEvents(rootElement);
         parseMode(rootElement);
         parseLogger(rootElement);
         parseWebServers(rootElement);
         parseAuthenticationScheme(rootElement);
         parseHttpServer(rootElement);

         
         serviceGroups = JiniLookupGroupConfig.getOseeJiniServiceGroups();
         
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage());
         throw new IllegalStateException(ex.getLocalizedMessage());
      }
   }

   private File getConfigFile() throws Exception {
      File toReturn = null;
      String configPath = System.getProperty(OSEE_CONFIG_FILE);
      if (Strings.isValid(configPath)) {
         File fromProp = new File(configPath);
         if (fromProp.exists() && fromProp.isFile() && fromProp.canRead()) {
            toReturn = fromProp;
         }
      } else {
         toReturn = getFileFromExtensionPoint();
      }
      if (toReturn == null) {
         throw new Exception(
               String.format("Unable to find a valid config file.%s", Strings.isValid(configPath) ? String.format(
                     " As speficied by -D%s=[%s]", OSEE_CONFIG_FILE, configPath) : ""));
      }
      return toReturn;
   }

   /**
    * @return
    * @throws IOException
    */
   private File getFileFromExtensionPoint() throws Exception {
      List<File> toReturn = new ArrayList<File>();
      IExtensionPoint expt =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.plugin.core.OseeConfigFile");
      if (expt != null) {
         IExtension exs[] = expt.getExtensions();
         for (IExtension ex : exs) {
            IConfigurationElement[] els = ex.getConfigurationElements();
            for (IConfigurationElement el : els) {
               if (el.getName().equals("OseeConfig")) {
                  String file = el.getAttribute("file");
                  Bundle bundle = Platform.getBundle(ex.getContributor().getName());

                  URL url = FileLocator.find(bundle, new Path(file), null);
                  url = FileLocator.toFileURL(url);
                  String path = url.getFile();
                  toReturn.add(new File(path));
               }
            }
         }
      }

      if (toReturn.isEmpty()) {
         throw new Exception(
               String.format(
                     "Unable to locate osee config file. Please check plugin configuration or specify a config file to use via [-D%s]",
                     OSEE_CONFIG_FILE));
         //      } else if (toReturn.size() > 1) {
         //         throw new Exception(String.format("More than one osee config file specified via extensions. %s", toReturn));
      }
      return toReturn.get(0);
   }

   private void parseServiceLookup(Element rootElement) {

      Element serviceLookup = Jaxp.getChild(rootElement, "ServiceLookup");
      if (serviceLookup != null) {
         NodeList location = serviceLookup.getElementsByTagName("Location");
         serviceLookups = new String[location.getLength()];
         for (int i = 0; i < location.getLength(); i++) {
            serviceLookups[i] = location.item(i).getTextContent().trim();
         }
      }
   }

   private void parseDisableRemoteEvents(Element rootElement) {
      Element disableRemoteEventsElement = Jaxp.getChild(rootElement, "DisableRemoteEvents");
      if (disableRemoteEventsElement != null) {
         disableRemoteEvents = true;
         logger.log(Level.SEVERE, "Remote Events Disabled");
      }
   }

   private void parseMode(Element rootElement) {
      String mode = Jaxp.getChildTextTrim(rootElement, "Mode");
      if (mode == null) {
         runMode = OseeRunMode.Production;
      } else {
         try {
            runMode = OseeRunMode.getEnum(mode);
         } catch (Exception ex) {
            runMode = OseeRunMode.Production;
            logger.log(Level.SEVERE, "Run mode defaulted to production", ex);
         }
      }
   }

   private void parseLogger(Element rootElement) {
      NodeList list = rootElement.getElementsByTagName("Logger");
      for (int i = 0; i < list.getLength(); i++) {
         Element el = (Element) list.item(i);
         String name = Jaxp.getChildTextTrim(el, "Name");
         String level = Jaxp.getChildTextTrim(el, "Level");

         Logger.getLogger(name).setLevel(Level.parse(level.toUpperCase()));

      }
   }

   private void parseWebServers(Element rootElement) {
      NodeList list = rootElement.getElementsByTagName("Bookmarks");
      for (int i = 0; i < list.getLength(); i++) {
         NodeList servers = rootElement.getElementsByTagName("site");
         bookmarks = new String[servers.getLength()][2];
         for (int j = 0; j < servers.getLength(); j++) {
            Element el = (Element) servers.item(j);
            NamedNodeMap node = el.getAttributes();
            if (node != null) {
               Node idNode = node.getNamedItem("name");
               Node urlNode = node.getNamedItem("url");
               if (idNode != null && urlNode != null) {
                  bookmarks[j][0] = idNode.getTextContent();
                  bookmarks[j][1] = urlNode.getTextContent();
               }
            }
         }
      }
   }

   private void parseHttpServer(Element rootElement) {
      String addressToUse = OseeProperties.getInstance().getRemoteHttpServer();
      if (Strings.isValid(addressToUse) != true) {
         NodeList list = rootElement.getElementsByTagName("HttpServer");
         for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            if (element != null) {
               String value = element.getAttribute("address");
               String port = element.getAttribute("port");
               if (Strings.isValid(value) && Strings.isValid(port)) {
                  addressToUse = value + ":" + port;
               }
            }
         }
      }
      if (Strings.isValid(addressToUse)) {
         remoteHttpServer = addressToUse;
      }
   }

   private void parseAuthenticationScheme(Element rootElement) {
      if (OseeProperties.getInstance().getAuthenticationProviderId() != null) {
         authenticationProvider = OseeProperties.getInstance().getAuthenticationProviderId();
         return;
      }
      NodeList list = rootElement.getElementsByTagName("AuthenticationProvider");
      for (int i = 0; i < list.getLength(); i++) {
         Element element = (Element) list.item(i);
         if (element != null) {
            String value = element.getAttribute("id");
            if (value != null && value.length() > 0) {
               authenticationProvider = value;
            }
         }
      }
   }

   protected static OSEEConfig getInstance() {
      if (signleton == null) {
         signleton = new OSEEConfig();
      }
      return signleton;
   }

   /**
    * @return Returns the serviceLookups.
    */
   public String[] getServiceLookups() {
      return serviceLookups;
   }

   public String[] getJiniServiceGroups() {
      return serviceGroups;
   }

   public OseeRunMode getRunMode() {
      return runMode;
   }

   public boolean isDisableRemoteEvents() {
      return disableRemoteEvents;
   }

   public String[][] getBookmarks() {
      return bookmarks;
   }

   public String getRemoteHttpServer() {
      return remoteHttpServer != null ? remoteHttpServer : "";
   }

   public String getAuthenticationProviderId() {
      return authenticationProvider;
   }
}