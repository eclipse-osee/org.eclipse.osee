/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.operations;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DoorsHierarchy;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Parent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.define.api.importing.ReqNumbering;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.rest.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David W. Miller
 */
public class RoughToRealRectificationOperation {
   private final OrcsApi orcsApi;
   private final BranchId branchId;
   private final XResultData results;
   private final RoughArtifactCollector rawData;
   private final IArtifactImportResolver artifactResolver;
   private final Map<RoughArtifact, ArtifactReadable> roughToRealArtifacts;
   private final List<String> toRectify;
   private String candidateParent = "";
   private final ArtifactReadable destinationArtifact;
   private final TransactionBuilder transaction;

   private final Map<String, ArtifactToken> knownArtsByReqNum = new HashMap<>();

   public RoughToRealRectificationOperation(OrcsApi orcsApi, BranchId branchId, XResultData results, TransactionBuilder transaction, ArtifactReadable destinationArtifact, RoughArtifactCollector rawData, IArtifactImportResolver artifactResolver, boolean deleteUnmatchedArtifacts, String toRectify) {
      this.results = results;
      this.orcsApi = orcsApi;
      this.branchId = branchId;
      this.rawData = rawData;
      this.transaction = transaction;
      this.artifactResolver = artifactResolver;
      this.destinationArtifact = destinationArtifact;
      this.roughToRealArtifacts = new HashMap<>();
      this.toRectify = new ArrayList<String>(Arrays.asList(toRectify.split(",")));
      roughToRealArtifacts.put(rawData.getParentRoughArtifact(), this.destinationArtifact);
   }

   public void doWork() {
      setupAllKnownArtifacts();
      for (String doorsIdToImport : toRectify) {
         if (!modExists(doorsIdToImport)) {
            RoughArtifact roughArtifact = getArtFromImported(doorsIdToImport);
            if (roughArtifact != null) {
               // find closest parent
               ArtifactToken parent = findParentArtifact(roughArtifact, branchId);
               if (parent == null) {
                  results.errorf("no parent found for %s",
                     roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsHierarchy.getName()));
                  return;
               }
               ArtifactId newParent = parent;
               if (!parent.getName().equals(candidateParent)) {
                  newParent = transaction.createArtifact(CoreArtifactTypes.HeadingMsWord, "Orphan Parent");
                  transaction.setSoleAttributeFromString(newParent, CoreAttributeTypes.DoorsHierarchy, candidateParent);
                  transaction.relate(parent, CoreRelationTypes.DefaultHierarchical_Child, newParent);
                  String toAdd = candidateParent.replace("-", ".");
                  knownArtsByReqNum.put(toAdd, ArtifactToken.valueOf(newParent, toAdd));
               }
               ArtifactId art = artifactResolver.resolve(roughArtifact, branchId, newParent, newParent);
               String addedArtReqNum =
                  roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsHierarchy.getName()).replace("-", ".");
               knownArtsByReqNum.put(addedArtReqNum, ArtifactToken.valueOf(art, addedArtReqNum));
               if (art == null) {
                  results.errorf("Artifact %s with Doors ID %s not found", roughArtifact.getName(),
                     roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsId.getName()));
               }
            }
         }
      }
   }

   private boolean modExists(String doorsId) {
      for (RoughArtifact roughArtifact : rawData.getRoughArtifacts()) {
         if (doorsId.equals(roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsId.getName()))) {
            String modded = roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsModId.getName());
            if (Strings.isValid(modded)) {
               results.errorf("found modded id %s for %s", modded, doorsId);
               return true;
            }
         }
      }
      return false;
   }

   private RoughArtifact getArtFromImported(String doorsId) {
      for (RoughArtifact roughArtifact : rawData.getRoughArtifacts()) {
         if (doorsId.equals(roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsId.getName()))) {
            return roughArtifact;
         }
      }
      results.errorf("DoorsId %s not found in import", doorsId);
      return null;
   }

   private void setupAllKnownArtifacts() {
      List<ArtifactToken> known =
         orcsApi.getQueryFactory().fromBranch(branchId).andRelatedRecursive(DefaultHierarchical_Parent,
            destinationArtifact).asArtifactTokens(DoorsHierarchy);
      known.forEach(item -> knownArtsByReqNum.put(item.getName().replace("-", "."), item)); // normalize to match ReqNumber
   }

   private ArtifactToken findParentArtifact(RoughArtifact roughArtifact, BranchId branch) {
      String doorsHierarchy = roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsHierarchy.getName());
      ReqNumbering reqNumber = new ReqNumbering(doorsHierarchy, true);
      String currentParent = reqNumber.getParentString();
      candidateParent = currentParent;
      ArtifactToken foundParent = knownArtsByReqNum.get(currentParent);
      while (foundParent == null && reqNumber.getLength() > 1) {
         candidateParent = currentParent;
         reqNumber = new ReqNumbering(currentParent);
         currentParent = reqNumber.getParentString();
         if (reqNumber.getLength() < 2) {
            results.errorf("Parent String %s not found correctly", candidateParent);
            break;
         }
         foundParent = knownArtsByReqNum.get(currentParent);
      }
      return foundParent;
   }
}
