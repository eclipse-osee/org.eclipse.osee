/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.List;

public interface IAtsStateDefinition extends IStateToken {

   /**
    * Identification
    */
   @Override
   public abstract String getName();

   public abstract void setName(String string);

   /**
    * Returns fully qualified name of <work definition name>.<this state name>
    */
   public abstract String getFullName();

   public abstract int getOrdinal();

   public abstract void setOrdinal(int ordinal);

   /**
    * State Type
    */
   @Override
   public abstract StateType getStateType();

   public abstract void setStateType(StateType StateType);

   /**
    * Layout
    */
   public abstract List<IAtsLayoutItem> getLayoutItems();

   /**
    * States
    */
   public abstract List<IAtsStateDefinition> getToStates();

   public abstract IAtsStateDefinition getDefaultToState();

   public abstract void setDefaultToState(IAtsStateDefinition defaultToState);

   /**
    * Parent Work Definition
    */
   public abstract IAtsWorkDefinition getWorkDefinition();

   public abstract void setWorkDefinition(IAtsWorkDefinition workDefinition);

   public abstract List<IAtsStateDefinition> getOverrideAttributeValidationStates();

   @Override
   public abstract int hashCode();

   @Override
   public abstract boolean equals(Object obj);

   /**
    * Set how much (of 100%) this state's percent complete will contribute to the full percent complete of work
    * definitions.
    * 
    * @param percentWeight int value where all stateWeights in workdefinition == 100
    */
   public abstract void setStateWeight(int percentWeight);

   public abstract int getStateWeight();

   public abstract void setRecommendedPercentComplete(int recommendedPercentComplete);

   public abstract Integer getRecommendedPercentComplete();

   public abstract void setColor(StateColor stateColor);

   public abstract StateColor getColor();

   /**
    * Reviews
    */
   public abstract List<IAtsDecisionReviewDefinition> getDecisionReviews();

   public abstract List<IAtsPeerReviewDefinition> getPeerReviews();

   /**
    * Rules
    */
   public abstract void removeRule(String rule);

   public abstract void addRule(String rule);

   public abstract List<String> getRules();

   public abstract boolean hasRule(String name);

   /**
    * Misc
    */
   @Override
   public abstract String toString();

}