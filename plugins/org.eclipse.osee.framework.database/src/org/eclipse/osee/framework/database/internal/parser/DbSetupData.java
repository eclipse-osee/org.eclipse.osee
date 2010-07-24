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
package org.eclipse.osee.framework.database.internal.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class DbSetupData implements Serializable {

   private static final long serialVersionUID = -7195682073850593321L;

   public enum ServicesFields {
      Server;
   }

   public enum ServerInfoFields {
      id,
      dbInfo,
      hostAddress,
      port,
      connectsWith,
      serverConfig,
      isProduction;
   }

   Map<ServerInfoFields, String> serverFieldMap;

   public DbSetupData() {
      this.serverFieldMap = new HashMap<ServerInfoFields, String>();
   }

   public void addServerInfo(ServerInfoFields field, String value) {
      serverFieldMap.put(field, value);
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

   @Override
   public String toString() {
      StringBuilder toReturn = new StringBuilder("Service: \n");
      Set<ServerInfoFields> keys = serverFieldMap.keySet();
      for (ServerInfoFields field : keys) {
         String value = serverFieldMap.get(field);
         toReturn.append(String.format("%s: %s ", field, value));
      }
      return toReturn.toString();
   }

   public Map<ServerInfoFields, String> getServerFieldMap() {
      return serverFieldMap;
   }
}
