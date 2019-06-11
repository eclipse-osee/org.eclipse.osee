/*******************************************************************************
 * Copyright (c) 2019 Boeing.
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
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DoorsID;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Parent;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.define.api.importing.ReqNumbering;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.rest.importing.resolvers.RoughArtifactTranslatorImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David W. Miller
 */
public class DoorsTier4RoughToRealOperation {
   private final OrcsApi orcsApi;
   private final RoughArtifactCollector rawData;
   private final ArtifactReadable destinationArtifact;
   private final RoughArtifactTranslatorImpl translator = new RoughArtifactTranslatorImpl();
   private final BranchId branch;
   private final Map<String, ArtifactToken> knownArtsByReqNum = new HashMap<>();
   private final Map<String, ArtifactToken> knownArtsByDoorsID = new HashMap<>();
   private final XResultData results;

   public DoorsTier4RoughToRealOperation(OrcsApi orcsApi, XResultData results, BranchId branch, ArtifactReadable destinationArtifact, RoughArtifactCollector rawData) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.rawData = rawData;
      this.destinationArtifact = destinationArtifact;
      this.results = results;
   }

   public XResultData doWork() {
      setupAllKnownArtifacts();
      String operationComment = String.format("Add or Modify %s", destinationArtifact.getName());
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem, operationComment);
      for (RoughArtifact roughArtifact : rawData.getParentRoughArtifact().getChildren()) {
         String name = roughArtifact.getName();
         if (name.length() > 32) {
            name = name.substring(0, 32);
         }

         resolve(transaction, roughArtifact, branch, destinationArtifact, destinationArtifact);
         results.logf("%s resolved", roughArtifact.getName());
      }
      transaction.commit();
      return results;
   }

   public void resolve(TransactionBuilder transaction, RoughArtifact roughArtifact, BranchId branch, ArtifactId realParentId, ArtifactId rootId) {
      ArtifactToken realArtifact = findExistingArtifact(roughArtifact, branch);

      if (realArtifact != null) {
         getTranslator().translate(transaction, roughArtifact, realArtifact);
         knownArtsByDoorsID.put(roughArtifact.getRoughAttribute("Doors ID"), realArtifact);
         knownArtsByReqNum.put(roughArtifact.getRoughAttribute("Doors Hierarchy").replace("-", "."), realArtifact);
      }

      if (realArtifact == null) {
         ArtifactToken parentArtifact = findParentArtifact(roughArtifact, branch);
         if (parentArtifact != null) {
            ArtifactTypeToken artifactType = roughArtifact.getType();
            if (artifactType.equals(ArtifactTypeToken.SENTINEL)) {
               results.logf("Unknown Type error in DoorsTier4RoughToRealOperation Name: %s", roughArtifact.getName());
            }
            ArtifactToken createdArt =
               transaction.createArtifact(artifactType, roughArtifact.getName(), roughArtifact.getGuid());
            getTranslator().translate(transaction, roughArtifact, createdArt);
            transaction.relate(parentArtifact, CoreRelationTypes.Default_Hierarchical__Child, createdArt, USER_DEFINED);
            knownArtsByDoorsID.put(roughArtifact.getRoughAttribute("Doors ID"), createdArt);
            knownArtsByReqNum.put(roughArtifact.getRoughAttribute("Doors Hierarchy").replace("-", "."), createdArt);
         } else {
            results.logf("Doors ID resolver cant find parent. roughArtifactifact: [%s]. Doors Hierarchy: [%s]",
               roughArtifact.getName(), roughArtifact.getAttributes().getSoleAttributeValue("Doors Hierarchy"));
         }
      }
   }

   private ArtifactToken findExistingArtifact(RoughArtifact roughArtifact, BranchId branch) {
      String modDoorsID = roughArtifact.getRoughAttribute("Doors Mod ID");
      if (Strings.isInValid(modDoorsID)) {
         // if there is no mod Id, we are creating a new artifact
         return null;
      }

      return knownArtsByDoorsID.get(modDoorsID);
   }

   private ArtifactToken findParentArtifact(RoughArtifact roughArtifact, BranchId branch) {
      String doorsHierarchy = roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsHierarchy.getName());
      ReqNumbering reqNumber = new ReqNumbering(doorsHierarchy, true);

      ArtifactToken currentParent = knownArtsByReqNum.get(reqNumber.getParentString());

      if (currentParent == null) {
         results.errorf("Couldn't find parent for %s with number %s", roughArtifact.getRoughAttribute("Doors ID"),
            doorsHierarchy);
      }

      return currentParent;
   }

   private void setupAllKnownArtifacts() {
      List<ArtifactToken> known =
         orcsApi.getQueryFactory().fromBranch(branch).andRelatedRecursive(Default_Hierarchical__Parent,
            destinationArtifact).loadArtifactTokens(DoorsHierarchy);
      known.forEach(item -> knownArtsByReqNum.put(item.getName().replace("-", "."), item)); // normalize to match ReqNumber
      List<ArtifactToken> knownIds =
         orcsApi.getQueryFactory().fromBranch(branch).andRelatedRecursive(Default_Hierarchical__Parent,
            destinationArtifact).loadArtifactTokens(DoorsID);
      knownIds.forEach(item -> knownArtsByDoorsID.put(item.getName(), item));
   }

   private RoughArtifactTranslatorImpl getTranslator() {
      return translator;
   }
}
