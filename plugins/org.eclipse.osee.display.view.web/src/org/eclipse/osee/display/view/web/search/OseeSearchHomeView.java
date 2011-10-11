/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.search;

import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeSearchHomeView extends CustomComponent implements Navigator.View {

   protected OseeSearchHeaderComponent searchHeader;

   protected void createLayout() {
      this.setSizeFull();

      final VerticalLayout vertLayout = new VerticalLayout();
      vertLayout.setSizeFull();

      if (searchHeader != null) {
         searchHeader.setShowOseeTitleAbove(true);
         vertLayout.addComponent(searchHeader);
         vertLayout.setComponentAlignment(searchHeader, Alignment.MIDDLE_CENTER);
      }

      setCompositionRoot(vertLayout);
   }

   @Override
   public void navigateTo(String requestedDataId) {
      if (searchHeader != null) {
         searchHeader.createLayout();
      }
      createLayout();
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

   @Override
   public void init(Navigator navigator, Application application) {
      //Do nothing.
   }
}
