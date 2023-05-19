/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.disposition.rest;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Dominic Guss
 */
public class DispoSwaggerGenerator {

   private static final String definitionPath = "../org.eclipse.osee.web.ui/src/swagger/definitions/";
   // Only one period in the definition file name is supported
   private static final String definitionFile = "org_eclipse_osee_disposition_rest.json";
   private static final String infoTitle = "Dispo API Endpoint Definitions";
   private static final String infoDescription = "Allows interactive support for Dispo API endpoints.";
   private static final String serverUrl = "/dispo";
   private static final String serverDescription = "Dispo";

   public static void main(String[] args) {

      Set<Class<?>> allClasses = Lib.getAllClassesUnderPackage("org.eclipse.osee.disposition.rest.resources");
      Set<Class<?>> swaggerClasses = new HashSet<Class<?>>();

      for (Class<?> clazz : allClasses) {
         if (clazz.isAnnotationPresent(Swagger.class)) {
            swaggerClasses.add(clazz);
         }
      }

      System.out.println("Creating Swagger " + definitionFile + " definitions file.  Please wait...");

      // Read in all applicable classes, creating initial Swagger openAPI definition object
      OpenAPI openAPI = new Reader(new OpenAPI()).read(swaggerClasses);

      Info info = new Info();
      info.setTitle(infoTitle);
      info.setDescription(infoDescription);
      openAPI.setInfo(info);

      Server server = new Server();
      server.setUrl(serverUrl);
      server.setDescription(serverDescription);
      openAPI.addServersItem(server);

      System.out.println("Swagger " + definitionFile + " definitions file created.");
      System.out.println("");
   }
}
