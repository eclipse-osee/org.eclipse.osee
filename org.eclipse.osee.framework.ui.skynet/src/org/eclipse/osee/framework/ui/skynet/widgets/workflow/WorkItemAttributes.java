/*
 * Created on Jun 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

/**
 * @author Donald G. Dunne
 */
public class WorkItemAttributes {

   public static WorkItemAttributes WORK_ID = new WorkItemAttributes("Work Id");
   public static WorkItemAttributes WORK_PARENT_ID = new WorkItemAttributes("Work Parent Id");
   public static WorkItemAttributes WORK_DATA = new WorkItemAttributes("Work Data");
   public static WorkItemAttributes WORK_PAGE_NAME = new WorkItemAttributes("Work Page Name");
   public static WorkItemAttributes WORK_TYPE = new WorkItemAttributes("Work Type");
   public static WorkItemAttributes TRANSITION = new WorkItemAttributes("Transition");
   public static WorkItemAttributes START_PAGE = new WorkItemAttributes("Start Page");

   private String attributeTypeName;

   public WorkItemAttributes(String name, String attributeTypeName) {
      this.attributeTypeName = attributeTypeName;
   }

   /**
    * Creates attribute with displayName = "<name>" and storeName = "osee.wi.<name>"
    * 
    * @param name
    */
   public WorkItemAttributes(String name) {
      this(name, "osee.wi." + name);
   }

   public String getAttributeTypeName() {
      return attributeTypeName;
   }

}
