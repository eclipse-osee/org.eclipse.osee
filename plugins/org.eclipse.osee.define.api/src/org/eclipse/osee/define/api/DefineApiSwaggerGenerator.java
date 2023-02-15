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

package org.eclipse.osee.define.api;

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
import org.eclipse.osee.define.api.publishing.PublishingEndpoint;
import org.eclipse.osee.define.api.publishing.datarights.DataRightsEndpoint;
import org.eclipse.osee.define.api.publishing.templatemanager.TemplateManagerEndpoint;
import org.eclipse.osee.define.api.synchronization.SynchronizationEndpoint;

/**
 * @author Dominic Guss
 */
public class DefineApiSwaggerGenerator {
   private static final String definitionPath = "../org.eclipse.osee.web.ui/src/swagger/definitions/";

   public static void main(String[] args) {

      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(DefineBranchEndpointApi.class);
      classes.add(GitEndpoint.class);
      classes.add(ImportEndpoint.class);
      classes.add(TraceabilityEndpoint.class);
      classes.add(PublishingEndpoint.class);
      classes.add(DataRightsEndpoint.class);
      classes.add(TemplateManagerEndpoint.class);
      classes.add(SynchronizationEndpoint.class);

      System.out.println("Creating Swagger definitions file.  Please wait...");

      // Read in all applicable classes, creating initial Swagger openAPI definition object
      OpenAPI openAPI = new Reader(new OpenAPI()).read(classes);

      Info info = new Info();
      info.setTitle("Define API Endpoint Definitions");
      info.setDescription("Allows interactive support for Define API endpoints.");
      openAPI.setInfo(info);

      Server server = new Server();
      server.setUrl("/define");
      server.setDescription("Define");
      openAPI.addServersItem(server);

      // Add searchable tagging support to groups of endpoints
      Map<String, PathItem> taggedPaths =
         openAPI.getPaths().entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(),
            addTagsToPathItem(entry.getKey(), entry.getValue()))).collect(
               Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      Paths paths = new Paths();
      paths.putAll(taggedPaths);
      openAPI.setPaths(paths);

      // Only one period for extension allowed
      try (FileWriter fr = new FileWriter(definitionPath + "org_eclipse_osee_define_api.json")) {
         fr.write(Json.mapper().writeValueAsString(openAPI));
      } catch (JsonProcessingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

      System.out.println("Swagger definitions file created.");
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
