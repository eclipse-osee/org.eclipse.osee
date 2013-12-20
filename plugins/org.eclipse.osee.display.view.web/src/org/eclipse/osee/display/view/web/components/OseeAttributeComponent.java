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

import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeAttributeComponent extends VerticalLayout implements AttributeComponent {

   private boolean isLayoutComplete = false;
   private final GridLayout attributesLayout = new GridLayout(3, 1);
   private int rowIndex = 0;

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         createLayout();
         isLayoutComplete = true;
      }
   }

   private void createLayout() {

      Label titleLabel = new Label("Attributes");
      titleLabel.setStyleName(CssConstants.OSEE_ATTRIBUTESTITLELABEL);

      addComponent(titleLabel);
      addComponent(attributesLayout);
   }

   @Override
   public void clearAll() {
      attributesLayout.removeAllComponents();
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      // do nothing
   }

   @Override
   public void addAttribute(String type, String value) {
      synchronized (getApplication()) {
         if (type != null && !type.trim().isEmpty() && value != null && !value.trim().isEmpty() && !type.toLowerCase().contains(
            "name")) {

            Label attrLabel = new Label(String.format("%s:", type));
            attrLabel.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL);
            attributesLayout.setComponentAlignment(attrLabel, Alignment.TOP_RIGHT);

            Label gridSpacer = new Label();
            gridSpacer.setWidth(5, UNITS_PIXELS);

            attributesLayout.addComponent(attrLabel, 0, rowIndex);
            attributesLayout.addComponent(gridSpacer, 1, rowIndex);

            if (value.length() > 150) {

               TextField attrValue = new TextField();
               attrValue.setValue(value);
               attrValue.setWidth(500, UNITS_PIXELS);
               attrValue.setHeight(150, UNITS_PIXELS);

               attributesLayout.setRows(attributesLayout.getRows() + 1);
               attributesLayout.addComponent(attrValue, 2, rowIndex);
            } else {

               Label attrValue = new Label(value);
               attrValue.setStyleName(CssConstants.OSEE_ATTRIBUTEVALUE);

               attributesLayout.setRows(attributesLayout.getRows() + 1);
               attributesLayout.addComponent(attrValue, 2, rowIndex);
            }
            rowIndex++;
         }
      }
   }
}
