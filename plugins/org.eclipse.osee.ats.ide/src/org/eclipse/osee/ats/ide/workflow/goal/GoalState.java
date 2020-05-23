/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class GoalState extends StateTypeAdapter {
   public static GoalState InWork = new GoalState("InWork", StateType.Working);
   public static GoalState Completed = new GoalState("Completed", StateType.Completed);
   public static GoalState Cancelled = new GoalState("Cancelled", StateType.Cancelled);

   private GoalState(String pageName, StateType StateType) {
      super(GoalState.class, pageName, StateType);
   }

   public static GoalState valueOf(String pageName) {
      return StateTypeAdapter.valueOfPage(GoalState.class, pageName);
   }

   public List<GoalState> values() {
      return StateTypeAdapter.pages(GoalState.class);
   }

};
