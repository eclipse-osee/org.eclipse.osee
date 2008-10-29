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
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Properties;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerInfo {
   private static final String SERVER_ADDRESS = "serverAddress";
   private static final String PORT = "port";
   private static final String VERSION = "version";
   private static final String DATE_CREATED = "creationDate";
   protected static final String IS_ACCEPTING_REQUESTS = "isAcceptingRequests";

   protected final Properties properties;

   private OseeServerInfo() {
      super();
      this.properties = new Properties();
   }

   /**
    * @param port
    * @param serverAddress
    * @param version
    */
   protected OseeServerInfo(String serverAddress, int port, String version, Timestamp dateStarted, boolean isAcceptingRequests) {
      this();
      this.properties.put(SERVER_ADDRESS, serverAddress);
      this.properties.put(PORT, port);
      this.properties.put(VERSION, version);
      this.properties.put(DATE_CREATED, dateStarted);
      this.properties.put(IS_ACCEPTING_REQUESTS, isAcceptingRequests);
   }

   /**
    * @return the serverAddress
    */
   public String getServerAddress() {
      return properties.getProperty(SERVER_ADDRESS);
   }

   /**
    * @return the port
    */
   public int getPort() {
      return (Integer) properties.get(PORT);
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return properties.getProperty(VERSION);
   }

   /**
    * @return whether requests are accepted
    */
   public boolean isAcceptingRequests() {
      return (Boolean) properties.get(IS_ACCEPTING_REQUESTS);
   }

   /**
    * @return when server was launched
    */
   public Timestamp getDateStarted() {
      return (Timestamp) properties.get(DATE_CREATED);
   }

   /**
    * Write to output stream
    * 
    * @param outputStream
    * @throws OseeWrappedException
    */
   public void write(OutputStream outputStream) throws OseeWrappedException {
      try {
         properties.storeToXML(outputStream, "Application Server Info", "UTF-8");
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   /**
    * Create new instance from XML input
    * 
    * @param OseeServerInfo the new instance
    * @throws OseeWrappedException
    */
   public static OseeServerInfo fromXml(InputStream inputStream) throws OseeWrappedException {
      OseeServerInfo serverInfo = new OseeServerInfo();
      try {
         serverInfo.properties.loadFromXML(inputStream);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      return serverInfo;
   }
}
