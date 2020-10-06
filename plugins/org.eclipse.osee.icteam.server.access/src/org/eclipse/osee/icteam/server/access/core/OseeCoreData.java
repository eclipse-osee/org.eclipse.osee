/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.server.access.core;

import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * The class provides service to register the values for OrcsApi and atsServer once the server is launched
 *
 * @author Ajay Chandrahasan
 */
public class OseeCoreData {
   private static OrcsApi orcsApi;
   private static AtsApiServer atsServer;
   //    private final Set<Object> singletons = new HashSet<>();

   /**
    * Sets the value of OrcsApi used in org.eclipse.osee.icteam.server.access.xml
    *
    * @param orcsApi
    */
   public void setOrcsApi(final OrcsApi orcsApi) {
      OseeCoreData.orcsApi = orcsApi;
   }

   /**
    * To get OrcsApi value
    *
    * @return orcsApi value
    */
   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

   /**
    * Sets the value of atsServer used in org.eclipse.osee.icteam.icteam.server.access.xml
    *
    * @param atsServer
    */
   public void setAtsServer(final AtsApiServer atsServer) {
      OseeCoreData.atsServer = atsServer;
   }

   /**
    * To get atsServer value
    *
    * @return atsServer value
    */
   public static AtsApiServer getAtsServer() {
      return atsServer;
   }
}