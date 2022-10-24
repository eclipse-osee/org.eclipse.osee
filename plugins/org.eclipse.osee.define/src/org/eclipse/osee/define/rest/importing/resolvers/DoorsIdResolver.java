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

package org.eclipse.osee.define.rest.importing.resolvers;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.define.api.importing.ReqNumbering;
import org.eclipse.osee.define.api.importing.RoughArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David Miller
 */
public class DoorsIdResolver extends NewArtifactImportResolver {

   private final boolean createNewIfNotExist;

   public DoorsIdResolver(TransactionBuilder transaction, IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(transaction, translator, primaryArtifactType, secondaryArtifactType);
      this.createNewIfNotExist = createNewIfNotExist;
   }

   @Override
   public ArtifactToken resolve(RoughArtifact roughArtifact, BranchId branch, ArtifactId realParentId, ArtifactId rootId) {
      ArtifactToken realArtifact = findExistingArtifact(roughArtifact, branch);

      if (realArtifact.isValid()) {
         getTranslator().translate(transaction, roughArtifact, realArtifact);
      }

      if (realArtifact.isInvalid() && createNewIfNotExist) {
         ArtifactReadable rootArtifact =
            roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andId(rootId).getArtifact();
         ArtifactToken parentArtifact = findParentArtifact(roughArtifact, branch, rootArtifact);
         if (parentArtifact != null) {
            ArtifactTypeToken artifactType = getArtifactType(roughArtifact);
            ArtifactToken createdArt =
               transaction.createArtifact(artifactType, roughArtifact.getName(), roughArtifact.getGuid());
            getTranslator().translate(transaction, roughArtifact, createdArt);
            transaction.relate(parentArtifact, CoreRelationTypes.DefaultHierarchical_Child, createdArt);
         } else {
            roughArtifact.getResults().warningf(
               "Doors ID resolver cant find parent. roughArtifactifact: [%s]. Doors Hierarchy: [%s]",
               roughArtifact.getName(), roughArtifact.getAttributes().getSoleAttributeValue("Doors Hierarchy"));
         }
      }
      // the roughToRealOperation calling this will attempt to relate modified or created objects into
      // the hierarchy incorrectly, all should be correctly related before returning, so return null
      return null;
   }

   private ArtifactToken findExistingArtifact(RoughArtifact roughArtifact, BranchId branch) {
      Collection<String> doorsIDs =
         roughArtifact.getAttributes().getAttributeValueList(CoreAttributeTypes.DoorsId.getName());
      doorsIDs.remove(roughArtifact.getName());

      if (doorsIDs.size() < 1) {
         // when creating, there will only be the one ID in the list, this is a create case
         return null;
      }
      return roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andAttributeIs(CoreAttributeTypes.DoorsId,
         doorsIDs.iterator().next()).asArtifactTokenOrSentinel();
   }

   private ArtifactToken findParentArtifact(RoughArtifact roughArtifact, BranchId branch, ArtifactReadable rootId) {
      String doorsHierarchy = roughArtifact.getRoughAttribute(CoreAttributeTypes.DoorsHierarchy.getName());
      ReqNumbering reqNumber = new ReqNumbering(doorsHierarchy, true);
      String reqParent = reqNumber.getParentString();
      reqParent = new ReqNumbering(reqParent, true).getParentString();
      List<ArtifactToken> results = roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andAttributeIs(
         CoreAttributeTypes.DoorsHierarchy, reqParent).asArtifactTokens();
      while (results.size() == 0 && reqParent.length() > 3) {
         reqParent = new ReqNumbering(reqParent, true).getParentString();
         results = roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andAttributeIs(
            CoreAttributeTypes.DoorsHierarchy, reqParent).asArtifactTokens();
      }
      for (ArtifactToken artifact : results) {
         ArtifactReadable possibleParent =
            roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andId(artifact).getArtifact();
         if (possibleParent.isDescendantOf(rootId)) {
            return artifact;
         }
      }
      return null;
   }

}
