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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeAttributeComponent extends VerticalLayout implements AttributeComponent {

   private boolean isLayoutComplete = false;
   private final VerticalLayout attrLabelsLayout = new VerticalLayout();
   private final VerticalLayout attrValuesLayout = new VerticalLayout();
   private final VerticalLayout longAttrValuesLayout = new VerticalLayout();

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

      attrLabelsLayout.setWidth(200, UNITS_PIXELS);
      attrValuesLayout.setWidth(500, UNITS_PIXELS);

      Label spacer = new Label("");
      spacer.setWidth(15, UNITS_PIXELS);
      Label spacer2 = new Label("");
      spacer2.setWidth(5, UNITS_PIXELS);

      HorizontalLayout hLayout_Lists = new HorizontalLayout();

      hLayout_Lists.addComponent(spacer);
      hLayout_Lists.addComponent(attrLabelsLayout);
      hLayout_Lists.addComponent(spacer2);
      hLayout_Lists.addComponent(attrValuesLayout);

      addComponent(titleLabel);
      addComponent(hLayout_Lists);
      addComponent(longAttrValuesLayout);
   }

   @Override
   public void clearAll() {
      attrLabelsLayout.removeAllComponents();
      attrValuesLayout.removeAllComponents();
      longAttrValuesLayout.removeAllComponents();
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      OseeExceptionDialogComponent dlg =
         new OseeExceptionDialogComponent(msgType, shortMsg, longMsg, getApplication().getMainWindow());
   }

   @Override
   public void addAttribute(String type, String value) {
      if (type != null && !type.isEmpty() && value != null && !value.isEmpty()) {

         if (!type.equalsIgnoreCase("Word Template Content")) {
            Label attrLabel = new Label(String.format("%s:", type));
            attrLabel.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL);

            Label attrValue = new Label(value);
            attrValue.setStyleName(CssConstants.OSEE_ATTRIBUTEVALUE);

            attrLabelsLayout.addComponent(attrLabel);
            attrValuesLayout.addComponent(attrValue);
         } else {
            Label attrLabel = new Label(String.format("%s:", type));
            attrLabel.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL_LONG);

            TextField attrValue = new TextField();
            attrValue.setValue(value);
            attrValue.setWidth(600, UNITS_PIXELS);
            attrValue.setHeight(300, UNITS_PIXELS);

            Label vSpacer_bottomAttr = new Label();
            vSpacer_bottomAttr.setHeight(15, UNITS_PIXELS);

            longAttrValuesLayout.addComponent(attrLabel);
            longAttrValuesLayout.addComponent(attrValue);
            longAttrValuesLayout.addComponent(vSpacer_bottomAttr);

            longAttrValuesLayout.setComponentAlignment(attrLabel, Alignment.BOTTOM_LEFT);
         }
      }
   }

}
