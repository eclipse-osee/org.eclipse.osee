package org.eclipse.osee.display.view.web.internal;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class OseeFooter extends VerticalLayout {

   private boolean populated;

   public OseeFooter() {
   }

   @Override
   public void attach() {
      if (populated) {
         // Only populate the layout once
         return;
      }

      Label ll = new Label("Add Navigation Links Here");
      ll.setWidth(null);
      addComponent(ll);
      setComponentAlignment(ll, Alignment.MIDDLE_CENTER);

      Label summary = new Label(getApplicationInfo());
      addComponent(summary);
      populated = true;
   }

   public String getApplicationInfo() {
      StringBuilder builder = new StringBuilder();
      String productName = getProductName();
      if (productName != null) {
         builder.append(productName);
         builder.append(", ");
      }
      builder.append("Version: ");
      builder.append(getApplication().getVersion());
      return builder.toString();
   }

   private String getProductName() {
      return "Open System Engineering Environment Open Source Edition";
   }
}