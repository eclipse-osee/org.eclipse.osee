/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.validate;

import java.util.Collection;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class UniqueNameRuleCheckOperation extends AbstractOperation {
   private final Collection<Artifact> itemsToCheck;
   private final XResultData rd;
   private final Set<UniqueNameRule> rulesToValidate;

   public UniqueNameRuleCheckOperation(Collection<Artifact> itemsToCheck, XResultData rd, Set<UniqueNameRule> rulesToValidate) {
      super("Unique Name Check", Activator.PLUGIN_ID);
      this.itemsToCheck = itemsToCheck;
      this.rd = rd;
      this.rulesToValidate = rulesToValidate;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      rd.log("\n" + getName());
      double total = itemsToCheck.size() + rulesToValidate.size();
      if (total > 0) {
         int workAmount = calculateWork(1 / total);
         for (Artifact art : itemsToCheck) {
            for (UniqueNameRule rule : rulesToValidate) {
               checkForCancelledStatus(monitor);
               ValidationResult result = rule.validate(art, monitor);
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
