/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Identify AI/TeamDef names to keep. Delete AIs/TeamsDefs that have no workflows. Merge those that do to their parent
 * and then delete. Report only. Set persist = true to persist. Will need to be changed depending on what conversion is
 * needed.
 *
 * @author Donald G. Dunne
 */
public class ConvertAtsAisAndTeamDefsOperation {

   private final AtsApi atsApi;
   private final boolean persist = false;
   Map<String, ArtifactToken> keepAiArts = new HashMap<String, ArtifactToken>();
   Map<String, ArtifactToken> keepAiTeamDefArts = new HashMap<String, ArtifactToken>();

   public ConvertAtsAisAndTeamDefsOperation(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData run() {

      List<String> keepNames = Arrays.asList("PROG1 Change Request", "PROG1 PL ARB", "PROG1 Integration Test", "PROG1",
         "PROG1 Requirements", "PROG1 Software Test", "PROG1 Code", "");

      XResultData rd = new XResultData();
      IAtsChangeSet changes = atsApi.createChangeSet("Cleanup AIs and TeamDefs");

      List<ArtifactToken> ais = atsApi.getQueryService().getArtifacts(AtsArtifactTypes.ActionableItem);
      for (ArtifactToken ai : ais) {
         if (keepNames.contains(ai.getName())) {
            keepAiArts.put(ai.getName(), ai);
         }
      }

      for (ArtifactToken ai : ais) {
         if (ai.getName().startsWith("PROG1")) {
            if (keepNames.contains(ai.getName())) {
               rd.logf("KEEP AI: %s\n", ai.toStringWithId());
            } else {
               Collection<ArtifactToken> referencedArts = getReferencedAiArts(ai);
               if (referencedArts.isEmpty()) {
                  rd.logf("Processing AI: %s - NO REFRENCES, JUST DELETE\n", ai.toStringWithId());
                  if (persist) {
                     changes.deleteArtifact(ai);
                  }
               } else {
                  rd.logf("Processing AI: %s - ====> CLEANUP [%s] REFRENCES AND DELETE\n", ai.toStringWithId(),
                     referencedArts.size());
                  cleanupAndDeleteArt("AI", ai, referencedArts, keepAiArts, AtsAttributeTypes.ActionableItemReference,
                     rd, changes);
               }
            }
         } else {
            rd.warningf("Skipping AI: %s\n", ai.toStringWithId());
         }
      }

      List<ArtifactToken> teamDefs = atsApi.getQueryService().getArtifacts(AtsArtifactTypes.TeamDefinition);
      for (ArtifactToken teamDef : teamDefs) {
         if (keepNames.contains(teamDef.getName())) {
            keepAiTeamDefArts.put(teamDef.getName(), teamDef);
         }
      }

      for (ArtifactToken teamDef : teamDefs) {
         if (teamDef.getName().startsWith("PROG1")) {
            if (keepNames.contains(teamDef.getName())) {
               rd.logf("KEEP TeamDef: %s\n", teamDef.toStringWithId());
            } else {
               Collection<ArtifactToken> referencedArts = getReferencedTeamDefArts(teamDef);
               if (referencedArts.isEmpty()) {
                  rd.logf("Processing TeamDef: %s - NO REFRENCES, JUST DELETE\n", teamDef.toStringWithId());
                  if (persist) {
                     changes.deleteArtifact(teamDef);
                  }
               } else {
                  rd.logf("Processing TeamDef: %s - ====> CLEANUP [%s] REFRENCES AND DELETE\n",
                     teamDef.toStringWithId(), referencedArts.size());
                  cleanupAndDeleteArt("TeamDef", teamDef, referencedArts, keepAiTeamDefArts,
                     AtsAttributeTypes.TeamDefinitionReference, rd, changes);
               }
            }
         } else {
            rd.warningf("Skipping TeamDef: %s\n", teamDef.toStringWithId());
         }
      }

      changes.executeIfNeeded();

      return rd;
   }

   private void cleanupAndDeleteArt(String typeName, ArtifactToken artToDelete, Collection<ArtifactToken> referencedArts, //
      Map<String, ArtifactToken> keepArtsMap, AttributeTypeToken attrType, XResultData rd, IAtsChangeSet changes) {
      ArtifactToken topArt = null;
      for (Entry<String, ArtifactToken> entry : keepArtsMap.entrySet()) {
         String topArtName = entry.getKey();
         if (artToDelete.getName().startsWith(topArtName)) {
            topArt = entry.getValue();
            break;
         }
      }
      if (topArt == null) {
         rd.errorf("Can't get top %s for %s\n", typeName, artToDelete.toStringWithId());
      } else {
         for (ArtifactToken art : referencedArts) {
            if (persist) {
               if (typeName.equals("TeamDef")) {
                  changes.setSoleAttributeValue(art, attrType, topArt);
               } else {
                  changes.deleteAttribute(art, attrType, artToDelete);
                  changes.addAttribute(art, attrType, topArt);
               }
            }
         }
         if (persist) {
            changes.deleteArtifact(artToDelete);
         }
      }
   }

   private Collection<ArtifactToken> getReferencedAiArts(ArtifactToken ai) {
      Collection<ArtifactToken> artifacts = atsApi.getQueryService().getArtifacts(
         AtsAttributeTypes.ActionableItemReference, ai.getIdString(), atsApi.getAtsBranch());
      return artifacts;
   }

   private Collection<ArtifactToken> getReferencedTeamDefArts(ArtifactToken teamDef) {
      Collection<ArtifactToken> artifacts = atsApi.getQueryService().getArtifacts(
         AtsAttributeTypes.TeamDefinitionReference, teamDef.getIdString(), atsApi.getAtsBranch());
      return artifacts;
   }
}
