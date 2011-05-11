/*
 * Created on May 5, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TransitionToMenu {
   public static MenuManager createTransitionToMenuManager(final XViewer xViewer, String name, final Collection<TreeItem> selectedTreeItems) {
      MenuManager editMenuManager =
         new MenuManager(name, ImageManager.getImageDescriptor(AtsImage.TRANSITION), "transition-to");
      final Set<AbstractWorkflowArtifact> awas = new HashSet<AbstractWorkflowArtifact>();
      Set<StateDefinition> toStateDefs = new HashSet<StateDefinition>();
      for (TreeItem treeItem : selectedTreeItems) {
         if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) treeItem.getData();
            awas.add(awa);
            toStateDefs.addAll(awa.getStateDefinition().getToStates());
         }
      }
      if (toStateDefs.isEmpty()) {
         editMenuManager.add(new Action("No Transitionable Selections",
            ImageManager.getImageDescriptor(AtsImage.TRANSITION)) {

            @Override
            public void run() {
               AWorkbench.popup("Error", "No selection is in a transitionable state or transitionable together.");
            }

         });
      } else {
         Map<String, StateDefinition> nameToState = new HashMap<String, StateDefinition>();
         for (final StateDefinition stateDef : toStateDefs) {
            nameToState.put(stateDef.getPageName(), stateDef);
         }
         String[] toStates = nameToState.keySet().toArray(new String[nameToState.size()]);
         Arrays.sort(toStates);
         for (String toState : toStates) {
            final StateDefinition stateDef = nameToState.get(toState);
            editMenuManager.add(new Action(getTransitionToString(stateDef),
               ImageManager.getImageDescriptor(AtsImage.TRANSITION)) {

               @Override
               public void run() {
                  TransitionToOperation operation =
                     new TransitionToOperation("Transition-To " + stateDef, awas, stateDef.getName());
                  Operations.executeAsJob(operation, true);
               }

            });
         }
      }
      return editMenuManager;
   }

   private static String getTransitionToString(StateDefinition stateDef) {
      return stateDef.getPageName() + (stateDef.getWorkPageType() == WorkPageType.Working || stateDef.getPageName().equals(
         TeamState.Completed.getPageName()) || stateDef.getPageName().equals(TeamState.Cancelled.getPageName()) ? "" : " (" + stateDef.getWorkPageType().name() + ")");
   }

}
