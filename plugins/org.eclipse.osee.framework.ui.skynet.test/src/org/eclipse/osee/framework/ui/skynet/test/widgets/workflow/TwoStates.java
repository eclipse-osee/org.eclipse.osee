package org.eclipse.osee.framework.ui.skynet.test.widgets.workflow;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class TwoStates extends WorkPageAdapter {

   public static TwoStates Endorse = new TwoStates("Endorse", WorkPageType.Working);
   public static TwoStates Cancelled = new TwoStates("Cancelled", WorkPageType.Cancelled);
   public static TwoStates Completed = new TwoStates("Completed", WorkPageType.Completed);

   public TwoStates(String pageName, WorkPageType workPageType) {
      super(TwoStates.class, pageName, workPageType);
   }

   public static TwoStates valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(TwoStates.class, pageName);
   }

   public static List<TwoStates> values() {
      return WorkPageAdapter.pages(TwoStates.class);
   }

}
