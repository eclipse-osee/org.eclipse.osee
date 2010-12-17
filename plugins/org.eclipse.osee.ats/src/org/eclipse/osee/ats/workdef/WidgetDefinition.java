/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

public class WidgetDefinition extends StateItem {

   private String storeName;
   private String toolTip;
   private String description;
   private int height;
   private String xWidgetName;
   private String defaultValue;
   private final WidgetOptionHandler options = new WidgetOptionHandler();

   public WidgetDefinition(String name) {
      super(name);
   }

   public String getStoreName() {
      return storeName;
   }

   public void setStoreName(String storeName) {
      this.storeName = storeName;
   }

   public String getToolTip() {
      return toolTip;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   public boolean is(WidgetOption widgetOption) {
      return options.contains(widgetOption);
   }

   public void set(WidgetOption widgetOption) {
      options.add(widgetOption);
   }

   public String getXWidgetName() {
      return xWidgetName;
   }

   public void setXWidgetName(String xWidgetName) {
      this.xWidgetName = xWidgetName;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public void setDescription(String description) {
      this.description = description;
   }

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]", getName(), getStoreName());
   }

   public WidgetOptionHandler getOptions() {
      return options;
   }

}
