/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.importing.operations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.define.api.importing.DoorsImportFieldTokens;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.rest.importing.resolvers.RoughArtifactTranslatorImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David W. Miller
 */
public class DoorsTier12RoughToRealOperation {
   private final OrcsApi orcsApi;
   private final RoughArtifactCollector rawData;
   private final ArtifactReadable destinationArtifact;
   private final RoughArtifactTranslatorImpl translator = new RoughArtifactTranslatorImpl();
   private final BranchId branch;
   private final Map<String, ArtifactToken> knownArtsByReqNum = new HashMap<>();
   private final List<ArtifactToken> modifiedParents = new LinkedList<>();
   private final XResultData results;

   public DoorsTier12RoughToRealOperation(OrcsApi orcsApi, XResultData results, BranchId branch, ArtifactReadable destinationArtifact, RoughArtifactCollector rawData) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.rawData = rawData;
      this.destinationArtifact = destinationArtifact;
      this.results = results;
   }

   public XResultData doWork() {
      //setupAllKnownArtifacts();
      String operationComment = String.format("Modify Safety Hazard for %s", destinationArtifact.getName());
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem, operationComment);
      for (RoughArtifact roughArtifact : rawData.getRoughArtifacts()) {
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
      ArtifactReadable targetArtifact = findApplicableArtifact(roughArtifact, branch);
      if (targetArtifact != null && targetArtifact.isValid()) {

         transaction.createAttribute(targetArtifact, CoreAttributeTypes.Hazard,
            roughArtifact.getRoughAttribute(DoorsImportFieldTokens.blockAttrSafetyHazard.getImportTypeName()));
      } else {
         results.logf("Doors ID resolver cant find target artifact. roughArtifactifact: [%s]. DoorsId: [%s]",
            roughArtifact.getName(), roughArtifact.getAttributes().getSoleAttributeValue(
               DoorsImportFieldTokens.blockAttrSafetyHazard.getImportTypeName()));
      }
   }

   private ArtifactReadable findApplicableArtifact(RoughArtifact roughArtifact, BranchId branch) {
      String doorsId = roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsId.getName());

      System.out.println(doorsId);
      ArtifactReadable current = null;
      try {
         current = orcsApi.getQueryFactory().fromBranch(branch).andAttributeIs(CoreAttributeTypes.DoorsId,
            doorsId).asArtifact();
      } catch (Exception ex) {
         results.errorf("Couldn't find artifact for %s with doorsId %s",
            roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsId.getName()), doorsId);
         return null;
      }
      if (!current.isTypeEqual(CoreArtifactTypes.SubsystemRequirementMsWord)) {
         results.errorf("Invalid import type %s for Doors Id:%s", current.getArtifactType().getName(), doorsId);
         current = null;
      }

      return current;
   }

}
