/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.goal;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.workflow.state.StateTypeAdapter;

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
