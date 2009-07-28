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
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.sql.Timestamp;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerInfo extends BaseExchangeData {
   private static final long serialVersionUID = 2696663265012016128L;
   private static final String[] EMPTY_ARRAY = new String[0];
   private static final String SERVER_ADDRESS = "serverAddress";
   private static final String PORT = "port";
   protected static final String VERSION = "version";
   private static final String DATE_CREATED = "creationDate";
   private static final String SERVER_ID = "serverId";
   private static final String IS_ACCEPTING_REQUESTS = "isAcceptingRequests";

   private OseeServerInfo() {
      super();
   }

   public OseeServerInfo(String serverId, String serverAddress, int port, String[] version, Timestamp dateStarted, boolean isAcceptingRequests) {
      this();
      this.backingData.put(SERVER_ID, serverId);
      this.backingData.put(SERVER_ADDRESS, serverAddress);
      this.backingData.put(PORT, port);
      this.backingData.put(VERSION, version);
      this.backingData.put(DATE_CREATED, dateStarted.getTime());
      this.backingData.put(IS_ACCEPTING_REQUESTS, isAcceptingRequests);
   }

   /**
    * @return the serverId
    */
   public String getServerId() {
      return getString(SERVER_ID);
   }

   /**
    * @return the serverAddress
    */
   public String getServerAddress() {
      return getString(SERVER_ADDRESS);
   }

   /**
    * @return the port
    */
   public int getPort() {
      return backingData.getInt(PORT);
   }

   /**
    * @return the version
    */
   public String[] getVersion() {
      String[] toReturn = backingData.getArray(VERSION);
      return toReturn != null ? toReturn : EMPTY_ARRAY;
   }

   /**
    * @return whether requests are accepted
    */
   public boolean isAcceptingRequests() {
      return backingData.getBoolean(IS_ACCEPTING_REQUESTS);
   }

   /**
    * @return when server was launched
    */
   public Timestamp getDateStarted() {
      return new Timestamp(backingData.getLong(DATE_CREATED));
   }

   /**
    * @param whether server is accepting requests
    */
   public void setAcceptingRequests(boolean value) {
      backingData.put(IS_ACCEPTING_REQUESTS, value);
   }

   /**
    * Create new instance from XML input
    * 
    * @param OseeServerInfo the new instance
    * @throws OseeWrappedException
    */
   public static OseeServerInfo fromXml(InputStream inputStream) throws OseeWrappedException {
      OseeServerInfo serverInfo = new OseeServerInfo();
      serverInfo.loadfromXml(inputStream);
      return serverInfo;
   }
}
