/*
 * Created on Dec 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Collection;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;

/**
 * Input data object that provides data fields for Edit Status and Transitioning and error checking based on certain
 * criteria.
 * 
 * @author Donald G Dunne
 */
public class TransitionStatusData {

   private Integer percent, defaultPercent;
   private Double additionalHours;
   private boolean applyHoursToEachItem = false, splitHoursBetweenItems = true, showPercent;
   private Collection<? extends AbstractWorkflowArtifact> awas;

   public TransitionStatusData(Collection<? extends AbstractWorkflowArtifact> awas, boolean showPercent) {
      this.awas = awas;
      this.showPercent = showPercent;
   }

   public Result isValid() {
      if (isPercentRequired()) {
         if (isPercentSet()) {
            if (percent < 0 || percent > 99) {
               return new Result("Percent must be between 0 and 99.  Use Transition-To for completed.");
            }
         } else {
            return new Result("Percent must be entered.");
         }
      }
      if (!isHoursSet()) {
         return new Result("Hours must be entered.");
      }
      if (awas.size() > 1) {
         if (!splitHoursBetweenItems && !applyHoursToEachItem) {
            return new Result("Either \"Split Hours Spent\" or \"Apply Hours Spent\" must be selected");
         }
         if (splitHoursBetweenItems && applyHoursToEachItem) {
            return new Result("Select only \"Split Hours Spent\" or \"Apply Hours Spent\"");
         }
      }
      return Result.TrueResult;
   }

   private boolean isPercentSet() {
      return percent != null;
   }

   private boolean isHoursSet() {
      return additionalHours != null;
   }

   public Integer getPercent() {
      return percent;
   }

   public void setPercent(Integer percent) {
      this.percent = percent;
   }

   public Double getAdditionalHours() {
      return additionalHours;
   }

   public void setAdditionalHours(Double additionalHours) {
      this.additionalHours = additionalHours;
   }

   public boolean isSplitHoursBetweenItems() {
      return splitHoursBetweenItems;
   }

   public void setSplitHoursBetweenItems(boolean splitHoursBetweenItems) {
      this.splitHoursBetweenItems = splitHoursBetweenItems;
   }

   public Collection<? extends AbstractWorkflowArtifact> getAwas() {
      return awas;
   }

   public void setAwas(Collection<? extends AbstractWorkflowArtifact> awas) {
      this.awas = awas;
   }

   public boolean isPercentRequired() {
      return showPercent;
   }

   public void setPercentRequired(boolean percentRequired) {
      this.showPercent = percentRequired;
   }

   public Integer getDefaultPercent() {
      return defaultPercent;
   }

   public void setDefaultPercent(Integer defaultPercent) {
      this.defaultPercent = defaultPercent;
   }

   public void setApplyHoursToEachItem(boolean applyHoursToEachItem) {
      this.applyHoursToEachItem = applyHoursToEachItem;
   }

   public boolean isApplyHoursToEachItem() {
      return applyHoursToEachItem;
   }
}
