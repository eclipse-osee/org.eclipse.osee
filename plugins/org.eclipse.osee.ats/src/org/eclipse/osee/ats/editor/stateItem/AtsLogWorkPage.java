/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.stateItem;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.workdef.StateXWidgetPage;

/**
 * @author Donald G. Dunne
 */
public class AtsLogWorkPage extends StateXWidgetPage {

   public final static String PAGE_ID = "ats.Log";

   public static class EmptyWorkFlowDefinition implements IAtsWorkDefinition {

      private final String name;

      public EmptyWorkFlowDefinition(String name) {
         this.name = name;
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public IAtsStateDefinition getStateByName(String name) {
         return null;
      }

      @Override
      public IAtsStateDefinition getStartState() {
         return null;
      }

      @Override
      public String getId() {
         return "";
      }

      @Override
      public String getDescription() {
         return this.name;
      }

      @Override
      public List<IAtsStateDefinition> getStates() {
         return null;
      }

      @Override
      public String getGuid() {
         return null;
      }

   }

   public AtsLogWorkPage(String title) {
      super(new EmptyWorkFlowDefinition(PAGE_ID), new IAtsStateDefinition() {

         @Override
         public String getDescription() {
            return null;
         }

         @Override
         public void setWorkDefinition(IAtsWorkDefinition workDefinition) {
            // do nothing
         }

         @Override
         public boolean hasRule(String name) {
            return false;
         }

         @Override
         public IAtsWorkDefinition getWorkDefinition() {
            return null;
         }

         @Override
         public List<IAtsStateDefinition> getToStates() {
            return null;
         }

         @Override
         public int getStateWeight() {
            return 0;
         }

         @Override
         public StateType getStateType() {
            return null;
         }

         @Override
         public List<String> getRules() {
            return null;
         }

         @Override
         public Integer getRecommendedPercentComplete() {
            return null;
         }

         @Override
         public List<IAtsPeerReviewDefinition> getPeerReviews() {
            return null;
         }

         @Override
         public List<IAtsStateDefinition> getOverrideAttributeValidationStates() {
            return null;
         }

         @Override
         public int getOrdinal() {
            return 0;
         }

         @Override
         public String getName() {
            return PAGE_ID;
         }

         @Override
         public List<IAtsLayoutItem> getLayoutItems() {
            return null;
         }

         @Override
         public String getFullName() {
            return null;
         }

         @Override
         public IAtsStateDefinition getDefaultToState() {
            return null;
         }

         @Override
         public List<IAtsDecisionReviewDefinition> getDecisionReviews() {
            return null;
         }

         @Override
         public StateColor getColor() {
            return null;
         }
      }, (String) null, null);
   }

}
