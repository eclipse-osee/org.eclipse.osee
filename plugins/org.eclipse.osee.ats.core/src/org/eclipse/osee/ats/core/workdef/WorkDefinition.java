/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.core.internal.Activator;

public class WorkDefinition extends AbstractWorkDefItem {

   private final List<StateDefinition> states = new ArrayList<StateDefinition>(5);
   private final List<RuleDefinition> rules = new ArrayList<RuleDefinition>(5);
   private final Set<String> ids = new HashSet<String>();
   private StateDefinition startState;
   private String version;

   public WorkDefinition(String name) {
      super(name);
      ids.add(name);
   }

   public List<StateDefinition> getStates() {
      return states;
   }

   public List<StateDefinition> getStatesOrdered() {
      if (startState == null) {
         throw new IllegalArgumentException("Can't locate Start State for workflow " + getName());
      }

      // Get ordered pages starting with start page
      List<StateDefinition> orderedPages = new ArrayList<StateDefinition>();
      getOrderedStates(startState, orderedPages);

      // Move completed to the end if it exists
      StateDefinition completedPage = null;
      for (StateDefinition stateDefinition : orderedPages) {
         if (stateDefinition.isCompletedPage()) {
            completedPage = stateDefinition;
         }
      }
      if (completedPage != null) {
         orderedPages.remove(completedPage);
         orderedPages.add(completedPage);
      }
      // for (WorkPage wPage : orderedPages)
      //    System.out.println("Ordered Page: - " + wPage);
      return orderedPages;
   }

   private void getOrderedStates(StateDefinition stateDefinition, List<StateDefinition> pages) {
      if (pages.contains(stateDefinition)) {
         return;
      }
      // Add this page first
      pages.add(stateDefinition);
      // Add default page
      StateDefinition defaultToState = getDefaultToState(stateDefinition);
      if (defaultToState != null && !defaultToState.getName().equals(stateDefinition.getName())) {
         getOrderedStates(getDefaultToState(stateDefinition), pages);
      }
      // Add remaining pages
      for (StateDefinition stateDef : stateDefinition.getToStates()) {
         if (!pages.contains(stateDef)) {
            getOrderedStates(stateDef, pages);
         }
      }
   }

   public StateDefinition getDefaultToState(StateDefinition stateDefinition) {
      return stateDefinition.getDefaultToState();
   }

   public Collection<String> getStateNames() {
      List<String> names = new ArrayList<String>();
      for (StateDefinition state : states) {
         names.add(state.getName());
      }
      return names;
   }

   public StateDefinition getStateByName(String name) {
      for (StateDefinition state : states) {
         if (state.getName().equals(name)) {
            return state;
         }
      }
      return null;
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

   public List<RuleDefinition> getRules() {
      return rules;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
      AbstractWorkDefItem other = (AbstractWorkDefItem) obj;
      if (getName() == null) {
         if (other.getName() != null) {
            return false;
         }
      } else if (!getName().equals(other.getName())) {
         return false;
      }
      return true;
   }

   public StateDefinition getOrCreateState(String name) {
      StateDefinition stateDef = getStateByName(name);
      if (stateDef == null) {
         stateDef = new StateDefinition(name);
         getStates().add(stateDef);
      }
      return stateDef;
   }

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public StateDefinition getStartState() {
      return startState;
   }

   public void setStartState(StateDefinition startState) {
      this.startState = startState;
   }

   public Set<String> getIds() {
      return ids;
   }

   public boolean isStateWeightingEnabled() {
      for (StateDefinition stateDef : getStates()) {
         if (stateDef.getStateWeight() != 0) {
            return true;
         }
      }
      return false;
   }

   public IStatus validateStateWeighting() {
      if (!isStateWeightingEnabled()) {
         return Status.OK_STATUS;
      }
      int total = 0;
      for (StateDefinition stateDef : getStates()) {
         total += stateDef.getStateWeight();
      }
      if (total != 100) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
            "Total weight only %d, needs to equal 100 for all states", total));
      }
      return Status.OK_STATUS;
   }

}
