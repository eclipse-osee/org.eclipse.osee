package org.eclipse.osee.display.view.web;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class OseeFooter extends HorizontalLayout {

   private boolean populated;

   public OseeFooter() {
   }

   @Override
   public void attach() {
      if (populated) {
         // Only populate the layout once
         return;
      }

      Label summary = new Label(getApplicationInfo());
      addComponent(summary);
      populated = true;

      this.setStyleName(CssConstants.OSEE_FOOTER_BAR);
      this.setWidth(100, UNITS_PERCENTAGE);
      this.setHeight(null);
   }

   public String getApplicationInfo() {
      StringBuilder builder = new StringBuilder();
      //      String productName = getProductName();
      //      if (productName != null) {
      //         builder.append(productName);
      //         builder.append(", ");
      //      }
      builder.append("Version: ");
      builder.append(getApplication().getVersion());
      return builder.toString();
   }

   private String getProductName() {
      return "Open System Engineering Environment Open Source Edition";
   }
}