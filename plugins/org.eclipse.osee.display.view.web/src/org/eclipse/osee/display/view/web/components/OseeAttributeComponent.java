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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeAttributeComponent extends VerticalLayout implements AttributeComponent {

   private WebArtifact artifact;

   private void createLayout() {
      removeAllComponents();

      if (artifact != null) {
         final HorizontalLayout attributesLayout = new HorizontalLayout();
         final Button showHideButton = new Button("- Attributes");
         showHideButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
               attributesLayout.setVisible(!attributesLayout.isVisible());
               if (attributesLayout.isVisible()) {
                  showHideButton.setCaption("- Attributes");
               } else {
                  showHideButton.setCaption("+ Attributes");
               }
            }
         });

         Map<String, String> attributes = new HashMap<String, String>();
         attributes.put("Category", artifact.getAttr_Category());
         attributes.put("Developmental Assurance Level", artifact.getAttr_DevAssurLevel());
         attributes.put("Imported Paragraph Number", artifact.getAttr_ImpoParaNum());
         attributes.put("Partition", artifact.getAttr_Partition());
         attributes.put("Qualification Method", artifact.getAttr_QualMethod());
         attributes.put("Subsystem", artifact.getAttr_Subsystm());
         attributes.put("Technical Performance Parameter", artifact.getAttr_TechPerfParam());
         Set<Entry<String, String>> set = attributes.entrySet();
         VerticalLayout attrLabelsLayout = new VerticalLayout();
         VerticalLayout attrValuesLayout = new VerticalLayout();

         for (Entry<String, String> entry : set) {
            Label attrLabel = new Label(String.format("%s:", entry.getKey()));
            Label attrValue = new Label(entry.getValue());
            attrLabel.setStyleName(CssConstants.OSEE_ATTRIBUTELABEL);
            attrValue.setStyleName(CssConstants.OSEE_ATTRIBUTEVALUE);

            attrLabelsLayout.addComponent(attrLabel);
            attrValuesLayout.addComponent(attrValue);

            attrLabelsLayout.setComponentAlignment(attrLabel, Alignment.MIDDLE_RIGHT);
            attrValuesLayout.setComponentAlignment(attrValue, Alignment.MIDDLE_LEFT);
         }

         Label spacer = new Label("");
         spacer.setWidth(15, UNITS_PIXELS);
         attributesLayout.addComponent(attrLabelsLayout);
         attributesLayout.addComponent(spacer);
         attributesLayout.addComponent(attrValuesLayout);

         addComponent(showHideButton);
         addComponent(attributesLayout);
         setExpandRatio(attributesLayout, 1.0f);
      }
   }

   @Override
   public void clearAll() {
      this.artifact = null;
      createLayout();
   }

   @Override
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
      createLayout();
   }

   @Override
   public void setErrorMessage(String message) {
   }

}
