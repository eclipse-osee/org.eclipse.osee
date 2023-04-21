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

package org.eclipse.osee.mim;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Dominic Guss
 */
public class MimSwaggerGenerator {
   private static final String definitionPath = "../org.eclipse.osee.web.ui/src/swagger/definitions/";
   // Only one period in the definition file name is supported
   private static final String definitionFile = "org_eclipse_osee_mim.json";
   private static final String infoTitle = "Define MIM Endpoint Definitions";
   private static final String infoDescription = "Allows interactive support for MIM API endpoints.";
   private static final String serverUrl = "/mim";
   private static final String serverDescription = "MIM";

   public static void main(String[] args) {

      Set<Class<?>> allClasses = Lib.getAllClassesUnderPackage("org.eclipse.osee.mim");
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

      // Add searchable tagging support to groups of endpoints
      Map<String, PathItem> taggedPaths =
         openAPI.getPaths().entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(),
            addTagsToPathItem(entry.getKey(), entry.getValue()))).collect(
               Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      Paths paths = new Paths();
      paths.putAll(taggedPaths);
      openAPI.setPaths(paths);

      try (FileWriter fr = new FileWriter(definitionPath + definitionFile)) {
         fr.write(Json.mapper().writeValueAsString(openAPI));
      } catch (JsonProcessingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

      System.out.println("Swagger " + definitionFile + " definitions file created.");
      System.out.println("");
   }

   private static PathItem addTagsToPathItem(String path, PathItem pathItem) {
      String pathElements[] = path.split("/");

      if (pathItem.getGet() != null) {
         pathItem.getGet().addTagsItem(pathElements[1]);
      }
      if (pathItem.getDelete() != null) {
         pathItem.getDelete().addTagsItem(pathElements[1]);
      }
      if (pathItem.getHead() != null) {
         pathItem.getHead().addTagsItem(pathElements[1]);
      }
      if (pathItem.getPatch() != null) {
         pathItem.getPatch().addTagsItem(pathElements[1]);
      }
      if (pathItem.getPost() != null) {
         pathItem.getPost().addTagsItem(pathElements[1]);
      }
      if (pathItem.getPut() != null) {
         pathItem.getPut().addTagsItem(pathElements[1]);
      }
      if (pathItem.getTrace() != null) {
         pathItem.getTrace().addTagsItem(pathElements[1]);
      }
      if (pathItem.getOptions() != null) {
         pathItem.getOptions().addTagsItem(pathElements[1]);
      }
      return pathItem;
   }
}
