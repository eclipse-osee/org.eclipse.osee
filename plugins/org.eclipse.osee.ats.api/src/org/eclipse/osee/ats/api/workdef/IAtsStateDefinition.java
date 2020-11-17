/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workdef;

import java.util.List;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;

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

   void setLayoutItems(List<IAtsLayoutItem> layoutToSet);

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

   void addTransitionListener(IAtsTransitionHook listener);

   List<IAtsTransitionHook> getTransitionListeners();

   List<StateOption> getStateOptions();

   void setStateOptions(List<StateOption> stateOptions);

}
