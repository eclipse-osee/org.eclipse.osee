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
import com.vaadin.ui.Label;

@SuppressWarnings({"serial", "unchecked"})
public class View1 extends Label implements Navigator.View {

   @Override
   public String getWarningForNavigatingFrom() {
      return "You did not click save button, are you sure you want to navigate away?";
   }

   @Override
   public void init(Navigator navigator, Application application) {
      setValue("This is just a test of a view with guards");
   }

   @Override
   public void navigateTo(String requestedDataId) {
      // State info stored in requestedDataId
   }
}