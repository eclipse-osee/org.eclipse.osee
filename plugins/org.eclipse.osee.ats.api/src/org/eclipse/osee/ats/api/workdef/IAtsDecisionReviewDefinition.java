/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

import java.util.List;

public interface IAtsDecisionReviewDefinition {

   /**
    * Identification
    */
   public abstract String getName();

   public abstract String getDescription();

   /**
    * Created Review Options
    */
   public abstract String getReviewTitle();

   public abstract String getRelatedToState();

   public abstract ReviewBlockType getBlockingType();

   public abstract StateEventType getStateEventType();

   public abstract boolean isAutoTransitionToDecision();

   public abstract List<String> getAssignees();

   public abstract List<IAtsDecisionReviewOption> getOptions();

   @Override
   public abstract String toString();

}