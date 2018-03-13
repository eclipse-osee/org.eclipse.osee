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
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
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
   public boolean isCiSetConfigured(@PathParam("ciSet") String ciSet) {
      return dispoApi.isCiSetConfigured(ciSet);
   }

   @Path("{ciSet}/item/{item}/annotate")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response createDispoAnnotation(@PathParam("ciSet") String ciSet, @PathParam("item") String item, DispoAnnotationData data, @QueryParam("userName") String userName) {
      Response response;
      HashMap<ArtifactReadable, BranchId> set = dispoApi.getCiSet(ciSet);
      if (set != null && !set.isEmpty()) {
         ArtifactReadable dispoSet = set.keySet().iterator().next();
         BranchId branchId = set.get(dispoSet);
         String itemId = dispoApi.getDispoItemId(branchId, dispoSet.getIdString(), item);
         String createdAnnotationId = dispoApi.createDispoAnnotation(branchId, itemId, data, userName);
         if (!createdAnnotationId.isEmpty()) {
            response = Response.status(Response.Status.OK).build();
            boolean wasEdited = dispoApi.editDispoAnnotation(branchId, itemId, createdAnnotationId, data, userName);
            if (wasEdited) {
               response = Response.status(Response.Status.OK).build();
            } else {
               response = Response.status(Response.Status.NOT_MODIFIED).build();
            }
         } else {
            response = Response.status(Response.Status.NOT_ACCEPTABLE).build();
         }
      } else {
         response = Response.status(Response.Status.BAD_REQUEST).build();
      }
      return response;
   }

   @Path("{ciSet}/item/{item}/update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response editDispoAnnotation(@PathParam("ciSet") String ciSet, @PathParam("item") String item, DispoAnnotationData data, @QueryParam("userName") String userName) {
      Response response;
      HashMap<ArtifactReadable, BranchId> set = dispoApi.getCiSet(ciSet);
      if (set != null && !set.isEmpty()) {
         ArtifactReadable dispoSet = set.keySet().iterator().next();
         BranchId branchId = set.get(dispoSet);
         String itemId = dispoApi.getDispoItemId(branchId, dispoSet.getIdString(), item);
         List<DispoItem> dispoItems = dispoApi.getDispoItems(branchId, dispoSet.getIdString(), false);
         DispoItem dispoItem = DispoUtil.findDispoItem(dispoItems, item);
         DispoAnnotationData id = DispoUtil.getById(dispoItem.getAnnotationsList(), data.getId());
         boolean wasEdited = dispoApi.editDispoAnnotation(branchId, itemId, id.getId(), data, userName);
         if (wasEdited) {
            response = Response.status(Response.Status.OK).build();
         } else {
            response = Response.status(Response.Status.NOT_MODIFIED).build();
         }
      } else {
         response = Response.status(Response.Status.BAD_REQUEST).build();
      }
      return response;
   }

}
