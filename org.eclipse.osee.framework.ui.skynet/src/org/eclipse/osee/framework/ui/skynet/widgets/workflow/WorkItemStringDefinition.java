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
public class WorkItemStringDefinition extends WorkItemDefinition {

   public static String tagName = "WorkItemString";

   /**
    * @param name
    * @param itemId
    * @param parentItemId
    * @param data
    */
   public WorkItemStringDefinition(String name, String itemId, String value) {
      super(name, itemId, null);
      setData(value);
   }

   public String get() {
      return (String) getData();
   }

   public static boolean isWorkItemString(String xml) {
      return xml.contains(tagName);
   }

   public String toXml() {
      return AXml.getNameValueXml(tagName, name, get());
   }

   public static WorkItemStringDefinition createFromXml(String xml) {
      String strs[] = AXml.getNameValue(tagName, xml);
      return new WorkItemStringDefinition(strs[0], strs[0], strs[1]);
   }
}
