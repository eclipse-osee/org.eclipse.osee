/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class UniqueNameRule extends AbstractValidationRule {

   private final IArtifactType artifactType;
   private final Collection<GuidPair> guidPairs = new LinkedList<GuidPair>();

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
   public String toString() {
      return "Ensure no two artifacts have the same name value";
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) throws OseeCoreException {
      Collection<String> errorMessages = new ArrayList<String>();
      boolean validationPassed = true;
      if (hasArtifactType(artToValidate.getArtifactType())) {
         // validate that no other artifact of the given Artifact Type has the same name.
         List<Artifact> arts =
            ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType, artToValidate.getBranch(),
               DeletionFlag.EXCLUDE_DELETED);
         for (Artifact art : arts) {
            if (art.getName().equalsIgnoreCase(artToValidate.getName()) && art.getGuid() != artToValidate.getGuid() && !hasGuidPairAlreadyBeenEvaluated(
               art.getGuid(), artToValidate.getGuid())) {
               errorMessages.add(ValidateReqChangeReport.getRequirementHyperlink(artToValidate) + " and " + ValidateReqChangeReport.getRequirementHyperlink(art) + " have same name value:\"" + artToValidate.getName() + " \"");
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
}
