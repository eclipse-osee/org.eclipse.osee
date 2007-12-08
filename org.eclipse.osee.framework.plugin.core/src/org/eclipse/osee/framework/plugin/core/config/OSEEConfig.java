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

import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.DEFAULT_DB_CONNECTION;
import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.OSEE_CONFIG_FILE;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.JiniPlugin;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.data.DbInformation;
import org.eclipse.osee.framework.plugin.core.config.data.ServerConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.data.DbDetailData.ConfigField;
import org.eclipse.osee.framework.plugin.core.config.data.DbSetupData.ServerInfoFields;
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
   private boolean bypassSecurity = false;
   private boolean disableRemoteEvents = false;

   private String defaultWorkspace;
   private OseeRunMode runMode;
   private DbInformation databaseService;
   private String[][] bookmarks;
   private String mySqlInstallLocation;
   private String authenticationProvider;

   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(OSEEConfig.class);

   private static OSEEConfig signleton = null;

   private OSEEConfig(String configLocation) {
      super();
      try {
         logger.log(Level.INFO, "Using config file: " + configLocation);
         Document document = null;

         try {
            document = Jaxp.readXmlDocument(new File(configLocation));
         } catch (Exception ex) {
            document = Jaxp.readXmlDocumentFromResource(this.getClass(), configLocation);
         }

         Element rootElement = document.getDocumentElement();
         parseDefaultWorkspace(rootElement);
         parseServiceLookup(rootElement);
         parseBypassSecurity(rootElement);
         parseDisableRemoteEvents(rootElement);
         parseMode(rootElement);
         parseLogger(rootElement);
         ServerConfigUtil.getInstance().parseDatabaseConfigFile(rootElement);
         getDefaultClientData();
         parseWebServers(rootElement);
         parseAuthenticationScheme(rootElement);
         parseMySqlInstall(rootElement);

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
            System.setProperty(OseeProperties.OSEE_JINI_SERVICE_GROUPS, toStore);
         }
      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "There was an error parsing the config file.", ex);
      }
   }

   private void parseMySqlInstall(Element rootElement) {
      NodeList list = rootElement.getElementsByTagName("MySqlInfo");
      if (list.getLength() > 0) {
         Element el = (Element) list.item(0);
         this.mySqlInstallLocation = el.getAttribute("InstallLocation");
      } else {
         this.mySqlInstallLocation = null;
      }
   }

   private void parseDefaultWorkspace(Element rootElement) {
      defaultWorkspace = Jaxp.getChildTextTrim(rootElement, "DefaultWorkspace");
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

   private void parseBypassSecurity(Element rootElement) {
      Element bypassSecurityElement = Jaxp.getChild(rootElement, "BypassSecurity");
      if (bypassSecurityElement != null) {
         bypassSecurity = true;
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
         String configPath = System.getProperty(OSEE_CONFIG_FILE);
         signleton = new OSEEConfig(configPath);
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
    * @return Returns the defaultWorkspace.
    */
   public String getDefaultWorkspace() {
      return defaultWorkspace;
   }

   /**
    * @return Returns the bypassSecurity.
    */
   public boolean isBypassSecurity() {
      return bypassSecurity;
   }

   public SupportedDatabase getDBType() {
      return databaseService.getDatabaseDetails().getDbType();

   }

   public String getPassword() {
      return databaseService.getDatabaseDetails().getFieldValue(ConfigField.Password);
   }

   public String getPort() {
      return databaseService.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.port);
   }

   public String getServer() {
      return databaseService.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.hostAddress);
   }

   public String getServiceID() {
      return databaseService.getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
   }

   public String getUser() {
      return databaseService.getDatabaseDetails().getFieldValue(ConfigField.UserName);
   }

   public DbInformation getDatabaseService(String servicesId) {
      return ServerConfigUtil.getInstance().getService(servicesId);
   }

   public DbInformation getDefaultClientData() {
      if (databaseService == null) {
         databaseService = ServerConfigUtil.getInstance().getDefaultService();
      }
      return databaseService;
   }

   public DbInformation[] getAllDbInformation() {
      return ServerConfigUtil.getInstance().getAllDbServices();
   }

   public void setDefaultClientData(String id) {
      System.setProperty(DEFAULT_DB_CONNECTION, id);
      databaseService = ServerConfigUtil.getInstance().getDefaultService();
   }

   public boolean isDisableRemoteEvents() {
      return disableRemoteEvents;
   }

   public void setDisableRemoteEvents(boolean disableRemoteEvents) {
      this.disableRemoteEvents = disableRemoteEvents;
   }

   public String[][] getBookmarks() {
      return bookmarks;
   }

   public URL getBookmark(String name) throws MalformedURLException {
      URL toReturn = null;
      String[][] bookmarks = getBookmarks();
      if (bookmarks != null) {
         for (int i = 0; i < bookmarks.length; i++) {
            String id = bookmarks[i][0];
            if (id.equals(name)) {
               return new URL(bookmarks[i][1]);
            }
         }
      }
      return toReturn;
   }

   public String getMySqlInstallLocation() {
      return mySqlInstallLocation;
   }

   public String getAuthenticationProviderId() {
      return authenticationProvider;
   }
}