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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Shawn F. Cook
 */
public class UniqueNameRule extends AbstractValidationRule {

   private final IArtifactType artifactType;
   private final Collection<GuidPair> guidPairs = new LinkedList<>();

   public UniqueNameRule(IArtifactType artifactType) {
      this.artifactType = artifactType;
   }

   public boolean hasArtifactType(ArtifactType artType) {
      return artType.inheritsFrom(artifactType);
   }

   public IArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) throws OseeCoreException {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      if (hasArtifactType(artToValidate.getArtifactType())) {
         // validate that no other artifact of the given Artifact Type has the same name.
         List<Artifact> arts =
            ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType, artToValidate.getBranch(),
               DeletionFlag.EXCLUDE_DELETED);
         for (Artifact art : arts) {
            if (art.getName().equalsIgnoreCase(artToValidate.getName()) && art.getGuid() != artToValidate.getGuid() && !hasGuidPairAlreadyBeenEvaluated(
               art.getGuid(), artToValidate.getGuid())) {
               /**************************************************************************
                * Special case: Allow duplicate names of artifacts if<br/>
                * 1) Artifact name is numeric <br/>
                * 2) Artifact type is different<br/>
                */
               if (Strings.isNumeric(artToValidate.getName()) && !artToValidate.getArtifactType().equals(
                  art.getArtifactType())) {
                  continue;
               }
               errorMessages.add(ValidationReportOperation.getRequirementHyperlink(artToValidate) + " and " + ValidationReportOperation.getRequirementHyperlink(art) + " have same name value:\"" + artToValidate.getName() + " \"");
               validationPassed = false;
               addGuidPair(art.getGuid(), artToValidate.getGuid());
            }
         }
      }
      return new ValidationResult(errorMessages, validationPassed);
   }

   private void addGuidPair(String guidA, String guidB) {
      guidPairs.add(new GuidPair(guidA, guidB));
   }

   private boolean hasGuidPairAlreadyBeenEvaluated(String guidA, String guidB) {
      for (GuidPair guidPair : guidPairs) {
         if ((guidPair.getGuidA().equals(guidA) && guidPair.getGuidB().equals(guidB)) || guidPair.getGuidA().equals(
            guidB) && guidPair.getGuidB().equals(guidA)) {
            return true;
         }
      }
      return false;
   }

   private class GuidPair {
      private final String guidA, guidB;

      public GuidPair(String guidA, String guidB) {
         this.guidA = guidA;
         this.guidB = guidB;
      }

      public String getGuidA() {
         return guidA;
      }

      public String getGuidB() {
         return guidB;
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
