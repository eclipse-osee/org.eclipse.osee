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
   private final Collection<UuidPair> uuidPairs = new LinkedList<>();
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
            if (art.getName().equalsIgnoreCase(artToValidate.getName()) && !art.getUuid().equals(
               artToValidate.getUuid()) && !hasUuidPairAlreadyBeenEvaluated(art.getUuid(), artToValidate.getUuid())) {
               /**************************************************************************
                * Special case: Allow duplicate names of artifacts if<br/>
                * 1) Artifact name is numeric <br/>
                * 2) Artifact type is different<br/>
                */
               if (Strings.isNumeric(
                  artToValidate.getName()) && !artToValidate.getArtifactType().equals(art.getArtifactType())) {
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
               addUuidPair(art.getUuid(), artToValidate.getUuid());
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
      return parentArtifact.getArtifactType().equals(CoreArtifactTypes.SoftwareRequirement) && //
         (childArtifact.isOfType(CoreArtifactTypes.AbstractImplementationDetails) && //
            childArtifact.getParent().equals(parentArtifact));
   }

   private void addUuidPair(Long uuidA, Long uuidB) {
      uuidPairs.add(new UuidPair(uuidA, uuidB));
   }

   private boolean hasUuidPairAlreadyBeenEvaluated(Long uuidA, Long uuidB) {
      for (UuidPair uuidPair : uuidPairs) {
         if (uuidPair.getUuidA().equals(uuidA) && uuidPair.getUuidB().equals(uuidB) || uuidPair.getUuidA().equals(
            uuidB) && uuidPair.getUuidB().equals(uuidA)) {
            return true;
         }
      }
      return false;
   }

   private class UuidPair {
      private final Long uuidA, uuidB;

      public UuidPair(Long uuidA, Long uuidB) {
         this.uuidA = uuidA;
         this.uuidB = uuidB;
      }

      public Long getUuidA() {
         return uuidA;
      }

      public Long getUuidB() {
         return uuidB;
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
