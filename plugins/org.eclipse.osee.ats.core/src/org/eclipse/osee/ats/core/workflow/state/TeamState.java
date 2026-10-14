/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.core.workflow.state;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;

/**
 * @author Donald G. Dunne
 */
public class TeamState extends StateTypeAdapter {
   public static TeamState Endorse = new TeamState("Endorse", StateType.Working, StateColor.BLACK);
   public static TeamState Monitor = new TeamState("Monitor", StateType.Working, StateColor.BLACK);
   public static TeamState Open = new TeamState("Open", StateType.Working, StateColor.BLACK);
   public static TeamState Prepare = new TeamState("Prepare", StateType.Working, StateColor.BLACK);
   public static TeamState Analyze = new TeamState("Analyze", StateType.Working, StateColor.DARK_CYAN);
   public static TeamState Analyzed = new TeamState("Analyzed", StateType.Working, StateColor.DARK_CYAN);
   public static TeamState Authorize = new TeamState("Authorize", StateType.Working, StateColor.BLACK);
   public static TeamState Implement = new TeamState("Implement", StateType.Working, StateColor.DARK_BLUE);
   public static TeamState Review = new TeamState("Review", StateType.Working, StateColor.DARK_YELLOW);
   public static TeamState Test = new TeamState("Test", StateType.Working, StateColor.DARK_YELLOW);
   public static TeamState Completed = new TeamState("Completed", StateType.Completed, StateColor.DARK_GREEN);
   public static TeamState Closed = new TeamState("Closed", StateType.Completed, StateColor.DARK_GREEN);
   public static TeamState Cancelled = new TeamState("Cancelled", StateType.Cancelled, StateColor.DARK_RED);
   public static TeamState Validate = new TeamState("Validate", StateType.Working, StateColor.BLACK);

   private TeamState(String name, StateType StateType, StateColor stateColor) {
      super(TeamState.class, name, StateType, stateColor);
   }

   public static TeamState valueOf(String name) {
      return StateTypeAdapter.valueOfPage(TeamState.class, name);
   }

   public static List<TeamState> values() {
      return StateTypeAdapter.pages(TeamState.class);
   }

   public boolean isState(String stateName) {
      return getName().equals(stateName);
   }

   /**
    * Resolves the color for a state definition in order: explicit StateDef color (from andColor()) → TeamState default
    * for the state name → BLACK.
    */
   public static StateColor resolveStateColor(StateDefinition stateDef) {
      if (stateDef == null) {
         return StateColor.BLACK;
      }
      if (stateDef.getColor() != null) {
         return stateDef.getColor();
      }
      TeamState teamState = valueOf(stateDef.getName());
      if (teamState != null && teamState.getStateColor() != null) {
         return teamState.getStateColor();
      }
      return StateColor.BLACK;
   }

   public static boolean matches(String stateName, TeamState... states) {
      for (TeamState state : states) {
         if (stateName.equals(state.getName())) {
            return true;
         }
      }
      return false;
   }

}
