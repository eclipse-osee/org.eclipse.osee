/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;

/**
 * @author Donald G. Dunne
 */
public class WorkItemXWidgetDefinition extends WorkItemDefinition {

   public static String tagName = DynamicXWidgetLayout.XWIDGET;

   /**
    * @param name
    * @param itemId
    * @param parentItemId
    * @param data
    */
   public WorkItemXWidgetDefinition(DynamicXWidgetLayoutData xWidgetLayoutData) {
      super(xWidgetLayoutData.getLayoutName(), xWidgetLayoutData.getLayoutName(), null);
      setData(xWidgetLayoutData);
   }

   public WorkItemXWidgetDefinition(String name, String id) {
      super(name, id, null);
   }

   public DynamicXWidgetLayoutData get() {
      return (DynamicXWidgetLayoutData) getData();
   }

   public void set(DynamicXWidgetLayoutData xWidgetLayoutData) {
      setData(xWidgetLayoutData);
   }

   public static boolean isWorkItemXWidget(String xml) {
      return xml.contains(tagName);
   }

   public String toXml() {
      throw new IllegalStateException("WorkItemXWidgetDefinition.toXml() Not implemented.");
   }

   /**
    * Create WorkItemXWidgetDefinition from xml
    * 
    * @param xml <XWidget displayName="Problem" storageName="ats.Problem" xwidgetType="XTextDam" fill="Vertically"/>
    * @return
    * @throws Exception
    */
   public static WorkItemXWidgetDefinition createFromXml(String xml) throws Exception {
      List<DynamicXWidgetLayoutData> xWidgetLayoutDatas = XWidgetParser.extractWorkAttributes(null, xml);
      if (xWidgetLayoutDatas.size() == 0) throw new IllegalArgumentException(
            "Unable to create WorkItemXWidgetDefinition from xml\"" + xml + "\"");
      if (xWidgetLayoutDatas.size() > 1) throw new IllegalArgumentException(
            "Multiple WorkItemXWidgetDefinitions created from xml\"" + xml + "\"");
      DynamicXWidgetLayoutData data = xWidgetLayoutDatas.iterator().next();
      return new WorkItemXWidgetDefinition(data);
   }
}
