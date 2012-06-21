/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorkDefUtil {

   public static boolean isStateWeightingEnabled(IAtsWorkDefinition workDef) {
      for (IAtsStateDefinition stateDef : workDef.getStates()) {
         if (stateDef.getStateWeight() != 0) {
            return true;
         }
      }
      return false;
   }

   public static Collection<String> getStateNames(IAtsWorkDefinition workDef) {
      List<String> names = new ArrayList<String>();
      for (IAtsStateDefinition state : workDef.getStates()) {
         names.add(state.getName());
      }
      return names;
   }

   public static List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef) {
      List<IAtsStateDefinition> orderedPages = new ArrayList<IAtsStateDefinition>();
      List<IAtsStateDefinition> unOrderedPages = new ArrayList<IAtsStateDefinition>();
      for (int x = 1; x < workDef.getStates().size() + 1; x++) {
         for (IAtsStateDefinition state : workDef.getStates()) {
            if (state.getOrdinal() == x) {
               orderedPages.add(state);
            } else if (state.getOrdinal() == 0 && !unOrderedPages.contains(state)) {
               unOrderedPages.add(state);
            }
         }
      }
      orderedPages.addAll(unOrderedPages);
      return orderedPages;
   }

   public static List<IAtsStateDefinition> getStatesOrderedByDefaultToState(IAtsWorkDefinition workDef) {
      if (workDef.getStartState() == null) {
         throw new IllegalArgumentException("Can't locate Start State for workflow " + workDef.getName());
      }

      // Get ordered pages starting with start page
      List<IAtsStateDefinition> orderedPages = new ArrayList<IAtsStateDefinition>();
      getStatesOrderedByDefaultToState(workDef, workDef.getStartState(), orderedPages);

      // Move completed to the end if it exists
      IAtsStateDefinition completedPage = null;
      for (IAtsStateDefinition stateDefinition : orderedPages) {
         if (stateDefinition.getStateType().isCompletedState()) {
            completedPage = stateDefinition;
         }
      }
      if (completedPage != null) {
         orderedPages.remove(completedPage);
         orderedPages.add(completedPage);
      }
      return orderedPages;
   }

   public static void getStatesOrderedByDefaultToState(IAtsWorkDefinition workDef, IAtsStateDefinition stateDefinition, List<IAtsStateDefinition> pages) {
      if (pages.contains(stateDefinition)) {
         return;
      }
      // Add this page first
      pages.add(stateDefinition);
      // Add default page
      IAtsStateDefinition defaultToState = stateDefinition.getDefaultToState();
      if (defaultToState != null && !defaultToState.getName().equals(stateDefinition.getName())) {
         getStatesOrderedByDefaultToState(workDef, stateDefinition.getDefaultToState(), pages);
      }
      // Add remaining pages
      for (IAtsStateDefinition stateDef : stateDefinition.getToStates()) {
         if (!pages.contains(stateDef)) {
            getStatesOrderedByDefaultToState(workDef, stateDef, pages);
         }
      }
   }

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   public static List<IAtsWidgetDefinition> getWidgetsFromLayoutItems(IAtsStateDefinition stateDef) {
      List<IAtsWidgetDefinition> widgets = new ArrayList<IAtsWidgetDefinition>();
      getWidgets(stateDef, widgets, stateDef.getLayoutItems());
      return widgets;
   }

   private static void getWidgets(IAtsStateDefinition stateDef, List<IAtsWidgetDefinition> widgets, List<IAtsLayoutItem> stateItems) {
      for (IAtsLayoutItem stateItem : stateItems) {
         if (stateItem instanceof IAtsCompositeLayoutItem) {
            getWidgets(stateDef, widgets, ((IAtsCompositeLayoutItem) stateItem).getaLayoutItems());
         } else if (stateItem instanceof IAtsWidgetDefinition) {
            widgets.add((IAtsWidgetDefinition) stateItem);
         }
      }
   }

   public static boolean hasWidgetNamed(IAtsStateDefinition stateDef, String name) {
      for (IAtsWidgetDefinition widgetDef : getWidgetsFromLayoutItems(stateDef)) {
         if (widgetDef.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }
}
