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
package org.eclipse.osee.framework.db.connection.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class DbSetupData {

   public enum ServicesFields {
      Server;
   }

   public enum ServerInfoFields {
      id, dbInfo, hostAddress, port, connectsWith, isDefault, serverConfig, applicationServer;
   }

   Map<ServerInfoFields, String> serverFieldMap;
   private boolean isDefault;

   public DbSetupData() {
      this.serverFieldMap = new HashMap<ServerInfoFields, String>();
      this.isDefault = false;
   }

   public void addServerInfo(ServerInfoFields field, String value) {
      if (field.equals(ServerInfoFields.isDefault)) {
         isDefault = Boolean.parseBoolean(value);
      }
      serverFieldMap.put(field, value);
   }

   public boolean isDefault() {
      return isDefault;
   }

   public String getServerInfoValue(ServerInfoFields field) {
      if (serverFieldMap.containsKey(field)) {
         return serverFieldMap.get(field);
      }
      return "";
   }

   public String getId() {
      return serverFieldMap.get(ServerInfoFields.id);
   }

   public String getDbInfo() {
      return serverFieldMap.get(ServerInfoFields.dbInfo);
   }

   public String toString() {
      String toReturn = "Service: \n";
      Set<ServerInfoFields> keys = serverFieldMap.keySet();
      for (ServerInfoFields field : keys) {
         String value = serverFieldMap.get(field);
         toReturn += field + ": " + value + " ";
      }
      return toReturn;
   }

   public Map<ServerInfoFields, String> getServerFieldMap() {
      return serverFieldMap;
   }
}
