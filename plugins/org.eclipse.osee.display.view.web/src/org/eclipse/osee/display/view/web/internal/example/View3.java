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
package org.eclipse.osee.display.view.web.internal.example;

import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class View3 extends CustomComponent implements Navigator.View {

   VerticalLayout layout = new VerticalLayout();
   TextField tf = new TextField("Ticket number");
   Button show = new Button("Show ticket");
   Panel details = new Panel();

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

   @Override
   public void init(final Navigator navigator, Application application) {
      setCompositionRoot(layout);
      layout.addComponent(tf);
      layout.addComponent(show);
      layout.addComponent(details);
      show.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(ClickEvent event) {
            navigator.navigateTo(navigator.getUri(View3.class) + "/" + tf.toString());
         }
      });
   }

   @Override
   public void navigateTo(String requestedDataId) {
      if (requestedDataId == null) {
         tf.setValue("");
         details.setVisible(false);
      } else {
         tf.setValue(requestedDataId);
         details.setVisible(true);
         details.setCaption("Ticket #" + requestedDataId);
      }
   }

}
