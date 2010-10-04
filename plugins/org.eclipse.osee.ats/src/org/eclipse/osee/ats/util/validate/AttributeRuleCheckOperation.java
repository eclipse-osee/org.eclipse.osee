/*
 * Created on Jan 1, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.validate;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
final class AttributeRuleCheckOperation extends AbstractOperation {
   private final Collection<Artifact> itemsToCheck;
   private final XResultData rd;
   private final Set<AttributeSetRule> attributeSetRules;

   public AttributeRuleCheckOperation(Collection<Artifact> itemsToCheck, XResultData rd, Set<AttributeSetRule> attributeSetRules) {
      super("Attribute Check", SkynetGuiPlugin.PLUGIN_ID);
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
               if (attributeSetRule.hasArtifactType(art.getArtifactType())) {
                  // validate attribute is set and not invalidValue
                  List<String> attributeValues = art.getAttributesToStringList(attributeSetRule.getAttributeName());
                  int validValueFound = 0;
                  for (String attributeValue : attributeValues) {
                     checkForCancelledStatus(monitor);
                     String invalidValue = attributeSetRule.getInvalidValue();
                     if (attributeValue.equals(invalidValue)) {
                        rd.logError(ValidateReqChangeReport.getRequirementHyperlink(art) + " has invalid " + invalidValue + " \"" + attributeSetRule.getAttributeName() + "\" attribute");
                     } else {
                        validValueFound++;
                     }
                  }
                  if (validValueFound < attributeSetRule.getMinimumValues()) {
                     rd.logError(ValidateReqChangeReport.getRequirementHyperlink(art) + " has less than minimum " + attributeSetRule.getMinimumValues() + " values set for attribute \"" + attributeSetRule.getAttributeName() + "\"");
                  }
                  monitor.worked(workAmount);
               }
            }
         }
      }
   }
}