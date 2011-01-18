/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

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
   protected TaskResolutionOptionRule taskResolutionOptions;

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

   @Override
   public Integer getDefaultPercent() {
      return null;
   }

   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public void setWorkDefinition(WorkDefinition workDefinition) {
      this.workDefinition = workDefinition;
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

}
