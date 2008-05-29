/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class WorkPageDefinition extends WorkItemDefinition {

   private List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

   public WorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
   }

   public void addWorkItem(WorkItemDefinition workItemDefinition) {
      workItems.add(workItemDefinition);
   }

   public void removeWorkItem(WorkItemDefinition workItemDefinition) {
      workItems.remove(workItemDefinition);
   }

   /**
    * @return the workItems
    */
   public List<WorkItemDefinition> getWorkItems() {
      return workItems;
   }

   /**
    * @param workItems the workItems to set
    */
   public void setWorkItems(List<WorkItemDefinition> workItems) {
      this.workItems = workItems;
   }
}
