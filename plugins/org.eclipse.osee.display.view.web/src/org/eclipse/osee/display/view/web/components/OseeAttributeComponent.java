/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rightsimport com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
he Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.components;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.Application;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeAttributeComponent extends VerticalLayout implements AttributeComponent {

   private Collection<labelValuePair> attributes = new ArrayList<labelValuePair>();

   private class labelValuePair {
      private final String label;
      private final String value;

      public labelValuePair(String label, String value) {
         super();
         this.label = label;
         this.value = value;
      }

      @SuppressWarnings("unused")
      public String getLabel() {
         return label;
      }

      @SuppressWarnings("unused")
      public String getValue() {
         return value;
      }
   }

   private void createLayout() {
      removeAllComponents();

      final HorizontalLayout attributesLayout = new HorizontalLayout();

      //      final OseeShowHideButton showHideButton = new OseeShowHideButton("Attributes");
      //      showHideButton.addListener(new OseeShowHideButton.ClickListener() {
      //         @Override
      //         public void buttonClick(OseeShowHideButton.ClickEvent event) {
      //            attributesLayout.setVisible(showHideButton.isStateShow());
      //         }
      //      });
      //      attributesLayout.setVisible(showHideButton.isStateShow());

      Label titleLabel = new Label("Attributes");
      titleLabel.setStyleName(CssConstants.OSEE_ATTRIBUTESTITLELABEL);

      VerticalLayout attrLabelsLayout = new VerticalLayout();
      VerticalLayout attrValuesLayout = new VerticalLayout();

      for (labelValuePair pair : attributes) {
         Label attrLabel = new Label(String.format("%s:", pair.getLabel()));
         Label attrValue = new Label(pair.getValue());
         attrLabel.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL);
         attrValue.setStyleName(CssConstants.OSEE_ATTRIBUTEVALUE);

         attrLabelsLayout.addComponent(attrLabel);
         attrValuesLayout.addComponent(attrValue);
      }

      Label spacer = new Label("");
      spacer.setWidth(15, UNITS_PIXELS);
      Label spacer2 = new Label("");
      spacer2.setWidth(5, UNITS_PIXELS);
      attributesLayout.addComponent(spacer);
      attributesLayout.addComponent(attrLabelsLayout);
      attributesLayout.addComponent(spacer2);
      attributesLayout.addComponent(attrValuesLayout);

      addComponent(titleLabel);
      //      addComponent(showHideButton);
      addComponent(attributesLayout);
      setExpandRatio(attributesLayout, 1.0f);

      attrLabelsLayout.setWidth(200, UNITS_PIXELS);
      attrValuesLayout.setWidth(200, UNITS_PIXELS);
   }

   @Override
   public void clearAll() {
      attributes.clear();
      createLayout();
   }

   @Override
   public void setErrorMessage(String message) {
      Application app = this.getApplication();
      if (app != null) {
         Window mainWindow = app.getMainWindow();
         if (mainWindow != null) {
            mainWindow.showNotification(message, Notification.TYPE_ERROR_MESSAGE);
         } else {
            System.out.println("OseeAttributeComponent.setErrorMessage - ERROR: Application.getMainWindow() returns null value.");
         }
      } else {
         System.out.println("OseeAttributeComponent.setErrorMessage - ERROR: getApplication() returns null value.");
      }
   }

   @Override
   public void addAttribute(String type, String value) {
      if (type != null && !type.isEmpty() && value != null && !value.isEmpty()) {
         attributes.add(new labelValuePair(type, value));
      }
      createLayout();
   }

}
