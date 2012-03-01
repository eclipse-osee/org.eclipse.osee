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
package org.eclipse.osee.ats.artifact;

import java.util.List;
import org.eclipse.osee.ats.core.workflow.WorkPageAdapter;
import org.eclipse.osee.ats.core.workflow.WorkPageType;

public class GoalState extends WorkPageAdapter {
   public static GoalState InWork = new GoalState("InWork", WorkPageType.Working);
   public static GoalState Completed = new GoalState("Completed", WorkPageType.Completed);
   public static GoalState Cancelled = new GoalState("Cancelled", WorkPageType.Cancelled);

   private GoalState(String pageName, WorkPageType workPageType) {
      super(GoalState.class, pageName, workPageType);
   }

   public static GoalState valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(GoalState.class, pageName);
   }

   public List<GoalState> values() {
      return WorkPageAdapter.pages(GoalState.class);
   }

};
