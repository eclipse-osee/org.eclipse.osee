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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.JiniPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.data.DbInformation;
import org.eclipse.osee.framework.plugin.core.config.data.ServerConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.data.DbDetailData.ConfigField;
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

   private String[] serviceLookups = null;
   private String[] serviceGroups = null;
   //   private boolean bypassSecurity = false;
   private boolean disableRemoteEvents = false;

   //   private String defaultWorkspace;
   private OseeRunMode runMode;
   private DbInformation databaseService;
   private String[][] bookmarks;
   //   private String mySqlInstallLocation;
   private String authenticationProvider;
   private String remoteHttpServer;

   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(OSEEConfig.class);

   private static OSEEConfig signleton = null;

   private OSEEConfig() {
      super();
      try {
         File file = getConfigFileFromProperty();
         if (file == null) {
            file = getFileFromExtensionPoint();
         }
         if (file == null) {
            throw new NullPointerException("Unable to find a valid config file.");
         }

         logger.log(Level.INFO, "Using config file: " + file.getAbsolutePath());
         Document document = null;

         document = Jaxp.readXmlDocument(file);

         Element rootElement = document.getDocumentElement();
         parseServiceLookup(rootElement);
         parseDisableRemoteEvents(rootElement);
         parseMode(rootElement);
         parseLogger(rootElement);
         ServerConfigUtil.getInstance().parseDatabaseConfigFile(rootElement);
         getDefaultDatabaseService();
         parseWebServers(rootElement);
         parseAuthenticationScheme(rootElement);
         parseHttpServer(rootElement);

         serviceGroups = JiniPlugin.getInstance().getJiniVersion();
         String[] filterGroups = OseeProperties.getInstance().getOseeJiniServiceGroups();
         if (filterGroups != null && filterGroups.length > 0) {
            serviceGroups = filterGroups;
         }

         if (serviceGroups == null || serviceGroups.length == 0) {
            logger.log(
                  Level.SEVERE,
                  "[-D" + OseeProperties.OSEE_JINI_SERVICE_GROUPS + "] was not set.\n" + "Please enter the Jini Group this service register with.");
            System.exit(1);
         } else {
            String toStore = "";
            for (int index = 0; index < serviceGroups.length; index++) {
               toStore += serviceGroups[index];
               if (index + 1 < serviceGroups.length) {
                  toStore += ",";
               }
            }
            logger.log(Level.INFO, "osee.jini.lookup.groups: " + toStore);
            System.setProperty(OseeProperties.OSEE_JINI_SERVICE_GROUPS, toStore);
         }
      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "There was an error parsing the config file.", ex);
      }
   }

   /**
    * @return
    * @throws IOException
    */
   private File getFileFromExtensionPoint() throws IOException {
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
                  return new File(path);
               }
            }
         }
      }
      return null;
   }

   /**
    * @return
    */
   private File getConfigFileFromProperty() {
      String configPath = System.getProperty(OSEE_CONFIG_FILE);
      if (configPath != null) {
         File fromProp = new File(configPath);
         if (fromProp.exists() && fromProp.isFile()) {
            return fromProp;
         }
      }
      return null;
   }

   //   private void parseMySqlInstall(Element rootElement) {
   //      NodeList list = rootElement.getElementsByTagName("MySqlInfo");
   //      if (list.getLength() > 0) {
   //         Element el = (Element) list.item(0);
   //         this.mySqlInstallLocation = el.getAttribute("InstallLocation");
   //      } else {
   //         this.mySqlInstallLocation = null;
   //      }
   //   }

   //   private void parseDefaultWorkspace(Element rootElement) {
   //      defaultWorkspace = Jaxp.getChildTextTrim(rootElement, "DefaultWorkspace");
   //   }

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

   //   private void parseBypassSecurity(Element rootElement) {
   //      Element bypassSecurityElement = Jaxp.getChild(rootElement, "BypassSecurity");
   //      if (bypassSecurityElement != null) {
   //         bypassSecurity = true;
   //      }
   //   }

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

   /**
    * Get the configured database type. If we don't have a database configured the default value returned is
    * SupportedDatabase.oracle.
    */
   public SupportedDatabase getDBType() {
      if (databaseService != null && databaseService.getDatabaseDetails() != null) {
         return databaseService.getDatabaseDetails().getDbType();
      } else {
         return SupportedDatabase.oracle;
      }

   }

   //   public String getPassword() {
   //      return databaseService.getDatabaseDetails().getFieldValue(ConfigField.Password);
   //   }

   //   public String getPort() {
   //      return databaseService.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.port);
   //   }

   //   public String getServer() {
   //      return databaseService.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.hostAddress);
   //   }

   public String getServiceID() {
      return databaseService.getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
   }

   public String getUser() {
      return databaseService.getDatabaseDetails().getFieldValue(ConfigField.UserName);
   }

   public DbInformation getDatabaseService(String servicesId) {
      return ServerConfigUtil.getInstance().getService(servicesId);
   }

   //getDefaultDatabaseService
   public DbInformation getDefaultDatabaseService() {
      if (databaseService == null) {
         databaseService = ServerConfigUtil.getInstance().getDefaultService();
      }
      return databaseService;
   }

   public void setDefaultDatabaseService(String serviceName) {
      databaseService = getDatabaseService(serviceName);
   }

   //   public DbInformation[] getAllDbInformation() {
   //      return ServerConfigUtil.getInstance().getAllDbServices();
   //   }

   //   public void setDefaultClientData(String id) {
   //      System.setProperty(DEFAULT_DB_CONNECTION, id);
   //      databaseService = ServerConfigUtil.getInstance().getDefaultService();
   //   }

   public boolean isDisableRemoteEvents() {
      return disableRemoteEvents;
   }

   //   public void setDisableRemoteEvents(boolean disableRemoteEvents) {
   //      this.disableRemoteEvents = disableRemoteEvents;
   //   }

   public String[][] getBookmarks() {
      return bookmarks;
   }

   public String getRemoteHttpServer() {
      return remoteHttpServer != null ? remoteHttpServer : "";
   }

   //   public URL getBookmark(String name) throws MalformedURLException {
   //      URL toReturn = null;
   //      String[][] bookmarks = getBookmarks();
   //      if (bookmarks != null) {
   //         for (int i = 0; i < bookmarks.length; i++) {
   //            String id = bookmarks[i][0];
   //            if (id.equals(name)) {
   //               return new URL(bookmarks[i][1]);
   //            }
   //         }
   //      }
   //      return toReturn;
   //   }

   //   public String getMySqlInstallLocation() {
   //      return mySqlInstallLocation;
   //   }

   public String getAuthenticationProviderId() {
      return authenticationProvider;
   }
}