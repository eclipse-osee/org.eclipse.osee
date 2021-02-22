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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;

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
      Set<ArtifactId> newArts = new HashSet<>();
      Set<ArtifactId> modArts = new HashSet<>();
      Set<ArtifactId> deletedArts = new HashSet<>();

      Map<ArtifactId, Pair<ChangeItem, Set<ChangeItem>>> artToChanges = new HashMap<>();

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

   private void buildArtIdToChangeMap(List<ChangeItem> changes, Map<ArtifactId, Pair<ChangeItem, Set<ChangeItem>>> artToChanges) {
      for (ChangeItem change : changes) {
         ArtifactId artifact = change.getArtId();
         ChangeType changeType = change.getChangeType();
         if (changeType.isArtifactChange()) {
            if (!artToChanges.containsKey(artifact)) {
               artToChanges.put(artifact, new Pair<>(change, new HashSet<ChangeItem>()));
            } else {
               // This entry was added by an attribute change for this art so the changeType hasn't been set
               Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artifact);
               pair.setFirst(change);
            }
         } else if (changeType.isAttributeChange()) {
            if (!artToChanges.containsKey(artifact)) {
               Set<ChangeItem> changeSet = new HashSet<>();
               changeSet.add(change);
               artToChanges.put(artifact, new Pair<>(null, changeSet));
            } else {
               Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artifact);
               Set<ChangeItem> changeSet = pair.getSecond();
               changeSet.add(change);
            }
         }
      }
   }

   private void buildLists(Map<ArtifactId, Pair<ChangeItem, Set<ChangeItem>>> artToChanges, Set<ArtifactId> newArts, Set<ArtifactId> modArts, Set<ArtifactId> deletedArts) {
      for (ArtifactId artifact : artToChanges.keySet()) {
         Pair<ChangeItem, Set<ChangeItem>> pair = artToChanges.get(artifact);
         ChangeItem artChange = pair.getFirst();
         ModificationType modType = artChange.getNetChange().getModType();

         if (modType.equals(ModificationType.NEW)) {
            newArts.add(artifact);
         } else if (modType.equals(ModificationType.DELETED)) {
            deletedArts.add(artifact);
         } else if (modType.equals(ModificationType.MODIFIED) && isCountable(artChange, pair.getSecond())) {
            modArts.add(artifact);
         }
      }
   }

   private boolean isCountable(ChangeItem artChange, Set<ChangeItem> attrChanges) {
      boolean toReturn = false;

      // Was a synthetic artifact change added by AddArtifactChangeDataCallable
      for (ChangeItem change : attrChanges) {
         AttributeTypeToken attrType = orcsApi.tokenService().getAttributeType(change.getItemTypeId().getId());
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