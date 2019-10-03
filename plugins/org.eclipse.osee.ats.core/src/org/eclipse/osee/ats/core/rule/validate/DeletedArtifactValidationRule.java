/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.rule.validate;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
public class DeletedArtifactValidationRule extends AbstractValidationRule {

   public DeletedArtifactValidationRule(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData results) {
      if (atsApi.getStoreService().isDeleted(artifact)) {
         ArtifactToken relatedArtifact =
            atsApi.getRelationResolver().getRelatedOrSentinel(artifact, CoreRelationTypes.DefaultHierarchical_Parent);
         if (relatedArtifact.isValid()) {
            String errStr = "is deleted but still has a parent relation.  Please delete the relation.";
            logError(artifact, errStr, results);
         }
      }
   }

   @Override
   public String getRuleDescription() {
      return "Artifact was deleted, but the parent relation still exists.";
   }

   @Override
   public String getRuleTitle() {
      return "Deleted Artifact Validation Checks";
   }

}
