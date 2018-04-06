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
import org.eclipse.osee.disposition.model.CiSetData;
import org.eclipse.osee.disposition.model.CiTestPoint;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Megumi Telles
 */
@Path("ci")
public class ContinuousIntegrationResource {
   private final DispoApi dispoApi;

   public ContinuousIntegrationResource(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   @Path("annotations")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public HashMap<String, List<DispoAnnotationData>> getAllDispoAnnotations(CiSetData setData) {
      HashMap<String, List<DispoAnnotationData>> allDispoAnnotations = new HashMap<>();
      if (setData != null) {
         List<DispoItem> dispoItems =
            dispoApi.getDispoItems(BranchId.valueOf(setData.getBranchId()), setData.getDispoSetId(), false);
         for (DispoItem item : dispoItems) {
            allDispoAnnotations.put(item.getName(), item.getAnnotationsList());
         }
      }
      return allDispoAnnotations;
   }

   @Path("{item}/annotation")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public List<DispoAnnotationData> getAllDispoAnnotationsPerItem(CiSetData setData, @PathParam("item") String item) {
      if (setData != null) {
         BranchId branch = BranchId.valueOf(setData.getBranchId());
         String itemId = dispoApi.getDispoItemId(branch, setData.getDispoSetId(), item);
         if (itemId != null && !itemId.isEmpty()) {
            return dispoApi.getDispoAnnotations(branch, itemId);
         }
      }
      return null;
   }

   @GET
   @Path("sets")
   @Produces(MediaType.APPLICATION_JSON)
   public List<CiSetData> getAllCiSets() {
      return dispoApi.getAllCiSets();
   }

   @Path("annotate")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response createDispoAnnotation(CiItemData data, @QueryParam("userName") String userName) {
      Response response = Response.status(Response.Status.NOT_FOUND).build();
      if (data != null) {
         BranchId branch = BranchId.valueOf(data.getSetData().getBranchId());
         String itemId = dispoApi.getDispoItemId(branch, data.getSetData().getDispoSetId(), data.getScriptName());
         if (Strings.isInValid(itemId)) {
            dispoApi.createDispoItem(branch, data, userName);
            itemId = dispoApi.getDispoItemId(branch, data.getSetData().getDispoSetId(), data.getScriptName());
         }
         if (Strings.isValid(itemId)) {
            updateDiscrepencies(data, branch, itemId, userName);
            dispoApi.deleteAllDispoAnnotation(branch, itemId, userName, true);
            response = createAndUpdateAnnotation(data, userName, response, branch, itemId);
         }
      } else {
         response = Response.status(Response.Status.BAD_REQUEST).build();
      }
      return response;
   }

   private void updateDiscrepencies(CiItemData data, BranchId branch, String itemId, String userName) {
      CiTestPoint testPoints = data.getTestPoints();
      DispoItem item = dispoApi.getDispoItemById(branch, itemId);
      DispoItemData itemData = DispoUtil.itemArtToItemData(item, true);
      String asRanges = itemData.getDiscrepanciesAsRanges();
      if (!testPoints.getFail().equals(asRanges)) {
         // remove ones that are now passing
         List<Integer> ranges = DispoUtil.splitDiscrepancyLocations(testPoints.getPass());
         List<String> discrepToRemove = DispoUtil.findDiscrepancyLocsToRemove(ranges, item);
         for (String toRemove : discrepToRemove) {
            dispoApi.deleteDispoDiscrepancy(branch, itemId, toRemove, userName);
         }
         // add ones that are new
         ranges = DispoUtil.splitDiscrepancyLocations(testPoints.getFail());
         List<String> discrepToAdd = DispoUtil.findMissingDiscrepancyLocs(ranges, item);
         for (String toAdd : discrepToAdd) {
            Discrepancy discrepancy = new Discrepancy();
            String discrepancyId = dispoApi.createDispoDiscrepancy(branch, itemId, discrepancy, userName);
            discrepancy.setLocation(toAdd);
            dispoApi.editDispoDiscrepancy(branch, itemId, discrepancyId, discrepancy, userName);
         }
      }
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
