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

import java.util.Collection;
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
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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
   private final Map<ReqNumbering, ArtifactReadable> knownParents = new HashMap();
   private Pair<ReqNumbering, ArtifactReadable> lastParent = null;
   private final XResultData results;

   public DoorsTier4RoughToRealOperation(OrcsApi orcsApi, XResultData results, BranchId branch, ArtifactReadable destinationArtifact, RoughArtifactCollector rawData) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.rawData = rawData;
      this.destinationArtifact = destinationArtifact;
      this.results = results;
   }

   public XResultData doWork() {
      TransactionToken lastTransaction = null;
      for (RoughArtifact roughArtifact : rawData.getParentRoughArtifact().getChildren()) {
         String name = roughArtifact.getName();
         if (name.length() > 32) {
            name = name.substring(0, 32);
         }
         String operationComment = String.format("Add or Modify %s", name);
         TransactionBuilder transaction =
            orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem, operationComment);

         resolve(transaction, roughArtifact, branch, destinationArtifact, destinationArtifact);
         results.logf("%s resolved", roughArtifact.getName());
         lastTransaction = transaction.commit();
      }
      return results;
   }

   public void resolve(TransactionBuilder transaction, RoughArtifact roughArtifact, BranchId branch, ArtifactId realParentId, ArtifactId rootId) {
      ArtifactToken realArtifact = findExistingArtifact(roughArtifact, branch);

      if (realArtifact != null) {
         getTranslator().translate(transaction, roughArtifact, realArtifact);
      }

      if (realArtifact == null) {
         ArtifactReadable rootArtifact =
            roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andId(rootId).getArtifact();
         ArtifactToken parentArtifact = findParentArtifact(roughArtifact, branch, rootArtifact);
         if (parentArtifact != null) {
            ArtifactTypeToken artifactType = roughArtifact.getType();
            if (artifactType.equals(ArtifactTypeToken.SENTINEL)) {
               results.logf("Unknown Type error in DoorsTier4RoughToRealOperation Name: %s", roughArtifact.getName());
            }
            ArtifactToken createdArt =
               transaction.createArtifact(artifactType, roughArtifact.getName(), roughArtifact.getGuid());
            getTranslator().translate(transaction, roughArtifact, createdArt);
            transaction.relate(parentArtifact, CoreRelationTypes.Default_Hierarchical__Child, createdArt);

         } else {
            results.logf("Doors ID resolver cant find parent. roughArtifactifact: [%s]. Doors Hierarchy: [%s]",
               roughArtifact.getName(), roughArtifact.getAttributes().getSoleAttributeValue("Doors Hierarchy"));
         }
      }
   }

   private ArtifactToken findExistingArtifact(RoughArtifact roughArtifact, BranchId branch) {
      Collection<String> doorsIDs = roughArtifact.getAttributes().getAttributeValueList("Doors ID");
      doorsIDs.remove(roughArtifact.getName());

      if (doorsIDs.size() < 1) {
         // when creating, there will only be the one ID in the list, this is a create case
         return null;
      }
      List<ArtifactToken> resultArts =
         roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andAttributeIs(CoreAttributeTypes.DoorsID,
            doorsIDs.iterator().next()).loadArtifactTokens();

      if (resultArts.size() < 1) {
         return null;
      }
      if (resultArts.size() > 1) {
         for (ArtifactToken result : resultArts) {
            if (!result.getName().equals(roughArtifact.getName())) {
               return result;
            }
         }
      }
      ArtifactToken art = resultArts.iterator().next();
      if (resultArts.size() > 1) {
         this.results.errorf(
            "Could not find correct art in DoorsTier4RoughToRealOperation Name, didn't want %s from %s but returned: %s",
            roughArtifact.getName(), results.toString(), art.getName());
      }
      return art;
   }

   private ArtifactToken findParentArtifact(RoughArtifact roughArtifact, BranchId branch, ArtifactReadable rootId) {
      String doorsHierarchy = roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsHierarchy.getName());
      ReqNumbering reqNumber = new ReqNumbering(doorsHierarchy, true);

      // check last parent for match
      ArtifactReadable currentParent = getCachedParent(reqNumber);
      if (currentParent != null) {
         return currentParent;
      }

      int level = 1;
      boolean done = false;
      currentParent = rootId;

      while (!done) {
         List<ArtifactReadable> nextLevelArts =
            currentParent.getRelated(CoreRelationTypes.Default_Hierarchical__Child, CoreArtifactTypes.HeadingMSWord);
         boolean foundLevel = false;
         for (ArtifactReadable candidateParent : nextLevelArts) {
            String reqCandidate = candidateParent.getSoleAttributeValue(CoreAttributeTypes.DoorsHierarchy, "");
            if (Strings.isValid(reqCandidate) && reqCandidate.equals(reqNumber.getReqNumberByLevel(level))) {
               ++level;
               currentParent = candidateParent;
               foundLevel = true;
               break;
            }
         }
         if (!foundLevel) {
            done = true;
         }
      }
      String doorsLevel = currentParent.getSoleAttributeValue(CoreAttributeTypes.DoorsHierarchy, "");
      if (Strings.isValid(doorsLevel)) {
         ReqNumbering reqNum = new ReqNumbering(doorsLevel);
         lastParent = new Pair<ReqNumbering, ArtifactReadable>(reqNum, currentParent);
         knownParents.put(reqNum, currentParent);
      }
      return currentParent;
   }

   private ArtifactReadable getCachedParent(ReqNumbering reqNum) {
      String parent = reqNum.getParentString();
      if (lastParent != null) {
         ReqNumbering lpnum = lastParent.getFirst();
         if (parent.equals(lpnum.getNumberString())) {
            return lastParent.getSecond();
         }
      }
      ReqNumbering parentReqNum = new ReqNumbering(parent);
      return knownParents.get(parentReqNum);
   }

   private ArtifactReadable checkExtendedCache(ReqNumbering reqNum) {
      return null;
   }

   private RoughArtifactTranslatorImpl getTranslator() {
      return translator;
   }
}
