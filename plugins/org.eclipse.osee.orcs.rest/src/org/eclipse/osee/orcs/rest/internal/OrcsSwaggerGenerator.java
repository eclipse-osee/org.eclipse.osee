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

package org.eclipse.osee.orcs.rest.internal;

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
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.ExceptionRegistryEndpoint;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.ReportEndpoint;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author Dominic Guss
 */
public class OrcsSwaggerGenerator {

   private static final String definitionPath = "../org.eclipse.osee.web.ui/src/swagger/definitions/";

   public static void main(String[] args) {

      Set<Class<?>> classes = new HashSet<Class<?>>();
      // Do not add classes that have parent @Path annotations, e.g. ArtifactEndpoint relies on BranchesResource
      classes.add(BranchesResource.class);
      classes.add(ExceptionRegistryEndpoint.class);
      classes.add(OrcsWriterEndpoint.class);
      classes.add(KeyValueResource.class);
      classes.add(BranchEndpoint.class);
      classes.add(DatastoreEndpoint.class);
      classes.add(IndexerEndpoint.class);
      classes.add(ReportEndpoint.class);
      classes.add(ResourcesEndpoint.class);
      classes.add(TransactionEndpoint.class);
      classes.add(TypesEndpoint.class);

      System.out.println("Creating Swagger definitions file.  Please wait...");

      // Read in all applicable classes, creating initial Swagger openAPI definition object
      OpenAPI openAPI = new Reader(new OpenAPI()).read(classes);

      Info info = new Info();
      info.setTitle("Orcs Endpoint Definitions");
      info.setDescription("Allows interactive support for Orcs REST endpoints.");
      openAPI.setInfo(info);

      Server server = new Server();
      server.setUrl("/orcs");
      server.setDescription("ORCS");
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
      try (FileWriter fr = new FileWriter(definitionPath + "org_eclipse_osee_orcs_rest.json")) {
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
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getGet().addTagsItem(pathElements[3]);
         }
      }
      if (pathItem.getDelete() != null) {
         pathItem.getDelete().addTagsItem(pathElements[1]);
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getDelete().addTagsItem(pathElements[3]);
         }
      }
      if (pathItem.getHead() != null) {
         pathItem.getHead().addTagsItem(pathElements[1]);
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getHead().addTagsItem(pathElements[3]);
         }
      }
      if (pathItem.getPatch() != null) {
         pathItem.getPatch().addTagsItem(pathElements[1]);
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getPatch().addTagsItem(pathElements[3]);
         }
      }
      if (pathItem.getPost() != null) {
         pathItem.getPost().addTagsItem(pathElements[1]);
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getPost().addTagsItem(pathElements[3]);
         }
      }
      if (pathItem.getPut() != null) {
         pathItem.getPut().addTagsItem(pathElements[1]);
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getPut().addTagsItem(pathElements[3]);
         }
      }
      if (pathItem.getTrace() != null) {
         pathItem.getTrace().addTagsItem(pathElements[1]);
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getTrace().addTagsItem(pathElements[3]);
         }
      }
      if (pathItem.getOptions() != null) {
         pathItem.getOptions().addTagsItem(pathElements[1]);
         if (pathElements[1].equals("branch") && pathElements.length > 3) {
            pathItem.getOptions().addTagsItem(pathElements[3]);
         }
      }
      return pathItem;
   }
}
