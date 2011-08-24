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

import java.util.Collection;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
final class AttributeRuleCheckOperation extends AbstractOperation {
   private final Collection<Artifact> itemsToCheck;
   private final XResultData rd;
   private final Set<AttributeSetRule> attributeSetRules;

   public AttributeRuleCheckOperation(Collection<Artifact> itemsToCheck, XResultData rd, Set<AttributeSetRule> attributeSetRules) {
      super("Attribute Check", Activator.PLUGIN_ID);
      this.itemsToCheck = itemsToCheck;
      this.rd = rd;
      this.attributeSetRules = attributeSetRules;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      rd.log("\n" + getName());
      double total = itemsToCheck.size() + attributeSetRules.size();
      if (total > 0) {
         int workAmount = calculateWork(1 / total);
         for (Artifact art : itemsToCheck) {
            for (AttributeSetRule attributeSetRule : attributeSetRules) {
               checkForCancelledStatus(monitor);
               ValidationResult result = attributeSetRule.validate(art, monitor);
               if (!result.didValidationPass()) {
                  for (String errorMsg : result.getErrorMessages()) {
                     rd.logError(errorMsg);
                  }
               }
               monitor.worked(workAmount);
            }
         }
      }
   }
}