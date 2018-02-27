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
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Shawn F. Cook
 */
public class UniqueNameRule extends AbstractValidationRule {

   private final IArtifactType artifactType;
   private final Collection<IdPair> idPairs = new LinkedList<>();
   private final Map<IArtifactType, List<Artifact>> artTypeToArtifacts = new HashMap<IArtifactType, List<Artifact>>();

   public UniqueNameRule(IArtifactType artifactType) {
      this.artifactType = artifactType;
   }

   public boolean hasArtifactType(ArtifactType artType) {
      return artType.inheritsFrom(artifactType);
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      if (hasArtifactType(artToValidate.getArtifactType())) {
         // validate that no other artifact of the given Artifact Type has the same name.
         List<Artifact> arts = getArtifactsOfType(artToValidate.getBranchToken(), artToValidate.getArtifactType());
         for (Artifact art : arts) {
            if (art.getName().equalsIgnoreCase(artToValidate.getName()) && !art.getId().equals(
               artToValidate.getId()) && !hasIdPairAlreadyBeenEvaluated(art.getId(), artToValidate.getId())) {
               /**************************************************************************
                * Special case: Allow duplicate names of artifacts if<br/>
                * 1) Artifact name is numeric <br/>
                * 2) Artifact type is different<br/>
                */
               if (Strings.isNumeric(artToValidate.getName()) && !artToValidate.isTypeEqual(art.getArtifactType())) {
                  continue;
               }
               /**************************************************************************
                * Allow for a Software Requirement parent to have an Implementation Details <br/>
                * child of the same name.
                */
               if (isImplementationDetailsChild(artToValidate, art) || isImplementationDetailsChild(art,
                  artToValidate)) {
                  continue;
               }
               errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
                  artToValidate) + " and " + ValidationReportOperation.getRequirementHyperlink(
                     art) + " have same name value:\"" + artToValidate.getName() + " \"");
               validationPassed = false;
               addIdPair(art.getId(), artToValidate.getId());
            }
         }
      }
      return new ValidationResult(errorMessages, validationPassed);
   }

   protected List<Artifact> getArtifactsOfType(IOseeBranch branch, IArtifactType artToValidate) {
      List<Artifact> arts = artTypeToArtifacts.get(artToValidate);
      if (arts == null) {
         arts =
            ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType, branch, DeletionFlag.EXCLUDE_DELETED);
         artTypeToArtifacts.put(artToValidate, arts);
      }
      return arts;
   }

   private boolean isImplementationDetailsChild(Artifact childArtifact, Artifact parentArtifact) {
      return parentArtifact.isTypeEqual(CoreArtifactTypes.SoftwareRequirement) && //
         (childArtifact.isOfType(CoreArtifactTypes.AbstractImplementationDetails) && //
            childArtifact.getParent().equals(parentArtifact));
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
      return "<b>Unique Names Check: </b>Ensure no two artifacts have the same name value";
   }

   @Override
   public String getRuleTitle() {
      return "Unique Names Check:";
   }
}
