/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateDefinition extends IStateToken {

   /**
    * Identification
    */
   @Override
   public abstract String getName();

   /**
    * Returns fully qualified name of <work definition name>.<this state name>
    */
   public abstract String getFullName();

   public abstract int getOrdinal();

   /**
    * State Type
    */
   @Override
   public abstract StateType getStateType();

   /**
    * Layout
    */
   public abstract List<IAtsLayoutItem> getLayoutItems();

   /**
    * States
    */
   public abstract List<IAtsStateDefinition> getToStates();

   public abstract IAtsStateDefinition getDefaultToState();

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
   public abstract int getStateWeight();

   public abstract Integer getRecommendedPercentComplete();

   public abstract StateColor getColor();

   /**
    * Reviews
    */
   public abstract List<IAtsDecisionReviewDefinition> getDecisionReviews();

   public abstract List<IAtsPeerReviewDefinition> getPeerReviews();

   /**
    * Rules
    */
   public abstract List<String> getRules();

   public abstract boolean hasRule(String name);

   /**
    * Misc
    */
   @Override
   public abstract String toString();

}