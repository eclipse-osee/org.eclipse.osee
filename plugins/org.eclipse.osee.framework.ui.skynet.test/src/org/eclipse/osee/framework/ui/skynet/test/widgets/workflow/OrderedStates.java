package org.eclipse.osee.framework.ui.skynet.test.widgets.workflow;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public class OrderedStates extends WorkPageAdapter {

   public static OrderedStates One = new OrderedStates("One", WorkPageType.Working);
   public static OrderedStates Two = new OrderedStates("Two", WorkPageType.Working);
   public static OrderedStates Three = new OrderedStates("Three", WorkPageType.Working);
   public static OrderedStates Four = new OrderedStates("Four", WorkPageType.Working);
   public static OrderedStates Five = new OrderedStates("Five", WorkPageType.Working);
   public static OrderedStates Six = new OrderedStates("Six", WorkPageType.Working);
   public static OrderedStates Cancelled = new OrderedStates("Cancelled", WorkPageType.Cancelled);
   public static OrderedStates Completed = new OrderedStates("Completed", WorkPageType.Completed);

   public OrderedStates(String pageName, WorkPageType workPageType) {
      super(OrderedStates.class, pageName, workPageType);
   }

   public static OrderedStates valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(OrderedStates.class, pageName);
   }

   public static List<OrderedStates> values() {
      return WorkPageAdapter.pages(OrderedStates.class);
   }

}
