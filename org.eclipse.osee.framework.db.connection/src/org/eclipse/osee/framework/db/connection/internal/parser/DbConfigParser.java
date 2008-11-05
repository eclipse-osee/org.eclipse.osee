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
package org.eclipse.osee.framework.db.connection.internal.parser;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.internal.parser.DbInformation.DbObjectType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Roberto E. Escobar
 */
public class DbConfigParser {

   private final Map<String, DbDetailData> dbInfoMap;
   private final Map<String, DbSetupData> servicesMap;
   private final Map<String, DbConnectionData> connectionMap;

   private DbConfigParser() {
      dbInfoMap = new HashMap<String, DbDetailData>();
      servicesMap = new HashMap<String, DbSetupData>();
      connectionMap = new HashMap<String, DbConnectionData>();
   }

   public static DbInformation[] parse(Element rootElement) {
      DbConfigParser worker = new DbConfigParser();
      worker.parseDbInfo(rootElement);
      worker.parseDbService(rootElement);
      worker.parseDbConnection(rootElement);
      return worker.getAllDbServices();
   }

   private DbInformation[] getAllDbServices() {
      DbInformation[] info = new DbInformation[servicesMap.size()];
      int i = 0;
      for (String name : servicesMap.keySet()) {
         info[i] = buildDbInformation(name);
         i++;
      }
      return info;
   }

   private DbInformation buildDbInformation(String id) {
      DbSetupData serviceData = servicesMap.get(id);
      DbDetailData dbInfo = dbInfoMap.get(serviceData.getDbInfo());
      DbConnectionData connectionData =
            connectionMap.get(serviceData.getServerInfoValue(DbSetupData.ServerInfoFields.connectsWith));
      if (dbInfo != null && connectionData != null) {
         return new DbInformation(dbInfo, serviceData, connectionData);
      } else {
         return null;
      }
   }

   private void parseDbInfo(Element rootElement) {
      if (rootElement != null) {
         NodeList list = rootElement.getElementsByTagName(DbObjectType.DatabaseInfo.name());
         for (int i = 0; i < list.getLength(); i++) {
            DbDetailData dbInfo = new DbDetailData();
            Element element = (Element) list.item(i);
            NamedNodeMap map = element.getAttributes();
            if (map != null && map.getLength() != 0) {
               DbDetailData.DescriptionField[] descriptionFields = DbDetailData.DescriptionField.values();
               for (DbDetailData.DescriptionField descriptionField : descriptionFields) {
                  Node node = map.getNamedItem(descriptionField.name());
                  String value = node.getTextContent();
                  if (value != null && !value.equals("")) {
                     dbInfo.addDescription(descriptionField, value);
                  }
               }
            }
            DbDetailData.ConfigField[] configfields = DbDetailData.ConfigField.values();
            for (DbDetailData.ConfigField field : configfields) {
               Element fieldChild = Jaxp.getChild(element, field.name());
               if (fieldChild != null) {
                  NamedNodeMap configMap = fieldChild.getAttributes();
                  Node keyNode = configMap.getNamedItem(DbDetailData.ConfigPairField.key.name());
                  Node valueNode = configMap.getNamedItem(DbDetailData.ConfigPairField.value.name());
                  String key = null;
                  if (keyNode != null) {
                     key = keyNode.getTextContent();
                  }
                  String value = null;
                  if (valueNode != null) {
                     value = valueNode.getTextContent();
                  }
                  if (key != null && !key.equals("")) {
                     dbInfo.addConfigField(field, new Pair<String, String>(key, (value != null ? value : "")));
                  }

               }
            }
            dbInfoMap.put(dbInfo.getId(), dbInfo);
         }
      }
   }

   private void parseDbService(Element rootElement) {
      if (rootElement != null) {
         NodeList list = rootElement.getElementsByTagName(DbObjectType.AvailableDbServices.name());
         for (int i = 0; i < list.getLength(); i++) {

            Element element = (Element) list.item(i);

            NodeList serverList = element.getElementsByTagName(DbSetupData.ServicesFields.Server.name());
            for (int index = 0; index < serverList.getLength(); index++) {
               DbSetupData dbServicesData = new DbSetupData();
               Element serviceChild = (Element) serverList.item(index);
               NamedNodeMap map = serviceChild.getAttributes();
               if (map != null && map.getLength() != 0) {
                  DbSetupData.ServerInfoFields[] infoFields = DbSetupData.ServerInfoFields.values();
                  for (DbSetupData.ServerInfoFields infoField : infoFields) {
                     Node node = map.getNamedItem(infoField.name());
                     String value = null;
                     if (node != null) {
                        value = node.getTextContent();
                     }
                     if (value != null && !value.equals("")) {
                        dbServicesData.addServerInfo(infoField, value);
                     }
                  }
               }
               servicesMap.put(dbServicesData.getId(), dbServicesData);
            }
         }
      }
   }

   private void parseDbConnection(Element rootElement) {
      if (rootElement != null) {
         NodeList list = rootElement.getElementsByTagName(DbObjectType.ConnectionDescription.name());
         for (int i = 0; i < list.getLength(); i++) {
            DbConnectionData dbConnectionData = new DbConnectionData();
            Element element = (Element) list.item(i);

            NamedNodeMap map = element.getAttributes();
            if (map != null && map.getLength() != 0) {
               Node node = map.getNamedItem(DbConnectionData.DescriptionFields.id.name());
               if (node != null) {
                  String value = node.getTextContent();
                  if (value != null && !value.equals("")) {
                     dbConnectionData.setId(value);
                  }
               }
            }

            DbConnectionData.ConnectionFields[] connectionFields = DbConnectionData.ConnectionFields.values();
            for (DbConnectionData.ConnectionFields connectionField : connectionFields) {
               switch (connectionField) {
                  case Driver:
                     String driver = Jaxp.getChildText(element, connectionField.name());
                     if (driver != null && !driver.equals("")) {
                        dbConnectionData.setDBDriver(driver);
                     }
                     break;
                  case Url:
                     String url = Jaxp.getChildText(element, connectionField.name());
                     if (url != null && !url.equals("")) {
                        dbConnectionData.setRawUrl(url);
                     }
                     break;
                  case UrlAttributes:
                     Element urlAttributes =
                           Jaxp.getChild(element, DbConnectionData.ConnectionFields.UrlAttributes.name());
                     if (urlAttributes != null) {
                        NodeList attributes =
                              urlAttributes.getElementsByTagName(DbConnectionData.UrlAttributes.Entry.name());
                        if (attributes != null) {
                           for (int index = 0; index < attributes.getLength(); index++) {
                              Node node = attributes.item(index);
                              String attributeValue = node.getTextContent();
                              if (attributeValue != null && !attributeValue.equals("")) {
                                 dbConnectionData.addAttribute(attributeValue);
                              }
                           }
                        }
                     }
                     break;
                  case Property: {
                     NodeList nodes = element.getElementsByTagName(connectionField.name());
                     for (int j = 0; j < nodes.getLength(); j++) {
                        Element el = (Element) nodes.item(j);
                        dbConnectionData.getProperties().setProperty(el.getAttribute("key"), el.getAttribute("value"));
                     }
                  }
                     break;
                  default:
                     break;
               }
            }
            connectionMap.put(dbConnectionData.getId(), dbConnectionData);
         }
      }
   }
}
