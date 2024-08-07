/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.sql.Timestamp;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerInfo extends BaseExchangeData {
   private static final long serialVersionUID = 2696663265012016128L;
   private static final String[] EMPTY_ARRAY = new String[0];
   private static final String SERVER_URI = "uri";
   protected static final String VERSION = "version";
   private static final String DATE_CREATED = "creationDate";
   private static final String SERVER_ID = "serverId";
   protected static final String IS_ACCEPTING_REQUESTS = "isAcceptingRequests";
   private int port;

   public OseeServerInfo() {
      super();
   }

   public OseeServerInfo(String serverId, String uri, int port, String[] version, Timestamp dateStarted, boolean isAcceptingRequests) {
      this();
      this.backingData.put(SERVER_ID, serverId);
      this.backingData.put(SERVER_URI, uri);
      this.backingData.put(VERSION, version);
      this.backingData.put(DATE_CREATED, dateStarted.getTime());
      this.backingData.put(IS_ACCEPTING_REQUESTS, isAcceptingRequests);
      this.port = port;
   }

   /**
    * @return the serverId
    */
   public String getServerId() {
      return getString(SERVER_ID);
   }

   /**
    * @return the server uri
    */

   public String getUri() {
      return getString(SERVER_URI);
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
    * Create new instance from XML input
    *
    * @param OseeServerInfo the new instance
    */
   public static OseeServerInfo fromXml(InputStream inputStream) {
      OseeServerInfo serverInfo = new OseeServerInfo();
      serverInfo.loadfromXml(inputStream);
      return serverInfo;
   }

   public int getPort() {
      return port;
   }
}