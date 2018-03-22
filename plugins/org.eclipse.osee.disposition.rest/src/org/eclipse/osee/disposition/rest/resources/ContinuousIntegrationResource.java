/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.resources;

import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.CiItemData;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Megumi Telles
 */
@Path("ci")
public class ContinuousIntegrationResource {
   private final DispoApi dispoApi;

   public ContinuousIntegrationResource(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   @GET
   @Path("{ciSet}/annotations")
   @Produces(MediaType.APPLICATION_JSON)
   public HashMap<String, List<DispoAnnotationData>> getAllDispoAnnotations(@PathParam("ciSet") String ciSet) {
      HashMap<ArtifactReadable, BranchId> set = dispoApi.getCiSet(ciSet);
      HashMap<String, List<DispoAnnotationData>> allDispoAnnotations = new HashMap<>();
      if (set != null && !set.isEmpty()) {
         ArtifactReadable dispoSet = set.keySet().iterator().next();
         List<DispoItem> dispoItems = dispoApi.getDispoItems(set.get(dispoSet), dispoSet.getIdString(), false);
         for (DispoItem item : dispoItems) {
            allDispoAnnotations.put(item.getName(), item.getAnnotationsList());
         }
      }
      return allDispoAnnotations;
   }

   @GET
   @Path("{ciSet}/item/{item}/annotation")
   @Produces(MediaType.APPLICATION_JSON)
   public List<DispoAnnotationData> getAllDispoAnnotationsPerItem(@PathParam("ciSet") String ciSet, @PathParam("item") String item) {
      HashMap<ArtifactReadable, BranchId> set = dispoApi.getCiSet(ciSet);
      if (set != null && !set.isEmpty()) {
         ArtifactReadable dispoSet = set.keySet().iterator().next();
         BranchId branchId = set.get(dispoSet);
         String itemId = dispoApi.getDispoItemId(branchId, dispoSet.getIdString(), item);
         return dispoApi.getDispoAnnotations(branchId, itemId);
      }
      return null;
   }

   @GET
   @Path("{ciSet}/configured")
   @Produces(MediaType.APPLICATION_JSON)
   public BranchId getCiSetConfigured(@PathParam("ciSet") String ciSet) {
      return dispoApi.getCiSetConfigured(ciSet);
   }

   @Path("annotate")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response createDispoAnnotation(CiItemData data, @QueryParam("userName") String userName) {
      Response response = null;
      HashMap<ArtifactReadable, BranchId> set = dispoApi.getCiSet(data.getCiSet());
      if (set != null && !set.isEmpty()) {
         ArtifactReadable dispoSet = set.keySet().iterator().next();
         BranchId branchId = set.get(dispoSet);
         String itemId = dispoApi.getDispoItemId(branchId, dispoSet.getIdString(), data.getScriptName());
         dispoApi.deleteAllDispoAnnotation(branchId, itemId, userName, true);
         response = createAndUpdateAnnotation(data, userName, response, branchId, itemId);
      } else {
         response = Response.status(Response.Status.BAD_REQUEST).build();
      }
      return response;
   }

   private Response createAndUpdateAnnotation(CiItemData data, String userName, Response response, BranchId branchId, String itemId) {
      for (DispoAnnotationData annotation : data.getAnnotations()) {
         DispoAnnotationData temp = new DispoAnnotationData();
         String createdAnnotationId = dispoApi.createDispoAnnotation(branchId, itemId, temp, userName, true);
         if (!createdAnnotationId.isEmpty()) {
            response = Response.status(Response.Status.OK).build();
            initTempAnnotationData(annotation, temp);
            boolean wasEdited =
               dispoApi.editDispoAnnotation(branchId, itemId, createdAnnotationId, temp, userName, true);
            if (wasEdited) {
               response = Response.status(Response.Status.OK).build();
            } else {
               response = Response.status(Response.Status.NOT_MODIFIED).build();
               break;
            }
         } else {
            response = Response.status(Response.Status.NOT_ACCEPTABLE).build();
            break;
         }
      }
      return response;
   }

   private void initTempAnnotationData(DispoAnnotationData annotation, DispoAnnotationData temp) {
      temp.setLocationRefs(annotation.getLocationRefs());
      temp.setResolution(annotation.getResolution());
      temp.setResolutionType(annotation.getResolutionType());
      temp.setCustomerNotes(annotation.getCustomerNotes());
      temp.setDeveloperNotes(annotation.getDeveloperNotes());
   }

}
