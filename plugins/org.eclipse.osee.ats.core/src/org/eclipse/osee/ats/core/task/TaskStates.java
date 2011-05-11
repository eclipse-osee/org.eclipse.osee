package org.eclipse.osee.ats.core.task;

import java.util.List;
import org.eclipse.osee.framework.core.util.WorkPageAdapter;
import org.eclipse.osee.framework.core.util.WorkPageType;

public class TaskStates extends WorkPageAdapter {
   public static TaskStates InWork = new TaskStates("InWork", WorkPageType.Working);
   public static TaskStates Completed = new TaskStates("Completed", WorkPageType.Completed);
   public static TaskStates Cancelled = new TaskStates("Cancelled", WorkPageType.Cancelled);

   private TaskStates(String pageName, WorkPageType workPageType) {
      super(TaskStates.class, pageName, workPageType);
   }

   public static TaskStates valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(TaskStates.class, pageName);
   }

   public List<TaskStates> values() {
      return WorkPageAdapter.pages(TaskStates.class);
   }

};
