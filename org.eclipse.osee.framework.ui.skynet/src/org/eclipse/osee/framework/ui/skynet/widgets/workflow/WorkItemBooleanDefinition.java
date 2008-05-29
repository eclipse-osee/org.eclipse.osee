/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class WorkItemBooleanDefinition extends WorkItemStringDefinition {

   public static String tagName = "WorkItemString";

   /**
    * @param name
    * @param id
    * @param set
    */
   public WorkItemBooleanDefinition(String name, String id, Boolean set) {
      super(name, id, String.valueOf(set));
   }

   public WorkItemBooleanDefinition(String id, Boolean set) {
      super(id, id, String.valueOf(set));
   }

   public Boolean isSet() {
      return (Boolean) getData();
   }

   public static boolean isWorkItemBoolean(String xml) {
      return xml.contains(tagName);
   }

   public String toXml() {
      return AXml.getNameValueXml(tagName, name, String.valueOf(isSet()));
   }

   public static WorkItemStringDefinition createFromXml(String xml) {
      String strs[] = AXml.getNameValue(tagName, xml);
      return new WorkItemStringDefinition(strs[0], strs[0], strs[1]);
   }

}
