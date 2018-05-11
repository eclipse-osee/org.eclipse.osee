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
package org.eclipse.osee.ats.core.workflow.state;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class TeamState extends StateTypeAdapter {
   public static TeamState Endorse = new TeamState("Endorse", StateType.Working);
   public static TeamState Analyze = new TeamState("Analyze", StateType.Working);
   public static TeamState Authorize = new TeamState("Authorize", StateType.Working);
   public static TeamState Implement = new TeamState("Implement", StateType.Working);
   public static TeamState Completed = new TeamState("Completed", StateType.Completed);
   public static TeamState Cancelled = new TeamState("Cancelled", StateType.Cancelled);

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
