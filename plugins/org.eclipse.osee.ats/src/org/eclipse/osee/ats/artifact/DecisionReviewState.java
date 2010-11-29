package org.eclipse.osee.ats.artifact;

import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class DecisionReviewState extends WorkPageAdapter {
   public static DecisionReviewState Prepare = new DecisionReviewState("Prepare", WorkPageType.Working);
   public static DecisionReviewState Decision = new DecisionReviewState("Decision", WorkPageType.Working);
   public static DecisionReviewState Followup = new DecisionReviewState("Followup", WorkPageType.Working);
   public static DecisionReviewState Completed = new DecisionReviewState("Completed", WorkPageType.Completed);

   private DecisionReviewState(String pageName, WorkPageType workPageType) {
      super(DecisionReviewState.class, pageName, workPageType);
   }

   public static DecisionReviewState valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(DecisionReviewState.class, pageName);
   }

   public static Set<DecisionReviewState> values() {
      return WorkPageAdapter.pages(DecisionReviewState.class);
   }

};
