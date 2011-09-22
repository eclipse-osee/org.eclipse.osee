/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ui.web.internal;

import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class View2 extends CustomComponent implements Navigator.View {

   TextArea tf = new TextArea();
   VerticalLayout lo = new VerticalLayout();
   Button save = new Button("Save");
   boolean saved = true;

   @Override
   public String getWarningForNavigatingFrom() {
      return saved ? null : "The text you are editing has not been saved.";
   }

   @Override
   public void init(Navigator navigator, Application application) {
      setSizeFull();
      lo.addComponent(tf);
      tf.setRows(10);
      tf.setSizeFull();
      lo.setSizeFull();
      lo.setExpandRatio(tf, 1.0F);
      lo.addComponent(save);
      lo.setSpacing(true);
      lo.setComponentAlignment(save, Alignment.MIDDLE_RIGHT);
      setCompositionRoot(lo);
      save.addListener(new Button.ClickListener() {

         @Override
         public void buttonClick(ClickEvent event) {
            saved = true;
         }
      });

      tf.addListener(new Property.ValueChangeListener() {

         @Override
         public void valueChange(ValueChangeEvent event) {
            saved = false;
         }
      });
   }

   @Override
   public void navigateTo(String requestedDataId) {
      // Do Nothing
   }
}