/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.commit.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;

/**
 * @author Theron Virgin
 */
public class CatchWordMlChanges implements CommitAction {

   /**
    * Check that none of the artifacts that will be commited contain tracked changes or mismatching start and end
    * applicability tags. Use the change report to get attributeChanges and check their content for trackedChanges and
    * incorrect applicability tags
    */

   @Override
   public void runCommitAction(BranchId sourceBranch, BranchId destinationBranch) throws OseeCoreException {
      Set<Artifact> changedArtifacts = new HashSet<>();
      Collection<Change> changes = new ArrayList<>();
      IOperation operation = ChangeManager.compareTwoBranchesHead(sourceBranch, destinationBranch, changes);
      Operations.executeWorkAndCheckStatus(operation);

      Map<Integer, String> trackedChanges = new HashMap<Integer, String>();
      Map<Integer, String> applicabilityTags = new HashMap<Integer, String>();
      for (Change change : changes) {
         if (!change.getModificationType().isDeleted()) {
            if (change.getChangeType() == LoadChangeType.attribute) {
               Attribute<?> attribute = ((AttributeChange) change).getAttribute();

               if (attribute instanceof WordAttribute) {
                  if (((WordAttribute) attribute).containsWordAnnotations()) {
                     trackedChanges.put(attribute.getArtifact().getArtId(), attribute.getArtifact().getSafeName());
                  }

                  Boolean useInvalidTagsCheck =
                     Boolean.valueOf(OseeInfo.getCachedValue("osee.are.applicability.tags.invalid"));
                  Boolean isInvalidTags =
                     useInvalidTagsCheck ? (((WordAttribute) attribute).areApplicabilityTagsInvalid(destinationBranch,
                        getValidFeatureValuesForBranch(destinationBranch))) : useInvalidTagsCheck;
                  if (isInvalidTags) {
                     applicabilityTags.put(attribute.getArtifact().getArtId(), attribute.getArtifact().getSafeName());
                  }

               }
            }

            Artifact artifactChanged = change.getChangeArtifact();
            if (artifactChanged != null) {
               changedArtifacts.add(artifactChanged);
            }
         }
      }

      String err = null;
      if (!trackedChanges.isEmpty()) {
         err = String.format(
            "Commit Branch Failed. The following artifacts contain Tracked Changes. " //
               + " Please accept or reject and turn off track changes, then recommit : [%s]\n\n",
            trackedChanges.toString());
      }
      if (!applicabilityTags.isEmpty()) {
         String temp = String.format(
            "Commit Branch Failed. The following artifacts have inconsistent start and end applicability tags " //
               + "or the feature value pair is not valid based on the Product Line feature definition artifact. " //
               + "Please fix the tags, then recommit: [%s]",
            applicabilityTags.toString());

         err = (err == null) ? temp : err + temp;
      }

      if (err != null) {
         throw new OseeCoreException(err);
      }

      OseeValidator validator = OseeValidator.getInstance();
      for (Artifact artifactChanged : changedArtifacts) {
         if (!artifactChanged.isDeleted()) {
            IStatus status = validator.validate(IOseeValidator.LONG, artifactChanged);
            if (status.getSeverity() == IStatus.ERROR) {
               throw new OseeWrappedException(getArtifactErrorMessage(artifactChanged) + " " + status.getMessage(),
                  status.getException());
            }
         }
      }
   }

   private String getArtifactErrorMessage(Artifact artifact) {
      return String.format("Error validating: [(%s)(%s) - %s] on branchUuid:[%s]", artifact.getArtId(),
         artifact.getGuid(), artifact.getName(), artifact.getBranch());
   }

   private HashCollection<String, String> getValidFeatureValuesForBranch(BranchId branch) {
      List<FeatureDefinitionData> featureDefinitionData =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch).getFeatureDefinitionData();

      HashCollection<String, String> validFeatureValues = new HashCollection<>();
      for (FeatureDefinitionData feat : featureDefinitionData) {
         validFeatureValues.put(feat.getName(), feat.getValues());
      }

      return validFeatureValues;
   }

}
