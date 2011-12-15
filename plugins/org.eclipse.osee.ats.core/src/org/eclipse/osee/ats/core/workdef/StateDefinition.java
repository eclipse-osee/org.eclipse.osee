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
package org.eclipse.osee.ats.core.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

public class StateDefinition extends AbstractWorkDefItem implements IWorkPage {

   private WorkPageType workPageType;
   private int ordinal = 0;
   private final List<StateItem> stateItems = new ArrayList<StateItem>(5);
   private final List<RuleDefinition> rules = new ArrayList<RuleDefinition>(5);
   private final HashCollection<RuleDefinition, String> ruleToLocations = new HashCollection<RuleDefinition, String>();
   private final List<StateDefinition> toStates = new ArrayList<StateDefinition>(5);
   private StateDefinition defaultToState;
   private final List<StateDefinition> overrideAttributeValidationStates = new ArrayList<StateDefinition>(5);
   private final List<DecisionReviewDefinition> decisionReviews = new ArrayList<DecisionReviewDefinition>();
   private final List<PeerReviewDefinition> peerReviews = new ArrayList<PeerReviewDefinition>();
   private WorkDefinition workDefinition;
   private int percentWeight = 0;
   private Integer recommendedPercentComplete = null;
   private StateColor color = null;

   public StateDefinition(String name) {
      super(name);
   }

   public List<StateItem> getStateItems() {
      return stateItems;
   }

   public void addRule(RuleDefinition ruleDef, String location) {
      rules.add(ruleDef);
      ruleToLocations.put(ruleDef, location);
   }

   public Collection<String> getRuleLocations(RuleDefinition ruleDef) {
      return ruleToLocations.getValues(ruleDef);
   }

   public List<RuleDefinition> getRules() {
      return rules;
   }

   @Override
   public WorkPageType getWorkPageType() {
      return workPageType;
   }

   public void setWorkPageType(WorkPageType workPageType) {
      this.workPageType = workPageType;
   }

   public int getOrdinal() {
      return ordinal;
   }

   public void setOrdinal(int ordinal) {
      this.ordinal = ordinal;
   }

   @Override
   public boolean isCompletedOrCancelledPage() {
      return getWorkPageType().isCompletedOrCancelledPage();
   }

   @Override
   public boolean isCompletedPage() {
      return getWorkPageType().isCompletedPage();
   }

   @Override
   public boolean isCancelledPage() {
      return getWorkPageType().isCancelledPage();
   }

   @Override
   public boolean isWorkingPage() {
      return getWorkPageType().isWorkingPage();
   }

   public List<StateDefinition> getToStates() {
      return toStates;
   }

   @Override
   public String getPageName() {
      return getName();
   }

   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public void setWorkDefinition(WorkDefinition workDefinition) {
      this.workDefinition = workDefinition;
   }

   public boolean hasRule(RuleDefinitionOption option) {
      return hasRule(option.name());
   }

   public boolean hasRule(String name) {
      for (RuleDefinition rule : rules) {
         if (rule.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   public List<RuleDefinition> getRulesStartsWith(String name) {
      List<RuleDefinition> results = new ArrayList<RuleDefinition>();
      for (RuleDefinition rule : rules) {
         if (rule.getName().startsWith(name)) {
            results.add(rule);
         }
      }
      return results;
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]", getName(), getWorkPageType());
   }

   /**
    * Returns fully qualified name of <work definition name>.<this state name
    */
   public String getFullName() {
      if (workDefinition != null) {
         return workDefinition.getName() + "." + getName();
      }
      return getName();
   }

   public List<StateDefinition> getOverrideAttributeValidationStates() {
      return overrideAttributeValidationStates;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getFullName() == null) ? 0 : getFullName().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      StateDefinition other = (StateDefinition) obj;
      if (getFullName() == null) {
         if (other.getFullName() != null) {
            return false;
         }
      } else if (!getFullName().equals(other.getFullName())) {
         return false;
      }
      return true;
   }

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   public List<WidgetDefinition> getWidgetsFromStateItems() {
      List<WidgetDefinition> widgets = new ArrayList<WidgetDefinition>();
      getWidgets(widgets, getStateItems());
      return widgets;
   }

   private void getWidgets(List<WidgetDefinition> widgets, List<StateItem> stateItems) {
      for (StateItem stateItem : stateItems) {
         if (stateItem instanceof CompositeStateItem) {
            getWidgets(widgets, ((CompositeStateItem) stateItem).getStateItems());
         } else if (stateItem instanceof WidgetDefinition) {
            widgets.add((WidgetDefinition) stateItem);
         }
      }
   }

   public StateDefinition getDefaultToState() {
      return defaultToState;
   }

   public void setDefaultToState(StateDefinition defaultToState) {
      this.defaultToState = defaultToState;
   }

   public List<DecisionReviewDefinition> getDecisionReviews() {
      return decisionReviews;
   }

   public List<PeerReviewDefinition> getPeerReviews() {
      return peerReviews;
   }

   public int getStateWeight() {
      return percentWeight;
   }

   /**
    * Set how much (of 100%) this state's percent complete will contribute to the full percent complete of work
    * definitions.
    * 
    * @param percentWeight int value where all stateWeights in workdefinition == 100
    */
   public void setPercentWeight(int percentWeight) {
      this.percentWeight = percentWeight;
   }

   public void setRecommendedPercentComplete(int recommendedPercentComplete) {
      this.recommendedPercentComplete = recommendedPercentComplete;
   }

   public Integer getRecommendedPercentComplete() {
      return recommendedPercentComplete;
   }

   public void setColor(StateColor stateColor) {
      this.color = stateColor;
   }

   public StateColor getColor() {
      return color;
   }

   public boolean hasWidgetNamed(String name) {
      for (WidgetDefinition widgetDef : getWidgetsFromStateItems()) {
         if (widgetDef.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   public boolean hasWidgetWithXWidgetName(String xWidgetName) {
      for (WidgetDefinition widgetDef : getWidgetsFromStateItems()) {
         if (widgetDef.getXWidgetName().equals(xWidgetName)) {
            return true;
         }
      }
      return false;

   }

   public void removeRule(RuleDefinition ruleToRemove) {
      for (RuleDefinition rule : new CopyOnWriteArrayList<RuleDefinition>(rules)) {
         if (rule.getName().equals(ruleToRemove.getName())) {
            rules.remove(rule);
         }
      }
   }

}
