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
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class TeamState extends StateTypeAdapter {
   public static TeamState Endorse = new TeamState("Endorse", StateType.Working);
   public static TeamState Open = new TeamState("Open", StateType.Working);
   public static TeamState Prepare = new TeamState("Prepare", StateType.Working);
   public static TeamState Analyze = new TeamState("Analyze", StateType.Working);
   public static TeamState Authorize = new TeamState("Authorize", StateType.Working);
   public static TeamState Implement = new TeamState("Implement", StateType.Working);
   public static TeamState Review = new TeamState("Review", StateType.Working);
   public static TeamState Completed = new TeamState("Completed", StateType.Completed);
   public static TeamState Cancelled = new TeamState("Cancelled", StateType.Cancelled);
   public static TeamState Validate = new TeamState("Validate", StateType.Working);

   private TeamState(String pageName, StateType StateType) {
      super(TeamState.class, pageName, StateType);
   }

   public static TeamState valueOf(String pageName) {
      return StateTypeAdapter.valueOfPage(TeamState.class, pageName);
   }

   public static List<TeamState> values() {
      return StateTypeAdapter.pages(TeamState.class);
   }

}
