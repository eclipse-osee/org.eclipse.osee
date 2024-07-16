/*********************************************************************
 * Copyright (c) 2015 Boeing
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

/**
 * @author Donald G. Dunne
 */
public class OseeClient {

   public static final String OSEE_APPLICATION_SERVER = "osee.application.server";
   public static final String OSEE_APPLICATION_SERVER_DATA = "osee.application.server.data";
   public static final int PORT = 8089;
   public static final String DEFAULT_URL = "http://localhost:" + PORT;

   private OseeClient() {
      // utility class
   }

   public static String getOseeApplicationServer() {
      return System.getProperty(OSEE_APPLICATION_SERVER, DEFAULT_URL);
   }

   public static int getPort() {
      String[] splitForPort = getOseeApplicationServer().split(":");
      return splitForPort.length == 3 ? Integer.valueOf(splitForPort[2]) : PORT;
   }
}