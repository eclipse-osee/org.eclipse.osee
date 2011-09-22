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
package org.eclipse.osee.display.view.web.internal.search;

import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class SearchView1 extends CustomComponent implements Navigator.View {

   @Override
   public void init(Navigator navigator, Application application) {
      setSizeFull();
      addComponent(new Label("Place Holder for SearchView"));
   }

   @Override
   public void navigateTo(String requestedDataId) {
      // State info stored in requestedDataId
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }
}
