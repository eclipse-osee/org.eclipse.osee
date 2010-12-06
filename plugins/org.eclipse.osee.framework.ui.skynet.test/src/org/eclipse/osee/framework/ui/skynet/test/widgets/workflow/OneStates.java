package org.eclipse.osee.framework.ui.skynet.test.widgets.workflow;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class OneStates extends WorkPageAdapter {

   public static OneStates Endorse = new OneStates("Endorse", WorkPageType.Working, "This is OneStates Endorse");
   public static OneStates Cancelled = new OneStates("Cancelled", WorkPageType.Cancelled);
   public static OneStates Completed = new OneStates("Completed", WorkPageType.Completed);

   public OneStates(String pageName, WorkPageType workPageType) {
      super(OneStates.class, pageName, workPageType);
   }

   public OneStates(String pageName, WorkPageType workPageType, String description) {
      super(OneStates.class, pageName, workPageType);
      setDescription(description);
   }

   public static OneStates valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(OneStates.class, pageName);
   }

   public static List<OneStates> values() {
      return WorkPageAdapter.pages(OneStates.class);
   }

}
