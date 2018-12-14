/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Angel Avila
 */
@Path("typeCount")
public class ReportResource {
   private final OrcsApi orcsApi;
   private final AtsApi atsApi;

   public ReportResource(OrcsApi orcsApi, AtsApi atsApi) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
   }

   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getTypeCount(@QueryParam("branch") BranchId branch, @QueryParam("artTypes") List<Long> artTypes, @QueryParam("attrTypes") List<Long> attrTypes) {
      List<ChangeItem> changes = getChanges(branch);
      Set<Long> newArts = new HashSet<>();
      Set<Long> modArts = new HashSet<>();
      Set<Long> deletedArts = new HashSet<>();

      Map<Integer, Pair<ChangeItem, Set<ChangeItem>>> artToChanges =
         new HashMap<Integer, Pair<ChangeItem, Set<ChangeItem>>>();

      buildArtIdToChangeMap(changes, artToChanges);
      buildLists(artToChanges, newArts, modArts, deletedArts);

      final TypeCountWriter writer = new TypeCountWriter(orcsApi);
      final String fileName = String.format("Type_Count_Report_%s", System.currentTimeMillis());
      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws WebApplicationException, IOException {
            writer.write(branch, newArts, modArts, deletedArts, artTypes, attrTypes, outputStream);
            outputStream.flush();
         }
      };
      String contentDisposition =
         String.format("attachment; filename=\"%s.xml\"; creation-date=\"%s\"", fileName, new Date());
      return Response.ok(streamingOutput).header("Content-Disposition", contentDisposition).type(
         "application/xml").build();
   }

   private void buildArtIdToChangeMap(List<ChangeItem> changes, Map<Integer, Pair<ChangeItem, Set<ChangeItem>>> artToChanges) {
      for (ChangeItem change : changes) {
         int artId = change.getArtId().getId().intValue();
         ChangeType changeType = change.getChangeType();
         if (changeType.isArtifactChange()) {
            if (!artToChanges.containsKey(artId)) {
               artToChanges.put(artId, new Pair<>(change, new HashSet<ChangeItem>()));
            } else {
               // This entry was added by an attribute change for this art so the changeType hasn't been set
               Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artId);
               pair.setFirst(change);
            }
         } else if (changeType.isAttributeChange()) {
            if (!artToChanges.containsKey(artId)) {
               Set<ChangeItem> changeSet = new HashSet<>();
               changeSet.add(change);
               artToChanges.put(artId, new Pair<>(null, changeSet));
            } else {
               Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artId);
               Set<ChangeItem> changeSet = pair.getSecond();
               changeSet.add(change);
            }
         }

      }
   }

   private void buildLists(Map<Integer, Pair<ChangeItem, Set<ChangeItem>>> artToChanges, Set<Long> newArts, Set<Long> modArts, Set<Long> deletedArts) {
      AttributeTypes attributeTypes = orcsApi.getOrcsTypes().getAttributeTypes();
      ArtifactTypes artifactTypes = orcsApi.getOrcsTypes().getArtifactTypes();

      for (Integer artId : artToChanges.keySet()) {
         Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artId);
         ChangeItem artChange = pair.getFirst();
         ModificationType modType = artChange.getNetChange().getModType();

         if (modType.equals(ModificationType.NEW)) {
            newArts.add((long) artId);
         } else if (modType.equals(ModificationType.DELETED)) {
            deletedArts.add((long) artId);
         } else if (modType.equals(ModificationType.MODIFIED) && isCountable(artChange, pair.getSecond(),
            attributeTypes, artifactTypes)) {
            modArts.add((long) artId);
         }
      }

   }

   private boolean isCountable(ChangeItem artChange, Set<ChangeItem> attrChanges, AttributeTypes attributeTypes, ArtifactTypes artTypes) {
      boolean toReturn = false;

      // Was a synthetic artifact change added by AddArtifactChangeDataCallable
      for (ChangeItem change : attrChanges) {
         AttributeTypeId attrType = attributeTypes.get(change.getItemTypeId());
         if (attrType.matches(WordTemplateContent)) {
            toReturn = true;
            break;
         }
      }

      return toReturn;
   }

   private List<ChangeItem> getChanges(BranchId branch) {
      List<ChangeItem> results = atsApi.getBranchService().getChangeData(branch);
      if (results != null) {
         return results;
      }
      return Collections.emptyList();
   }
}