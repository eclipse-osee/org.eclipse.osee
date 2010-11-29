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
package org.eclipse.osee.ats.util;

import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class TeamState extends WorkPageAdapter {
   public static TeamState Endorse = new TeamState("Endorse", WorkPageType.Working);
   public static TeamState Analyze = new TeamState("Analyze", WorkPageType.Working);
   public static TeamState Authorize = new TeamState("Authorize", WorkPageType.Working);
   public static TeamState Implement = new TeamState("Implement", WorkPageType.Working);
   public static TeamState Completed = new TeamState("Completed", WorkPageType.Completed);
   public static TeamState Cancelled = new TeamState("Cancelled", WorkPageType.Cancelled);

   private TeamState(String pageName, WorkPageType workPageType) {
      super(TeamState.class, pageName, workPageType);
   }

   public static TeamState valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(TeamState.class, pageName);
   }

   public static Set<TeamState> values() {
      return WorkPageAdapter.pages(TeamState.class);
   }

}
