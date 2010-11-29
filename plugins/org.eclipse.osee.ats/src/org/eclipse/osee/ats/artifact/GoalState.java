package org.eclipse.osee.ats.artifact;

import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

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

   public Set<GoalState> values() {
      return WorkPageAdapter.pages(GoalState.class);
   }

};
