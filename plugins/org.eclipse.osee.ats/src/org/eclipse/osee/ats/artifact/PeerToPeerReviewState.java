package org.eclipse.osee.ats.artifact;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class PeerToPeerReviewState extends WorkPageAdapter {
   public static PeerToPeerReviewState Prepare = new PeerToPeerReviewState("Prepare", WorkPageType.Working);
   public static PeerToPeerReviewState Review = new PeerToPeerReviewState("Review", WorkPageType.Working);
   public static PeerToPeerReviewState Completed = new PeerToPeerReviewState("Completed", WorkPageType.Completed);

   private PeerToPeerReviewState(String pageName, WorkPageType workPageType) {
      super(PeerToPeerReviewState.class, pageName, workPageType);
   }

   public static PeerToPeerReviewState valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(PeerToPeerReviewState.class, pageName);
   }

   public List<PeerToPeerReviewState> values() {
      return WorkPageAdapter.pages(PeerToPeerReviewState.class);
   }

};
