/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class StateDefinition extends AbstractWorkDefItem implements IWorkPage {

   private boolean startState = false;
   private WorkPageType workPageType;
   private int ordinal = 0;
   private final List<StateItem> stateItems = new ArrayList<StateItem>(5);
   private final List<RuleDefinition> rules = new ArrayList<RuleDefinition>(5);
   private final List<StateDefinition> toStates = new ArrayList<StateDefinition>(5);
   private final List<StateDefinition> defaultToStates = new ArrayList<StateDefinition>(5);
   private final List<StateDefinition> returnStates = new ArrayList<StateDefinition>(5);
   private WorkDefinition workDefinition;
   protected TaskResolutionOptionRule taskResolutionOptions;

   public StateDefinition(String name) {
      super(name);
   }

   public boolean isStartState() {
      return startState;
   }

   public void setStartState(boolean startState) {
      this.startState = startState;
   }

   public List<StateItem> getStateItems() {
      return stateItems;
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

   public List<StateDefinition> getReturnStates() {
      return returnStates;
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

   public List<StateDefinition> getDefaultToStates() {
      return defaultToStates;
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]", getName(), getWorkPageType());
   }

   /**
    * Returns fully qualified name of <work definition name>.<this state name
    */
   public String getFullName() {
      return workDefinition.getName() + "." + getName();
   }
}
