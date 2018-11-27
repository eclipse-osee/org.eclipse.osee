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
import org.eclipse.osee.framework.core.util.result.XResultData;
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
   public void validate(ArtifactToken artifact, XResultData results) {
      try {
         int count =
            atsApi.getRelationResolver().getRelatedCount(artifact, CoreRelationTypes.Default_Hierarchical__Parent);
         if (count == 0) {
            results.errorf("%s is orphaned (no parent on Default Hierarchy).", artifact.toStringWithId());
         } else if (count > 1) {
            results.errorf("%s has %s parents (duplicate parents on Default Hierarchy).", artifact.toStringWithId(),
               count);
         }
      } catch (Exception ex) {
         results.errorf("Exception on orphaned and duplicate parent check on %s.  Exception %s",
            artifact.toStringWithId(), Lib.exceptionToString(ex));
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
