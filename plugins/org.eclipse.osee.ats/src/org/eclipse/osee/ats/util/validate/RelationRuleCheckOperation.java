/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
final class RelationRuleCheckOperation extends AbstractOperation {
   private final Collection<Artifact> itemsToCheck;
   private final XResultData rd;
   private final Set<RelationSetRule> relationSetRules;

   public RelationRuleCheckOperation(Collection<Artifact> itemsToCheck, XResultData rd, Set<RelationSetRule> relationSetRules) {
      super("Relation Check", Activator.PLUGIN_ID);
      this.itemsToCheck = itemsToCheck;
      this.rd = rd;
      this.relationSetRules = relationSetRules;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      rd.log("\n" + getName());
      Collection<String> warnings = new ArrayList<String>();
      double total = itemsToCheck.size() + relationSetRules.size();
      if (total > 0) {
         int workAmount = calculateWork(1 / total);
         for (Artifact art : itemsToCheck) {
            // Validate relations set
            for (RelationSetRule relationSetRule : relationSetRules) {
               checkForCancelledStatus(monitor);
               ValidationResult result = relationSetRule.validate(art, monitor);
               if (!result.didValidationPass()) {
                  for (String errorMsg : result.getErrorMessages()) {
                     if (art.isOfType(CoreArtifactTypes.DirectSoftwareRequirement)) {
                        rd.logError(errorMsg);
                     } else {
                        warnings.add(errorMsg);
                     }
                  }
               }
               monitor.worked(workAmount);
            }
         }
         // flag warnings
         for (String warning : warnings) {
            rd.logWarning(warning);
         }
      }
   }
}