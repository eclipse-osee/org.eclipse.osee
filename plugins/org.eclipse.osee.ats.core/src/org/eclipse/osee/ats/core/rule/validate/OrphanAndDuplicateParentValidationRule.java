/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.rule.validate;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Megumi Telles
 * @author Donald G. .Dunne
 */
public class OrphanAndDuplicateParentValidationRule extends AbstractValidationRule {

   public OrphanAndDuplicateParentValidationRule(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData rd) {
      try {
         if (!atsApi.getStoreService().isHistorical(artifact)) {
            int count =
               atsApi.getRelationResolver().getRelatedCount(artifact, CoreRelationTypes.DefaultHierarchical_Parent);
            if (count == 0 && !CoreArtifactTokens.DefaultHierarchyRoot.equals(artifact)) {
               logError(artifact, "is orphaned (no parent on Default Hierarchy).", rd);
            } else if (count > 1) {
               logError(artifact, String.format("has %s parents (duplicate parents on Default Hierarchy).", count), rd);
            }
         }
      } catch (Exception ex) {
         String errStr =
            String.format("had exception on orphaned and duplicate parent check: %s", Lib.exceptionToString(ex));
         logError(artifact, errStr, rd);
      }
   }

   @Override
   public String getRuleDescription() {
      return "All Errors reported must be fixed.";
   }

   @Override
   public String getRuleTitle() {
      return "Orphan / Duplicate Parent Validation Checks";
   }

}
