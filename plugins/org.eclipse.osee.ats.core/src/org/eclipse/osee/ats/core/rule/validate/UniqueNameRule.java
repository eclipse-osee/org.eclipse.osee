/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.rule.validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Shawn F. Cook
 * @author Donald G. Dunne
 */
public class UniqueNameRule extends AbstractValidationRule {

   private final IArtifactType artifactType;
   private final Collection<IdPair> idPairs = new LinkedList<>();
   private final Map<IArtifactType, List<ArtifactToken>> artTypeToArtifacts =
      new HashMap<IArtifactType, List<ArtifactToken>>();

   public UniqueNameRule(IArtifactType artifactType, AtsApi atsApi) {
      super(atsApi);
      this.artifactType = artifactType;
   }

   public boolean hasArtifactType(IArtifactType artType) {
      return atsApi.getStoreService().inheritsFrom(artType, artifactType);
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData results) {
      if (hasArtifactType(atsApi.getStoreService().getArtifactType(artifact))) {
         // validate that no other artifact of the given Artifact Type has the same name.
         List<ArtifactToken> arts = getArtifactsOfType(artifact.getBranch(), artifact.getArtifactTypeId());
         for (ArtifactToken art : arts) {
            if (art.getName().equalsIgnoreCase(artifact.getName()) && art.notEqual(
               artifact) && !hasIdPairAlreadyBeenEvaluated(art.getId(), artifact.getId())) {
               /**************************************************************************
                * Special case: Allow duplicate names of artifacts if<br/>
                * 1) Artifact name is numeric <br/>
                * 2) Artifact type is different<br/>
                */
               if (Strings.isNumeric(artifact.getName()) && !artifact.isTypeEqual(art.getArtifactTypeId())) {
                  continue;
               }
               /**************************************************************************
                * Allow for a Software Requirement parent to have an Implementation Details <br/>
                * child of the same name.
                */
               if (isImplementationDetailsChild(artifact, art) || isImplementationDetailsChild(art, artifact)) {
                  continue;
               }
               results.errorf("Artifacts have same name value [%s]", art.getName());
               addIdPair(art.getId(), artifact.getId());
            }
         }
      }
   }

   protected List<ArtifactToken> getArtifactsOfType(BranchId branch, IArtifactType artifact) {
      List<ArtifactToken> arts = artTypeToArtifacts.get(artifact);
      if (arts == null) {
         arts = atsApi.getQueryService().getArtifactListFromTypeWithInheritence(artifactType, branch,
            DeletionFlag.EXCLUDE_DELETED);
         artTypeToArtifacts.put(artifact, arts);
      }
      return arts;
   }

   private boolean isImplementationDetailsChild(ArtifactId childArtifact, ArtifactId parentArtifact) {
      return atsApi.getStoreService().isOfType(parentArtifact, CoreArtifactTypes.SoftwareRequirement) && //
         (atsApi.getStoreService().isOfType(childArtifact, CoreArtifactTypes.AbstractImplementationDetails) && //
            atsApi.getRelationResolver().getParent(childArtifact).equals(parentArtifact));
   }

   private void addIdPair(Long idA, Long idB) {
      idPairs.add(new IdPair(idA, idB));
   }

   private boolean hasIdPairAlreadyBeenEvaluated(Long idA, Long idB) {
      for (IdPair idPair : idPairs) {
         if (idPair.getIdA().equals(idA) && idPair.getIdB().equals(idB) || idPair.getIdA().equals(
            idB) && idPair.getIdB().equals(idA)) {
            return true;
         }
      }
      return false;
   }

   private class IdPair {
      private final Long idA, idB;

      public IdPair(Long idA, Long idB) {
         this.idA = idA;
         this.idB = idB;
      }

      public Long getIdA() {
         return idA;
      }

      public Long getIdB() {
         return idB;
      }
   }

   @Override
   public String getRuleDescription() {
      return "Ensure no two artifacts have the same name value";
   }

   @Override
   public String getRuleTitle() {
      return "Unique Names Check:";
   }
}
