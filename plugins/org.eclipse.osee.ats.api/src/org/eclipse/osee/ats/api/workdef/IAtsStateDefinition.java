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

   @Override
   String getName();

   String getFullName();

   int getOrdinal();

   @Override
   StateType getStateType();

   List<IAtsLayoutItem> getLayoutItems();

   List<IAtsStateDefinition> getToStates();

   IAtsStateDefinition getDefaultToState();

   IAtsWorkDefinition getWorkDefinition();

   void setWorkDefinition(IAtsWorkDefinition workDefinition);

   List<IAtsStateDefinition> getOverrideAttributeValidationStates();

   @Override
   int hashCode();

   @Override
   boolean equals(Object obj);

   /**
    * Set how much (of 100%) this state's percent complete will contribute to the full percent complete of work
    * definitions.
    *
    * @param percentWeight int value where all stateWeights in workdefinition == 100
    */
   int getStateWeight();

   Integer getRecommendedPercentComplete();

   StateColor getColor();

   List<IAtsDecisionReviewDefinition> getDecisionReviews();

   List<IAtsPeerReviewDefinition> getPeerReviews();

   List<String> getRules();

   boolean hasRule(String name);

   @Override
   String toString();

}
